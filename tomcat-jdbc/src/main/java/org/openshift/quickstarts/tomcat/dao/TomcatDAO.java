package org.openshift.quickstarts.tomcat.dao;

import org.openshift.quickstarts.tomcat.model.TomcatEntry;

import java.util.List;

/**
 *
 */
public interface TomcatDAO {

    void save(TomcatEntry entry);

    List<TomcatEntry> list();
}
