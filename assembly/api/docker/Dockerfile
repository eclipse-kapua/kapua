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

ENV DATASTORE_ADDR es:9200

ENV EVENT_BROKER_URL=${EVENT_BROKER_URL:-amqp://events-broker:5672}
ENV EVENT_BROKER_USERNAME=${EVENT_BROKER_USERNAME:-kapua-sys}
ENV EVENT_BROKER_PASSWORD=${EVENT_BROKER_PASSWORD:-kapua-password}
ENV SERVICE_BROKER_URL=${SERVICE_BROKER_URL:-amqp://events-broker:5672}
ENV SERVICE_BROKER_USERNAME=${SERVICE_BROKER_USERNAME:-kapua-sys}
ENV SERVICE_BROKER_PASSWORD=${SERVICE_BROKER_PASSWORD:-kapua-password}

ENV JOB_ENGINE_BASE_ADDR http://job-engine:8080/v1

ENV JAVA_OPTS "-Dapi.cors.origins.allowed=\${API_CORS_ORIGINS_ALLOWED} \
               -Dcommons.db.schema.update=true \
               -Dlocator.guice.stage=PRODUCTION \
               -Dcommons.db.connection.host=\${SQL_DB_ADDR} \
               -Dcommons.db.connection.port=\${SQL_DB_PORT} \
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
               -Ddevice.management.response.stacktrace.show=\${DEVICE_MANAGEMENT_RESPONSE_STACKTRACE_SHOW:-false} \
               -Djavax.xml.bind.JAXBContextFactory=org.eclipse.persistence.jaxb.JAXBContextFactory \
               -Djob.engine.base.url=\${JOB_ENGINE_BASE_ADDR} \
               -Djob.engine.client.auth.mode=access_token \
               -Dcipher.key=\${CIPHER_KEY} \
               -Dcrypto.secret.key=\${CRYPTO_SECRET_KEY} \
               -Dauthentication.token.expire.after=\${AUTH_TOKEN_TTL} \
               -Dauthentication.refresh.token.expire.after=\${REFRESH_AUTH_TOKEN_TTL} \
               -Dapi.cors.refresh.interval=\${CORS_ENDPOINTS_REFRESH_INTERVAL} \
               -Dcommons.db.jdbc.database.target=\${DB_TARGET:-} \
               -Dcommons.db.connection.host=\${DB_HOST:-db} \
               -Dcommons.db.name=\${DB_NAME:-kapuadb} \
               -Dcommons.db.connection.port=\${DB_PORT:-3306} \
               -Dcommons.db.schema=\${DB_SCHEMA_NAME:-kapuadb} \
               -Dcommons.db.username=\${DB_USERNAME:-kapua} \
               -Dcommons.db.password=\${DB_PASSWORD:-kapua} \
               -Dcommons.db.connection.scheme=\${DB_CONNECTION_SCHEME:-jdbc:h2:tcp} \
               -Dcommons.db.jdbcConnectionUrlResolver=\${DB_RESOLVER:-DEFAULT} \
               -Dcommons.db.jdbc.driver=\${DB_DRIVER:-org.h2.Driver} \
               -Dcommons.db.connection.additionalOptions=\${DB_CONNECTION_ADDITIONAL_OPTIONS} \
               \${JETTY_DEBUG_OPTS} \
               \${JETTY_JMX_OPTS}"

USER 0

RUN chown -R 1000:0 /opt/jetty /var/opt/jetty && \
    chmod -R g=u /opt/jetty /var/opt/jetty

USER 1000

ENTRYPOINT /var/opt/jetty/run-api
