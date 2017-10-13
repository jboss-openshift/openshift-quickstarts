Running on OpenShift
====================

This quickstart is designed to run within OpenShift v3, accessing a remote Data
Grid server running in the same OpenShift project (i.e. the remote-* profiles
apply when running in OpenShift).

This quickstart requires a hotrod service named carcache-hotrod.

JBoss Data Grid Configuration
-----------------------------

This quickstart requires a JBoss Data Grid instance to be running within your OpenShift
project.  An instance can be created easily using the **datagrid65-basic** template.

In the OpenShift console, select a project and click *Add to Project*.  Select
the *datagrid65-basic* template.  Set the following parameters as specified:

* `APPLICATION_NAME`: carcache
* `INFINISPAN_CONNECTORS`: hotrod
* `CACHE_NAMES`: carcache

Press *Create*.  This will create a JBoss Data Grid instance which provides the
*carcache-hotrod* service.

Creating the Quickstart Application
-----------------------------------

The Quickstart can be run on either JBoss EAP or JBoss Web Server.

To create the application, select either the **eap64-basic-s2i**,
**jws30-tomcat7-basic-s2i**, or **jws30-tomcat8-basic-s2i** templates, depending
on the environment you want to use.  Specify the following for the source
configuration:

* `SOURCE_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
* `SOURCE_REPOSITORY_REF`: 1.2
* `CONTEXT_DIR`: datagrid/carmart

> Note, the `SOURCE_REPOSITORY_REF` is the branch used when building the
> source.  Latest source is always available in the `master` branch.

Changes from the original quickstart
------------------------------------

The only changes that have been made to this quickstart are:

* datagrid.properties:
  * datagrid.host set to the hotrod service name: carcache-hotrod
* pom.xml
  * added activation based on env variables to remote-jboss and remote-tomcat profiles
  * added openshift profile
