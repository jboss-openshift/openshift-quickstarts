package org.openshift.quickstarts.processserver.timerprocess;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fspolti on 1/20/17.
 */
public class TimerProcessTest {

    private KieSession kieSession = KieServices.Factory.get().getKieClasspathContainer().newKieSession();

    @Test
    public void testTimerProcess() {

        //registering the Work Item Handler
        registerWIH();;

        //Starting first process, a PLUS operation
        WorkflowProcessInstance process1 = getWorkFlowProcessInstance();
        Assert.assertNotNull(process1);
        kieSession.getWorkItemManager().completeWorkItem(1, parameters(10, 234, Operation.PLUS));
        Assert.assertEquals(process1.getVariable("result"), 244);

        //Starting second process, a MINUS operation
        WorkflowProcessInstance process2 = getWorkFlowProcessInstance();
        Assert.assertNotNull(process2);
        kieSession.getWorkItemManager().completeWorkItem(2, parameters(489, 120, Operation.MINUS));
        Assert.assertEquals(process2.getVariable("result"), 369);

        //Starting third process, a TIMES operation
        WorkflowProcessInstance process3 = getWorkFlowProcessInstance();
        Assert.assertNotNull(process3);
        kieSession.getWorkItemManager().completeWorkItem(3, parameters(34, 12, Operation.TIMES));
        Assert.assertEquals(process3.getVariable("result"), 408);

        //Starting fourth process, a DIVIDE operation
        WorkflowProcessInstance process4 = getWorkFlowProcessInstance();
        Assert.assertNotNull(process4);
        kieSession.getWorkItemManager().completeWorkItem(4, parameters(5000, 100,Operation.DIVIDE));
        Assert.assertEquals(process4.getVariable("result"), 50);
    }

    /*
    * Returns a new process
    */
    private WorkflowProcessInstance getWorkFlowProcessInstance() {
        return (WorkflowProcessInstance) kieSession.startProcess("timerprocess.QuartzTimerProcess");
    }

    /*
    * Register the Work Item Handler - needed by Human Tasks.
    */
    private void registerWIH() {
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kieSession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
    }

    /*
    * Set Process needed input
    * @param Integer number1
    * @param Integer number2
    * @param Enum operator
    */
    private  Map<String, Object> parameters(Integer number1, Integer number2, Enum operator) {
        Inputs input = new Inputs();
        input.setNumber1(number1);
        input.setNumber2(number2);
        input.setOperator(String.valueOf(Operation.valueOf(operator.name()).operator));
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("input", input);
        return parameters;
    }

    /*
     * Valid math operations
     */
    private enum Operation {
        PLUS("+"),
        MINUS("-"),
        TIMES("*"),
        DIVIDE("/");

        private String operator;

        Operation(String operator) {
            this.operator = operator;
        }

        public String operator() {
            return operator;
        }
    }
}