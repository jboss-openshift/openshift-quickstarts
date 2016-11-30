package org.openshift.quickstarts.processserver.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieContext;
import org.openshift.quickstarts.processserver.library.types.Book;
import org.openshift.quickstarts.processserver.library.types.Loan;

public final class Library {

    private static final Object[][] INIT = new Object[][] {
        new Object[] {"978-0-307-35193-7", "World War Z", "An Oral History of the Zombie War", 1},
        new Object[] {"978-0-7360-9829-8", "Successful Sports Officiating", "American Sports Education Program.", 3},
        new Object[] {"978-0-7434-8773-3", "The Time Machine", "H. G. Wells' story of a time traveler.", 8},
        new Object[] {"978-1-101-15402-1", "The Island of Dr. Moreau", "H. G. Wells' story of what may be most relevant to modern ethical dimemmas.", 5},
        new Object[] {"978-1-4000-5-80-2", "The Zombie Survival Guide", "Complete Protection from the Living Dead", 6},
        new Object[] {"978-1-448-14153-1", "Doctor Who: Summer Falls", "Story of Amelia Williams.", 2},
        new Object[] {"978-1-4516-7486-6", "Tesla, Man Out of Time", "Explores the brilliant and prescient mind of one of the twentieth century's greatest scientists and inventors, Nikola Tesla.", 4},
        new Object[] {"978-1-59474-449-5", "Pride and Prejudice and Zombies", "The Classic Regency Romance -- Now with Ultraviolent Zombie Mayhem!", 7}
    };

    private static final Library INSTANCE = new Library();

    public static final Library library() {
        return INSTANCE;
    }

    private EntityManagerFactory emf = null;

    private static final class Init extends LibraryTransaction<Object> {
        private Init(EntityManagerFactory emf) {
            super(emf);
        }
        @Override
        public Object call() throws Exception {
            Query query = em().createQuery("select count(b) from Book b");
            Long result = (Long)query.getSingleResult();
            if (result.intValue() == 0) {
                for (Object[] b : INIT) {
                    String isbn = (String)b[0];
                    String title = (String)b[1];
                    String synopsis = (String)b[2];
                    Integer quantity = (Integer)b[3];
                    for (int i=0; i < quantity; i++) {
                        Book book = new Book(isbn, title, synopsis);
                        em().persist(book);
                    }
                }
            }
            return null;
        }
    }
    public synchronized Library init(KieContext kcontext) {
        if (emf == null) {
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            try {
                // https://issues.jboss.org/browse/DROOLS-1108
                ClassLoader cl = ((InternalKnowledgeBase)kcontext.getKieRuntime().getKieBase()).getRootClassLoader();
                Thread.currentThread().setContextClassLoader(cl);
                emf = Persistence.createEntityManagerFactory("library");
            } finally {
                Thread.currentThread().setContextClassLoader(tccl);
            }
            new Init(emf).transact();
        }
        return this;
    }

    private static final class GetFirstAvailableBooks extends LibraryTransaction<Collection<Book>> {
        private final String keyword;
        private GetFirstAvailableBooks(EntityManagerFactory emf, String keyword) {
            super(emf);
            this.keyword = keyword;
        }
        @Override
        public Collection<Book> call() throws Exception {
            Collection<Book> books = new ArrayList<Book>();
            Query query = em().createQuery("from Book b where b.available = true and ( b.title like :title or b.synopsis like :synopsis )");
            String kw = "%" + keyword + "%";
            query.setParameter("title", kw);
            query.setParameter("synopsis", kw);
            List<?> results = query.getResultList();
            Set<String> isbns = new HashSet<String>();
            for (Object result : results) {
                Book book = (Book)result;
                if (!isbns.contains(book.getIsbn())) {
                    isbns.add(book.getIsbn());
                    books.add(book);
                }
            }
            return books;
        }
    }
    public Collection<Book> getFirstAvailableBooks(final String keyword) {
        return new GetFirstAvailableBooks(emf, keyword).transact();
    }

    private static final class AttemptLoan extends LibraryTransaction<Loan> {
        final String isbn;
        final long loanId;
        private AttemptLoan(EntityManagerFactory emf, String isbn, long loanId) {
            super(emf);
            this.isbn = isbn;
            this.loanId = loanId;
        }
        @Override
        public Loan call() throws Exception {
            Loan loan = new Loan();
            loan.setId(loanId);
            Book book = null;
            Query query = em().createQuery("from Book b where b.available = true and b.isbn = :isbn");
            query.setParameter("isbn", isbn);
            List<?> results = query.getResultList();
            for (Object result : results) {
                book = (Book)result;
                break;
            }
            if (book != null) {
                book.setAvailable(false);
                em().merge(book);
                loan.setApproved(true);
                loan.setNotes("Happy reading! Remaining copies: " + (results.size() - 1));
                loan.setBook(book);
            } else {
                loan.setApproved(false);
                loan.setNotes("No books matching isbn: " + isbn + " are available.");
            }
            return loan;
        }
    }
    public Loan attemptLoan(String isbn, long loanId) {
        return new AttemptLoan(emf, isbn, loanId).transact();
    }

    public static final class ReturnLoan extends LibraryTransaction<Boolean> {
        private final Loan loan;
        private ReturnLoan(EntityManagerFactory emf, Loan loan) {
            super(emf);
            this.loan = loan;
        }
        @Override
        public Boolean call() throws Exception {
            boolean returned = false;
            if (loan != null) {
                Book book = loan.getBook();
                if (book != null) {
                    book = em().find(Book.class, book.getId());
                    if (book != null) {
                        book.setAvailable(true);
                        em().merge(book);
                        returned = true;
                    }
                }
            }
            return returned;
        }
    }
    public boolean returnLoan(Loan loan) {
        return new ReturnLoan(emf, loan).transact();
    }

}
