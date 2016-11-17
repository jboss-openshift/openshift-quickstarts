##Overview

This directory contains details specific to using this quickstart with a Derby database.  It contains the [customer-schema.sql](./customer-schema.sql) file which includes the DDL and some seed data, along with this readme.

##Initializing the Database

No extra steps are required to initialize the database as it is injected into the image during the s2i build (from [../app/data/databases/derby](../app/data/databases/derby)).

##Running the QuickStart

The Derby quickstart requires the Derby JDBC driver to be installed in the JDV image.  This is accomplished by creating a image that contains the driver, as well as an installation script, which adds the driver configuration to the JDV configuration.  An example of a Derby driver image can be found [here](../../derby-driver-image).

The `datavirt63-extensions-support-s2i` template provides support for building the driver extension and integrating it into the JDV s2i process.  This template should be instantiated with the following parameter settings:

* `SOURCE_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
* `SOURCE_REPOSITORY_REF`: master
* `CONTEXT_DIR`: datavirt/dynamicvdb-datafederation/app
* `EXTENSIONS_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
* `EXTENSIONS_REPOSITORY_REF`: master
* `EXTENSIONS_DIR`: datavirt/derby-driver-image

Remember to note the Teiid username and password (both randomly generated), which should be displayed after processing the template.  Optionally, you can specify your own values:

* `TEIID_USERNAME`: teiidUser
* `TEIID_PASSWORD`: sup3rSecret!

> Note: the password must adhere to the password strength rules or deployment will fail.

Lastly, you must specify `derby` for the `QS_DB_TYPE` variable on the deployment configuration for the application, e.g.:

```
$ oc env dc/datavirt-app QS_DB_TYPE=derby
```
