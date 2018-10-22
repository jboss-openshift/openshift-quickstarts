jta-crash-rec: Example of JTA Crash Recovery
=============================
Author: Mike Musgrove, Ondrej Chaloupka
Level: Advanced  
Technologies: JTA, Crash Recovery  
Summary: The `jta-crash-rec` quickstart uses JTA and Byteman to show how to code distributed (XA) transactions in order to preserve ACID properties on server crash.  
Target Product: JBoss EAP  
Source: <https://github.com/jboss-developer/jboss-eap-quickstarts/>  

What is it?
-----------

The `jta-crash-rec` quickstart demonstrates how to code distributed or XA (eXtended Architecture) transactions
so that the ACID properties are preserved across participating resources deployed to Red Hat JBoss Enterprise Application Platform
running in OpenShift environment.
An XA transaction is one in which multiple resources, such as MDBs and databases, participate within the same transaction.
It ensures all operations are performed as a single entity of work.
The XA Transactions are defined by the Java EE JTA specification JSR-907.

This quickstart shows how to atomically update multiple resources within one transaction.
It updates a relational database table using JPA and sends a message using JMS.
You can observe the  paired updates to two different resources united under one XA transaction.

The relational database table in this example contains two columns that represent a "key" / "value" pair.
The application presents an HTML form containing two input text boxes and allows you to create, update, delete or list these pairs.
When you add or update a "key" / "value" pair, the quickstart starts a transaction, updates the database table,
produces a JMS message containing the update, and then commits the transaction. If all goes well,
eventually the MDB consumer gets the message and generates a database update,
setting the "value" corresponding to the "key" to something that indicates it was changed by the message consumer.

