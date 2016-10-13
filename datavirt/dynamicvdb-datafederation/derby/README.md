This only differs from the stock Teiid dynamicvdb-datafederation quickstart in the following ways:

* customer-schema.sql has been modified to work with Derby
* vdb/portfolio-vdb.xml has been modified to work with Derby
* vdb/deployments/accounts-db.jar is a read-only version of the accounts DB
* datasources provides configuration information for datasources and resource adapters required by this quickstart
** ACCOUNTS is the accounts database
** MARKETDATA represents market data files
** EXCEL represents the excel files

The file data can be mounted into the deployment as follows:

For the market data:
oc secrets new jdv-app-data teiid/teiid-quickstarts/dynamicvdb-datafederation/src/teiidfiles/data
oc volume dc/jdv-app --add --name=data --mount-path=/teiidfiles/data --type=secret --secret-name=jdv-app-data

For the excel files:
oc secrets new jdv-app-excel-files teiid/teiid-quickstarts/dynamicvdb-datafederation/src/teiidfiles/excelFiles
oc volume dc/jdv-app --add --name=excel-files --mount-path=/teiidfiles/excel-files --type=secret --secret-name=jdv-app-excel-files

Note: using secrets is not an ideal way to manage file datasources, but is used here for simplicity, as it saves having to work with "real" volumes.
