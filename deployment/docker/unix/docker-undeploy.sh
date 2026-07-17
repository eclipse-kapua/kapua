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

docker_common() {
    . "${SCRIPT_DIR}"/docker-common.sh
}

docker_undeploy() {
    declare -a COMPOSE_FILES;

    if [[ "$(docker ps | grep eclipsekapua/kapua-keycloak)" != "" ]] ; then
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/sso/docker-compose.keycloak.yml")
    fi

    docker compose -f "${SCRIPT_DIR}/../compose/docker-compose.yml" "${COMPOSE_FILES[@]}" down
}

docker_common

echo "Undeploying Eclipse Kapua..."
docker_undeploy || {
    echo "Undeploying Eclipse Kapua... ERROR!";
    exit 1;
}
echo "Undeploying Eclipse Kapua... DONE!"
