carmart: Basic Infinispan example
Author: Tristan Tarrant, Martin Gencur Level: Intermediate Technologies: Infinispan, CDI Summary: Shows how to use Infinispan instead of a relational database. Target Product: JDG Product Versions: EAP 6.x, JDG 6.x Source: https://github.com/infinispan/jdg-quickstart

What is it?

CarMart is a simple web application that uses Infinispan Cache instead of a relational database.

Users can list cars, add new cars, or remove them from the CarMart. Information about each car is stored in a cache. The application also shows cache statistics like stores, hits, retrievals, and more.

The CarMart quickstart can work in two modes:

Library mode - In this mode, the application and the data grid are running in the same JVM. All libraries (JAR files) are bundled with the application and deployed to Red Hat JBoss Enterprise Application Platform. The library mode enables fastest (local) access to the entries stored on the same node as the application instance, but also enables access to data stored in remote nodes (JVMs) that comprise the embedded distributed cluster.

Client-server mode - In this mode, the Cache is stored in a managed, distributed and clusterable data grid server. Applications can remotely access the data grid server using Hot Rod, memcached or REST client APIs. This web application bundles only the HotRod client and communicates with a remote JBoss Data Grid (JDG) server. The JDG server is configured via the standalone.xml configuration file.

System requirements

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform (EAP) 6.1 or later.

Configure Maven

If you have not yet done so, you must Configure Maven before testing the quickstarts.

Start EAP

Open a command line and navigate to the root of the EAP server directory.
The following shows the command line to start the server with the web profile:

For Linux:   $JBOSS_HOME/bin/standalone.sh
For Windows: %JBOSS_HOME%\bin\standalone.bat
Build and Deploy the Application in Library Mode

NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See Build and Deploy the Quickstarts for complete instructions and additional options.

Make sure you have started EAP as described above.
Open a command line and navigate to the root directory of this quickstart.
Type this command to build and deploy the archive:

mvn clean package jboss-as:deploy
This will deploy target/jboss-carmart.war to the running instance of the server.

Access the application

The application will be running at the following URL: http://localhost:8080/jboss-carmart/

Undeploy the Archive

Make sure you have started EAP as described above.
Open a command line and navigate to the root directory of this quickstart.
When you are finished testing, type this command to undeploy the archive:

mvn jboss-as:undeploy
Debug the Application

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

    mvn dependency:sources
    mvn dependency:resolve -Dclassifier=javadoc
Test the Application in Library mode

If you want to test the application, there are simple Arquillian Selenium tests prepared. To run these tests on EAP:

Stop EAP (if you have one running)
Open a command line and navigate to the root directory of this quickstart.
Build the quickstart using:

mvn clean package
Type this command to run the tests:

mvn test -Puitests-jbossas -Das7home=/path/to/server
Build and Start the Application in Client-Server Mode (using HotRod Client)

NOTE: The application must be deployed to JBoss Enterprise Application Platform (EAP). It can not be deployed to JDG since it does not support deployment of applications.

Obtain the JDG server distribution. See the following for more information: http://www.redhat.com/products/jbossenterprisemiddleware/data-grid/

Configure the remote datagrid in the $JDG_HOME/standalone/configuration/standalone.xml file. Copy the following XML into the Infinispan subsystem before the ending tag. If you have an existing carcache element, be sure to replace it with this one.

    <local-cache name="carcache" start="EAGER" batching="false"/>
Start the JDG server on localhost using port offset:

$JDG_HOME/bin/standalone.sh -Djboss.socket.binding.port-offset=100
Start EAP into which you want to deploy your application

$JBOSS_HOME/bin/standalone.sh
The application finds the JDG server using the values in the src/main/resources/META-INF/datagrid.properties file. If you are not running the JDG server on the default host and port, you must modify this file to contain the correct values. If you need to change the JDG address:port information, edit src/main/resources/META-INF/datagrid.properties file and specify address and port of the JDG server

datagrid.host=localhost
datagrid.hotrod.port=11322
Build the application in the example's directory:

mvn clean package -Premote-jbossas
Deploy the application

mvn jboss-as:deploy -Premote-jbossas
The application will be running at the following URL: http://localhost:8080/jboss-carmart/

Undeploy the application

mvn jboss-as:undeploy -Premote-jbossas
Test the Application in Client-Server mode (using HotRod client)

Obtain and configure JDG Server (steps 1 and 2 show above)
Make sure that none of EAP or JDG Server is running
Open a command line and navigate to the root directory of this quickstart.
Build the quickstart using:

mvn clean package -Premote-jbossas
Type this command to run the tests:

mvn test -Puitests-remote -Das7home=/path/to/as/server -DjdgServer=/path/to/jdg/server