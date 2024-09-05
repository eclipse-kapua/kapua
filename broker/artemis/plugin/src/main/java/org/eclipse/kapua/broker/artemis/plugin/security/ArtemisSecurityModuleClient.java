/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.broker.artemis.plugin.security;

import java.util.UUID;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.jms.JMSException;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.client.security.KapuaMessageListener;
import org.eclipse.kapua.client.security.amqpclient.Client;
import org.eclipse.kapua.client.security.amqpclient.ClientAMQP;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;

public class ArtemisSecurityModuleClient extends AbstractKapuaModule {

    private static final Logger logger = LoggerFactory.getLogger(ArtemisSecurityModuleClient.class);

    public static final String REQUEST_ADDRESS = "$SYS/SVC/auth/request";
    public static final String RESPONSE_ADDRESS_PATTERN = "$SYS/SVC/auth/response/%s_%s";

    @Override
    protected void configureModule() {
    }

    @Provides
    @Singleton
    @Named("clusterName")
    String clusterName(SystemSetting systemSetting) {
        return systemSetting.getString(SystemSettingKey.CLUSTER_NAME);
    }

    @Provides
    @Singleton
    @Named("brokerHost")
    String brokerHost(SystemSetting systemSetting) {
        return systemSetting.getString(SystemSettingKey.BROKER_HOST);
    }

    @Singleton
    @Provides
    @Named("serviceBusClient")
    public Client buildClient(
            SystemSetting systemSetting,
            @Named("clusterName") String clusterName,
            @Named("brokerHost") String brokerHost,
            KapuaMessageListener messageListener) {
        logger.info("building serviceBusClient...");
        //TODO change configuration (use service broker for now)
        String clientId = "svc-ath-" + UUID.randomUUID().toString();
        String host = systemSetting.getString(SystemSettingKey.SERVICE_BUS_HOST, "events-broker");
        int port = systemSetting.getInt(SystemSettingKey.SERVICE_BUS_PORT, 5672);
        String username = systemSetting.getString(SystemSettingKey.SERVICE_BUS_USERNAME, "username");
        String password = systemSetting.getString(SystemSettingKey.SERVICE_BUS_PASSWORD, "password");
        logger.info("Connecting auth service client to: {}:{}", host, port);
        try {
            return new ClientAMQP(username, password, host, port, clientId,
                REQUEST_ADDRESS,
                String.format(RESPONSE_ADDRESS_PATTERN, clusterName, brokerHost),
                messageListener);
        } catch (JMSException e) {
            throw new KapuaRuntimeException(KapuaErrorCodes.INTERNAL_ERROR, e, (Object[]) null);
        }
    }

}
