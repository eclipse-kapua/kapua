###############################################################################
# Copyright (c) 2019, 2025 Eurotech and/or its affiliates and others
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     Eurotech - initial API and implementation
#
###############################################################################

FROM @docker.account@/java-base-17

COPY maven /

USER 0

RUN useradd -u 1002 -g 0 -d '/opt/artemis' -s '/sbin/nologin' artemis && \
    chown -R artemis:root /opt/artemis && \
    chmod -R g=u /opt/artemis && \
    chmod -R +x /opt/artemis/bin

EXPOSE 5672

VOLUME /opt/activemq/kapua/

USER 1002

ENTRYPOINT /opt/artemis/run-event-broker