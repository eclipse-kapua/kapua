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

SCRIPT_DIR_SSO_COFIG="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

. "${SCRIPT_DIR_SSO_COFIG}/docker-common-sso.sh" "$1" # the first argument is the SSL enabler set via the --ssl deployment

#
# Build the Keycloak image if missing
if [[ "$(docker images -q "${KEYCLOAK_IMAGE}" 2> /dev/null)" == "" ]] ; then
  echo "Docker Image ${KEYCLOAK_IMAGE} not found!"

  # Build script is under deployment/commons/sso/keycloak/
  cd "${SCRIPT_DIR_SSO_COFIG}"/../../../commons/sso/keycloak/ || exit
  . ./build
  cd "${SCRIPT_DIR_SSO_COFIG}" || exit

else
  echo "Docker Image ${KEYCLOAK_IMAGE} found!"
fi

#
# If the certificate is missing, generate it (self-signed certificate) with a private key too
if [[ ! -s ${SSO_CRT} ]]; then
  echo "Certificate in ${SSO_CRT} not found!"

  echo "Generating certificate and private key..."
  if [[ ! -d ${SSO_CRT_DIR} ]]; then
    mkdir -p "${SSO_CRT_DIR}"
  fi
  openssl req -x509 \
          -newkey rsa:4096 \
          -keyout "${SSO_KEY}" \
          -out "${SSO_CRT}" \
          -days 365 \
          -nodes \
          -subj '/CN=localhost/O=Eclipse Kapua/C=XX' \
          -reqexts SAN \
          -extensions SAN \
          -config <(cat /etc/ssl/openssl.cnf <(printf "[SAN]\nsubjectAltName=DNS:localhost,IP:127.0.0.1,IP:${EXTERNAL_IP}"))
  echo "Generating certificate and private key... DONE!"

else
  echo "Certificate in ${SSO_CRT} found!"
fi

#
# Building the kapua-console with certificates for SSO stuff
echo "Building Console with SSO certificate..."
cp "${SSO_CRT}" . # Ugly but it works and I don't wanna change it :D
docker build -t "${DOCKER_ACCOUNT}"/kapua-console:${IMAGE_VERSION}-sso -f "${SCRIPT_DIR_SSO_COFIG}"/console/Dockerfile .
rm "${SSO_CERT_FILE}" # Ugly but it works and I don't wanna change it :D
echo "Building Console with SSO certificate... DONE!"

#
# Building the kapua-api with certificates for SSO stuff
echo "Building Rest API with SSO certificate..."
cp "${SSO_CRT}" . # Ugly but it works and I don't wanna change it :D
docker build -t "${DOCKER_ACCOUNT}"/kapua-api:${IMAGE_VERSION}-sso -f "${SCRIPT_DIR_SSO_COFIG}"/rest-api/Dockerfile .
rm "${SSO_CERT_FILE}" # Ugly but it works and I don't wanna change it :D
echo "Building Console with SSO certificate... DONE!"