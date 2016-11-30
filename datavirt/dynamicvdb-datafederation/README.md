##Overview

This only differs from the stock Teiid _dynamicvdb-datafederation_ quickstart in the following ways:

* The directory structure has changed.
* The [portfolio-vdb.xml](./app/portfolio-vdb.xml) has been modified to work with h2, Derby, MySQL and PostgreSQL.  The modifications include:
    * adding translator definitions for the above listed database types
    * setting `importer.widenUnsignedTypes=false` to ensure MySQL integer types are consistent with what is expected in the other models.
    * adding a reference to an environment variable, `QS_DB_TYPE`, which is used to specify the translator  used by the _Accounts_ source
* It includes the [hibernate-portfolio-vdb.xml](./app/hibernate-portfolio-vdb.xml) file from the _hibernate-on-top-of-teiid_ quickstart.

###Directory Structure

The directory structure is organized as follows:

* [./app/](./app) - everything that needs to be deployed to the server, which includes:
    * `*-vdb.xml` - the VDB files
    * [data/databases/h2/](./app/data/databases/h2) - the h2 database instance
    * [data/databases/derby/](./app/data/databases/derby) - the Derby database instance
    * [data/teiidfiles/data/](./app/data/teiidfiles/data) - the file data for the `market-data` resource
    * [data/teiidfiles/excelFiles/](./app/data/teiidfiles/excelFiles) - the file data for the `excel-file` resource
* [./derby/](./derby) - Derby specific DDL and instructions
* [./h2/](./h2) - h2 specific DDL and instructions
* [./mysql/](./mysql) - MySQL specific DDL and instructions
* [./postgresql/](./postgresql) - PostgreSQL specific DDL and instructions
* [./datasources.env](./datasources.env) provides configuration information for the datasource and resource adapters required by this quickstart

###Datasource Configuration

The datasources are configured using environment variables, which are defined through the [datasources.env](./datasources.env) file.
* datasources: (`DATASOURCES` variable)
    * `ACCOUNTS_DERBY` is the datasource configuration for use with Derby
    * `ACCOUNTS_H2` is the datasource configuration for use with h2
    * `ACCOUNTS_MYSQL5` is the datasource configuration for use with MySQL
    * `ACCOUNTS_POSTGRESQL` is the datasource configuration for use with PostgreSQL
* resource adapters: (`RESOURCE_ADAPTERS` variable)
    * `MARKETDATA` is the resource adapter configuration for the market data files
    * `EXCEL` is the resource adapter configuration for the Excel files

The `QS_DB_TYPE` variable is used to define which datasource configuration is used and can be one of (all lower case): `derby`, `h2`, `mysql5` or `postgresql`.  By default, this is set to `h2`, but may be changed by editing the deployment configuration for the application.  This must be defined as part of the deployment configuration within OpenShift to have any affect on the VDB.  Setting it in [datasources.env](./datasources.env) will affect the datasource that gets created, but the VDB will use the translator specified by the setting on the deployment configuration.  The [portfolio-vdb.xml](./app/portfolio-vdb.xml) file uses `${env.QS_DB_TYPE:h2}` to select the translator that should be used with the _Accounts_ model, and defaults to `h2`.

The configuration details are passed to the pod through a secret.

##Deploying the Quickstart

This quickstart can be deployed as follows:

1.  Instantiate one of the following templates:

    * `datavirt63-basic-s2i`
    * `datavirt63-secured-s2i` (includes configuration for https and jdbc (over SNI) routes)

    Specifying parameters as follows:

    * `SOURCE_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
    * `SOURCE_REPOSITORY_REF`: master
    * `CONTEXT_DIR`: datavirt/dynamicvdb-datafederation/app

    Remember to note the Teiid username and password (both randomly generated), which should be displayed after processing the template.  Optionally, you can specify your own values:

    * `TEIID_USERNAME`: teiidUser
    * `TEIID_PASSWORD`: sup3rSecret!

    > Note: the password must adhere to the password strength rules or deployment will fail.

2.  Create the secret for the datasource configuration.

    ```
    $ oc secrets new datavirt-app-config datasources.env
    ```

3.  Link the secret to the service account used by the application.

    ```
    $ oc secrets link datavirt-service-account datavirt-app-config
    ```

4.  Experiment with other the database types.

    Add `QS_DB_TYPE` to the `datavirt-app` deployment configuration, e.g.:

    ```
    $ oc env dc/datavirt-app QS_DB_TYPE=mysql5
    ```

    Set this to one of the values listed in the _Datasource Configuration_ section.  Follow the type specific instructions for initializing the database (e.g. seeding the data).  After modifying the deployment configuration, the current deployment should be replaced with a new deployment containing your changes.

> Note: Steps two and three are unnecessary if using the [datavirt-app-secret](https://github.com/jboss-openshift/application-templates/blob/master/secrets/datavirt-app-secret.yaml), as `datavirt-app-config` secret is defined and linked to the service account when that file is installed (e.g. `oc create -f application-templates/secrets/datavirt-app-secret.yaml`).

