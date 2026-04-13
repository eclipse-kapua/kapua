#!/usr/bin/env bash
###############################################################################
# Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Eurotech- initial API and implementation
###############################################################################

#
# This script copies Docker images from the local Docker registry into Minishift VM's one.
#
# This is done by saving local images to /tmp/kapua-containers-{someRandomness}/ using command:
#     docker save -o /tmp/kapua-containers-{someRandomness}/{outputFileName} {sourceImageName}
#
# Then sets Minishift Docker registry overriding environment variables:
#     DOCKER_CERT_PATH=~/.minishift/certs
#     DOCKER_HOST=tcp://192.168.99.100:2376
#     DOCKER_TLS_VERIFY=1
#
# And loading into the registry by running:
#     docker load < /tmp/kapua-containers-{theSameRandonessAsBefore}/{outputFileName}
#

ERROR=0
DOCKER_ACCOUNT=${DOCKER_ACCOUNT:=kapua}
IMAGE_VERSION=${IMAGE_VERSION:=2.0.0-SNAPSHOT}
SERVICES=("console" "api" "sql" "broker" "events-broker")
TMP_DIR="/tmp/kapua-containers-$(date +%s)"

echo "Creating tmp directory: ${TMP_DIR}..."
mkdir -p ${TMP_DIR} || die "Cannot create directory '${TMP_DIR}' where to copy files."
echo "Creating tmp directory... DONE!"
echo ""

(
    echo "Exporting Kapua images..."
    for SERVICE in ${SERVICES[@]}; do
        echo "    kapua-${SERVICE}:${IMAGE_VERSION}"
        docker save -o ${TMP_DIR}/${SERVICE} kapua/kapua-${SERVICE}:${IMAGE_VERSION} &> /dev/null ||
            {
                echo "    Cannot export image: kapua-${SERVICE}:${IMAGE_VERSION}"
                echo "    Perhaps it is not present in the local Docker registry?"
                echo ""
                exit 1
            }
    done
    echo "Exporting Kapua images... DONE!"
    echo ""

    eval $(minishift docker-env)

    echo "Importing Kapua images into Minishift..."
    for SERVICE in ${SERVICES[@]}; do

        echo "kapua-${SERVICE}:${IMAGE_VERSION}"
        docker load < ${TMP_DIR}/${SERVICE} &> /dev/null ||
            {
                echo "    Cannot import image: kapua-${SERVICE}:${IMAGE_VERSION}"
                echo "    Perhaps due to previous errors?"
                exit 1
            }
    done
    echo "Importing Kapua images into Minishift... DONE!"
    echo ""
) ||
    {
        ERROR=1;
        echo "Exporting/Importing Kapua images... FAILED!"
        echo "    Perhaps docker images are not present in the local Docker registry?"
        echo "    Please check running command:"
        echo "        docker images"
        echo "    If no 'kapua/kapua*:${IMAGE_VERSION}' images are present, please build them running command:"
        echo "        mvn clean install -Pconsole,docker"
        echo ""
    }

echo "Removing tmp directory: ${TMP_DIR}..."
if [ -d "${TMP_DIR}" ]; then
    rm -rf ${TMP_DIR}
fi
echo "Removing tmp directory... DONE!"

exit ${ERROR}
