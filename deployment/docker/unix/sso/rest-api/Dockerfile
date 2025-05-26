################################################################################
#    Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
#
#    This program and the accompanying materials are made
#    available under the terms of the Eclipse Public License 2.0
#    which is available at https://www.eclipse.org/legal/epl-2.0/
#
#    SPDX-License-Identifier: EPL-2.0
#
#    Contributors:
#        Eurotech
################################################################################
FROM kapua/kapua-api:latest

USER 0
COPY ../tls.crt /tmp/tls.crt
RUN keytool -noprompt  -importcert -file "/tmp/tls.crt" -alias "ssocert" -keystore "${JAVA_HOME}/lib/security/cacerts" -storepass "changeit"
USER 1000
