Running on OpenShift
====================

This quickstart is designed to run within OpenShift v3, accessing a remote Data
in a MongoDB server instance running in the same OpenShift project.



Creating the Quickstart Application
-----------------------------------

For this quickstart you can use two Application Templates:

 - eap64-mongodb-persistent-s2i - the data written in the database is persisted, so you will not loose it if you recreate the pod.
 - eap64-mongodb-s2i - This Application template does not persist any data, if you recreate the pod the data will be lost.

Specify the following for the source configuration:

* `SOURCE_REPOSITORY_URL`: https://github.com/jboss-openshift/openshift-quickstarts
* `SOURCE_REPOSITORY_REF`: 1.2
* `CONTEXT_DIR`: kitchensink-mongodb

> Note, the `SOURCE_REPOSITORY_REF` is the branch used when building the
> source.  Latest source is always available in the `master` branch.