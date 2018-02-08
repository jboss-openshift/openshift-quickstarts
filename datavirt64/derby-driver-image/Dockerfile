# Copyright 2016 Red Hat
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ------------------------------------------------------------------------
#
# This is a Dockerfile for a Derby JDBC driver add-on for EAP.

FROM scratch

# Labels
LABEL name="Derby JDBC Driver" \
      version="10.12.1.1"

# Copy add-on details
COPY install.sh \
     install.properties \
     /extensions/
COPY modules /extensions/modules/

# Download the driver into the module folder
ADD https://repo1.maven.org/maven2/org/apache/derby/derbyclient/10.12.1.1/derbyclient-10.12.1.1.jar \
    https://repo1.maven.org/maven2/org/apache/derby/derby/10.12.1.1/derby-10.12.1.1.jar \
    /extensions/modules/system/layers/openshift/org/apache/derby/main/
