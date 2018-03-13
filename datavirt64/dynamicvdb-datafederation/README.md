## Overview

This only differs from the stock Teiid _dynamicvdb-datafederation_ quickstart in the following ways:

* The directory structure has changed.
* The [portfolio-vdb.xml](./app/portfolio-vdb.xml) has been modified to work with h2, Derby, MySQL and PostgreSQL.  The modifications include:
    * adding translator definitions for the above listed database types
    * setting `importer.widenUnsignedTypes=false` to ensure MySQL integer types are consistent with what is expected in the other models.
    * adding a reference to an environment variable, `QS_DB_TYPE`, which is used to specify the translator  used by the _Accounts_ source
* It includes the [hibernate-portfolio-vdb.xml](./app/hibernate-portfolio-vdb.xml) file from the _hibernate-on-top-of-teiid_ quickstart.
* It includes a modified version of the jdg-remote-cache-materialization quickstart, which has been modified as follows:
    * The jar file is output directly into the modules directory hierarchy.
    * The kits folder has been removed.  The modules directory hierarchy is now created by maven automatically.
    * The modules folder is created in the parent directory, so it gets copied automatically as part of the s2i process
    * The jdg-remote-cache-mat-vdb.xml file is copied into the parent directory, so it gets deployed automatically as part of the s2i process.

### Directory Structure

The directory structure is organized as follows:

* [./app/](./app) - everything that needs to be deployed to the server, which includes:
    * `*-vdb.xml` - the VDB files that are based on the dynamicvdb-datafederation quickstart.
    * [data/databases/h2/](./app/data/databases/h2) - the h2 database instance
    * [data/databases/derby/](./app/data/databases/derby) - the Derby database instance
    * [data/teiidfiles/data/](./app/data/teiidfiles/data) - the file data for the `market-data` resource
    * [data/teiidfiles/excelFiles/](./app/data/teiidfiles/excelFiles) - the file data for the `excel-file` resource
    * [datagrid-materialization/](./app/datagrid-materialization/src/vdb/) - the VDB and its related .dodeploy file, that define the JDG source tables and the view to be materialized. These files are based on the jdg7.1-remote-cache-materialization quickstart.
* [./app/](./resources/secrets) - the datavirt-app-secret.yaml secrets file used for testing
* [./derby/](./derby) - Derby specific DDL and instructions
* [./h2/](./h2) - h2 specific DDL and instructions
* [./mysql/](./mysql) - MySQL specific DDL and instructions
* [./postgresql/](./postgresql) - PostgreSQL specific DDL and instructions
* [./datasources.env](./datasources.env) provides configuration information for the datasource and resource adapters required by this quickstart

### Datasource Configuration

The datasources are configured using environment variables, which are defined through the [datasources.env](./datasources.env) file.
* datasources: (`DATASOURCES` variable)
    * `ACCOUNTS_DERBY` is the datasource configuration for use with Derby
    * `ACCOUNTS_H2` is the datasource configuration for use with h2
    * `ACCOUNTS_MYSQL5` is the datasource configuration for use with MySQL
    * `ACCOUNTS_POSTGRESQL` is the datasource configuration for use with PostgreSQL
* resource adapters: (`RESOURCE_ADAPTERS` variable)
    * `MARKETDATA` is the resource adapter configuration for the market data files
    * `EXCEL` is the resource adapter configuration for the Excel files
    * `MAT_CACHE` is the resource adapter configuration for the JDG cache used for materialization.  This is added to the `RESOURCE_ADAPTERS` list if the `jdg-mat-cache-vdb.xml` is deployed on the server.

