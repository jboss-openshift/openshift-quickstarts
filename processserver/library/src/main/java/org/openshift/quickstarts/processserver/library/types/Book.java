package org.openshift.quickstarts.processserver.library.types;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "book", propOrder = {"id", "isbn", "title", "synopsis", "available"})
@Entity
@Table(name="book")
public class Book implements Serializable {

    @Id
    @Column(name="book_id")
    @GeneratedValue
    protected long id;
    @Column
    protected String isbn;
    @Column
    protected String title;
    @Column
    protected String synopsis;
    @Column
    protected boolean available;

    public Book() {}

    public Book(String isbn, String title, String synopsis) {
        setIsbn(isbn);
        setTitle(title);
        setSynopsis(synopsis);
        setAvailable(true);
    }

    /**
     * Gets the value of the id property.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the isbn property.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the value of the isbn property.
     */
    public void setIsbn(String value) {
        this.isbn = value;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the synopsis property.
     */
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * Sets the value of the synopsis property.
     */
    public void setSynopsis(String value) {
        this.synopsis = value;
    }

    /**
     * Gets the value of the available property.
     * 
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the value of the available property.
     * 
     */
    public void setAvailable(boolean value) {
        this.available = value;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", isbn=" + isbn + ", title=" + title + ", synopsis=" + synopsis + ", available="
 + available + "]";
    }

}
