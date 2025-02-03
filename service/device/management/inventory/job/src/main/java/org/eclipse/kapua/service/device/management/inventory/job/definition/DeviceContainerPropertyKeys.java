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
package org.eclipse.kapua.service.device.management.inventory.job.definition;

import org.eclipse.kapua.service.job.step.definition.JobPropertyKey;

public class DeviceContainerPropertyKeys implements JobPropertyKey {

    public static final String CONTAINER_INPUT = "containerInput";
    public static final String TIMEOUT = "timeout";

    private DeviceContainerPropertyKeys() {
    }
}
