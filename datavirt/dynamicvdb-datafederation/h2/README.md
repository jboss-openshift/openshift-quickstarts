##Overview

This directory contains details specific to using this quickstart with a h2 database.  It contains the [customer-schema.sql](./customer-schema.sql) file which includes the DDL and some seed data, along with this readme.

##Initializing the Database

No extra steps are required to initialize the database as it is injected into the image during the s2i build (from [../app/data/databases/h2](../app/data/databases/h2)).


##Running the QuickStart

Follow the instructions in the main [README](../README.md) and set `QS_DB_TYPE=h2`, e.g.:

```
$ oc env dc/datavirt-app QS_DB_TYPE=h2
```

> Note, if `QS_DB_TYPE` is not set, it defaults to `h2`.
