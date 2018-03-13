## Overview

This directory contains details specific to using this quickstart with a MySQL database.  It contains the [customer-schema.sql](./customer-schema.sql) file which includes the DDL and some seed data, along with this readme.

## Initializing the Database

The MySQL database can be setup by instantiating one of the MySQL templates (e.g. `mysql-ephemeral`) with the following parameters:

* DATABASE\_SERVICE\_NAME=accounts-mysql5
* MYSQL\_USER=myuser
* MYSQL\_PASSWORD=mypass
* MYSQL\_DATABASE=accounts

The username and password parameters match what is defined in [datasources.env](../datasources.env).  If you use a different user and/or password, you must modify the settings in this file to match what you're using for the DB.  After modifying the file, you will have to recreate the secret so the application can pick up the changes.  You may also have to trigger a new deployment or recreate the pod, as changes to secrets are not propagated to running pods.

Once the database is up and running, the tables and seed data can be installed by invoking the following:

```
$ cat customer-schema.sql |  oc exec accounts-mysql5-1-22hwq -i -- scl enable rh-mysql57 -- mysql -u '$MYSQL_USER' -p'$MYSQL_PASSWORD' -h '$ACCOUNTS_MYSQL5_SERVICE_HOST' accounts
```

> Note: replace `accounts-mysql5-1-22hwq` with the pod name from your environment.  Also note the single quote marks around some of the paramters.  These ensure the variables are expanded from the pod's environment, not from your client's environment.  Additionally, no space between -p and '$MYSQL_PASSWORD'.

The above command simply pipes the SQL commands through to `mysql` running inside the pod.  The `-i` option allows stdin to be forwarded to the executed command and the `scl` command is required so the `mysql` command will resolve appropriately (i.e. it _enables_ the _rh-mysql57_ software collection).

## Running the QuickStart

1.  Follow the instructions in the main [README](../README.md)

2.  Set the database type. 

    Add `QS_DB_TYPE` to the `datavirt-app` deployment configuration, e.g.:

    ```
    $ oc env dc/datavirt-app QS_DB_TYPE=mysql5
    ```