The `QS_DB_TYPE` variable is used to define which datasource configuration is used and can be one of (all lower case): `derby`, `h2`, `mysql5` or `postgresql`.  By default, this is set to `h2`, but may be changed by editing the deployment configuration for the application.  This must be defined as part of the deployment configuration within OpenShift to have any affect on the VDB.  Setting it in [datasources.env](./datasources.env) will affect the datasource that gets created, but the VDB will use the translator specified by the setting on the deployment configuration.  The [portfolio-vdb.xml](./app/portfolio-vdb.xml) file uses `${env.QS_DB_TYPE:h2}` to select the translator that should be used with the _Accounts_ model, and defaults to `h2`.

The `VDB_DIRS` variable is used to enable the materialization aspect of this quickstart.  If this environment variable is set with a value of ".,datagrid-materialization/src/vdb", the [datagrid-materialization](./app/datagrid-materialization) project will be built and included in the image.

The configuration details are passed to the pod through a secret.

## Deploying the Quickstart

This quickstart can be deployed as follows:

1.  Create the secret for the datasource configuration.

    ```
    $ oc secrets new datavirt-app-config datasources.env
    ```
    
2.  Instantiate one of the following templates:

    * `datavirt64-basic-s2i`   (needs datavirt-app-config)
    * `datavirt64-secured-s2i` (needs datavirt-app-config and datavirt-app-secret same as datavirt64-extensions-support-s2i in derby guide)
        includes configuration for https and jdbc (over SNI) routes

    Specifying parameters as follows:

    * `SOURCE_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
    * `SOURCE_REPOSITORY_REF`: master
    * `CONTEXT_DIR`: datavirt64/dynamicvdb-datafederation/app

    Remember to note the Teiid username and password (both randomly generated), which should be displayed after processing the template.  Optionally, you can specify your own values:

    * `TEIID_USERNAME`: teiidUser
    * `TEIID_PASSWORD`: sup3rSecret!

    > Note: the password must adhere to the password strength rules or deployment will fail.

    In case you follow materialization guide there needs to be added materialization VDB project parameter:
        *  VDB Deployment Directories (VDB_DIRS): ".,datagrid-materialization/src/vdb" 
 
    Verify by trying this url: http://{route-address}/odata/Portfolio  (asks for teiidUser, sup3rSecret!)

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

> Note: Steps one and three are unnecessary if using the [datavirt-app-secret](https://github.com/jboss-openshift/openshift-quickstarts/blob/master/datavirt64/dynamicvdb-datafederation/app/resources/secrets/datavirt-app-secret.yaml), as `datavirt-app-config` secret is defined and linked to the service account when that file is installed (e.g. `oc create -f openshift-quickstarts/datavirt64/dynamicvdb-datafederation/app/resources/secrets/datavirt-app-secret.yaml`).

### Using Red Hat JBoss Data Grid for Materialization

As a prerequisite, you will need to create a data grid service in your OpenShift project.  This can
be done by using one of the datagrid71-* templates (e.g. datagrid71-basic).  When processing the template,
ensure the following parameters are set appropriately:

    * USERNAME=jdg
    * PASSWORD=JBoss.123
    * JDG User Roles/Groups(ADMIN_GROUP)=admin,___schema_manager
    * HOTROD_AUTHENTICATION=true
    * CONTAINER_SECURITY_ROLE_MAPPER=identity-role-mapper
    * CONTAINER_SECURITY_ROLES="admin=ALL,jdg=ALL"
    * DATAVIRT_CACHE_NAMES=stockCache

The above will configure three caches: stockCache, ST_stockCache and teiid-alias-naming-cache.

Once the data grid server is up and running, a materialized view based on a JDG cache can be added to the project by specifying the `DATAGRID_MATERIALIZATION` environment variable on the build configuration, e.g.:

    ```
    $ oc env bc/datavirt-app DATAGRID_MATERIALIZATION=true
    ```

This should trigger a new build, followed by a new deployment.  You can access the cache directly using `Select * From StockJDGSource.StockCache` view, and you can access the materialized view at `select * from StocksMatView.Stock`.  For more information, reference the jdg7.1-remote-cache-materialization quickstart for Red Hat JBoss Data Virtualization.
 
