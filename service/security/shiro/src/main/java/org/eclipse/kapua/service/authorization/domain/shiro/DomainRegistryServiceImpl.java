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
package org.eclipse.kapua.service.authorization.domain.shiro;

import java.util.Optional;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainCreator;
import org.eclipse.kapua.service.authorization.domain.DomainFactory;
import org.eclipse.kapua.service.authorization.domain.DomainListResult;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.domain.DomainRepository;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DomainRegistryService} implementation.
 *
 * @since 1.0
 */
@Singleton
public class DomainRegistryServiceImpl implements DomainRegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainRegistryServiceImpl.class);

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final TxManager txManager;
    private final DomainRepository domainRepository;
    private final DomainFactory domainFactory;

    public DomainRegistryServiceImpl(
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            TxManager txManager,
            DomainRepository domainRepository,
            DomainFactory domainFactory) {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.txManager = txManager;
        this.domainRepository = domainRepository;
        this.domainFactory = domainFactory;
    }

    @Override
    public Domain create(DomainCreator domainCreator)
            throws KapuaException {
        ArgumentValidator.notNull(domainCreator, "domainCreator");
        ArgumentValidator.notEmptyOrNull(domainCreator.getName(), "domainCreator.name");
        ArgumentValidator.notNull(domainCreator.getActions(), "domainCreator.actions");
        ArgumentValidator.notEmptyOrNull(domainCreator.getServiceName(), "domainCreator.serviceName");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DOMAIN, Actions.write, null));
        Domain domain = new DomainImpl();

        domain.setName(domainCreator.getName());
        domain.setServiceName(domainCreator.getServiceName());
        domain.setGroupable(domainCreator.getGroupable());

        if (domainCreator.getActions() != null) {
            domain.setActions(domainCreator.getActions());
        }

        return txManager.execute(tx -> domainRepository.create(tx, domain));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId domainId) throws KapuaException {
        ArgumentValidator.notNull(domainId, "domainId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DOMAIN, Actions.delete, null));

        txManager.execute(tx -> domainRepository.delete(tx, scopeId, domainId));
    }

    @Override
    public Domain find(KapuaId scopeId, KapuaId domainId)
            throws KapuaException {
        ArgumentValidator.notNull(domainId, "domainId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DOMAIN, Actions.read, KapuaId.ANY));

        return txManager.execute(tx -> domainRepository.find(tx, scopeId, domainId))
                .orElse(null);
    }

    @Override
    public Domain findByName(String name)
            throws KapuaException {

        ArgumentValidator.notNull(name, "name");
        // Do find
        final Optional<Domain> foundDomain = txManager.execute(tx -> domainRepository.findByName(tx, KapuaId.ANY, name));
        if (foundDomain.isPresent()) {
            authorizationService.checkPermission(permissionFactory.newPermission(Domains.DOMAIN, Actions.read, KapuaId.ANY));
        }
        return foundDomain
                .orElse(null);
    }

    @Override
    public DomainListResult query(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DOMAIN, Actions.read, KapuaId.ANY));

        return txManager.execute(tx -> domainRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DOMAIN, Actions.read, KapuaId.ANY));

        return txManager.execute(tx -> domainRepository.count(tx, query));
    }

    //@ListenServiceEvent(fromAddress="account")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }
        LOGGER.info("DomainRegistryService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteDomainByAccountId(kapuaEvent.getEntityId());
        }
    }

    private void deleteDomainByAccountId(KapuaId accountId) throws KapuaException {
        KapuaQuery query = new KapuaQuery(accountId);

        DomainListResult domainsToDelete = query(query);

        for (Domain d : domainsToDelete.getItems()) {
            delete(d.getScopeId(), d.getId());
        }
    }
}
