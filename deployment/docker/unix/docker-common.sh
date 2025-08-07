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

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export IMAGE_VERSION=${IMAGE_VERSION:=2.0.0-SNAPSHOT}
export CRYPTO_SECRET_KEY="${CRYPTO_SECRET_KEY:=dockerSecretKey!}"

# Certificates
export KAPUA_CRT_DIR="${KAPUA_CRT_DIR:=$(mktemp -d)}"
export KAPUA_CERT_FILE="${KAPUA_CERT_FILE:=cert.pem}"
export KAPUA_KEY_FILE="${KAPUA_KEY_FILE:=key.pem}"
export KAPUA_CA_FILE="${KAPUA_CA_FILE:=cert.pem}"