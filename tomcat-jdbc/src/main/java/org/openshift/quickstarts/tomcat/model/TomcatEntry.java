package org.openshift.quickstarts.tomcat.model;

import java.io.Serializable;

/**
 *
 */
public class TomcatEntry {

    private Serializable id;

    private String summary;

    private String description;

    public TomcatEntry() {
    }

    public TomcatEntry(String summary, String description) {
        this(null, summary, description);
    }

    public TomcatEntry(Serializable id, String summary, String description) {
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
}
