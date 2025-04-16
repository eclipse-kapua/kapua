/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.event.ServiceEventBusDriver;
import org.eclipse.kapua.commons.event.ServiceEventMarshaler;
import org.eclipse.kapua.commons.event.jms.JMSServiceEventBus;
import org.eclipse.kapua.commons.metric.CommonsMetric;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.event.ServiceEventBus;

import com.google.inject.Provides;

public class CommonsModuleEventBus extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
    }

    @Provides
    @Singleton
    ServiceEventBus serviceEventBus(ServiceEventBusDriver serviceEventBusDriver) {
        return serviceEventBusDriver.getEventBus();
    }

    @Provides
    @Singleton
    public ServiceEventBusDriver serviceEventBusDriver(SystemSetting systemSetting, CommonsMetric commonsMetric, ServiceEventMarshaler serviceEventMarshaler) {
        return new JMSServiceEventBus(systemSetting, commonsMetric, serviceEventMarshaler, "$SYS/EVT/%s");
    }

}
