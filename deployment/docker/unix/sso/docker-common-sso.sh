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

SCRIPT_DIR_SSO="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export DOCKER_ACCOUNT=${DOCKER_ACCOUNT:=eclipsekapua}

# Parse ifconfig and take the first available IP address (should work for all *nix systems)
EXTERNAL_IP=$(ifconfig | grep -E "([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v '127.0.0.1' | awk '{ print $2 }' | cut -f2 -d: | head -n1)
export EXTERNAL_IP

export SSO_CRT_DIR="${SCRIPT_DIR_SSO}/../../target/compose/sso/certs" # Certs are created there since Keycloak use a volume based on that directory
export SSO_CERT_FILE="${SSO_CERT_FILE:=tls.crt}"
export SSO_KEY_FILE="${SSO_KEY_FILE:=tls.key}"

export SSO_CRT="${SSO_CRT:=${SSO_CRT_DIR}/${SSO_CERT_FILE}}"
export SSO_KEY="${SSO_KEY:=${SSO_CRT_DIR}/${SSO_KEY_FILE}}"

export KAPUA_CONSOLE_URL="${KAPUA_CONSOLE_URL:=http://${EXTERNAL_IP}:8080}"

export KEYCLOAK_IMAGE="${KEYCLOAK_IMAGE:=eclipsekapua/kapua-keycloak:${IMAGE_VERSION}}"
export KEYCLOAK_URL="${KEYCLOAK_URL:=http://${EXTERNAL_IP}:9090}" # Use https://${EXTERNAL_IP}:9443} in order to enable TLS
