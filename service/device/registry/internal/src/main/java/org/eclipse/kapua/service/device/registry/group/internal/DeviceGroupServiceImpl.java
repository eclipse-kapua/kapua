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

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.service.authorization.access.GroupQueryHelper;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupAttributes;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupRepository;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.device.registry.group.DeviceGroup;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupCreator;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupListResult;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupService;
import org.eclipse.kapua.storage.TxManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Collectors;

/**
 * {@link DeviceGroupService} implementation.
 *
 * @since 2.1.0
 */
@Singleton
public class DeviceGroupServiceImpl implements DeviceGroupService {

    private final TxManager txManager;
    private final GroupService groupService;
    private final GroupFactory groupFactory;
    private final GroupQueryHelper groupQueryHelper;
    private final DeviceGroupServiceValidationUtils deviceGroupServiceValidationUtils;

    /**
     * Injectable constructor
     *
     * @param groupService The {@link GroupRepository} instance.
     * @since 2.0.0
     */
    @Inject
    public DeviceGroupServiceImpl(
            TxManager txManager,
            GroupService groupService,
            GroupFactory groupFactory,
            GroupQueryHelper groupQueryHelper,
            DeviceGroupServiceValidationUtils deviceGroupServiceValidationUtils
    ) {
        this.txManager = txManager;
        this.groupService = groupService;
        this.groupFactory = groupFactory;
        this.groupQueryHelper = groupQueryHelper;
        this.deviceGroupServiceValidationUtils = deviceGroupServiceValidationUtils;
    }

    @Override
    public DeviceGroup create(DeviceGroupCreator deviceGroupCreator) throws KapuaException {
        // Validate preconditions
        deviceGroupServiceValidationUtils.validateCreatePreConditions(deviceGroupCreator);

        // Convert
        GroupCreator groupCreator = groupFactory.newCreator(deviceGroupCreator.getScopeId());
        groupCreator.setName(deviceGroupCreator.getName());
        groupCreator.setDescription(deviceGroupCreator.getDescription());
        groupCreator.setDomain(Domains.DEVICE);

        // Do create
        Group group = KapuaSecurityUtils.doPrivileged(() -> groupService.create(groupCreator));

        // Convert
        DeviceGroup deviceGroup = new DeviceGroupImpl(group.getScopeId());
        deviceGroup.setId(group.getId());
        deviceGroup.setTagIds(group.getTagIds());
        deviceGroup.setName(group.getName());
        deviceGroup.setDescription(group.getDescription());
        deviceGroup.setEntityAttributes(group.getEntityAttributes());
        deviceGroup.setEntityProperties(group.getEntityProperties());
        deviceGroup.setOptlock(group.getOptlock());

        // Return result
        return deviceGroup;
    }

    @Override
    public DeviceGroup update(DeviceGroup deviceGroup) throws KapuaException {
        // Validate preconditions
        deviceGroupServiceValidationUtils.validateUpdatePreConditions(deviceGroup);

        // Convert
        Group group = groupFactory.newEntity(deviceGroup.getScopeId());
        group.setId(deviceGroup.getId());
        group.setTagIds(deviceGroup.getTagIds());
        group.setName(deviceGroup.getName());
        group.setDomain(Domains.DEVICE);
        group.setDescription(deviceGroup.getDescription());
        group.setEntityAttributes(deviceGroup.getEntityAttributes());
        group.setEntityProperties(deviceGroup.getEntityProperties());
        group.setOptlock(deviceGroup.getOptlock());

        // Do update
        Group updatedGroup = KapuaSecurityUtils.doPrivileged(() -> groupService.update(group));

        // Convert
        DeviceGroup updatedDeviceGroup = new DeviceGroupImpl(updatedGroup.getScopeId());
        updatedDeviceGroup.setId(updatedGroup.getId());
        updatedDeviceGroup.setTagIds(updatedGroup.getTagIds());
        updatedDeviceGroup.setName(updatedGroup.getName());
        updatedDeviceGroup.setDescription(updatedGroup.getDescription());
        updatedDeviceGroup.setEntityAttributes(updatedGroup.getEntityAttributes());
        updatedDeviceGroup.setEntityProperties(updatedGroup.getEntityProperties());
        updatedDeviceGroup.setOptlock(updatedGroup.getOptlock());

        // Return result
        return updatedDeviceGroup;
    }

