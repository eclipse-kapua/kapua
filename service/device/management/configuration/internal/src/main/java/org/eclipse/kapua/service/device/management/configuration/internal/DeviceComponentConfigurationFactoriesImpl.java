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
package org.eclipse.kapua.service.device.management.configuration.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfigurationFactories;

public class DeviceComponentConfigurationFactoriesImpl implements DeviceComponentConfigurationFactories {

    private final List<String> ids = new ArrayList<>();

    @Override
    public List<String> getIds() {
        return ids;
    }
}
