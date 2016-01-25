package org.openshift.quickstarts.todolist.model;

import java.io.Serializable;

/**
 *
 */
public class TodoEntry {

    private Serializable id;

    private String summary;

    private String description;

    public TodoEntry() {
    }

    public TodoEntry(String summary, String description) {
        this(null, summary, description);
    }

    public TodoEntry(Serializable id, String summary, String description) {
        this.id = id;
        this.summary = summary;
        this.description = description;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TodoEntry{" +
                "id=" + id +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
