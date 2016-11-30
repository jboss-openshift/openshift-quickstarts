package org.openshift.quickstarts.processserver.library.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.kie.api.KieServices;
import org.openshift.quickstarts.processserver.library.types.Book;
import org.openshift.quickstarts.processserver.library.types.Loan;
import org.openshift.quickstarts.processserver.library.types.Suggestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryTest {

    private static final Logger logger = LoggerFactory.getLogger(LibraryTest.class);

    @Test
    public void testLocal() {
        LibraryConfig appcfg = new LibraryConfig();
        appcfg.setKieSession(KieServices.Factory.get().getKieClasspathContainer().newKieSession());
        LibraryClient client = new LibraryClient(appcfg);
        // get 1st suggestion
        Suggestion suggestion1_Zombie = client.getSuggestion("Zombie");
        Book book1_WorldWarZ = suggestion1_Zombie.getBook();
        logger.info("Received suggestion for book: " + book1_WorldWarZ.getTitle() + " (isbn: " + book1_WorldWarZ.getIsbn() + ")");
        assertEquals("World War Z", book1_WorldWarZ.getTitle());
        // take out 1st loan
        logger.info("Attempting 1st loan for isbn: " + book1_WorldWarZ.getIsbn());
        Loan loan1_WorldWarZ = client.attemptLoan(book1_WorldWarZ.getIsbn());
        logger.info("1st loan approved? " + loan1_WorldWarZ.isApproved());
        assertTrue(loan1_WorldWarZ.isApproved());
        // 2nd loan should not be approved since 1st loan hasn't been returned
        logger.info("Attempting 2nd loan for isbn: " + book1_WorldWarZ.getIsbn());
        Loan loan2_WorldWarZ = client.attemptLoan(book1_WorldWarZ.getIsbn());
        logger.info("2nd loan approved? " + loan2_WorldWarZ.isApproved());
        assertFalse(loan2_WorldWarZ.isApproved());
        // return 1st loan
        logger.info("Returning 1st loan for isbn: " + loan1_WorldWarZ.getBook().getIsbn());
        boolean return1_ack = client.returnLoan(loan1_WorldWarZ);
        logger.info("1st loan return acknowledged? " + return1_ack);
        assertTrue(return1_ack);
        // try 2nd loan again; this time it should work
        logger.info("Re-attempting 2nd loan for isbn: " + book1_WorldWarZ.getIsbn());
        loan2_WorldWarZ = client.attemptLoan(book1_WorldWarZ.getIsbn());
        logger.info("Re-attempt of 2nd loan approved? " + loan2_WorldWarZ.isApproved());
        assertTrue(loan2_WorldWarZ.isApproved());
        // get 2nd suggestion, and since 1st book not available (again), 2nd match will return
        Suggestion suggestion2_TheZombieSurvivalGuide = client.getSuggestion("Zombie");
        Book book2_TheZombieSurvivalGuide = suggestion2_TheZombieSurvivalGuide.getBook();
        logger.info("Received suggestion for book: " + book2_TheZombieSurvivalGuide.getTitle() + " (isbn: " + book2_TheZombieSurvivalGuide.getIsbn() + ")");
        assertEquals("The Zombie Survival Guide", book2_TheZombieSurvivalGuide.getTitle());
        // take out 3rd loan
        logger.info("Attempting 3rd loan for isbn: " + book2_TheZombieSurvivalGuide.getIsbn());
        Loan loan3_TheZombieSurvivalGuide = client.attemptLoan(book2_TheZombieSurvivalGuide.getIsbn());
        logger.info("3rd loan approved? " + loan3_TheZombieSurvivalGuide.isApproved());
        assertTrue(loan3_TheZombieSurvivalGuide.isApproved());
        // return 2nd loan
        logger.info("Returning 2nd loan for isbn: " + loan2_WorldWarZ.getBook().getIsbn());
        boolean return2_ack = client.returnLoan(loan2_WorldWarZ);
        logger.info("2nd loan return acknowledged? " + return2_ack);
        assertTrue(return2_ack);
        // return 3rd loan
        logger.info("Returning 3rd loan for isbn: " + loan3_TheZombieSurvivalGuide.getBook().getIsbn());
        boolean return3_ack = client.returnLoan(loan3_TheZombieSurvivalGuide);
        logger.info("3rd loan return acknowledged? " + return3_ack);
        assertTrue(return3_ack);
    }

}
