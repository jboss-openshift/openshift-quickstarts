package org.openshift.quickstarts.tomcat.service;

import org.openshift.quickstarts.tomcat.dao.JdbcTomcatDAO;
import org.openshift.quickstarts.tomcat.dao.TomcatDAO;
import org.openshift.quickstarts.tomcat.model.TomcatEntry;

import java.util.List;

/**
 *
 */
public class TomcatService {

    private TomcatDAO dao = new JdbcTomcatDAO();

    public void addEntry(TomcatEntry entry) {
        dao.save(entry);
    }

    public List<TomcatEntry> getAllEntries() {
        return dao.list();
    }
}
