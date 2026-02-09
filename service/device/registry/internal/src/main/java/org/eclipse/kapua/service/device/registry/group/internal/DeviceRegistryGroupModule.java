/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.registry.group.internal;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupFactory;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * @since 2.1.0
 */
public class DeviceRegistryGroupModule extends AbstractKapuaModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistryGroupModule.class);

    @Override
    protected void configureModule() {
        bind(DeviceGroupFactory.class).to(DeviceGroupFactoryImpl.class).in(Singleton.class);
    }

    @ProvidesIntoSet
    public Domain deviceDomain() {
        return new DomainEntry(Domains.DEVICE_GROUP, DeviceGroup.class.getName(), true, Actions.read, Actions.delete, Actions.write);
    }


    @Provides
    @Singleton
    DeviceGroupServiceValidationUtils deviceGroupServiceValidationUtils(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupService groupService
    ) {
        return new DeviceGroupServiceValidationUtilsImpl(
            authorizationService,
            permissionFactory,
            groupService
        );
    }

    @Provides
    @Singleton
    DeviceGroupService deviceGroupService(
            GroupService groupService,
            GroupFactory groupFactory,
            DeviceGroupServiceValidationUtils deviceGroupServiceValidationUtils
    ) {
        return new DeviceGroupServiceImpl(
            groupService,
            groupFactory,
            deviceGroupServiceValidationUtils
        );
    }
}