For verification that transaction crash recovery works you halt the JBoss EAP server in the middle of an XA transaction
after the database modification has been committed, but before the JMS producer is committed.
There is prepared a [Byteman](http://byteman.jboss.org) script in the quickstart which arrages halting the transaction process.
Then manual steps need to be process. The application pod is scaled to 0 instances
and then migration pod can start working to finish unfinished XA transaction to drive it to commit.
When application pod is scaled up to 1 instance again transaction is already recovered
and the data is updated and you can verify that everything is in a consistent state.

JBoss EAP ships with H2, an in-memory database written in Java. In this example, we use H2 for the database.
Although H2 XA support is not recommended for production systems, the example does illustrate the general steps you need to perform for any datasource vendor.
This example provides its own H2 XA datasource configuration. It is defined in the `jta-crash-rec-ds.xml` file in the WEB-INF folder of the WAR archive.

<aside class="warning">
This quickstart uses the H2 database included with Red Hat JBoss Enterprise Application Platform.
It is a lightweight, relational example datasource that is used for examples only.
It is not robust or scalable, is not supported, and should *NOT* be used in a production environment!
In your application use proper cloud native storage service to store your application data.
</aside>

<aside class="note">
This quickstart uses a `*-ds.xml` datasource configuration file for convenience and ease of database configuration.
These files are deprecated in JBoss EAP and should not be used in a production environment.
Instead, you should configure the datasource using the Management CLI or Management Console.
Datasource configuration is documented in the [Configuration Guide](https://access.redhat.com/documentation/en/jboss-enterprise-application-platform/)
for Red Hat JBoss Enterprise Application Platform.
</aside>


System requirements
-------------------

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 6.x or 7.x or later.

You need to have running the [OpenShift platform](https://www.okd.io) where the quickstart will be deployed to.
If you expect to experiment on the local computer check the [Minishift project](https://github.com/minishift/minishift).

<aside class="note">
This quickstart is based on the [JBoss Enterprise Application Platform quickstarts](https://github.com/jboss-developer/jboss-eap-quickstarts).
If you want to run the quickstart as a standalone application check the description there.
<aside>


Running quickstart for the JBoss EAP 6.x and JBoss EAP7.x
--------------------------------------------------------

You need to
[import latest JBoss EAP for OpenShift Image streams](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.1/html/red_hat_jboss_enterprise_application_platform_for_openshift/build_run_java_app_s2i#import_imagestreams_templates)
to your OpenShift instance.
The image stream serves as source for docker images used
in the quickstart to start the application pods.

The quickstart is deployed by applying the [template](https://docs.okd.io/latest/dev_guide/templates.html)
which is recipe of the docker images with additional settings
to be deployed at the OpenShift.
This quickstarts uses the [JBoss EAP docker image](https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift).

When the JBoss EAP imaget streams are deployed you will just import the template and
create the application.

For JBoss EAP 7.1.x, see https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/7.1/html/red_hat_jboss_enterprise_application_platform_for_openshift/advanced_tutorials#transaction_recovery_demo

For JBoss EAP CD, see 'Getting Started with JBoss EAP for OpenShift Container Platform' section of the documetation at: https://access.redhat.com/documentation/en-us/jboss_enterprise_application_platform_continuous_delivery/

For JBoss EAP 7.1.x use the template `eap71-qs-test-s2i.json`.
For JBoss EAP 6.4.x use the template `eap64-qs-test-s2i.json`.

### JBoss EAP 6.4.x

```bash
oc create -f eap64-qs-test-s2i.json
oc new-app --template=eap64-qs-test-s2i
```

### JBoss EAP 7.1.x

```bash
oc create -f eap71-qs-test-s2i.json
oc new-app --template=eap71-qs-test-s2i.json
```

### Access the application 

The application will be running at the following URL: <http://[eap-app service IP address]/jboss-jta-crash-rec/XA>. 
You can obtain the `eap-app` service IP address by checking routes

```bash
oc get route
```


### Test the application

1. When you access the application, you will find a web page containing two html input boxes for adding "key" / "value" pairs to a database.
   Instructions for using the application are shown at the top of the application web page.

2. When you add a new "key" / "value" pair, the change is committed to the database and a JMS message sent.
  The message consumer then updates the newly inserted row by appending the text *"updated via JMS"* to the value.
  Since the consumer updates the row asynchronously, you may need to click _Refresh Table_ to see the text added to the "key" / "value" pair you previously entered.

3. When an _XA transaction_ is committed, the application server completes the transaction in two phases.
    * In phase 1 each of the resources, in this example the database and the JMS message producer, are asked to prepare to commit any changes made during the transaction. 
    * If all resources vote to commit then the application server starts phase 2 in which it tells each resource to commit those changes. 
    * The added complexity is to cope with failures, especially failures that occur during phase 2.
      Some failure modes require cooperation between the application server and the resources in order to guarantee that any pending changes are recovered. 

4. To demonstrate XA recovery, the application template enables Byteman tool to halt transaction processing while _phase 2_ is running as follows:
    * If you enter a new "key" / "value" pair for the second time the Byteman rule is executed and the JVM halts.
    * Scale down the `eap-app` to zero instances. That way you give chance for the background migration pod
      to start working and finish transactions.
      ```
      oc scale dc eap-app --replicas=0
      ```
    * You can check the process of the migration pod by looking at the application server log.
      ```
      oc logs dc/eap-app-migration -f
      ```
      (you will end the printing by pressing `CTRL+C`)
    * Scale up the `eap-app` to one instance again.
      ```
      oc scale dc eap-app --replicas=1
      ```
    * Verify by accessing the application at URL <http://[eap-app service IP address]/jboss-jta-crash-rec/XA>
      that data were written and the MDB consumer updated values with message *updated via JMS*.

_Note:_ Be aware of the fact that Byteman rules are installed even you restart the application server by scaling it up and down.


Server Log: Expected warnings and errors
-----------------------------------

_Note:_ You will see the following warnings in the application or migration server log. You can ignore these warnings. 

    WFLYJCA0091: -ds.xml file deployments are deprecated. Support may be removed in a future version.
    HHH000431: Unable to determine H2 database version, certain features may not work
    ARJUNA016037: Could not find new XAResource

