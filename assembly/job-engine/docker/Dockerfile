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

FROM @docker.account@/jetty-base

COPY maven /

ENV BROKER_ADDR broker
ENV BROKER_PORT 1883

ENV DATASTORE_ADDR es:9200

ENV SQL_DB_ADDR db
ENV SQL_DB_PORT 3306

ENV EVENT_BROKER_URL=${EVENT_BROKER_URL:-amqp://events-broker:5672}
ENV EVENT_BROKER_USERNAME=${EVENT_BROKER_USERNAME:-kapua-sys}
ENV EVENT_BROKER_PASSWORD=${EVENT_BROKER_PASSWORD:-kapua-password}
ENV SERVICE_BROKER_URL=${SERVICE_BROKER_URL:-amqp://events-broker:5672}
ENV SERVICE_BROKER_USERNAME=${SERVICE_BROKER_USERNAME:-kapua-sys}
ENV SERVICE_BROKER_PASSWORD=${SERVICE_BROKER_PASSWORD:-kapua-password}

ENV JAVA_OPTS "-Dcommons.db.schema.update=true \
               -Dcommons.db.connection.host=\${SQL_DB_ADDR} \
               -Dcommons.db.connection.port=\${SQL_DB_PORT} \
               -Dlocator.guice.stage=PRODUCTION \
               -Dbroker.host=\${BROKER_ADDR} \
               -Ddatastore.elasticsearch.nodes=\${DATASTORE_ADDR} \
               -Dcommons.eventbus.url="${EVENT_BROKER_URL}" \
               -Dcommons.eventbus.username="${EVENT_BROKER_USERNAME}" \
               -Dcommons.eventbus.password="${EVENT_BROKER_PASSWORD}" \
               -Dcommons.servicebus.url="${SERVICE_BROKER_URL}" \
               -Dcommons.servicebus.username="${SERVICE_BROKER_USERNAME}" \
               -Dcommons.servicebus.password="${SERVICE_BROKER_PASSWORD}" \
               -Dcertificate.jwt.private.key=file:///etc/opt/kapua/key.pk8 \
               -Dcertificate.jwt.certificate=file:///etc/opt/kapua/cert.pem \
               -Ddatastore.disable=\${KAPUA_DISABLE_DATASTORE:-false} \
               -Djavax.xml.bind.JAXBContextFactory=org.eclipse.persistence.jaxb.JAXBContextFactory \
               -Dcipher.key=\${CIPHER_KEY} \
               -Dcrypto.secret.key=\${CRYPTO_SECRET_KEY} \
               \${JETTY_JMX_OPTS} \
               \${JETTY_DEBUG_OPTS}"

USER 0

RUN chown -R 1000:0 /opt/jetty /var/opt/jetty && \
    chmod -R g=u /opt/jetty /var/opt/jetty

USER 1000
