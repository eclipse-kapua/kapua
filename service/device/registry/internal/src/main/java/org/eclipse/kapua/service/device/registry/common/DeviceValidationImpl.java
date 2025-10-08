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
 *     Red Hat
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.common;

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.CheckStrategy;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupAttributes;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceExtendedProperty;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.internal.DeviceRegistryServiceImpl;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagAttributes;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagListResult;
import org.eclipse.kapua.service.tag.TagQuery;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.storage.TxContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Logic used to validate preconditions required to execute the {@link DeviceRegistryServiceImpl} operations.
 *
 * @since 1.0.0
 */
public final class DeviceValidationImpl implements DeviceValidation {

    private final Integer birthFieldsClobMaxLength;
    private final Integer birthFieldsExtendedPropertyValueMaxLength;
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final GroupService groupService;
    private final GroupFactory groupFactory;
    private final DeviceConnectionService deviceConnectionService;
    private final DeviceEventService deviceEventService;
    private final DeviceFactory deviceFactory;
    private final DeviceRepository deviceRepository;
    protected final ServiceConfigurationManager serviceConfigurationManager;
    private final TagService tagService;
    private final TagFactory tagFactory;

    public DeviceValidationImpl(
            Integer birthFieldsClobMaxLength,
            Integer birthFieldsExtendedPropertyValueMaxLength,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            GroupService groupService,
            GroupFactory groupFactory,
            DeviceConnectionService deviceConnectionService,
            DeviceEventService deviceEventService,
            DeviceFactory deviceFactory,
            DeviceRepository deviceRepository,
            ServiceConfigurationManager serviceConfigurationManager,
            TagService tagService,
            TagFactory tagFactory
    ) {
        this.birthFieldsClobMaxLength = birthFieldsClobMaxLength;
        this.birthFieldsExtendedPropertyValueMaxLength = birthFieldsExtendedPropertyValueMaxLength;
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.groupService = groupService;
        this.groupFactory = groupFactory;
        this.deviceConnectionService = deviceConnectionService;
        this.deviceEventService = deviceEventService;
        this.deviceRepository = deviceRepository;
        this.deviceFactory = deviceFactory;
        this.serviceConfigurationManager = serviceConfigurationManager;
        this.tagService = tagService;
        this.tagFactory = tagFactory;
    }

    @Override
    public void validateCreatePreconditions(DeviceCreator deviceCreator) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(deviceCreator, "deviceCreator");
        ArgumentValidator.notNull(deviceCreator.getScopeId(), "deviceCreator.scopeId");

        // .tagIds
        validateTagIds(deviceCreator.getScopeId(), deviceCreator.getTagIds());

        // .groupId
        validateDeviceCreatorGroupId(deviceCreator);

        // .groupIds
        validateDeviceCreatorGroupIds(deviceCreator);

        // .clientId
        ArgumentValidator.notEmptyOrNull(deviceCreator.getClientId(), "deviceCreator.clientId");
        ArgumentValidator.lengthRange(deviceCreator.getClientId(), 1, 255, "deviceCreator.clientId");
        ArgumentValidator.match(deviceCreator.getClientId(), DeviceValidationRegex.CLIENT_ID, "deviceCreator.clientId");

        // .status
        ArgumentValidator.notNull(deviceCreator.getStatus(), "deviceCreator.status");

        // .connectionId
        if (deviceCreator.getConnectionId() != null) {
            ArgumentValidator.notNull(
                    KapuaSecurityUtils.doPrivileged(
                            () -> deviceConnectionService.find(deviceCreator.getScopeId(), deviceCreator.getConnectionId())
                    ), "deviceCreator.connectionId");
        }

        // .lastEventId
        if (deviceCreator.getLastEventId() != null) {
            ArgumentValidator.notNull(
                    KapuaSecurityUtils.doPrivileged(
                            () -> deviceEventService.find(deviceCreator.getScopeId(), deviceCreator.getLastEventId())
                    ), "deviceCreator.lastEventId");
        }

