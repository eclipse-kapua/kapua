/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.connection.listener.internal;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.event.ListenServiceEvent;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.service.device.connection.listener.DeviceConnectionEventListenerService;
import org.eclipse.kapua.service.device.connection.listener.DeviceConnectionEventReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DeviceConnectionEventListenerServiceImpl implements DeviceConnectionEventListenerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConnectionEventListenerServiceImpl.class);

    protected ArrayList<DeviceConnectionEventReceiver> receivers = new ArrayList<>();

    @Inject
    public DeviceConnectionEventListenerServiceImpl() {
        LOGGER.info("Initializing {}", DeviceConnectionEventListenerServiceImpl.class);
    }

    @Override
    @ListenServiceEvent(fromAddress = "device")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        LOGGER.info("Received event: {}", kapuaEvent);

        for(DeviceConnectionEventReceiver receiver : receivers) {
            LOGGER.debug("Notifying receiver: {}", receiver);
            receiver.receiveEvent(kapuaEvent);
        }
    }

    @Override
    public void addReceiver(DeviceConnectionEventReceiver receiver) {
        LOGGER.info("Add receiver: {}", receiver);
        synchronized(receivers) {
            receivers.add(receiver);
        }
    }

    @Override
    public void removeReceiver(DeviceConnectionEventReceiver receiver) {
        LOGGER.info("Rmove receiver: {}", receiver);
        synchronized(receivers) {
            receivers.remove(receiver);
        }
    }

}
