## Overview

This directory contains details specific to using this quickstart with a PostgreSQL database.  It contains the [customer-schema.sql](./customer-schema.sql) file which includes the DDL and some seed data, along with this readme.

## Initializing the Database

The PostgreSQL database can be setup by instantiating one of the PostgreSQL templates (e.g. `postgresql-ephemeral`) with the following parameters:

* DATABASE\_SERVICE\_NAME=accounts-postgresql
* POSTGRESQL\_USER=pguser
* POSTGRESQL\_PASSWORD=pgpass
* POSTGRESQL\_DATABASE=accounts

The username and password parameters match what is defined in [datasources.env](../datasources.env).  If you use a different user and/or password, you must modify the settings in this file to match what you're using for the DB.  After modifying the file, you will have to recreate the secret so the application can pick up the changes.  You may also have to trigger a new deployment or recreate the pod, as changes to secrets are not propagated to running pods.

You will also need to configure the database to support XA transactions.  To do this, you will need to set `max_prepared_transactions` to a non-zero value.  This can be accomplished by executing the following command:

```
$ oc env dc/accounts POSTGRESQL_MAX_PREPARED_TRANSACTIONS=10
```

Once the database is up and running, the tables and seed data can be installed by invoking the following:

```
$ cat customer-schema.sql |  oc exec accounts-postgresql-2-0at2i -i -- scl enable rh-postgresql95 -- psql -U pguser -d accounts
```

> Note: replace `accounts-postgresql-2-0at2i` with the pod name from your environment.

The above command simply pipes the SQL commands through to `psql` running inside the pod.  The `-i` option allows stdin to be forwarded to the executed command and the `scl` command is required so the `psql` command will resolve appropriately (i.e. it _enables_ the _rh-postgresql95_ software collection).

## Running the QuickStart

Follow the instructions in the main [README](../README.md) and set `QS_DB_TYPE=postgresql`, e.g.:

```
$ oc env dc/datavirt-app QS_DB_TYPE=postgresql
```
