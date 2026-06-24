/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.configuration.internal;

import javax.inject.Singleton;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfigurationFactories;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;

/**
 * Device configuration entity service factory implementation.
 *
 * @since 1.0
 */
@Singleton
public class DeviceConfigurationFactoryImpl implements DeviceConfigurationFactory {

    @Override
    public DeviceComponentConfiguration newComponentConfigurationInstance(String componentConfigurationId) {
        return new DeviceComponentConfigurationImpl(componentConfigurationId);
    }

    @Override
    public DeviceConfiguration newConfigurationInstance() {
        return new DeviceConfigurationImpl();
    }

    @Override
    public DeviceComponentConfigurationFactories newComponentConfigurationFactories() {
        return new DeviceComponentConfigurationFactoriesImpl();
    }

}
