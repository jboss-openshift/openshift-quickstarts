package org.openshift.quickstarts.procexecserver.library.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryTest {

    private static final Logger logger = LoggerFactory.getLogger(LibraryTest.class);

    @Test
    public void testLocal() {
        LibraryClient client = new LibraryClient();
        LibraryCallback callback = new LibraryCallback();
        client.runLocal(callback);
        assertEquals(1, callback.getQueryResultsSize());
        assertEquals("World War Z", callback.getSuggestion().getBook().getTitle());
        logger.info("********** " + callback.getSuggestion().getBook().getTitle() + " **********");
    }

}
