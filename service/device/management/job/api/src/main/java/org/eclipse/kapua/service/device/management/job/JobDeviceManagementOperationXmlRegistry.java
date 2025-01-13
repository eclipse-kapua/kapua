/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.job;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

/**
 * {@link JobDeviceManagementOperation} xml factory class
 *
 * @since 1.1.0
 */
@XmlRegistry
public class JobDeviceManagementOperationXmlRegistry {

    private final JobDeviceManagementOperationFactory jobDeviceManagementOperationFactory = KapuaLocator.getInstance().getFactory(JobDeviceManagementOperationFactory.class);

    public JobDeviceManagementOperation newJobDeviceManagementOperation() {
        return jobDeviceManagementOperationFactory.newEntity(null);
    }

    public JobDeviceManagementOperationCreator newJobDeviceManagementOperationCreator() {
        return jobDeviceManagementOperationFactory.newCreator(null);
    }

    public JobDeviceManagementOperationQuery newQuery() {
        return jobDeviceManagementOperationFactory.newQuery(null);
    }
}