    @Override
    public DeviceGroup find(KapuaId scopeId, KapuaId deviceGroupId) throws KapuaException {
        // Validate preconditions
        deviceGroupServiceValidationUtils.validateFindPreConditions(scopeId, deviceGroupId);

        // Do find
        Group group = KapuaSecurityUtils.doPrivileged(() -> groupService.find(scopeId, deviceGroupId));

        if (!group.getDomain().equals(Domains.DEVICE)) {
            throw new KapuaEntityNotFoundException(DeviceGroup.TYPE, deviceGroupId);
        }

        // Convert
        DeviceGroup deviceGroup = new DeviceGroupImpl(group.getScopeId());
        deviceGroup.setId(group.getId());
        deviceGroup.setTagIds(group.getTagIds());
        deviceGroup.setName(group.getName());
        deviceGroup.setDescription(group.getDescription());
        deviceGroup.setEntityAttributes(group.getEntityAttributes());
        deviceGroup.setEntityProperties(group.getEntityProperties());
        deviceGroup.setOptlock(group.getOptlock());

        // Validate post-conditions
        deviceGroupServiceValidationUtils.validateFindPostConditions(deviceGroup);

        // Return result
        return deviceGroup;
    }

    @Override
    public DeviceGroupListResult query(KapuaQuery query) throws KapuaException {
        // Validate preconditions
        deviceGroupServiceValidationUtils.validateQueryPreConditions(query);

        // Convert
        GroupQuery groupQuery = groupFactory.newQuery(query.getScopeId());
        groupQuery.setAskTotalCount(query.getAskTotalCount());
        groupQuery.setLimit(query.getLimit());
        groupQuery.setOffset(query.getOffset());
        groupQuery.setSortCriteria(query.getSortCriteria());
        groupQuery.setFetchAttributes(query.getFetchAttributes());

        QueryPredicate queryPredicate;
        if (query.getPredicate() != null) {
            queryPredicate = groupQuery.andPredicate(
                groupQuery.attributePredicate(GroupAttributes.DOMAIN, Domains.DEVICE),
                query.getPredicate()
            );
        }
        else {
            queryPredicate = groupQuery.attributePredicate(GroupAttributes.DOMAIN, Domains.DEVICE);
        }

        groupQuery.setPredicate(queryPredicate);

        // Do query
        txManager.execute(tx -> {
            groupQueryHelper.handleGroupVisibility(Domains.DEVICE, groupQuery);
            return null;
        });

        GroupListResult groups = KapuaSecurityUtils.doPrivileged(() -> groupService.query(groupQuery));

        // Convert
        DeviceGroupListResult deviceGroups = new DeviceGroupListResultImpl();
        deviceGroups.setLimitExceeded(groups.isLimitExceeded());
        deviceGroups.setTotalCount(groups.getTotalCount());
        deviceGroups.addItems(
            groups.getItems()
                  .stream()
                  .map((group)-> {
                      DeviceGroup deviceGroup = new DeviceGroupImpl(group.getScopeId());
                      deviceGroup.setId(group.getId());
                      deviceGroup.setTagIds(group.getTagIds());
                      deviceGroup.setName(group.getName());
                      deviceGroup.setDescription(group.getDescription());
                      deviceGroup.setEntityAttributes(group.getEntityAttributes());
                      deviceGroup.setEntityProperties(group.getEntityProperties());
                      deviceGroup.setOptlock(group.getOptlock());

                      return deviceGroup;
                  }
            ).collect(Collectors.toList())
        );

        // Return result
        return deviceGroups;
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        // Validate preconditions
        deviceGroupServiceValidationUtils.validateCountPreConditions(query);

        // Convert
        GroupQuery groupQuery = groupFactory.newQuery(query.getScopeId());
        groupQuery.setAskTotalCount(query.getAskTotalCount());
        groupQuery.setLimit(query.getLimit());
        groupQuery.setOffset(query.getOffset());
        groupQuery.setSortCriteria(query.getSortCriteria());
        groupQuery.setFetchAttributes(query.getFetchAttributes());

        QueryPredicate queryPredicate;
        if (query.getPredicate() != null) {
            queryPredicate = groupQuery.andPredicate(
                    groupQuery.attributePredicate(GroupAttributes.DOMAIN, Domains.DEVICE),
                    query.getPredicate()
            );
        }
        else {
            queryPredicate = groupQuery.attributePredicate(GroupAttributes.DOMAIN, Domains.DEVICE);
        }

        groupQuery.setPredicate(queryPredicate);

        // Do count
        txManager.execute(tx -> {
            groupQueryHelper.handleGroupVisibility(Domains.DEVICE, groupQuery);
            return null;
        });

        return KapuaSecurityUtils.doPrivileged(() -> groupService.count(query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId deviceGroupId) throws KapuaException {
        // Validate preconditions
        deviceGroupServiceValidationUtils.validateDeletePreConditions(scopeId, deviceGroupId);

        // Do delete
        KapuaSecurityUtils.doPrivileged(() -> groupService.delete(scopeId, deviceGroupId));
    }
}
