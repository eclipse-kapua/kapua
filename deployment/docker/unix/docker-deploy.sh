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

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

docker_common() {
    . "${SCRIPT_DIR}"/docker-common.sh
}

docker_compose() {

    declare -a COMPOSE_FILES;

    # Debug Mode
    if [[ "$1" == true ]]; then
      echo "Debug mode enabled!"
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.broker-debug.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.consumer-lifecycle-debug.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.consumer-telemetry-debug.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.service-authentication-debug.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.console-debug.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.job-engine-debug.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.rest-debug.yml")
    fi

    # JMX Mode
    if [[ "$2" == true ]]; then
      echo "JMX mode enabled!"
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.broker-jmx.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.console-jmx.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.consumer-lifecycle-jmx.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.consumer-telemetry-jmx.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.service-authentication-jmx.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.job-engine-jmx.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.rest-jmx.yml")
    fi

    # Dev Mode
    if [[ "$3" == true ]]; then
      echo "Dev mode enabled!"
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.db-dev.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.es-dev.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.api-dev.yml")
    fi

    # SSL
    if [[ "$4" == true ]]; then
      echo "SSL enabled!"
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.broker-ssl.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.console-ssl.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/extras/docker-compose.rest-ssl.yml")
    fi

    # SSO Mode
    if [[ "$5" == true ]]; then
      echo "SSO enabled!"
      . "${SCRIPT_DIR}/sso/docker-sso-config.sh" "$4"

      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/sso/docker-compose.console-sso.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/sso/docker-compose.rest-api-sso.yml")
      COMPOSE_FILES+=(-f "${SCRIPT_DIR}/../compose/sso/docker-compose.keycloak.yml")
    fi

    # Swagger UI
    if [[ "$6" == false ]]; then
      echo "Swagger disabled!"
    fi
    export KAPUA_SWAGGER_ENABLE=$6

    docker compose -f "${SCRIPT_DIR}/../compose/docker-compose.yml" "${COMPOSE_FILES[@]}" up -d
}

print_usage_deploy() {
    echo "Usage: $(basename "$0") [-h|--help] [--dev] [--debug] [--jmx] [--logs] [--ssl] [--sso] [--no-swagger]" >&2
}

DEBUG_MODE=false
DEV_MODE=false
OPEN_LOGS=false
JMX_MODE=false
SSL_MODE=false
SSO_MODE=false
SWAGGER=true
for option in "$@"; do
  case $option in
    -h|--help)
      print_usage_deploy
      exit 0;
      ;;
    --debug)
      DEBUG_MODE=true
      ;;
    --dev)
      DEV_MODE=true
      ;;
    --logs)
      OPEN_LOGS=true
      ;;
    --jmx)
      JMX_MODE=true
      ;;
    --ssl)
      SSL_MODE=true
        ;;
    --sso)
      SSO_MODE=true
      ;;
    --no-swagger)
      SWAGGER=false
      ;;
    -*)
      echo "ERROR: Unrecognised option $option"
      print_usage_deploy
      exit 1
      ;;
    *)
      ;;
  esac
done

docker_common

# Configure certificates if required
if [[ ${SSL_MODE} == true ]]; then
    . "${SCRIPT_DIR}/configure-certificates.sh"
fi

echo "Deploying Eclipse Kapua version $IMAGE_VERSION..."
docker_compose ${DEBUG_MODE} ${JMX_MODE} ${DEV_MODE} ${SSL_MODE} ${SSO_MODE} ${SWAGGER} || {
    echo "Deploying Eclipse Kapua... ERROR!"
    exit 1
}
echo "Deploying Eclipse Kapua... DONE!"

if [[ ${OPEN_LOGS} == true ]]; then
    . "${SCRIPT_DIR}/docker-logs.sh"
else
    echo "Run \"docker compose -f ${SCRIPT_DIR}/../compose/docker-compose.yml logs -f\" for container logs"
fi
