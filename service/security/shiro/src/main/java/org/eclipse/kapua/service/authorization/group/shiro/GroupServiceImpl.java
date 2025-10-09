/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.event.ServiceEvent;
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

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link GroupService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class GroupServiceImpl extends KapuaConfigurableServiceBase implements GroupService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final GroupServiceValidationUtils groupServiceValidationUtils;

    private final GroupRepository groupRepository;

    /**
     * Injectable constructor
     *
     * @param txManager
     * @param serviceConfigurationManager The {@link ServiceConfigurationManager} instance.
     * @param authorizationService The {@link AuthorizationService} instance.
     * @param permissionFactory The {@link PermissionFactory} instance.
     * @param groupRepository The {@link GroupRepository} instance.
     * @since 2.0.0
     */
    @Inject
    public GroupServiceImpl(
            TxManager txManager,
            ServiceConfigurationManager serviceConfigurationManager,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupServiceValidationUtils groupServiceValidationUtils,
            GroupRepository groupRepository
    ) {
        super(
            txManager,
            serviceConfigurationManager,
            Domains.GROUP,
            authorizationService,
            permissionFactory
        );

        this.groupServiceValidationUtils = groupServiceValidationUtils;
        this.groupRepository = groupRepository;
    }

    @Override
    public Group create(GroupCreator groupCreator) throws KapuaException {
        // Validate preconditions
        groupServiceValidationUtils.validateCreatePreconditions(groupCreator);

        // Do create
        return txManager.execute(tx -> {
            // Validate in-transaction conditions
            groupServiceValidationUtils.validateCreateInTransaction(tx, groupCreator);

            // Do create
            Group group = new GroupImpl(groupCreator.getScopeId());
            group.setName(groupCreator.getName());
            group.setDescription(groupCreator.getDescription());

            return groupRepository.create(tx, group);
        });
    }

    @Override
    public Group update(Group group) throws KapuaException {
        // Validate preconditions
        groupServiceValidationUtils.validateUpdatePreconditions(group);

        // Do update
        return txManager.execute(tx -> {
            // Validate in-transaction conditions
            groupServiceValidationUtils.validateUpdateInTransaction(tx, group);

            // Update
            return groupRepository.update(tx, group);
        });
    }



    @Override
    public Group find(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        // Validate preconditions
        groupServiceValidationUtils.validateFindPreconditions(scopeId, groupId);

        // Do find
        return txManager.execute(
            tx -> groupRepository
                .find(tx, scopeId, groupId))
                .orElse(null);
    }

    @Override
    public GroupListResult query(KapuaQuery query) throws KapuaException {
        // Validate preconditions
        groupServiceValidationUtils.validateQueryPreconditions(query);

        // Do query
        return txManager.execute(tx -> groupRepository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Validate preconditions
        groupServiceValidationUtils.validateCountPreconditions(query);

        // Do count
        return txManager.execute(tx -> groupRepository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        // Validate preconditions
        groupServiceValidationUtils.validateDeletePreconditions(scopeId, groupId);

        // Do delete
        txManager.execute(
            tx -> {
                // Validate in-transaction conditions
                groupServiceValidationUtils.validateDeleteInTransaction(tx, scopeId, groupId);

                // Delete
                groupRepository.delete(tx, scopeId, groupId);
                return null;
            }
        );
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
        GroupQuery query = new GroupQueryImpl(accountId);

        GroupListResult groupsToDelete = query(query);

        for (Group g : groupsToDelete.getItems()) {
            delete(g.getScopeId(), g.getId());
        }
    }
}
