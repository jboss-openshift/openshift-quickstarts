## Overview

This directory contains details specific to using this quickstart with a Derby database.  It contains the [customer-schema.sql](./customer-schema.sql) file which includes the DDL and some seed data, along with this readme.  The customer-schema.sql is provided for informational purposes or for your use if you would like to setup your own database instance that contains the same data.

## Initializing the Database

No extra steps are required to initialize the database as it is injected into the image during the s2i build (from [../app/data/databases/derby](../app/data/databases/derby)).

## Running the QuickStart

1. Data Source Configuration

The Derby quickstart requires the Derby JDBC driver to be installed in the JDV image.  This is accomplished by creating a image that contains the driver, as well as an installation script, which adds the driver configuration to the JDV configuration.  An example of a Derby driver image can be found [here](../../derby-driver-image).


2. Create the app-secret

`datavirt-app-config` secret is defined and linked to the service account when that file is installed (e.g. `oc create -f secret_file.yaml`).
    *  `oc create -f openshift-quickstarts/datavirt64/dynamicvdb-datafederation/app/resources/secrets/datavirt-app-secret.yaml`
    
    
3.  Instantiate the template

The `datavirt64-extensions-support-s2i` template provides support for building the driver extension and integrating it into the JDV s2i process.  This template should be instantiated with the following parameter settings:

* `SOURCE_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
* `SOURCE_REPOSITORY_REF`: master
* `CONTEXT_DIR`: datavirt64/dynamicvdb-datafederation/app
* `EXTENSIONS_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
* `EXTENSIONS_REPOSITORY_REF`: master
* `EXTENSIONS_DIR`: datavirt64/derby-driver-image

Remember to note the Teiid username and password (both randomly generated), which should be displayed after processing the template.  Optionally, you can specify your own values:

* `TEIID_USERNAME`: teiidUser
* `TEIID_PASSWORD`: sup3rSecret!

> Note: the password must adhere to the password strength rules or deployment will fail.

4.  Set the database type. 

    Add `QS_DB_TYPE` to the `datavirt-app` deployment configuration, e.g.:

    ```
    $ oc env dc/datavirt-app QS_DB_TYPE=derby
    ```
