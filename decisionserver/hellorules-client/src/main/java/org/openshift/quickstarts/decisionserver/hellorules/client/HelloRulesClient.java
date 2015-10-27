package org.openshift.quickstarts.decisionserver.hellorules.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.helper.BatchExecutionHelper;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.openshift.quickstarts.decisionserver.hellorules.Greeting;
import org.openshift.quickstarts.decisionserver.hellorules.Person;

public class HelloRulesClient {

    public static void main(String... args) throws Exception {
        boolean run = false;
        if (args != null && args.length > 0) {
            HelloRulesClient client = new HelloRulesClient(null);
            for (String method : args) {
                if ("runLocal".equals(method)) {
                    client.runLocal();
                    run = true;
                } else if ("runRemoteRest".equals(method)) {
                    client.runRemoteRest();
                    run = true;
                } else if ("runRemoteHornetQ".equals(method)) {
                    client.runRemoteHornetQ();
                    run = true;
                } else if ("runRemoteActiveMQ".equals(method)) {
                    client.runRemoteActiveMQ();
                    run = true;
                }
            }
        }
        if (!run) {
            throw new Exception("Nothing run! Must specify -Dexec.args=runLocal" +
                " (or runRemoteRest, runRemoteHornetMQ, runRemoteActiveMQ).");
        }
    }

    private final HelloRulesCallback callback;

    public static class HelloRulesCallback {
        public HelloRulesCallback() {}
        public int queryResultsSize = 0;
        public String salutation = null;
        public void reset() {
            queryResultsSize = 0;
            salutation = null;
        }
    }

    // See HelloRulesTest for HelloRulesCallback usage.
    public HelloRulesClient(HelloRulesCallback callback) {
        this.callback = callback != null ? callback : new HelloRulesCallback();
    }

    public void runLocal() {
        KieContainer container = KieServices.Factory.get().getKieClasspathContainer();
        StatelessKieSession session = container.newStatelessKieSession();
        BatchExecutionCommand batch = createBatch();
        ExecutionResults execResults = session.execute(batch);
        handleResults(execResults);
    }

    public void runRemoteRest() {
        String path = "/kie-server/services/rest/server";
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(
            getRemoteUrl("http", "localhost", "8080") + path,
            //getUrl("http", "kie-app-dward.router.default.svc.cluster.local", null) + path,
            "kieserver", "kieserver1!");
        runRemote(config);
    }

    private void runRemoteHornetQ() throws Exception {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, getRemoteUrl("remote", "localhost", "4447"));
        props.setProperty(Context.SECURITY_PRINCIPAL, "kieserver");
        props.setProperty(Context.SECURITY_CREDENTIALS, "kieserver1!");
        InitialContext context = new InitialContext(props);
        KieServicesConfiguration config = KieServicesFactory.newJMSConfiguration(context,
            "kieserver", "kieserver1!");
        runRemote(config);
    }

    private void runRemoteActiveMQ() throws Exception {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, getRemoteUrl("tcp", "localhost", "61616"));
        //props.setProperty(Context.PROVIDER_URL, getUrl("ssl", "secure-kie-app-dward.router.default.svc.cluster.local", "443"));
        //System.setProperty ("jsse.enableSNIExtension", "true");
        props.setProperty(Context.SECURITY_PRINCIPAL, "kieserver");
        props.setProperty(Context.SECURITY_CREDENTIALS, "kieserver1!");
        InitialContext context = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory)context.lookup("ConnectionFactory");
        Queue requestQueue = (Queue)context.lookup("dynamicQueues/queue/KIE.SERVER.REQUEST");
        Queue responseQueue = (Queue)context.lookup("dynamicQueues/queue/KIE.SERVER.RESPONSE");
        KieServicesConfiguration config = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue,
            "kieserver", "kieserver1!");
        runRemote(config);
    }

    private void runRemote(KieServicesConfiguration config) {
        config.setMarshallingFormat(MarshallingFormat.XSTREAM);
        RuleServicesClient client = KieServicesFactory.newKieServicesClient(config).getServicesClient(RuleServicesClient.class);
        BatchExecutionCommand batch = createBatch();
        ServiceResponse<String> response = client.executeCommands("HelloRulesContainer", batch);
        System.out.println(response);
        ExecutionResults execResults = (ExecutionResults)BatchExecutionHelper.newXStreamMarshaller().fromXML(response.getResult());
        handleResults(execResults);
    }

    private String getRemoteUrl(String defaultProtocol, String defaultHost, String defaultPort) {
        String protocol = trimToNull(System.getProperty("protocol", defaultProtocol));
        String host = trimToNull(System.getProperty("host", defaultHost));
        String port = trimToNull(System.getProperty("port", defaultPort));
        String url = protocol + "://" + host + (port != null ? ":" + port : "");
        System.out.println("---------> url: " + url);
        return url;
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

    private BatchExecutionCommand createBatch() {
        Person person = new Person(System.getProperty("user.name"));
        List<Command<?>> cmds = new ArrayList<Command<?>>();
        cmds.add(CommandFactory.newInsert(person));
        cmds.add(CommandFactory.newFireAllRules());
        cmds.add(CommandFactory.newQuery("greetings", "get greeting"));
        return CommandFactory.newBatchExecution(cmds, "HelloRulesSession");
    }

    private void handleResults(ExecutionResults execResults) {
        callback.reset();
        QueryResults queryResults = (QueryResults)execResults.getValue("greetings");
        callback.queryResultsSize = queryResults.size();
        String salutation = null;
        for (QueryResultsRow queryResult : queryResults) {
            Greeting greeting = (Greeting)queryResult.get("greeting");
            if (greeting != null) {
                salutation = greeting.getSalutation();
                break;
            }
        }
        System.out.println("********** HelloRulesClient.java: " + salutation + " **********");
        callback.salutation = salutation;
    }

}
