package org.openshift.quickstarts.processserver.library;

import java.util.concurrent.Callable;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

public abstract class LibraryTransaction<T> implements Callable<T> {

    private final EntityManager em;

    LibraryTransaction(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }

    LibraryTransaction(EntityManager em) {
        this.em = em;
    }

    protected EntityManager em() {
        return em;
    }

    public final T transact() {
        T object = null;
        int status = Status.STATUS_UNKNOWN;
        UserTransaction ut = null;
        try {
            ut = (UserTransaction)new InitialContext().lookup("java:jboss/UserTransaction");
            if (ut != null) {
                status = ut.getStatus();
            }
        } catch (Exception e) {}
        boolean emt = (status == Status.STATUS_UNKNOWN);
        boolean utt = (status == Status.STATUS_NO_TRANSACTION);
        if (emt) {
            em.getTransaction().begin();
        } else {
            if (utt) {
                try {
                    ut.begin();
                } catch (Exception ute) {
                    throw new RuntimeException(ute);
                }
            }
            em.joinTransaction();
        }
        boolean success = false;
        try {
            object = call();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (emt) {
                em.getTransaction().rollback();
            } else if (utt) {
                try {
                    ut.rollback();
                } catch (Exception ute) {
                    throw new RuntimeException(ute);
                }
            } else {
                try {
                    ut.setRollbackOnly();
                } catch (Exception ute) {
                    throw new RuntimeException(ute);
                }
            }
        }
        if (success) {
            if (emt) {
                em.getTransaction().commit();
            } else if (utt) {
                try {
                    ut.commit();
                } catch (Exception ute) {
                    throw new RuntimeException(ute);
                }
            }
        }
        return object;
    }

    public abstract T call() throws Exception;

}
