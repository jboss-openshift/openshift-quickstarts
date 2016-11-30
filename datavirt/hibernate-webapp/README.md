This project is derived from the [hibernate-on-top-of-teiid](https://github.com/teiid/teiid-quickstarts/tree/8.12/hibernate-on-top-of-teiid) quickstart.  It has been modified in the following ways:

* The datasource is defined externally, using the properties in [datasources.env](./datasources.env).
* The pom.xml has been modified to no longer reference a parent pom.
* The vdb has been moved to [hibernate-portfolio-vdb.xml](../dynamicvdb-datafederation/derby/vdb/hibernate-portfolio-vdb.xml)
* EAP deployment.* dependencies are no longer specified in MANIFEST.MF.
* The war file is now named: ROOT.war

This application now connects to a remote VDB.

Steps to install:

1. Follow the instructions for deploying the (dynamicvdb-datafederation/derby)[../dynamicvdb-datafederation/derby) quickstart on OpenShift.
2. Use the eap64-third-party-db-s2i template to build and deploy the hibernate-app in this directory.
3. Create a secret, which defines the configuration details for the datasource required by this application:

```
$ oc secrets new eap-app-config datasources.env
```

Once the pod is up and running, navigate to the URL specified in the route.
