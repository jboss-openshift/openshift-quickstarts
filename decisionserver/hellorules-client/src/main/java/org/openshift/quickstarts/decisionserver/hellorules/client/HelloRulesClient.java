package org.openshift.quickstarts.decisionserver.hellorules.client;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.remote.common.rest.KieRemoteHttpRequest;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.openshift.quickstarts.decisionserver.hellorules.Greeting;
import org.openshift.quickstarts.decisionserver.hellorules.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloRulesClient {

    private static final Logger logger = LoggerFactory.getLogger(HelloRulesClient.class);

    public static void main(String... args) throws Exception {
        HelloRulesClient client = new HelloRulesClient();
        String command = (args != null && args.length > 0) ? args[0] : null;
        HelloRulesCallback callback = new HelloRulesCallback();
        if (client.runCommand(command, callback)) {
            logger.info("********** " + callback.getSalutation() + " **********");
        } else {
            throw new Exception("Nothing run! Must specify -Dexec.args=runLocal (or runRemoteRest, runRemoteHornetMQ, runRemoteActiveMQ).");
        }
    }

    // package-protected for HelloRulesServlet
    boolean runCommand(String command, HelloRulesCallback callback) throws Exception {
        boolean run = false;
        command = trimToNull(command);
        HelloRulesClient client = new HelloRulesClient();
        if ("runLocal".equals(command)) {
            client.runLocal(callback);
            run = true;
        } else if ("runRemoteRest".equals(command)) {
            client.runRemoteRest(callback);
            run = true;
        } else if ("runRemoteHornetQ".equals(command)) {
            client.runRemoteHornetQ(callback);
            run = true;
        } else if ("runRemoteActiveMQ".equals(command)) {
            client.runRemoteActiveMQ(callback);
            run = true;
        }
        return run;
    }

    // package-protected for HelloRulesTest
    void runLocal(HelloRulesCallback callback) {
        KieContainer container = KieServices.Factory.get().getKieClasspathContainer();
        StatelessKieSession session = container.newStatelessKieSession();
        BatchExecutionCommand batch = createBatch();
        ExecutionResults execResults = session.execute(batch);
        handleResults(callback, execResults);
    }

    private void runRemoteRest(HelloRulesCallback callback) throws Exception {
        String baseurl = getBaseUrl(callback, "http", "localhost", "8080");
        String resturl = baseurl + "/kie-server/services/rest/server";
        logger.debug("---------> resturl: " + resturl);
        String username = getUsername(callback);
        String password = getPassword(callback);
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(resturl, username, password);
        if (resturl.toLowerCase().startsWith("https")) {
            config.setUseSsl(true);
            forgiveUnknownCert();
        }
        runRemote(callback, config);
    }

    private void runRemoteHornetQ(HelloRulesCallback callback) throws Exception {
        String baseurl = getBaseUrl(callback, "remote", "localhost", "4447");
        String username = getUsername(callback);
        String password = getPassword(callback);
        String qusername = getQUsername(callback);
        String qpassword = getQPassword(callback);
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, baseurl);
        props.setProperty(Context.SECURITY_PRINCIPAL, username);
        props.setProperty(Context.SECURITY_CREDENTIALS, password);
        InitialContext context = new InitialContext(props);
        KieServicesConfiguration config = KieServicesFactory.newJMSConfiguration(context, qusername, qpassword);
        runRemote(callback, config);
    }

    private void runRemoteActiveMQ(HelloRulesCallback callback) throws Exception {
        String baseurl = getBaseUrl(callback, "tcp", "localhost", "61616");
        String username = getUsername(callback);
        String password = getPassword(callback);
        String qusername = getQUsername(callback);
        String qpassword = getQPassword(callback);
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, baseurl);
        props.setProperty(Context.SECURITY_PRINCIPAL, username);
        props.setProperty(Context.SECURITY_CREDENTIALS, password);
        InitialContext context = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory)context.lookup("ConnectionFactory");
        Queue requestQueue = (Queue)context.lookup("dynamicQueues/queue/KIE.SERVER.REQUEST");
        Queue responseQueue = (Queue)context.lookup("dynamicQueues/queue/KIE.SERVER.RESPONSE");
        KieServicesConfiguration config = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue, qusername, qpassword);
        runRemote(callback, config);
    }

    private void runRemote(HelloRulesCallback callback, KieServicesConfiguration config) {
        MarshallingFormat marshallingFormat = getMarshallingFormat();
        config.setMarshallingFormat(marshallingFormat);
        if (MarshallingFormat.JAXB.equals(marshallingFormat)) {
            Set<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(Greeting.class);
            classes.add(Person.class);
            config.addJaxbClasses(classes);
        }
        RuleServicesClient client = KieServicesFactory.newKieServicesClient(config).getServicesClient(RuleServicesClient.class);
        BatchExecutionCommand batch = createBatch();
        ServiceResponse<ExecutionResults> response = client.executeCommandsWithResults("decisionserver-hellorules", batch);
        //logger.info(String.valueOf(response));
        ExecutionResults execResults = response.getResult();
        handleResults(callback, execResults);
    }

    private BatchExecutionCommand createBatch() {
        Person person = new Person(System.getProperty("user.name"));
        List<Command<?>> cmds = new ArrayList<Command<?>>();
        KieCommands commands = KieServices.Factory.get().getCommands();
        cmds.add(commands.newInsert(person));
        cmds.add(commands.newFireAllRules());
        cmds.add(commands.newQuery("greetings", "get greeting"));
        return commands.newBatchExecution(cmds, "HelloRulesSession");
    }

    private void handleResults(HelloRulesCallback callback, ExecutionResults execResults) {
        QueryResults queryResults = (QueryResults)execResults.getValue("greetings");
        if (queryResults != null) {
            callback.setQueryResultsSize(queryResults.size());
            for (QueryResultsRow queryResult : queryResults) {
                Greeting greeting = (Greeting)queryResult.get("greeting");
                if (greeting != null) {
                    callback.setSalutation(greeting.getSalutation());
                    break;
                }
            }
        }
    }

    private String getBaseUrl(HelloRulesCallback callback, String defaultProtocol, String defaultHost, String defaultPort) {
        String protocol = trimToNull(callback.getProtocol());
        if (protocol == null) {
            protocol = trimToNull(System.getProperty("protocol", defaultProtocol));
        }
        String host = trimToNull(callback.getHost());
        if (host == null) {
            host = trimToNull(System.getProperty("host", System.getProperty("jboss.bind.address", defaultHost)));
        }
        String port = trimToNull(callback.getPort());
        if (port == null) {
            if ("https".equalsIgnoreCase(protocol)) {
                defaultPort = null;
            }
            port = trimToNull(System.getProperty("port", defaultPort));
        }
        String baseurl = protocol + "://" + host + (port != null ? ":" + port : "");
        logger.info("---------> baseurl: " + baseurl);
        return baseurl;
    }

    private String getUsername(HelloRulesCallback callback) {
        String username = trimToNull(callback.getUsername());
        if (username == null) {
            username = trimToNull(System.getProperty("username", "kieserver"));
        }
        logger.debug("---------> username: " + username);
        return username;
    }

    private String getPassword(HelloRulesCallback callback) {
        String password = callback.getPassword();
        if (password == null) {
            password = System.getProperty("password", "kieserver1!");
        }
        logger.debug("---------> password: " + password);
        return password;
    }

    private String getQUsername(HelloRulesCallback callback) {
        String qusername = trimToNull(callback.getQUsername());
        if (qusername == null) {
            qusername = trimToNull(System.getProperty("qusername", getUsername(callback)));
        }
        logger.debug("---------> qusername: " + qusername);
        return qusername;
    }

    private String getQPassword(HelloRulesCallback callback) {
        String qpassword = callback.getQPassword();
        if (qpassword == null) {
            qpassword = System.getProperty("qpassword", getPassword(callback));
        }
        logger.debug("---------> qpassword: " + qpassword);
        return qpassword;
    }

    private MarshallingFormat getMarshallingFormat() {
        // can use xstream, xml (jaxb), or json
        String type = System.getProperty("MarshallingFormat", "xstream");
        if (type.trim().equalsIgnoreCase("jaxb")) {
            type = "xml";
        }
        MarshallingFormat marshallingFormat = MarshallingFormat.fromType(type);
        logger.debug(String.format("--------->  %s MarshallingFormat.%s", marshallingFormat.getType(), marshallingFormat.name()));
        return marshallingFormat;
    }

    private String trimToNull(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() == 0) {
                str = null;
            }
        }
        return str;
    }

    // only needed for non-production test scenarios where the TLS certificate isn't set up properly
    private void forgiveUnknownCert() throws Exception {
        KieRemoteHttpRequest.ConnectionFactory connf = new KieRemoteHttpRequest.ConnectionFactory() {
            public HttpURLConnection create(URL u) throws IOException {
                return forgiveUnknownCert((HttpURLConnection)u.openConnection());
            }
            public HttpURLConnection create(URL u, Proxy p) throws IOException {
                return forgiveUnknownCert((HttpURLConnection)u.openConnection(p));
            }
            private HttpURLConnection forgiveUnknownCert(HttpURLConnection conn) throws IOException {
                if (conn instanceof HttpsURLConnection) {
                    HttpsURLConnection sconn = HttpsURLConnection.class.cast(conn);
                    sconn.setHostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String arg0, SSLSession arg1) {
                            return true;
                        }
                    });
                    try {
                        SSLContext context = SSLContext.getInstance("TLS");
                        context.init(null, new TrustManager[] {
                            new X509TrustManager() {
                                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                                public X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }
                            }
                        }, null);
                        sconn.setSSLSocketFactory(context.getSocketFactory());
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }
                return conn;
            }
        };
        Field field = KieRemoteHttpRequest.class.getDeclaredField("CONNECTION_FACTORY");
        field.setAccessible(true);
        field.set(null, connf);
    }

}
