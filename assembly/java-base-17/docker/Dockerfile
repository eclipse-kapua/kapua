###############################################################################
# Copyright (c) 2018, 2025 Eurotech and/or its affiliates and others
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

FROM @docker.base.image@

ENV JAVA_HOME=/usr/lib/jvm/jre-openjdk

# Install packages
#
# Java 17: well is Java
# curl: required to download jetty, H2 and others
# openssl: SSL support
# tar: Unpack archives
# gzip: Unpack archives
# shadow-utils: To run useradd command
RUN microdnf install -y \
    java-17-openjdk-headless \
    curl \
    openssl \
    tar \
    gzip \
    shadow-utils && \
    microdnf clean all

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
