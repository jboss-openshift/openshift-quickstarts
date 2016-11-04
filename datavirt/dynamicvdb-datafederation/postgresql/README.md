##Overview

This only differs from the stock Teiid dynamicvdb-datafederation quickstart in the following ways:

* customer-schema.sql has been modified to work with PostgreSQL
* vdb/portfolio-vdb.xml has been modified to work with PostgreSQL
* datasources.env provides configuration information for the datasource and resource adapters required by this quickstart
    * ACCOUNTS is the accounts database
    * MARKETDATA represents market data files
    * EXCEL represents the excel files

The configuration details are passed to the pod through a secret.  To create the secret for the configuration details:
```
$ oc secrets new jdv-app-config datasources.env
```

The file data can be mounted into the deployment as follows:

For the market data:
```
$ oc secrets new jdv-app-data teiid/teiid-quickstarts/dynamicvdb-datafederation/src/teiidfiles/data
$ oc volume dc/jdv-app --add --name=data --mount-path=/teiidfiles/data --type=secret --secret-name=jdv-app-data
```

For the excel files:
```
$ oc secrets new jdv-app-excel-files teiid/teiid-quickstarts/dynamicvdb-datafederation/src/teiidfiles/excelFiles
$ oc volume dc/jdv-app --add --name=excel-files --mount-path=/teiidfiles/excel-files --type=secret --secret-name=jdv-app-excel-files
```

If a service account is associated with the deployment (e.g. `jdv-service-account`), you will need to add the data file secrets to the service account, e.g.: (links secrets defined above to the service account)
```
$ oc secrets link jdv-service-account jdv-app-data jdv-app-excel-files
```

Note: using secrets is not an ideal way to manage file datasources, but is used here for simplicity, as it saves having to work with "real" volumes.

##Initializing the Database

The PostgreSQL database can be setup by instantiating one of the PostgreSQL templates (e.g. `postgresql-ephemeral`) with the following parameters:

* DATABASE\_SERVICE\_NAME=accounts
* POSTGRESQL\_USER=pguser
* POSTGRESQL\_PASSWORD=pgpass
* POSTGRESQL\_DATABASE=accounts

The username and password parameters match what is defined in `datasources.env` (or you can modify the settings in this file to match what you're using for the DB).

You will also need to configure the database to support XA transactions.  To do this, you will need to set `max_prepared_transactions` to a non-zero value.  This can be accomplished by executing the following command:

```
$ oc env dc/accounts POSTGRESQL\_MAX\_PREPARED\_TRANSACTIONS=10
```

Once the database is up and running, the tables and seed data can be installed by invoking the following:

```
$ cat customer-schema.sql |  oc exec accounts-2-0at2i -i -- scl enable rh-postgresql95 -- psql -U pguser -d accounts
```

> Note: replace `accounts-2-0at2i` with the pod name in your environment.

The above command simply pipes the SQL commands through to `psql` running inside the pod.  The `-i` option allows stdin to be forwarded to the executed command and the `scl` command is required so the `psql` command will resolve appropriately.
