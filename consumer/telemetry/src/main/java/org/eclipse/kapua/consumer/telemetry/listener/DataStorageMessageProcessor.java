/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.consumer.telemetry.listener;

import org.apache.camel.spi.UriEndpoint;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.camel.message.CamelKapuaMessage;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreCommunicationException;
import org.eclipse.kapua.service.device.management.asset.store.DeviceAssetStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data storage message listener
 *
 * @since 1.0
 */
@UriEndpoint(title = "Data storage message processor", syntax = "bean:dataMessageProcessor", scheme = "bean")
public class DataStorageMessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DataStorageMessageProcessor.class);

    private final MessageStoreService messageStoreService = KapuaLocator.getInstance().getService(MessageStoreService.class);

    private final DeviceAssetStoreService deviceAssetStoreService = KapuaLocator.getInstance().getService(DeviceAssetStoreService.class);

    /**
     * Process a data message.
     *
     * @throws KapuaException
     */
    public void processMessage(CamelKapuaMessage<?> message) throws KapuaException {
        // data messages
        LOG.debug("Received data message from device channel: client id '{}' - {}", message.getMessage().getClientId(), message.getMessage().getChannel());
        try {
            messageStoreService.store(message.getMessage(), message.getDatastoreId());
        } catch (DatastoreCommunicationException e) {
            message.setDatastoreId(e.getUuid());
            throw e;
        }
    }

    /**
     * Update asset
     * @param message
     * @throws KapuaException
     */
    public void updateAssets(CamelKapuaMessage<?> message) throws KapuaException {
        // Update asset values in AssetStoreService
        if (message.getMessage().getChannel().toString().startsWith("W1/A1")) {
            KapuaId scopeId = message.getMessage().getScopeId();
            KapuaId deviceId = message.getMessage().getDeviceId();
            if (deviceAssetStoreService.isServiceEnabled(scopeId) && deviceAssetStoreService.isApplicationEnabled(scopeId, deviceId)) {
                deviceAssetStoreService.storeAssetValues((KapuaDataMessage) message.getMessage());
            }
        } else {
            LOG.debug("This message did not matched W1/A1. Channel was: {}", message.getMessage().getChannel());
        }
    }

}
