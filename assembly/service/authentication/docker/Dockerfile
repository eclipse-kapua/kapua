###############################################################################
# Copyright (c) 2020, 2025 Eurotech and/or its affiliates and others
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

ENV SERVICE_BROKER_URL=${SERVICE_BROKER_URL:-failover:(amqp://events-broker:5672)}
ENV SERVICE_BROKER_USERNAME=${SERVICE_BROKER_USERNAME:-kapua-sys}
ENV SERVICE_BROKER_PASSWORD=${SERVICE_BROKER_PASSWORD:-kapua-password}
ENV EVENT_BROKER_URL=${EVENT_BROKER_URL:-failover:(amqp://events-broker:5672)}
ENV EVENT_BROKER_USERNAME=${EVENT_BROKER_USERNAME:-kapua-sys}
ENV EVENT_BROKER_PASSWORD=${EVENT_BROKER_PASSWORD:-kapua-password}
ENV SERVICE_HOST=${SERVICE_HOST:-authentication}

ENV SQL_DB_ADDR db
ENV SQL_DB_PORT 3306

ENV JAVA_OPTS -Dcommons.db.schema.update=true \
              -Dcommons.db.connection.host=${SQL_DB_ADDR} \
              -Dcommons.db.connection.port=${SQL_DB_PORT} \
              -Dlocator.guice.stage=PRODUCTION \
              -Dcommons.eventbus.url="${EVENT_BROKER_URL}" \
              -Dcommons.eventbus.username="${EVENT_BROKER_USERNAME}" \
              -Dcommons.eventbus.password="${EVENT_BROKER_PASSWORD}" \
              -Dcertificate.jwt.private.key=file:///etc/opt/kapua/key.pk8 \
              -Dcertificate.jwt.certificate=file:///etc/opt/kapua/cert.pem \
              -Dcommons.servicebus.url="${SERVICE_BROKER_URL}" \
              -Dcommons.servicebus.username="${SERVICE_BROKER_USERNAME}" \
              -Dcommons.servicebus.password="${SERVICE_BROKER_PASSWORD}"

EXPOSE 8080

ENTRYPOINT /run-service-authentication
