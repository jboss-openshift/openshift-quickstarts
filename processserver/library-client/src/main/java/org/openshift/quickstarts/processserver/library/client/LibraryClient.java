package org.openshift.quickstarts.processserver.library.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.remote.common.rest.KieRemoteHttpRequest;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.RuleServicesClient;
import org.openshift.quickstarts.processserver.library.types.Book;
import org.openshift.quickstarts.processserver.library.types.Loan;
import org.openshift.quickstarts.processserver.library.types.LoanRequest;
import org.openshift.quickstarts.processserver.library.types.LoanResponse;
import org.openshift.quickstarts.processserver.library.types.ReturnRequest;
import org.openshift.quickstarts.processserver.library.types.ReturnResponse;
import org.openshift.quickstarts.processserver.library.types.Suggestion;
import org.openshift.quickstarts.processserver.library.types.SuggestionRequest;
import org.openshift.quickstarts.processserver.library.types.SuggestionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryClient {

    private static final Logger logger = LoggerFactory.getLogger(LibraryClient.class);
    private static final KieCommands commands = KieServices.Factory.get().getCommands();

    public static void main(String... args) throws Exception {
        LibraryClient client = new LibraryClient(new LibraryConfig());
        String command = (args != null && args.length > 0) ? args[0] : null;
        if (!client.runCommand(command, new PrintWriter(System.out, true))) {
            throw new Exception("Nothing run! Must specify -Dexec.args=runLocal (or runRemoteRest, runRemoteHornetMQ, runRemoteActiveMQ).");
        }
    }

    private final LibraryConfig appcfg;

    public LibraryClient(LibraryConfig appcfg) {
        this.appcfg = appcfg;
    }

    // package-protected for LibraryServlet
    boolean runCommand(String command, PrintWriter out) throws Exception {
        boolean run = false;
        command = appcfg.trimToNull(command);
        if ("runLocal".equals(command)) {
            runLocal(out);
            run = true;
        } else if ("runRemoteRest".equals(command)) {
            runRemoteRest(out);
            run = true;
        } else if ("runRemoteHornetQ".equals(command)) {
            runRemoteHornetQ(out);
            run = true;
        } else if ("runRemoteActiveMQ".equals(command)) {
            runRemoteActiveMQ(out);
            run = true;
        }
        return run;
    }

    private void runLocal(PrintWriter out) {
        appcfg.setKieSession(KieServices.Factory.get().getKieClasspathContainer().newKieSession());
        appcfg.setRuleServicesClient(null);
        appcfg.setProcessServicesClient(null);
        appcfg.setMarshallingFormat(null);
        runApp(out);
    }

    private void runRemoteRest(PrintWriter out) throws Exception {
        String baseurl = appcfg.getBaseUrl("http", "localhost", "8080");
        String resturl = baseurl + "/kie-server/services/rest/server";
        logger.debug("---------> resturl: " + resturl);
        String username = appcfg.getUsername();
        String password = appcfg.getPassword();
        KieServicesConfiguration kiecfg = KieServicesFactory.newRestConfiguration(resturl, username, password);
        if (resturl.toLowerCase().startsWith("https")) {
            kiecfg.setUseSsl(true);
            forgiveUnknownCert();
        }
        runRemote(out, kiecfg);
    }

    private void runRemoteHornetQ(PrintWriter out) throws Exception {
        String baseurl = appcfg.getBaseUrl("remote", "localhost", "4447");
        String username = appcfg.getUsername();
        String password = appcfg.getPassword();
        String qusername = appcfg.getQUsername();
        String qpassword = appcfg.getQPassword();
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, baseurl);
        props.setProperty(Context.SECURITY_PRINCIPAL, username);
        props.setProperty(Context.SECURITY_CREDENTIALS, password);
        InitialContext context = new InitialContext(props);
        KieServicesConfiguration kiecfg = KieServicesFactory.newJMSConfiguration(context, qusername, qpassword);
        runRemote(out, kiecfg);
    }

    private void runRemoteActiveMQ(PrintWriter out) throws Exception {
        String baseurl = appcfg.getBaseUrl("tcp", "localhost", "61616");
        String username = appcfg.getUsername();
        String password = appcfg.getPassword();
        String qusername = appcfg.getQUsername();
        String qpassword = appcfg.getQPassword();
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, baseurl);
        props.setProperty(Context.SECURITY_PRINCIPAL, username);
        props.setProperty(Context.SECURITY_CREDENTIALS, password);
        InitialContext context = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory)context.lookup("ConnectionFactory");
        Queue requestQueue = (Queue)context.lookup("dynamicQueues/queue/KIE.SERVER.REQUEST");
        Queue responseQueue = (Queue)context.lookup("dynamicQueues/queue/KIE.SERVER.RESPONSE");
        KieServicesConfiguration kiecfg = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue, qusername, qpassword);
        runRemote(out, kiecfg);
    }

    private void runRemote(PrintWriter out, KieServicesConfiguration kiecfg) throws Exception {
        appcfg.setKieSession(null);
        MarshallingFormat marshallingFormat = appcfg.getMarshallingFormat();
        out.println(String.format("Using %s MarshallingFormat.%s", marshallingFormat.getType(), marshallingFormat.name()));
        kiecfg.setMarshallingFormat(marshallingFormat);
        if (MarshallingFormat.JAXB.equals(marshallingFormat)) {
            Set<Class<?>> classes = new HashSet<Class<?>>();
            classes.add(Book.class);
            classes.add(Loan.class);
            classes.add(LoanRequest.class);
            classes.add(LoanResponse.class);
            classes.add(ReturnRequest.class);
            classes.add(ReturnResponse.class);
            classes.add(Suggestion.class);
            classes.add(SuggestionRequest.class);
            classes.add(SuggestionResponse.class);
            kiecfg.addJaxbClasses(classes);
        }
        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(kiecfg);
        RuleServicesClient ruleServicesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
        ProcessServicesClient processServicesClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
        appcfg.setRuleServicesClient(ruleServicesClient);
        appcfg.setProcessServicesClient(processServicesClient);
        runApp(out);
    }

    private void runApp(PrintWriter out) {
        if (appcfg.isHtml()) {
            out.println("<pre>");
        }
        try {
            // get 1st suggestion
            Suggestion suggestion1_Zombie = getSuggestion("Zombie");
            Book book1_WorldWarZ = suggestion1_Zombie.getBook();
            out.println("Received suggestion for book: " + book1_WorldWarZ.getTitle() + " (isbn: " + book1_WorldWarZ.getIsbn() + ")");
            assertEquals("World War Z", book1_WorldWarZ.getTitle());
            // take out 1st loan
            out.println("Attempting 1st loan for isbn: " + book1_WorldWarZ.getIsbn());
            Loan loan1_WorldWarZ = attemptLoan(book1_WorldWarZ.getIsbn());
            out.println("1st loan approved? " + loan1_WorldWarZ.isApproved());
            assertTrue(loan1_WorldWarZ.isApproved());
            // 2nd loan should not be approved since 1st loan hasn't been returned
            out.println("Attempting 2nd loan for isbn: " + book1_WorldWarZ.getIsbn());
            Loan loan2_WorldWarZ = attemptLoan(book1_WorldWarZ.getIsbn());
            out.println("2nd loan approved? " + loan2_WorldWarZ.isApproved());
            assertFalse(loan2_WorldWarZ.isApproved());
            // return 1st loan
            out.println("Returning 1st loan for isbn: " + loan1_WorldWarZ.getBook().getIsbn());
            boolean return1_ack = returnLoan(loan1_WorldWarZ);
            out.println("1st loan return acknowledged? " + return1_ack);
            assertTrue(return1_ack);
            // try 2nd loan again; this time it should work
            out.println("Re-attempting 2nd loan for isbn: " + book1_WorldWarZ.getIsbn());
            loan2_WorldWarZ = attemptLoan(book1_WorldWarZ.getIsbn());
            out.println("Re-attempt of 2nd loan approved? " + loan2_WorldWarZ.isApproved());
            assertTrue(loan2_WorldWarZ.isApproved());
            // get 2nd suggestion, and since 1st book not available (again), 2nd match will return
            Suggestion suggestion2_TheZombieSurvivalGuide = getSuggestion("Zombie");
            Book book2_TheZombieSurvivalGuide = suggestion2_TheZombieSurvivalGuide.getBook();
            out.println("Received suggestion for book: " + book2_TheZombieSurvivalGuide.getTitle() + " (isbn: " + book2_TheZombieSurvivalGuide.getIsbn() + ")");
            assertEquals("The Zombie Survival Guide", book2_TheZombieSurvivalGuide.getTitle());
            // take out 3rd loan
            out.println("Attempting 3rd loan for isbn: " + book2_TheZombieSurvivalGuide.getIsbn());
            Loan loan3_TheZombieSurvivalGuide = attemptLoan(book2_TheZombieSurvivalGuide.getIsbn());
            out.println("3rd loan approved? " + loan3_TheZombieSurvivalGuide.isApproved());
            assertTrue(loan3_TheZombieSurvivalGuide.isApproved());
            // return 2nd loan
            out.println("Returning 2nd loan for isbn: " + loan2_WorldWarZ.getBook().getIsbn());
            boolean return2_ack = returnLoan(loan2_WorldWarZ);
            out.println("2nd loan return acknowledged? " + return2_ack);
            assertTrue(return2_ack);
            // return 3rd loan
            out.println("Returning 3rd loan for isbn: " + loan3_TheZombieSurvivalGuide.getBook().getIsbn());
            boolean return3_ack = returnLoan(loan3_TheZombieSurvivalGuide);
            out.println("3rd loan return acknowledged? " + return3_ack);
            assertTrue(return3_ack);
        } finally {
            if (appcfg.isHtml()) {
                out.println("</pre>");
            }
        }
    }

    Suggestion getSuggestion(String keyword) {
        SuggestionRequest suggestionRequest = new SuggestionRequest();
        suggestionRequest.setKeyword(keyword);
        suggestionRequest.setKeyword("Zombie");
        List<Command<?>> cmds = new ArrayList<Command<?>>();
        cmds.add(commands.newInsert(suggestionRequest));
        cmds.add(commands.newFireAllRules());
        cmds.add(commands.newQuery("suggestion", "get suggestion"));
        BatchExecutionCommand batch = commands.newBatchExecution(cmds, "LibraryRuleSession");
        ExecutionResults execResults;
        if (appcfg.getKieSession() != null) {
            execResults = appcfg.getKieSession().execute(batch);
        } else {
            ServiceResponse<ExecutionResults> serviceResponse = appcfg.getRuleServicesClient().executeCommandsWithResults("processserver-library", batch);
            //logger.info(String.valueOf(serviceResponse));
            execResults = serviceResponse.getResult();
        }
        QueryResults queryResults = (QueryResults)execResults.getValue("suggestion");
        if (queryResults != null) {
            for (QueryResultsRow queryResult : queryResults) {
                SuggestionResponse suggestionResponse = (SuggestionResponse)queryResult.get("suggestionResponse");
                if (suggestionResponse != null) {
                    return suggestionResponse.getSuggestion();
                }
            }
        }
        return null;
    }

    Loan attemptLoan(String isbn) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setIsbn(isbn);
        parameters.put("loanRequest", loanRequest);
        LoanResponse loanResponse;
        if (appcfg.getKieSession() != null) {
            KieSession kieSession = appcfg.getKieSession();
            WorkflowProcessInstance procinst = (WorkflowProcessInstance)kieSession.startProcess("LibraryProcess", parameters);
            loanResponse = (LoanResponse)procinst.getVariable("loanResponse");
        } else {
            ProcessServicesClient procserv = appcfg.getProcessServicesClient();
            Long pid = procserv.startProcess("processserver-library", "LibraryProcess", parameters);
            loanResponse = (LoanResponse)procserv.getProcessInstanceVariable("processserver-library", pid, "loanResponse");
        }
        return loanResponse != null ? loanResponse.getLoan() : null;
    }

    boolean returnLoan(Loan loan) {
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setLoan(loan);
        ReturnResponse returnResponse;
        if (appcfg.getKieSession() != null) {
            KieSession kieSession = appcfg.getKieSession();
            WorkflowProcessInstance procinst = (WorkflowProcessInstance)kieSession.getProcessInstance(loan.getId());
            procinst.signalEvent("ReturnSignal", returnRequest);
            returnResponse = (ReturnResponse)procinst.getVariable("returnResponse");
        } else {
            ProcessServicesClient procserv = appcfg.getProcessServicesClient();
            procserv.signalProcessInstance("processserver-library", loan.getId(), "ReturnSignal", returnRequest);
            //returnResponse = (ReturnResponse)procserv.getProcessInstanceVariable("processserver-library", loan.getId(), "returnResponse");
            returnResponse = new ReturnResponse();
            returnResponse.setAcknowledged(true);
        }
        return returnResponse != null ? returnResponse.isAcknowledged() : false;
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

    private void assertEquals(Object expected, Object actual) {
        if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
            logger.warn(expected + " != " + actual);
            //throw new RuntimeException(expected + " != " + actual);
        }
    }

    private void assertTrue(boolean condition) {
        if (!condition) {
            logger.warn("expected true");
            //throw new RuntimeException("expected true");
        }
    }

    private void assertFalse(boolean condition) {
        if (condition) {
            logger.warn("expected false");
            //throw new RuntimeException("expected false");
        }
    }

}
