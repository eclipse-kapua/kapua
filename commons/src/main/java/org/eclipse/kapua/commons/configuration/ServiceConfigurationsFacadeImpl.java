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

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.eclipse.kapua.service.config.ServiceConfiguration;

public class ServiceConfigurationsFacadeImpl implements ServiceConfigurationsFacade {

    private final Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass;
    private final Map<String, ServiceConfigurationManager> serviceConfigurationManagersByServiceClassName;
    protected final AuthorizationService authorizationService;
    protected final PermissionFactory permissionFactory;
    protected final AccountService accountService;

    @Inject
    public ServiceConfigurationsFacadeImpl(Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass, AuthorizationService authorizationService,
            PermissionFactory permissionFactory, AccountService accountService) {
        this.serviceConfigurationManagersByServiceClass = serviceConfigurationManagersByServiceClass;
        this.serviceConfigurationManagersByServiceClassName =
                serviceConfigurationManagersByServiceClass.entrySet().stream().collect(Collectors.toMap(kv -> kv.getKey().getName(), kv -> kv.getValue()));
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.accountService = accountService;
    }

    @Override
    public ServiceConfiguration fetchAllConfigurations(KapuaId scopeId) throws KapuaException {
        final ServiceConfiguration res = new ServiceConfiguration();

        for (ServiceConfigurationManager configurableService : serviceConfigurationManagersByServiceClass.values()) {
            if (!authorizationService.isPermitted(permissionFactory.newPermission(configurableService.getDomain(), Actions.read, scopeId))) {
                continue;
            }
            configurableService.extractServiceComponentConfiguration(scopeId).ifPresent(res.getComponentConfigurations()::add);
        }
        return res;
    }

    @Override
    public ServiceComponentConfiguration fetchConfiguration(KapuaId scopeId, String serviceId) throws KapuaException {
        final ServiceConfigurationManager serviceConfigurationManager = serviceConfigurationManagersByServiceClassName.get(serviceId);
        if (serviceConfigurationManager == null) {
            throw new KapuaIllegalArgumentException("service.pid", serviceId);
        }
        authorizationService.checkPermission(permissionFactory.newPermission(serviceConfigurationManager.getDomain(), Actions.read, scopeId));
        return serviceConfigurationManager.extractServiceComponentConfiguration(scopeId).orElse(null);
    }

    @Override
    public void update(KapuaId scopeId, ServiceConfiguration newServiceConfiguration) throws KapuaException {
        final Account account = accountService.find(scopeId);

        for (ServiceComponentConfiguration newServiceComponentConfiguration : newServiceConfiguration.getComponentConfigurations()) {
            doUpdateServiceComponentConfiguration(account, scopeId, newServiceComponentConfiguration.getId(), newServiceComponentConfiguration);
        }
    }

    @Override
    public void update(KapuaId scopeId, String serviceId, ServiceComponentConfiguration newServiceComponentConfiguration) throws KapuaException {
        final Account account = accountService.find(scopeId);
        doUpdateServiceComponentConfiguration(account, scopeId, serviceId, newServiceComponentConfiguration);
    }

    private void doUpdateServiceComponentConfiguration(Account account, KapuaId scopeId, String serviceId, ServiceComponentConfiguration newServiceComponentConfiguration) throws KapuaException {
        final ServiceConfigurationManager serviceConfigurationManager = serviceConfigurationManagersByServiceClassName.get(serviceId);
        if (serviceConfigurationManager == null) {
            throw new KapuaIllegalArgumentException("serviceConfiguration.componentConfiguration.id", newServiceComponentConfiguration.getId());
        }
        if (!authorizationService.isPermitted(permissionFactory.newPermission(serviceConfigurationManager.getDomain(), Actions.write, scopeId))) {
            //TODO: Or maybe throw?
            return;
        }
        serviceConfigurationManager.setConfigValues(scopeId, Optional.ofNullable(account.getScopeId()), newServiceComponentConfiguration.getProperties());
    }
}
