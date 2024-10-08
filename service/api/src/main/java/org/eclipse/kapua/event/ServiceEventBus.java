/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.event;

/**
 * Service event bus definition.
 *
 * @since 1.0
 */
public interface ServiceEventBus {

    /**
     * Publish the event to the bus
     *
     * @param address address in which to publish the event
     * @param event   event to publish
     * @throws ServiceEventBusException
     */
    void publish(String address, ServiceEvent event) throws ServiceEventBusException;

    /**
     * Subscribe for a specific address event
     *
     * @param address       address to listen for events
     * @param name          subscriber name. It's used to share events between multiple instances of the same consumer.
     * @param serviceEventBusListener listener to invoke when an event is received
     * @throws ServiceEventBusException
     */
    void subscribe(String address, String name, ServiceEventBusListener serviceEventBusListener) throws ServiceEventBusException;
}
