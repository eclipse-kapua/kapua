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
# This script pulls Docker images from a Docker registry
#

DOCKER_ACCOUNT=${DOCKER_ACCOUNT:=kapua}
IMAGE_VERSION=${IMAGE_VERSION:=2.0.0-SNAPSHOT}
SERVICES=("console" "api" "sql" "broker" "events-broker")

echo "Pulling Kapua images..."
(
    for SERVICE in ${SERVICES[@]}; do
        echo "kapua-${SERVICE}:${IMAGE_VERSION}"
        docker pull ${DOCKER_ACCOUNT}/kapua-${SERVICE}:${IMAGE_VERSION} &> /dev/null ||
            {
                echo "    Cannot pull image: kapua-${SERVICE}:${IMAGE_VERSION}"
                echo "    Perhaps it is not present in the Docker registry?"
                echo ""
                exit 1
            }
    done

    echo "Pulling Kapua images... DONE!"
    echo ""
) ||
    {
        echo "Pulling Kapua images... FAILED!"
        exit 1;
    }

