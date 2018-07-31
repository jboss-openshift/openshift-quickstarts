## Overview

These are exammple configuration files for configuring one or more security domains.  These example demonstrate configuring an LDAP security domain.

###  Configure LDAP using configProps

The properties file is loaded as configProps.  The datavirt64-basic-s2i.json (or non-ldap) template can be used as an example application deployment.

file:  securitydomain.env 

example:

the following will load the env file as environment properties into the project

oc new-app -f {path}/datavirt64-basic-s2i.json \
-p TEIID_USERNAME=teiidUser  -p TEIID_PASSWORD=P@ssword1 -p IMAGE_STREAM_NAMESPACE={projectname} --build-env-file=securitydomain.env


###  Configurre LDAP using secret

The properties are loaded using a secret file,  The datavirt64-ldap-s2i.json template can be used to for the application deployment.

file:  datavirt-security-secret.yaml     the secret file defines the datavirt-security-config secret that corresponds to the secret name property value for property  "CONFIG_SECURITY_DOMAIN_NAME"

example:

the followinig will load the secret and link it

oc create -f {path}/datavirt-security-secret.yaml

# this is assuming the jdv quickstarts are also being loaded, hence including datavirt-app-config
oc secrets link datavirt-service-account datavirt-app-config datavirt-security-config
