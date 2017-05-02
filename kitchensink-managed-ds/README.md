kitchensink: Assortment of technologies including Arquillian
========================
Author: Pete Muir  
Level: Intermediate  
Technologies: CDI, JSF, JPA, EJB, JAX-RS, BV  
Summary: The `kitchensink` quickstart demonstrates a Java EE 6 web-enabled database application using JSF, CDI, EJB, JPA and Bean Validation.  
Target Product: JBoss EAP  
Source: <https://github.com/jboss-developer/jboss-eap-quickstarts/>  

What is it?
-----------

The `kitchensink` quickstart is a deployable Maven 3 project designed to help you get your foot in the door developing with Java EE 6 on Red Hat JBoss Enterprise Application Platform. 

It demonstrates how to create a compliant Java EE 6 application using JSF 2.1, CDI 1.0, JAX-RS, EJB 3.1, JPA 2.0 and Bean Validation 1.0. It also includes a persistence unit and some sample persistence and transaction code to introduce you to database access in enterprise Java. 

_Note: This quickstart uses the H2 database included with Red Hat JBoss Enterprise Application Platform 6. It is a lightweight, relational example datasource that is used for examples only. It is not robust or scalable, is not supported, and should NOT be used in a production environment!_

_Note: This quickstart uses a `*-ds.xml` datasource configuration file for convenience and ease of database configuration. These files are deprecated in JBoss EAP 6.4 and should not be used in a production environment. Instead, you should configure the datasource using the Management CLI or Management Console. Datasource configuration is documented in the [Administration and Configuration Guide](https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/) for Red Hat JBoss Enterprise Application Platform._


Creating the Quickstart Application
-----------------------------------

For this quickstart you can use the following Application Template:

 - jboss-eap64-openshift


Specify the following for the source configuration:

* `SOURCE_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
* `SOURCE_REPOSITORY_REF`: master
* `CONTEXT_DIR`: kitchensink-managed-ds

Access the application 
---------------------

The application will be running at the following URL: <http://${APPLICATION_NAME}-${NAMESPACE}.${SERVICE_SUFFIX_NAME}/>.
Example: <http://eap-app-fspolti.router.default.svc.cluster.local/>


Server Log: Expected warnings and errors
-----------------------------------

_Note:_ You will see the following warnings in the server log. You can ignore these warnings.

    JBAS010489: -ds.xml file deployments are deprecated. Support may be removed in a future version.

    HHH000431: Unable to determine H2 database version, certain features may not work


Undeploy the Archive
--------------------

To undeploy the application you can just remove the project or scale the pods down.