        // .displayName
        if (!Strings.isNullOrEmpty(deviceCreator.getDisplayName())) {
            ArgumentValidator.lengthRange(deviceCreator.getDisplayName(), 1, 255, "deviceCreator.displayName");
        }

        // .serialNumber
        if (!Strings.isNullOrEmpty(deviceCreator.getSerialNumber())) {
            ArgumentValidator.lengthRange(deviceCreator.getSerialNumber(), 1, 255, "deviceCreator.serialNumber");
        }

        // .modelId
        if (!Strings.isNullOrEmpty(deviceCreator.getModelId())) {
            ArgumentValidator.lengthRange(deviceCreator.getModelId(), 1, 255, "deviceCreator.modelId");
        }

        // .modelName
        if (!Strings.isNullOrEmpty(deviceCreator.getModelName())) {
            ArgumentValidator.lengthRange(deviceCreator.getModelName(), 1, 255, "deviceCreator.modelName");
        }

        // .imei
        if (!Strings.isNullOrEmpty(deviceCreator.getImei())) {
            ArgumentValidator.lengthRange(deviceCreator.getImei(), 1, 24, "deviceCreator.imei");
        }

        // .imsi
        if (!Strings.isNullOrEmpty(deviceCreator.getImsi())) {
            ArgumentValidator.lengthRange(deviceCreator.getImsi(), 1, 15, "deviceCreator.imsi");
        }

        // .iccid
        if (!Strings.isNullOrEmpty(deviceCreator.getIccid())) {
            ArgumentValidator.lengthRange(deviceCreator.getIccid(), 1, 22, "deviceCreator.iccid");
        }

        // .biosVersion
        if (!Strings.isNullOrEmpty(deviceCreator.getBiosVersion())) {
            ArgumentValidator.lengthRange(deviceCreator.getBiosVersion(), 1, 255, "deviceCreator.biosVersion");
        }

        // .firmwareVersion
        if (!Strings.isNullOrEmpty(deviceCreator.getFirmwareVersion())) {
            ArgumentValidator.lengthRange(deviceCreator.getFirmwareVersion(), 1, 255, "deviceCreator.firmwareVersion");
        }

        // .osVersion
        if (!Strings.isNullOrEmpty(deviceCreator.getOsVersion())) {
            ArgumentValidator.lengthRange(deviceCreator.getOsVersion(), 1, 255, "deviceCreator.osVersion");
        }

        // .jvmVersion
        if (!Strings.isNullOrEmpty(deviceCreator.getJvmVersion())) {
            ArgumentValidator.lengthRange(deviceCreator.getJvmVersion(), 1, 255, "deviceCreator.jvmVersion");
        }

        // .osgiFrameworkVersion
        if (!Strings.isNullOrEmpty(deviceCreator.getOsgiFrameworkVersion())) {
            ArgumentValidator.lengthRange(deviceCreator.getOsgiFrameworkVersion(), 1, 255, "deviceCreator.osgiFrameworkVersion");
        }

        // .applicationFrameworkVersion
        if (!Strings.isNullOrEmpty(deviceCreator.getApplicationFrameworkVersion())) {
            ArgumentValidator.lengthRange(deviceCreator.getApplicationFrameworkVersion(), 1, 255, "deviceCreator.applicationFrameworkVersion");
        }

        // .connectionInterface
        if (!Strings.isNullOrEmpty(deviceCreator.getConnectionInterface())) {
            ArgumentValidator.lengthRange(deviceCreator.getConnectionInterface(), 1, birthFieldsClobMaxLength, "deviceCreator.connectionInterface");
        }

        // .connectionIp
        if (!Strings.isNullOrEmpty(deviceCreator.getConnectionIp())) {
            ArgumentValidator.lengthRange(deviceCreator.getConnectionIp(), 1, birthFieldsClobMaxLength, "deviceCreator.connectionIp");
        }

