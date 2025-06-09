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
package org.eclipse.kapua.service.device.management.registry.operation.notification.internal;

import org.eclipse.kapua.commons.jpa.KapuaEntityJpaRepository;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotification;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationListResult;
import org.eclipse.kapua.service.device.management.registry.operation.notification.ManagementOperationNotificationRepository;

public class ManagementOperationNotificationImplJpaRepository
        extends KapuaEntityJpaRepository<ManagementOperationNotification, ManagementOperationNotificationImpl, ManagementOperationNotificationListResult>
        implements ManagementOperationNotificationRepository {

    public ManagementOperationNotificationImplJpaRepository(KapuaJpaRepositoryConfiguration configuration) {
        super(ManagementOperationNotificationImpl.class, ManagementOperationNotification.TYPE, () -> new ManagementOperationNotificationListResult(), configuration);
    }
}
