###############################################################################
# Copyright (c) 2018, 2026 Eurotech and/or its affiliates and others
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Eurotech - initial API and implementation
#
###############################################################################

FROM alpine:3

ENV JAVA_HOME=/usr/lib/jvm/default-jvm

# Update system packages to obtain recent and updated packages that also contains security fixes
RUN apk upgrade --no-cache

# Install packages
#
# Java 17: well is Java
# bash: to run bash scripts
# curl: required to download jetty, H2 and others
# openssl: SSL support
# shadow: To run useradd command
RUN apk add --no-cache \
    openjdk17-jre-headless \
    bash \
    curl \
    openssl \
    shadow

# Install Jolokia agent
RUN mkdir -p /opt/jolokia && \
    curl -s @jolokia.agent.url@ -o /opt/jolokia/jolokia-jvm-agent.jar

# Generate X509 certificate and private key
RUN mkdir -p /etc/opt/kapua && \
    openssl req -x509 -newkey rsa:4096 -keyout /etc/opt/kapua/key.pem -out /etc/opt/kapua/cert.pem -days 365 -nodes -subj '/O=Eclipse Kapua/C=XX' && \
    openssl pkcs8 -topk8 -in /etc/opt/kapua/key.pem -out /etc/opt/kapua/key.pk8 -nocrypt && \
    chmod +r /etc/opt/kapua/key.pk8 && \
    rm /etc/opt/kapua/key.pem

EXPOSE 8778
