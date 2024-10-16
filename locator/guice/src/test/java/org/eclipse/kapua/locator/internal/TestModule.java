/*******************************************************************************
 * Copyright (c) 2017, 2022 Red Hat Inc and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.locator.internal;

import com.google.inject.Provides;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.event.ServiceEventBusDriver;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.event.ServiceEventBusException;
import org.eclipse.kapua.event.Subscription;

import javax.inject.Singleton;

public class TestModule extends AbstractKapuaModule {
    @Override
    protected void configureModule() {

    }

    @Provides
    @Singleton
    ServiceEventBusDriver serviceEventBusDriver() {
        return new ServiceEventBusDriver() {

            private Boolean connected = Boolean.FALSE;

            @Override
            public String getType() {
                return "test";
            }

            @Override
            public void start() throws ServiceEventBusException {
                connected = Boolean.TRUE;
            }

            @Override
            public void stop() throws ServiceEventBusException {
                connected = Boolean.FALSE;
            }

            @Override
            public ServiceEventBus getEventBus() {
                return new ServiceEventBus() {
                    @Override
                    public void publish(String address, ServiceEvent event) throws ServiceEventBusException {
                        //Nothing to do!
                    }

                    @Override
                    public void subscribe(Subscription subscription) throws ServiceEventBusException {
                        //Nothing to do!
                    }
                };
            }

            @Override
            public Boolean isConnected() {
                return connected;
            }
        };
    }
}
