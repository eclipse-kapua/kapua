#!/usr/bin/env bash
###############################################################################
# Copyright (c) 2016, 2022 Red Hat Inc and others
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Red Hat Inc - initial API and implementation
#     Eurotech
###############################################################################

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
. ${SCRIPT_DIR}/openshift-common.sh

: DOCKER_ACCOUNT=${DOCKER_ACCOUNT:=kapua}
: IMAGE_VERSION=${IMAGE_VERSION:=2.1.0-FALSE_EXTENSIONS-SNAPSHOT}
: JAVA_OPTS_EXTRA=${JAVA_OPTS_EXTRA:=''}

### Test if the project is already created ... fail otherwise

${OC} login ${OPENSHIFT_HOST} --username=${OPENSHIFT_USER} --password=${OPENSHIFT_PASS} ${OPENSHIFT_LOGIN_OPTS}

${OC} describe "project/${OPENSHIFT_PROJECT_NAME}" &>/dev/null || die "Project '${OPENSHIFT_PROJECT_NAME}' not created or OpenShift is unreachable. Try with:\n\n\toc new-project ${OPENSHIFT_PROJECT_NAME}\n\n"

### Create Kapua from template

echo "Creating Kapua from templates..."

${OC} new-app -n "${OPENSHIFT_PROJECT_NAME}" -f templates/kapua-template-core.yml    -p "DOCKER_ACCOUNT=${DOCKER_ACCOUNT}" -p "IMAGE_VERSION=${IMAGE_VERSION}" -p "JAVA_OPTS_EXTRA=${JAVA_OPTS_EXTRA}"
${OC} new-app -n "${OPENSHIFT_PROJECT_NAME}" -f templates/kapua-template-broker.yml  -p "DOCKER_ACCOUNT=${DOCKER_ACCOUNT}" -p "IMAGE_VERSION=${IMAGE_VERSION}" -p "JAVA_OPTS_EXTRA=${JAVA_OPTS_EXTRA}"
${OC} new-app -n "${OPENSHIFT_PROJECT_NAME}" -f templates/kapua-template-console.yml -p "DOCKER_ACCOUNT=${DOCKER_ACCOUNT}" -p "IMAGE_VERSION=${IMAGE_VERSION}" -p "JAVA_OPTS_EXTRA=${JAVA_OPTS_EXTRA}"
${OC} new-app -n "${OPENSHIFT_PROJECT_NAME}" -f templates/kapua-template-api.yml     -p "DOCKER_ACCOUNT=${DOCKER_ACCOUNT}" -p "IMAGE_VERSION=${IMAGE_VERSION}" -p "JAVA_OPTS_EXTRA=${JAVA_OPTS_EXTRA}"

echo "Creating Kapua from templates... DONE!"
