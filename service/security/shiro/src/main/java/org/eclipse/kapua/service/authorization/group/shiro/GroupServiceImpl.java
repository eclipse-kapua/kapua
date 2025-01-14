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
package org.eclipse.kapua.service.authorization.group.shiro;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupRepository;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link GroupService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class GroupServiceImpl extends KapuaConfigurableServiceBase implements GroupService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupServiceImpl.class);
    private final PermissionFactory permissionFactory;
    private final AuthorizationService authorizationService;
    private final TxManager txManager;
    private final GroupRepository groupRepository;

    /**
     * Injectable constructor
     *
     * @param permissionFactory
     *         The {@link PermissionFactory} instance.
     * @param authorizationService
     *         The {@link AuthorizationService} instance.
     * @param serviceConfigurationManager
     *         The {@link ServiceConfigurationManager} instance.
     * @param txManager
     * @param groupRepository
     * @since 2.0.0
     */
    @Inject
    public GroupServiceImpl(PermissionFactory permissionFactory,
            AuthorizationService authorizationService,
            ServiceConfigurationManager serviceConfigurationManager,
            TxManager txManager, GroupRepository groupRepository) {
        super(txManager, serviceConfigurationManager, Domains.GROUP, authorizationService, permissionFactory);
        this.permissionFactory = permissionFactory;
        this.authorizationService = authorizationService;
        this.txManager = txManager;
        this.groupRepository = groupRepository;
    }

    @Override
    public Group create(GroupCreator groupCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(groupCreator, "groupCreator");
        ArgumentValidator.notNull(groupCreator.getScopeId(), "roleCreator.scopeId");
        ArgumentValidator.validateEntityName(groupCreator.getName(), "groupCreator.name");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.write, groupCreator.getScopeId()));
        return txManager.execute(tx -> {
            // Check entity limit
            serviceConfigurationManager.checkAllowedEntities(tx, groupCreator.getScopeId(), "Groups");
            // Do create
            Group group = new GroupImpl(groupCreator.getScopeId());
            group.setName(groupCreator.getName());
            group.setDescription(groupCreator.getDescription());
            // Check duplicate name
            if (groupRepository.countEntitiesWithNameInScope(tx, group.getScopeId(), group.getName()) > 0) {
                throw new KapuaDuplicateNameException(group.getName());
            }
            return groupRepository.create(tx, group);
        });
    }

    @Override
    public Group update(Group group) throws KapuaException {
        // Argument validator
        ArgumentValidator.notNull(group, "group");
        ArgumentValidator.notNull(group.getId(), "group.id");
        ArgumentValidator.notNull(group.getScopeId(), "group.scopeId");
        ArgumentValidator.validateEntityName(group.getName(), "group.name");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.write, group.getScopeId()));
        return txManager.execute(tx -> {
            // Check existence
            if (!groupRepository.find(tx, group.getScopeId(), group.getId()).isPresent()) {
                throw new KapuaEntityNotFoundException(Group.TYPE, group.getId());
            }
            // Do update
            // Check duplicate name
            if (groupRepository.countOtherEntitiesWithNameInScope(tx, group.getScopeId(), group.getId(), group.getName()) > 0) {
                throw new KapuaDuplicateNameException(group.getName());
            }
            return groupRepository.update(tx, group);
        });
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(groupId, "groupId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.delete, scopeId));

        txManager.execute(tx -> groupRepository.delete(tx, scopeId, groupId));
    }

    @Override
    public Group find(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(groupId, "groupId");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, scopeId));
        // Do find
        return txManager.execute(tx -> groupRepository.find(tx, scopeId, groupId))
                .orElse(null);
    }

    @Override
    public GroupListResult query(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()));
        // Do query
        return txManager.execute(tx -> groupRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.GROUP, Actions.read, query.getScopeId()));
        // Do count
        return txManager.execute(tx -> groupRepository.count(tx, query));
    }

    //@ListenServiceEvent(fromAddress="account")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }

        LOG.info("GroupService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteGroupByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }

    private void deleteGroupByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        GroupQuery query = new GroupQuery(accountId);

        GroupListResult groupsToDelete = query(query);

        for (Group g : groupsToDelete.getItems()) {
            delete(g.getScopeId(), g.getId());
        }
    }
}
