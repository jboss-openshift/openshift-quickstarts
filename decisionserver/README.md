## Decision Server Quickstart

This quickstart is intend to use within our [Decision Server Container](https://github.com/jboss-container-images/jboss-decisionserver-6-openshift-image).

## How to use it?

To deploy the Hello Rules demo you can use one of Decision Server templates already installed in your OpenShift Catalog.

To deploy it on your OpenShift instance, just execute the following commands:

```bash
oc login https://<your_openshift_address:<port>
oc new-project decisionserver
oc new-app jboss-decisionserver64-openshift
```

You should see a response from the above command similar to this:

```bash
--> Found image 7227d60 (4 weeks old) in image stream "openshift/jboss-decisionserver64-openshift" under tag "latest" for "jboss-decisionserver64-openshift"

    JBoss BRMS Realtime Decision Server 6.4 
    --------------------------------------- 
    Platform for executing business rules on JBoss BRMS Realtime Decision Server 6.4.

    Tags: builder, decisionserver, decisionserver6

    * This image will be deployed in deployment config "jboss-decisionserver64-openshift"
    * Ports 8080/tcp, 8443/tcp, 8778/tcp will be load balanced by service "jboss-decisionserver64-openshift"
      * Other containers can access this service through the hostname "jboss-decisionserver64-openshift"

--> Creating resources ...
    deploymentconfig "jboss-decisionserver64-openshift" created
    service "jboss-decisionserver64-openshift" created
--> Success
    Run 'oc status' to view your app.

```


Now you can deploy the [hellorules-client](hellorules-client) in the same or another project and test your decision server container.

To deploy the hello rules client you can use the **eap64-basic-s2i** (It is available in the OpenShift Catalog) template and specify the above quickstart to be deployed.
To do so, execute the following commands:

```bash
oc new-app eap64-basic-s2i \
    -p SOURCE_REPOSITORY_URL=https://github.com/jboss-openshift/openshift-quickstarts \
    -p SOURCE_REPOSITORY_REF=master \ 
    -p CONTEXT_DIR=decisionserver
```

As result you should see something like this:
```bash
> -p SOURCE_REPOSITORY_URL=https://github.com/jboss-openshift/openshift-quickstarts \
> -p SOURCE_REPOSITORY_REF=master \
> -p CONTEXT_DIR=decisionserver
--> Deploying template "openshift/eap64-basic-s2i" to project decisionserver

     Red Hat JBoss EAP 6.4 (no https)
     ---------
     Application template for EAP 6 applications built using S2I.

     A new EAP 6 based application has been created in your project.

     * With parameters:
        * Application Name=eap-app
        * Custom http Route Hostname=
        * Git Repository URL=https://github.com/jboss-openshift/openshift-quickstarts
        * Git Reference=master
        * Context Directory=decisionserver
        * Queues=
        * Topics=
        * HornetQ Password=iAIjByCI # generated
        * Github Webhook Secret=IAkpehgt # generated
        * Generic Webhook Secret=u8WDaTdt # generated
        * ImageStream Namespace=openshift
        * JGroups Cluster Password=YaDcFwOY # generated
        * Deploy Exploded Archives=false
        * Maven mirror URL=
        * ARTIFACT_DIR=

--> Creating resources ...
    service "eap-app" created
    route "eap-app" created
    imagestream "eap-app" created
    buildconfig "eap-app" created
    deploymentconfig "eap-app" created
--> Success
    Build scheduled, use 'oc logs -f bc/eap-app' to track its progress.
    Run 'oc status' to view your app.

```

After the application is built, access the hello rules client app through the route created:

```bash
$ oc get routes eap-app
NAME      HOST/PORT                                         PATH      SERVICES   PORT      TERMINATION   WILDCARD
eap-app   eap-app-decisionserver.<your_openshift_suffix>             eap-app    <all>                   None
```

Note that this route should be resolvable.

And example of request would be something like this:

```bash
http://eap-app-decisionserver.<your_openshift_suffix>/hellorules?command=runRemoteRest&protocol=http&host=kie-app-decisionserver.<your_openshift_suffix>&port=80&username=kieserver&password=<the_generated_kie_password>
```

#### Found an issue?
Feel free to report it [here](https://github.com/jboss-openshift/openshift-quickstarts/issues/new).

__For any feedback, please send us an email (cloud-enablement-feedback@redhat.com) and let us know about your thoughts.__