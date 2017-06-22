package org.jboss.as.quickstarts.datagrid.carmart;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;


/**
 * Creates a deployment from a build Web Archive using ShrinkWrap ZipImporter
 * 
 * @author tsykora@redhat.com
 * 
 */
public class Deployments {
    //properties defined in pom.xml
    private static final String ARCHIVE_NAME = System.getProperty("carmart.war.file");
    private static final String BUILD_DIRECTORY = System.getProperty("carmart.war.directory");

    public static WebArchive createDeployment() {
        System.out.println(BUILD_DIRECTORY + '/' + ARCHIVE_NAME);
        return ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME).importFrom(new File(BUILD_DIRECTORY + '/' + ARCHIVE_NAME))
                .as(WebArchive.class);
    }
}
