#!/bin/bash

set -e

# import the common functions for installing modules and configuring drivers
source /usr/local/s2i/install-common.sh

# should be the directory where this script is located
injected_dir=$1

# install the JDBC client module
chmod -R ugo+rX ${injected_dir}/modules
install_modules ${injected_dir}/modules

# configure the JDBC driver in standalone.xml.  Driver is named "derby"
configure_drivers ${injected_dir}/install.properties
