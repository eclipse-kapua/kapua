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
#     Eurotech - initial API and implementation
#
###############################################################################

FROM @docker.account@/java-base

COPY maven /

USER 0

RUN useradd -u 1001 -g 0 -d '/var/opt/h2' -s '/sbin/nologin' h2 && \
    mkdir -p /var/opt/h2/data && chmod -R a+rw /var/opt/h2 && \
    mkdir -p /opt/h2 && chmod a+r /opt/h2 && \
    cd /opt/h2 && \
    curl -s https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar -o h2.jar

VOLUME /var/opt/h2/data

EXPOSE 3306 8181

USER 1001

ENTRYPOINT /var/opt/h2/run-h2
