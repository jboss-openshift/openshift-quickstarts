package org.openshift.quickstarts.decisionserver.hellorules.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloRulesTest {

    private static final Logger logger = LoggerFactory.getLogger(HelloRulesTest.class);

    @Test
    public void testLocal() {
        HelloRulesClient client = new HelloRulesClient();
        HelloRulesCallback callback = new HelloRulesCallback();
        client.runLocal(callback);
        assertEquals(1, callback.getQueryResultsSize());
        assertEquals("Hello " + System.getProperty("user.name") + "!", callback.getSalutation());
        logger.info("********** " + callback.getSalutation() + " **********");
    }

}
