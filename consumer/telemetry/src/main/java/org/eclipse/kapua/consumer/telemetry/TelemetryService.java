/*******************************************************************************
 * Copyright (c) 2025, 2026 Eurotech and/or its affiliates and others
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

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.consumer.telemetry.util.ServiceStatusChecker;
import org.eclipse.kapua.service.client.message.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("telemetryService")
public class TelemetryService {

    protected static final Logger logger = LoggerFactory.getLogger(TelemetryService.class);

    private final ServiceStatusChecker serviceStatusChecker;

    @Inject
    public TelemetryService(ServiceStatusChecker serviceStatusChecker) {
        this.serviceStatusChecker = serviceStatusChecker;
    }

    public void setServiceEnabledLevel(Exchange exchange) throws Exception {
        String accountName = extractAccountNameFromTopic(exchange.getMessage().getHeader(MessageConstants.PROPERTY_ORIGINAL_TOPIC, String.class));
        exchange.getMessage().setHeader("serviceMessageAndAssetEnabled", serviceStatusChecker.getServiceEnabledLevel(exchange, accountName));
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