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

FROM @docker.account@/java-base

COPY maven /

ENV BROKER_HOST=message-broker
ENV BROKER_ID=broker-id

ENV DATASTORE_ADDR es:9200
ENV DATASTORE_CLIENT org.eclipse.kapua.service.elasticsearch.client.rest.RestElasticsearchClientProvider

ENV SQL_DB_ADDR db
ENV SQL_DB_PORT 3306

ENV EVENT_BROKER_URL=${EVENT_BROKER_URL:-amqp://events-broker:5672}
ENV EVENT_BROKER_USERNAME=${EVENT_BROKER_USERNAME:-kapua-sys}
ENV EVENT_BROKER_PASSWORD=${EVENT_BROKER_PASSWORD:-kapua-password}
ENV SERVICE_BROKER_URL=${SERVICE_BROKER_URL:-amqp://events-broker:5672}
ENV SERVICE_BROKER_USERNAME=${SERVICE_BROKER_USERNAME:-kapua-sys}
ENV SERVICE_BROKER_PASSWORD=${SERVICE_BROKER_PASSWORD:-kapua-password}

USER 0

RUN useradd -u 1002 -g 0 -d '/opt/artemis' -s '/sbin/nologin' artemis && \
    chown -R artemis:root /opt/artemis && \
    chmod -R g=u /opt/artemis && \
    chmod -R +x /opt/artemis/bin

ENV JAVA_ARGS "-Dcommons.db.schema.update=true \
                   -Dcommons.db.connection.host=\${SQL_DB_ADDR} \
                   -Dcommons.db.connection.port=\${SQL_DB_PORT} \
                   -Dcommons.eventbus.url="${EVENT_BROKER_URL}" \
                   -Dcommons.eventbus.username="${EVENT_BROKER_USERNAME}" \
                   -Dcommons.eventbus.password="${EVENT_BROKER_PASSWORD}" \
                   -Dcommons.servicebus.url="${SERVICE_BROKER_URL}" \
                   -Dcommons.servicebus.username="${SERVICE_BROKER_USERNAME}" \
                   -Dcommons.servicebus.password="${SERVICE_BROKER_PASSWORD}" \
                   -Dbroker.ip=message-broker \
                   -Dlocator.class.impl=org.eclipse.kapua.locator.guice.GuiceLocatorImpl \
                   -Dlocator.guice.stage=PRODUCTION \
                   -Dcertificate.jwt.private.key=file:///etc/opt/kapua/key.pk8 \
                   -Dcertificate.jwt.certificate=file:///etc/opt/kapua/cert.pem"

EXPOSE 1883 1893 5672 8883

VOLUME /opt/activemq/kapua/

USER 1002

ENTRYPOINT /opt/artemis/run-broker