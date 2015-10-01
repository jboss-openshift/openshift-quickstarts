package org.openshift.quickstarts.decisionserver.hellorules;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
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

public class HelloRulesTest {

    public static void main(String... args) {
        HelloRulesTest test = new HelloRulesTest();
        //test.testLocal();
        test.testRemote();
    }

    @Test
    public void testLocal() {
        KieContainer container = KieServices.Factory.get().getKieClasspathContainer();
        StatelessKieSession session = container.newStatelessKieSession();
        BatchExecutionCommand batch = createBatch();
        ExecutionResults execResults = session.execute(batch);
        handleResults(execResults);
    }

    public void testRemote() {
        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(
            "http://localhost:8080/kie-server/services/rest/server",
            //"http://kie-app-dward.router.default.svc.cluster.local/kie-server/services/rest/server",
            "kieserver", "kieserver1!");
        config.setMarshallingFormat(MarshallingFormat.XSTREAM);
        RuleServicesClient client = KieServicesFactory.newKieServicesClient(config).getServicesClient(RuleServicesClient.class);
        BatchExecutionCommand batch = createBatch();
        ServiceResponse<String> response = client.executeCommands("HelloRulesContainer", batch);
        System.out.println(response);
        ExecutionResults execResults = (ExecutionResults)BatchExecutionHelper.newXStreamMarshaller().fromXML(response.getResult());
        handleResults(execResults);
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
        QueryResults queryResults = (QueryResults)execResults.getValue("greetings");
        assertEquals(1, queryResults.size());
        String salutation = null;
        for (QueryResultsRow queryResult : queryResults) {
            Greeting greeting = (Greeting)queryResult.get("greeting");
            if (greeting != null) {
                salutation = greeting.getSalutation();
                break;
            }
        }
        System.out.println("********** HelloRulesTest.java: " + salutation + " **********");
        assertEquals("Hello " + System.getProperty("user.name") + "!", salutation);
    }

}
