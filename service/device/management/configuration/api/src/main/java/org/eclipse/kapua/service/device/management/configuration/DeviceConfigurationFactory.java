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
package org.eclipse.kapua.service.device.management.configuration;

import org.eclipse.kapua.model.KapuaObjectFactory;

/**
 * Device configuration entity service factory definition.
 *
 * @since 1.0
 */
public interface DeviceConfigurationFactory extends KapuaObjectFactory {

    /**
     * Creates a new {@link DeviceComponentConfiguration} using the given component configuration identifier
     *
     * @return
     */
    DeviceComponentConfiguration newComponentConfigurationInstance(String componentConfigurationId);

    /**
     * Creates a new {@link DeviceConfiguration}
     *
     * @return
     */
    DeviceConfiguration newConfigurationInstance();

    DeviceComponentConfigurationFactories newComponentConfigurationFactories();
}
