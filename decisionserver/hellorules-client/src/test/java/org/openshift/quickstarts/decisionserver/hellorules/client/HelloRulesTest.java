package org.openshift.quickstarts.decisionserver.hellorules.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openshift.quickstarts.decisionserver.hellorules.client.HelloRulesClient.HelloRulesCallback;

public class HelloRulesTest {

    @Test
    public void testLocal() {
        HelloRulesCallback callback = new HelloRulesCallback();
        HelloRulesClient client = new HelloRulesClient(callback);
        client.runLocal();
        assertEquals(1, callback.queryResultsSize);
        assertEquals("Hello " + System.getProperty("user.name") + "!", callback.salutation);
    }

}
