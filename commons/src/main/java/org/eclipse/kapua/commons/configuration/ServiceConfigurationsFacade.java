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
package org.eclipse.kapua.commons.configuration;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.eclipse.kapua.service.config.ServiceConfiguration;
import org.eclipse.kapua.storage.TxManager;

public interface ServiceConfigurationsFacade {

    ServiceConfiguration fetchAllConfigurations(KapuaId scopeId) throws KapuaException;

    public static class ServiceConfigurationsFacadeImpl implements ServiceConfigurationsFacade {

        private final Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass;
        //which one? Most likely move <class>org.eclipse.kapua.commons.configuration.ServiceConfigImpl</class> in its own persistence unit
        //This way we can abstract all services from the details of how and where the service configuration is stored (would be about time....)
        protected final TxManager txManager;
        protected final AuthorizationService authorizationService;
        protected final PermissionFactory permissionFactory;

        @Inject
        public ServiceConfigurationsFacadeImpl(Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass, TxManager txManager, AuthorizationService authorizationService,
                PermissionFactory permissionFactory) {
            this.serviceConfigurationManagersByServiceClass = serviceConfigurationManagersByServiceClass;
            this.txManager = txManager;
            this.authorizationService = authorizationService;
            this.permissionFactory = permissionFactory;
        }

        @Override
        public ServiceConfiguration fetchAllConfigurations(KapuaId scopeId) throws KapuaException {
            return txManager.execute(tx -> {
                final ServiceConfiguration res = new ServiceConfiguration();

                for (ServiceConfigurationManager configurableService : serviceConfigurationManagersByServiceClass.values()) {
                    //TODO: check permissions or skip. Have the ServiceConfigurationManager expose its own domain
                    if (!authorizationService.isPermitted(permissionFactory.newPermission("", Actions.read, scopeId))) {
                        continue;
                    }
                    res.getComponentConfigurations().add(configurableService.extractServiceComponentConfiguration(tx, scopeId));
                }
                Collections.sort(res.getComponentConfigurations(), Comparator.comparing(ServiceComponentConfiguration::getName));
                return res;
            });
        }
    }
}
