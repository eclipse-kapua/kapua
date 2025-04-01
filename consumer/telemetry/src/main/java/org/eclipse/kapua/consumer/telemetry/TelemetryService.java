/*******************************************************************************
 * Copyright (c) 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.consumer.telemetry;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.client.message.MessageConstants;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("telemetryService")
public class TelemetryService {

    protected static final Logger logger = LoggerFactory.getLogger(TelemetryService.class);
    private final ServiceConfigurationManager serviceConfigurationManager;
    private final AccountService accountService;

    @Inject
    public TelemetryService(AccountService accountService, ServiceConfigurationManager serviceConfigurationManager) {
        this.accountService = accountService;
        this.serviceConfigurationManager = serviceConfigurationManager;
    }

    public boolean isDatastoreServiceEnabled(Exchange exchange) throws Exception {
        Account account = extractAccount(exchange);
        Map<String, Object> config = serviceConfigurationManager.getConfigValues(account.getId(), false);
        Boolean enabled = (Boolean)config.get("enabled");
        Integer ttl = (Integer)config.get("dataTTL");
        return Boolean.TRUE.equals(enabled) && (ttl!=null && ttl.intValue()!=MessageStoreConfiguration.DISABLED);
    }

    private Account extractAccount(Exchange exchange) throws KapuaException {
        String topic = exchange.getMessage().getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class);
        String accountName = extractAccountNameFromTopic(topic);
        Account account = accountService.findByName(accountName);
        if (account == null) {
            logger.warn("Cannot find account for account name {}", accountName);
            throw new KapuaIllegalArgumentException("account", "nill");
        }
        else {
            return account;
        }
    }

    private String extractAccountNameFromTopic(String topic) throws KapuaIllegalArgumentException {
        int index = topic.indexOf('/');
        if (index>0) {
            return topic.substring(0, index);
        }
        else {
            logger.warn("Cannot extract account name for topic {}", topic);
            throw new KapuaIllegalArgumentException("account_name", "N/A");
        }
    }
}