carmart: Basic Infinispan example
=================================
Author: Tristan Tarrant, Martin Gencur
Level: Intermediate
Technologies: Infinispan, CDI
Summary: Shows how to use Infinispan instead of a relational database.
Target Product: JDG
Product Versions: EAP 6.x, JDG 6.x
Source: <https://github.com/infinispan/jdg-quickstart>

What is it?
-----------

CarMart is a simple web application that uses Infinispan Cache instead of a relational database.

Users can list cars, add new cars, or remove them from the CarMart. Information about each car is stored in a cache. The application also shows cache statistics like stores, hits, retrievals, and more.

The CarMart quickstart can work in two modes: 

* _Library mode_  - In this mode, the application and the data grid are running in the same JVM. All libraries (JAR files) are bundled with the application and deployed to Red Hat JBoss Enterprise Application Platform. The library mode enables fastest (local) access to the entries stored on the same node as the application instance, but also enables access to data stored in remote nodes (JVMs) that comprise the embedded distributed cluster.

* _Client-server mode_ - In this mode, the Cache is stored in  a managed, distributed and clusterable data grid server.  Applications can remotely access the data grid server using Hot Rod, memcached or REST client APIs. This web application bundles only the HotRod client and communicates with a remote JBoss Data Grid (JDG) server. The JDG server is configured via the `standalone.xml` configuration file.


System requirements
-------------------

To run this quickstarts you need an Openshift V3 instance running with the following Application Templates available:
- datagrid-basic
- jboss-eap64-openshift

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (EAP) 6.1 or later.


Start JDG Container
---------

1. Open the Openshift Console
2. Create a new project and add an JBoss Datagrid pod
2. Change the **CACHE_NAMES** to: **distributed-cache,carcache**


Build and Deploy the Application in Library Mode
------------------------------------------------

On OpenShift only the Client-Server more is available.


Build and Start the Application in Client-Server Mode (using HotRod Client)
---------------------------------------------------------------------------

NOTE: The application must be deployed to JBoss Enterprise Application Platform (EAP). It can not be deployed to JDG since it does not support deployment of applications.

1. Add a JBoss EAP instance to the same project than JDG (eap64-basic)

2. Fill the fields:
    - APPLICATION_NAME - carmart
    - SOURCE_REPOSITORY_URL - https://github.com/jboss-openshift/openshift-quickstarts
    - SOURCE_REPOSITORY_REF - master
    - CONTEXT_DIR - datagrid/carmart

3. Click in **CREATE** button, it will start to build the application

4. The application will be running at the following URL: <http://${APPLICATION_NAME}-${ROUTER_SUFFIX}/>
     - Example: <http://carcache-new-fspolti.router.default.svc.cluster.local/>

9. Undeploy the application

        You can scale down the pod or remove it by deleting the project or the all services.