        // .applicationIdentifiers
        if (!Strings.isNullOrEmpty(deviceCreator.getApplicationIdentifiers())) {
            ArgumentValidator.lengthRange(deviceCreator.getApplicationIdentifiers(), 1, 1024, "deviceCreator.applicationIdentifiers");
        }

        // .acceptEncoding
        if (!Strings.isNullOrEmpty(deviceCreator.getAcceptEncoding())) {
            ArgumentValidator.lengthRange(deviceCreator.getAcceptEncoding(), 1, 255, "deviceCreator.acceptEncoding");
        }

        // .customAttribute1
        if (!Strings.isNullOrEmpty(deviceCreator.getCustomAttribute1())) {
            ArgumentValidator.lengthRange(deviceCreator.getCustomAttribute1(), 1, 255, "deviceCreator.customAttribute1");
        }

        // .customAttribute2
        if (!Strings.isNullOrEmpty(deviceCreator.getCustomAttribute2())) {
            ArgumentValidator.lengthRange(deviceCreator.getCustomAttribute2(), 1, 255, "deviceCreator.customAttribute2");
        }

        // .customAttribute3
        if (!Strings.isNullOrEmpty(deviceCreator.getCustomAttribute3())) {
            ArgumentValidator.lengthRange(deviceCreator.getCustomAttribute3(), 1, 255, "deviceCreator.customAttribute3");
        }

        // .customAttribute4
        if (!Strings.isNullOrEmpty(deviceCreator.getCustomAttribute4())) {
            ArgumentValidator.lengthRange(deviceCreator.getCustomAttribute4(), 1, 255, "deviceCreator.customAttribute4");
        }

        // .customAttribute5
        if (!Strings.isNullOrEmpty(deviceCreator.getCustomAttribute5())) {
            ArgumentValidator.lengthRange(deviceCreator.getCustomAttribute5(), 1, 255, "deviceCreator.customAttribute5");
        }

        // .extendedProperties
        for (DeviceExtendedProperty deviceExtendedProperty : deviceCreator.getExtendedProperties()) {
            // .groupName
            ArgumentValidator.notNull(deviceExtendedProperty.getGroupName(), "deviceCreator.extendedProperties[].groupName");
            ArgumentValidator.lengthRange(deviceExtendedProperty.getGroupName(), 1, 64, "deviceCreator.extendedProperties[].groupName");

            // .name
            ArgumentValidator.notNull(deviceExtendedProperty.getName(), "deviceCreator.extendedProperties[].name");
            ArgumentValidator.lengthRange(deviceExtendedProperty.getName(), 1, 64, "deviceCreator.extendedProperties[].name");

            // .value
            if (!Strings.isNullOrEmpty(deviceExtendedProperty.getValue())) {
                ArgumentValidator.lengthRange(deviceExtendedProperty.getValue(), 1, birthFieldsExtendedPropertyValueMaxLength, "deviceCreator.extendedProperties[].value");
            }
        }

        //
        // Check access

        // Check that current Subject can manage the target Group of the Device
        // authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.write, deviceCreator.getScopeId(), deviceCreator.getGroupId()));

