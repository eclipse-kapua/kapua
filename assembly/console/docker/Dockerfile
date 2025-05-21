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

ENV SERVICE_BROKER_ADDR amqp://events-broker:5672

ENV JOB_ENGINE_BASE_ADDR http://job-engine:8080/v1

ENV JAVA_OPTS "-Dcommons.db.schema.update=true \
               -Dlocator.guice.stage=PRODUCTION \
               -Dbroker.host=\${BROKER_ADDR} \
               -Ddatastore.elasticsearch.nodes=\${DATASTORE_ADDR} \
               -Dcommons.eventbus.url=\${SERVICE_BROKER_ADDR} \
               -Dcertificate.jwt.private.key=file:///etc/opt/kapua/key.pk8 \
               -Dcertificate.jwt.certificate=file:///etc/opt/kapua/cert.pem \
               -Ddatastore.disable=\${KAPUA_DISABLE_DATASTORE:-false} \
               -Djob.engine.base.url=\${JOB_ENGINE_BASE_ADDR} \
               -Djob.engine.client.auth.mode=access_token \
               -Dcipher.key=\${CIPHER_KEY} \
               -Dcrypto.secret.key=\${CRYPTO_SECRET_KEY} \
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

ENTRYPOINT /var/opt/jetty/run-console
