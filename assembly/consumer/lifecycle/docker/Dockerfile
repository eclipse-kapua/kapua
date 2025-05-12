###############################################################################
# Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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

ENV BROKER_HOST=${BROKER_HOST:-message-broker}
ENV BROKER_PORT=${BROKER_PORT:-5672}

ENV BROKER_INTERNAL_USERNAME=${BROKER_INTERNAL_USERNAME:-internalUsername}
ENV BROKER_INTERNAL_PASSWORD=${BROKER_INTERNAL_PASSWORD:-internalPassword}
ENV CONSUMER_HOST=${CONSUMER_HOST:-lifecycle}

ENV SQL_DB_PORT 3306

ENV EVENT_BROKER_URL=${EVENT_BROKER_URL:-amqp://events-broker:5672}
ENV EVENT_BROKER_USERNAME=${EVENT_BROKER_USERNAME:-kapua-sys}
ENV EVENT_BROKER_PASSWORD=${EVENT_BROKER_PASSWORD:-kapua-password}
ENV SERVICE_BROKER_URL=${SERVICE_BROKER_URL:-amqp://events-broker:5672}
ENV SERVICE_BROKER_USERNAME=${SERVICE_BROKER_USERNAME:-kapua-sys}
ENV SERVICE_BROKER_PASSWORD=${SERVICE_BROKER_PASSWORD:-kapua-password}
ENV JOB_ENGINE_BASE_ADDR http://job-engine:8080/v1

ENV JAVA_OPTS "-Dcommons.db.schema.update=true \
              -Dcommons.db.connection.port=\${SQL_DB_PORT} \
              -Dlocator.guice.stage=PRODUCTION \
              -Dbroker.host=\${BROKER_HOST} \
              -Dbroker.port=\${BROKER_PORT} \
              -Dcommons.eventbus.url="${EVENT_BROKER_URL}" \
              -Dcommons.eventbus.username="${EVENT_BROKER_USERNAME}" \
              -Dcommons.eventbus.password="${EVENT_BROKER_PASSWORD}" \
              -Dcommons.servicebus.url="${SERVICE_BROKER_URL}" \
              -Dcommons.servicebus.username="${SERVICE_BROKER_USERNAME}" \
              -Dcommons.servicebus.password="${SERVICE_BROKER_PASSWORD}" \
              -Dconsumer.jaxb_context_class_name=org.eclipse.kapua.consumer.lifecycle.LifecycleJAXBContextProvider \
              -Djob.engine.base.url=\${JOB_ENGINE_BASE_ADDR} \
              -Dcertificate.jwt.private.key=file:///etc/opt/kapua/key.pk8 \
              -Dcertificate.jwt.certificate=file:///etc/opt/kapua/cert.pem \
              -Dcipher.key=\${CIPHER_KEY} \
              -Dcrypto.secret.key=\${CRYPTO_SECRET_KEY}"

EXPOSE 8080

ENTRYPOINT /run-consumer-lifecycle