        // Check that current Subject can manage all the target Groups
        Set<Permission> groupPermissions = buildSetPermissionsFromGroupIds(Domains.DEVICE, Actions.write, deviceCreator.getScopeId(), deviceCreator.getGroupIds());
        authorizationService.checkPermissions(groupPermissions);
    }

    @Override
    public void validateCreateInTransaction(TxContext tx, DeviceCreator deviceCreator) throws KapuaException {

        // Check entity limit
        serviceConfigurationManager.checkAllowedEntities(tx, deviceCreator.getScopeId(), "Devices");

        //
        // Check duplicates
        // .clientId
        if (deviceRepository.findByClientId(tx, deviceCreator.getScopeId(), deviceCreator.getClientId()).isPresent()) {
            throw new KapuaDuplicateNameException(deviceCreator.getClientId());
        }
    }

    @Override
    public void validateUpdatePreconditions(Device device) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(device, "device");
        ArgumentValidator.notNull(device.getScopeId(), "device.scopeId");
        ArgumentValidator.notNull(device.getId(), "device.id");

        // .groupId
        validateDeviceGroupId(device);

        // .groupIds
        validateDeviceGroupIds(device);

        // .tagIds
        validateTagIds(device.getScopeId(), device.getTagIds());

        // .clientId
        ArgumentValidator.notEmptyOrNull(device.getClientId(), "device.clientId");
        ArgumentValidator.lengthRange(device.getClientId(), 1, 255, "device.clientId");
        ArgumentValidator.match(device.getClientId(), DeviceValidationRegex.CLIENT_ID, "device.clientId");

        // .status
        ArgumentValidator.notNull(device.getStatus(), "device.status");

        // .connectionId
        if (device.getConnectionId() != null) {
            ArgumentValidator.notNull(
                    KapuaSecurityUtils.doPrivileged(
                            () -> deviceConnectionService.find(device.getScopeId(), device.getConnectionId())
                    ), "device.connectionId");
        }

        // .lastEventId
        if (device.getLastEventId() != null) {
            ArgumentValidator.notNull(
                    KapuaSecurityUtils.doPrivileged(
                            () -> deviceEventService.find(device.getScopeId(), device.getLastEventId())
                    ), "device.lastEventId");
        }

        // .displayName
        if (!Strings.isNullOrEmpty(device.getDisplayName())) {
            ArgumentValidator.lengthRange(device.getDisplayName(), 1, 255, "device.displayName");
        }

        // .serialNumber
        if (!Strings.isNullOrEmpty(device.getSerialNumber())) {
            ArgumentValidator.lengthRange(device.getSerialNumber(), 1, 255, "device.serialNumber");
        }

        // .modelId
        if (!Strings.isNullOrEmpty(device.getModelId())) {
            ArgumentValidator.lengthRange(device.getModelId(), 1, 255, "device.modelId");
        }

        // .modelName
        if (!Strings.isNullOrEmpty(device.getModelName())) {
            ArgumentValidator.lengthRange(device.getModelName(), 1, 255, "device.modelName");
        }

        // .imei
        if (!Strings.isNullOrEmpty(device.getImei())) {
            ArgumentValidator.lengthRange(device.getImei(), 1, 24, "device.imei");
        }

        // .imsi
        if (!Strings.isNullOrEmpty(device.getImsi())) {
            ArgumentValidator.lengthRange(device.getImsi(), 1, 15, "device.imsi");
        }

        // .iccid
        if (!Strings.isNullOrEmpty(device.getIccid())) {
            ArgumentValidator.lengthRange(device.getIccid(), 1, 22, "device.iccid");
        }

        // .biosVersion
        if (!Strings.isNullOrEmpty(device.getBiosVersion())) {
            ArgumentValidator.lengthRange(device.getBiosVersion(), 1, 255, "device.biosVersion");
        }

        // .firmwareVersion
        if (!Strings.isNullOrEmpty(device.getFirmwareVersion())) {
            ArgumentValidator.lengthRange(device.getFirmwareVersion(), 1, 255, "device.firmwareVersion");
        }

        // .osVersion
        if (!Strings.isNullOrEmpty(device.getOsVersion())) {
            ArgumentValidator.lengthRange(device.getOsVersion(), 1, 255, "device.osVersion");
        }

        // .jvmVersion
        if (!Strings.isNullOrEmpty(device.getJvmVersion())) {
            ArgumentValidator.lengthRange(device.getJvmVersion(), 1, 255, "device.jvmVersion");
        }

        // .osgiFrameworkVersion
        if (!Strings.isNullOrEmpty(device.getOsgiFrameworkVersion())) {
            ArgumentValidator.lengthRange(device.getOsgiFrameworkVersion(), 1, 255, "device.osgiFrameworkVersion");
        }

        // .applicationFrameworkVersion
        if (!Strings.isNullOrEmpty(device.getApplicationFrameworkVersion())) {
            ArgumentValidator.lengthRange(device.getApplicationFrameworkVersion(), 1, 255, "device.applicationFrameworkVersion");
        }

        // .connectionInterface
        if (!Strings.isNullOrEmpty(device.getConnectionInterface())) {
            ArgumentValidator.lengthRange(device.getConnectionInterface(), 1, birthFieldsClobMaxLength, "device.connectionInterface");
        }

        // .connectionIp
        if (!Strings.isNullOrEmpty(device.getConnectionIp())) {
            ArgumentValidator.lengthRange(device.getConnectionIp(), 1, birthFieldsClobMaxLength, "device.connectionIp");
        }

        // .applicationIdentifiers
        if (!Strings.isNullOrEmpty(device.getApplicationIdentifiers())) {
            ArgumentValidator.lengthRange(device.getApplicationIdentifiers(), 1, 1024, "device.applicationIdentifiers");
        }

        // .acceptEncoding
        if (!Strings.isNullOrEmpty(device.getAcceptEncoding())) {
            ArgumentValidator.lengthRange(device.getAcceptEncoding(), 1, 255, "device.acceptEncoding");
        }

        // .customAttribute1
        if (!Strings.isNullOrEmpty(device.getCustomAttribute1())) {
            ArgumentValidator.lengthRange(device.getCustomAttribute1(), 1, 255, "device.customAttribute1");
        }

        // .customAttribute2
        if (!Strings.isNullOrEmpty(device.getCustomAttribute2())) {
            ArgumentValidator.lengthRange(device.getCustomAttribute2(), 1, 255, "device.customAttribute2");
        }

        // .customAttribute3
        if (!Strings.isNullOrEmpty(device.getCustomAttribute3())) {
            ArgumentValidator.lengthRange(device.getCustomAttribute3(), 1, 255, "device.customAttribute3");
        }

        // .customAttribute4
        if (!Strings.isNullOrEmpty(device.getCustomAttribute4())) {
            ArgumentValidator.lengthRange(device.getCustomAttribute4(), 1, 255, "device.customAttribute4");
        }

        // .customAttribute5
        if (!Strings.isNullOrEmpty(device.getCustomAttribute5())) {
            ArgumentValidator.lengthRange(device.getCustomAttribute5(), 1, 255, "device.customAttribute5");
        }

        // .extendedProperties
        for (DeviceExtendedProperty deviceExtendedProperty : device.getExtendedProperties()) {
            // .groupName
            ArgumentValidator.notNull(deviceExtendedProperty.getGroupName(), "device.extendedProperties[].groupName");
            ArgumentValidator.lengthRange(deviceExtendedProperty.getGroupName(), 1, 64, "device.extendedProperties[].groupName");

            // .name
            ArgumentValidator.notNull(deviceExtendedProperty.getName(), "device.extendedProperties[].name");
            ArgumentValidator.lengthRange(deviceExtendedProperty.getName(), 1, 64, "device.extendedProperties[].name");

            // .value
            if (!Strings.isNullOrEmpty(deviceExtendedProperty.getValue())) {
                ArgumentValidator.lengthRange(deviceExtendedProperty.getValue(), 1, birthFieldsExtendedPropertyValueMaxLength, "device.extendedProperties[].value");
            }
        }

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.write, device.getScopeId(), Group.ANY));
    }

    @Override
    public void validateUpdateInTransaction(TxContext txContext, Device device) throws KapuaException {

        // .groupId
        // checkAccessDeviceGroupId(txContext, device);

        // .groupIds
        checkAccessDeviceGroupIds(txContext, device);
    }

    @Override
    public void validateFindPreconditions(KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(deviceId, "deviceId");

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.read, scopeId, Group.ANY));
    }

    @Override
    public void validateFindByClientIdPreconditions(KapuaId scopeId, String clientId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notEmptyOrNull(clientId, "clientId");

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.read, scopeId, Group.ANY));
    }

    @Override
    public void validateFindByFieldPostconditions(Device device) throws KapuaException {
        // Check access
        if (device != null) {
            Set<Permission> groupPermissions = buildSetPermissionsFromGroupIds(Domains.DEVICE, Actions.read, device.getScopeId(), device.getGroupIds());
            authorizationService.checkPermissions(groupPermissions, CheckStrategy.AT_LEAST_ONE_OF);
        }
    }

    @Override
    public void validateQueryPreconditions(KapuaQuery query) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(query, "query");

        //
        // .fetchAttributes
        List<String> fetchAttributes = query.getFetchAttributes();
        if (fetchAttributes != null) {
            for (String fetchAttribute : fetchAttributes) {
                ArgumentValidator.match(fetchAttribute, DeviceValidationRegex.QUERY_FETCH_ATTRIBUTES, "fetchAttributes");
            }
        }

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.read, query.getScopeId(), Group.ANY));
    }

    @Override
    public void validateCountPreconditions(KapuaQuery query) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.read, query.getScopeId(), Group.ANY));
    }

    @Override
    public void validateDeletePreconditions(KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(deviceId, "deviceId");

        //
        // Check access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.delete, scopeId, Group.ANY));
    }

    @Override
    public void validateDeleteInTransaction(TxContext txContext, KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        //
        // Check access
        // Check current Subject can delete from all Group Ids
        Set<KapuaId> groupIds = findCurrentGroupIds(txContext, scopeId, deviceId);
        Set<Permission> groupPermissions = buildSetPermissionsFromGroupIds(Domains.DEVICE, Actions.delete, scopeId, groupIds);
        authorizationService.checkPermissions(groupPermissions);
    }


    //
    // Private methods
    //

    // Groups validation

    /**
     * Finds the current {@link Group} id assigned to the given {@link Device}.
     *
     * @param scopeId  The {@link Device#getScopeId()}
     * @param deviceId The {@link Device#getId()}
     * @return The {@link Group} id found.
     * @throws KapuaException if any error occurs while looking for the Group.
     * @since 1.0.0
     */
    private KapuaId findCurrentGroupId(TxContext tx, KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        try {
            Optional<Device> optionalDevice = deviceRepository.find(tx, scopeId, deviceId);

            return optionalDevice
                    .map(Device::getGroupId)
                    .orElse(null);
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error while searching groupId");
        }
    }

    /**
     * Finds the current Set of {@link Group} ids assigned to the given {@link Device}.
     *
     * @param scopeId  The {@link Device#getScopeId()}
     * @param deviceId The {@link Device#getId()}
     * @return The Set of {@link Group} ids found.
     * @throws KapuaException if any error occurs while looking for the Group.
     * @since 2.1.0
     */
    private Set<KapuaId> findCurrentGroupIds(TxContext tx, KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        try {
            Optional<Device> optionalDevice = deviceRepository.find(tx, scopeId, deviceId);

            return optionalDevice
                    .map(Device::getGroupIds)
                    .orElse(Collections.emptySet());
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error while searching groupId");
        }
    }

    /**
     * Applies validation logics to {@link DeviceCreator#getGroupId()} attribute.
     * <p>
     * Requirements are:
     * <ul>
     *     <li>The Group defined must exists</li>
     * </ul>
     *
     * @param deviceCreator The {@link DeviceCreator} to check
     * @throws KapuaException
     * @since 2.1.0
     */
    private void validateDeviceCreatorGroupId(DeviceCreator deviceCreator) throws KapuaException {
        //
        // Check that target Group exist
        checkGroupExistence(deviceCreator.getScopeId(), deviceCreator.getGroupId());
    }

    /**
     * Applies validation logics to {@link DeviceCreator#getGroupIds()} attribute.
     * <p>
     * Requirements are:
     * <ul>
     *     <li>All Groups defined must exists</li>
     * </ul>
     *
     * @param deviceCreator The {@link DeviceCreator} to validate
     * @throws KapuaException
     * @since 2.1.0
     */
    private void validateDeviceCreatorGroupIds(DeviceCreator deviceCreator) throws KapuaException {
        if (!deviceCreator.getGroupIds().isEmpty()) {
            Set<KapuaId> groupIds = deviceCreator.getGroupIds();

            //
            // Check existence of all Groups
            GroupQuery groupQuery = groupFactory.newQuery(deviceCreator.getScopeId());
            groupQuery.setPredicate(groupQuery.attributePredicate(GroupAttributes.ENTITY_ID, groupIds));

            GroupListResult dbGroups = KapuaSecurityUtils.doPrivileged(() -> groupService.query(groupQuery));
            if (groupIds.size() != dbGroups.getSize()) {
                // Some groups have not been found
                Set<KapuaId> dbGroupIds =
                        dbGroups.getItems()
                                .stream()
                                .map(Group::getId)
                                .collect(Collectors.toSet());

                for (KapuaId groupId : groupIds) {
                    if (!dbGroupIds.contains(groupId)) {
                        throw new KapuaEntityNotFoundException(Group.TYPE, groupId);
                    }
                }
            }
        }
    }

    /**
     * Applies validation logics to {@link Device#getGroupId()} attribute.
     * <p>
     * Requirements are:
     * <ul>
     *     <li>device:write Permission on the current Group</li>
     *     <li>If Group is updated, new Group defined must exists</li>
     *     <li>If Group is updated, device:write Permission on new Group defined</li>
     * </ul>
     *
     * @param device The {@link Device} to check
     * @throws KapuaException
     * @since 2.1.0
     */
    private void validateDeviceGroupId(Device device) throws KapuaException {
        //
        // Check that target Group exist
        checkGroupExistence(device.getScopeId(), device.getGroupId());
    }

    private void checkAccessDeviceGroupId(TxContext txContext, Device device) throws KapuaException {
        //
        // Check that current Subject can manage the current Group of the Device
        KapuaId currentGroupId = findCurrentGroupId(txContext, device.getScopeId(), device.getId());
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.write, device.getScopeId(), currentGroupId));

        // If Group has been updated
        if (!Objects.equals(currentGroupId, device.getGroupId())) {
            //
            // Check that current Subject can manage the target Group of the Device
            authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE, Actions.write, device.getScopeId(), device.getGroupId()));
        }
    }

    private void validateDeviceGroupIds(Device device) throws KapuaException {
        if (!device.getGroupIds().isEmpty()) {
            Set<KapuaId> groupIds = device.getGroupIds();

            GroupQuery groupQuery = groupFactory.newQuery(device.getScopeId());
            groupQuery.setPredicate(groupQuery.attributePredicate(GroupAttributes.ENTITY_ID, groupIds));

            GroupListResult dbGroups = KapuaSecurityUtils.doPrivileged(() -> groupService.query(groupQuery));
            if (groupIds.size() != dbGroups.getSize()) {
                // Some groups have not been found
                Set<KapuaId> dbGroupIds =
                        dbGroups.getItems()
                                .stream()
                                .map(Group::getId)
                                .collect(Collectors.toSet());

                for (KapuaId groupId : groupIds) {
                    if (!dbGroupIds.contains(groupId)) {
                        throw new KapuaEntityNotFoundException(Group.TYPE, groupId);
                    }
                }
            }
        }
    }

    private void checkAccessDeviceGroupIds(TxContext txContext, Device device) throws KapuaException {
        //
        // Check that current User can manage at least one of the current Groups of the Device
        Set<KapuaId> currentGroupIds = findCurrentGroupIds(txContext, device.getScopeId(), device.getId());

        Set<Permission> currentGroupPermissions =
                currentGroupIds.stream()
                        .map(groupId -> permissionFactory.newPermission(Domains.DEVICE, Actions.write, device.getScopeId(), groupId))
                        .collect(Collectors.toSet());

        authorizationService.checkPermissions(currentGroupPermissions, CheckStrategy.AT_LEAST_ONE_OF);

        //
        // Check access to Groups that have been changed
        Set<KapuaId> updatedGroupIds = new HashSet<>();

        // Added groups - all Groups present in the updated Device but not on DB
        updatedGroupIds.addAll(
                device.getGroupIds()
                        .stream()
                        .filter(groupId -> !currentGroupIds.contains(groupId))
                        .collect(Collectors.toSet())
        );

        // Removed groups - all Groups not present in the updated Device but exists on DB
        updatedGroupIds.addAll(
                currentGroupIds
                        .stream()
                        .filter(currentGroupId1-> !device.getGroupIds().contains(currentGroupId1))
                        .collect(Collectors.toSet())
        );

        // If any of the Group have been changed, check authorization for those Groups
        if (!updatedGroupIds.isEmpty()) {
            Set<Permission> groupPermissions =
                    updatedGroupIds
                            .stream()
                            .map(groupId -> permissionFactory.newPermission(Domains.DEVICE, Actions.write, device.getScopeId(), groupId))
                            .collect(Collectors.toSet());

            authorizationService.checkPermissions(groupPermissions);
        }
    }


    /**
     * Checks that the given Group exists.
     *
     * @param scopeId The {@link Group#getScopeId()}
     * @param groupId The {@link Group#getId()}
     * @throws KapuaException
     * @since 2.1.0
     */
    private void checkGroupExistence(KapuaId scopeId, KapuaId groupId) throws KapuaException {
        if (groupId != null) {
            Group group = KapuaSecurityUtils.doPrivileged(() -> groupService.find(scopeId, groupId));

            if (group == null) {
                throw new KapuaEntityNotFoundException(Group.TYPE, groupId);
            }
        }
    }

    /**
     * Builds a set of {@link Permission} from a collection of Group Ids
     *
     * @param domain The {@link Permission#getDomain()}
     * @param action The {@link Permission#getAction()}
     * @param scopeId The {@link Permission#getTargetScopeId()}
     * @param groupIds The collection of Group Ids
     *
     * @return The set of {@link Permission}
     * @since 2.1.0
     */
    private Set<Permission> buildSetPermissionsFromGroupIds(String domain, Actions action, KapuaId scopeId, Collection<KapuaId> groupIds) {
        return groupIds.stream()
                .map(groupId -> permissionFactory.newPermission(domain, action, scopeId, groupId))
                .collect(Collectors.toSet());

    }

    // Tags validation

    /**
     * Applies validation logics to {@link Device#getTagIds()} and {@link DeviceCreator#getTagIds()} attribute.
     * <p>
     * Requirements are:
     * <ul>
     *     <li>The Tags defined must exists</li>
     * </ul>
     *
     * @param tagIds The {@link DeviceCreator} to check
     * @throws KapuaException
     * @since 2.1.0
     */
    private void validateTagIds(KapuaId scopeId, Collection<KapuaId> tagIds) throws KapuaException {
        // Look for Tags
        TagQuery tagQuery = tagFactory.newQuery(scopeId);
        tagQuery.setPredicate(tagQuery.attributePredicate(TagAttributes.ENTITY_ID, tagIds));
        TagListResult dbTags = KapuaSecurityUtils.doPrivileged(() -> tagService.query(tagQuery));

        // Match Tags found with given Tag IDs
        if (tagIds.size() != dbTags.getSize()) {
            // Some tags have not been found
            Set<KapuaId> dbTagsIds =
                    dbTags.getItems()
                            .stream()
                            .map(Tag::getId)
                            .collect(Collectors.toSet());

            for (KapuaId tagId : tagIds) {
                if (!dbTagsIds.contains(tagId)) {
                    throw new KapuaEntityNotFoundException(Tag.TYPE, tagId);
                }
            }
        }
    }
}
