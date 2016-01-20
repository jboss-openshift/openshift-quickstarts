package org.openshift.quickstarts.procexecserver.library;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.openshift.quickstarts.procexecserver.library.types.Book;
import org.openshift.quickstarts.procexecserver.library.types.Loan;
import org.openshift.quickstarts.procexecserver.library.types.ObjectFactory;

@SuppressWarnings("serial")
public final class Library implements Serializable {

    private static final Library INSTANCE = new Library();
    private static final Integer ZERO = Integer.valueOf(0);

    private final Map<String, Book> isbns_to_books = new TreeMap<String, Book>();
    private final Map<String, Integer> isbns_to_quantities = Collections.synchronizedMap(new TreeMap<String, Integer>());
    private final Object librarian = new Object();

    private Library() {
        addBook("978-0-307-35193-7", "World War Z", "An Oral History of the Zombie War", 1);
        addBook("978-0-7360-9829-8", "Successful Sports Officiating", "American Sports Education Program.", 3);
        addBook("978-0-7434-8773-3", "The Time Machine", "H. G. Wells' story of a time traveler.", 8);
        addBook("978-1-101-15402-1", "The Island of Dr. Moreau", "H. G. Wells' story of what may be most relevant to modern ethical dimemmas.", 5);
        addBook("978-1-4000-5-80-2", "The Zombie Survival Guide", "Complete Protection from the Living Dead", 6);
        addBook("978-1-448-14153-1", "Doctor Who: Summer Falls", "Story of Amelia Williams.", 2);
        addBook("978-1-4516-7486-6", "Tesla, Man Out of Time", "Explores the brilliant and prescient mind of one of the twentieth century's greatest scientists and inventors, Nikola Tesla.", 4);
        addBook("978-1-59474-449-5", "Pride and Prejudice and Zombies", "The Classic Regency Romance -- Now with Ultraviolent Zombie Mayhem!", 7);
    }

    private void addBook(String isbn, String title, String synopsis, int quantity) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setSynopsis(synopsis);
        isbns_to_books.put(isbn, book);
        isbns_to_quantities.put(isbn, quantity);
    }

    public Collection<Book> getAllBooks() {
        return isbns_to_books.values();
    }

    public Collection<Book> getAvailableBooks() {
        synchronized (librarian) {
            Collection<Book> books = new LinkedList<Book>();
            for (Entry<String, Integer> entry : isbns_to_quantities.entrySet()) {
                if (entry.getValue() > 0) {
                    books.add(getBook(entry.getKey()));
                }
            }
            return books;
        }
    }

    public Book getBook(String isbn) {
        return isbns_to_books.get(isbn);
    }

    public Integer getQuantity(String isbn) {
        Integer quantity = null;
        if (isbn != null) {
            synchronized (librarian) {
                quantity = isbns_to_quantities.get(isbn);
            }
        }
        return quantity != null ? quantity : ZERO;
    }

    public Integer getQuantity(Book book) {
        return book != null ? getQuantity(book.getIsbn()) : ZERO;
    }

    public Loan attemptLoan(String isbn, String loanId) {
        Loan loan = new Loan();
        loan.setId(loanId);
        Book book = getBook(isbn);
        if (book != null) {
            synchronized (librarian) {
                int quantity = getQuantity(book);
                if (quantity > 0) {
                    quantity--;
                    isbns_to_quantities.put(isbn, quantity);
                    loan.setApproved(true);
                    loan.setNotes("Happy reading! Remaining copies: " + quantity);
                    loan.setBook(book);
                } else {
                    loan.setApproved(false);
                    loan.setNotes("Book has no copies available.");
                }
            }
        } else {
            loan.setApproved(false);
            loan.setNotes("No book matching isbn: " + isbn);
        }
        return loan;
    }

    public boolean returnLoan(Loan loan) {
        if (loan != null) {
            Book book = loan.getBook();
            if (book != null) {
                String isbn = book.getIsbn();
                if (isbn != null) {
                    synchronized (librarian) {
                        Integer quantity = isbns_to_quantities.get(isbn);
                        if (quantity != null) {
                            quantity = new Integer(quantity.intValue() + 1);
                            isbns_to_quantities.put(isbn, quantity);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean detailed) {
        StringWriter sw = new StringWriter();
        synchronized (librarian) {
            try {
                if (detailed) {
                    JAXBContext ctx = JAXBContext.newInstance("org.openshift.quickstarts.procexecserver.library.types");
                    Marshaller m = ctx.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                    ObjectFactory of = new ObjectFactory();
                    for (Book book : isbns_to_books.values()) {
                        int quantity = isbns_to_quantities.get(book.getIsbn());
                        sw.write("\nBook (quantity=" + quantity + ")\n");
                        m.marshal(of.createBook(book), sw);
                        sw.write('\n');
                    }
                } else {
                    for (Book book : isbns_to_books.values()) {
                        int quantity = isbns_to_quantities.get(book.getIsbn());
                        sw.write(book.getTitle() + " (" + quantity + ")\n");
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return sw.toString().trim();
    }

    public static final Library library() {
        return INSTANCE;
    }

    public static void main(String... args) {
        System.out.println(library().toString(false));
        System.out.println();
        System.out.println(library().toString(true));
    }

}
