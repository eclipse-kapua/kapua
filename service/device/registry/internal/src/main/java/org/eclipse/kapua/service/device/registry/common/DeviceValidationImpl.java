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
 *     Red Hat
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.service.device.registry.common;

import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceExtendedProperty;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceRepository;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.eclipse.kapua.service.device.registry.internal.DeviceRegistryServiceImpl;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.storage.TxContext;

import com.google.common.base.Strings;

/**
 * Logic used to validate preconditions required to execute the {@link DeviceRegistryServiceImpl} operations.
 *
 * @since 1.0.0
 */
public final class DeviceValidationImpl implements DeviceValidation {

    private final Integer birthFieldsClobMaxLength;
    private final Integer birthFieldsExtendedPropertyValueMaxLength;
    private final AuthorizationService authorizationService;
    private final GroupService groupService;
    private final DeviceConnectionService deviceConnectionService;
    private final DeviceEventService deviceEventService;
    private final DeviceRepository deviceRepository;
    private final TagService tagService;

    public DeviceValidationImpl(
            Integer birthFieldsClobMaxLength,
            Integer birthFieldsExtendedPropertyValueMaxLength,
            AuthorizationService authorizationService,
            GroupService groupService,
            DeviceConnectionService deviceConnectionService,
            DeviceEventService deviceEventService,
            DeviceRepository deviceRepository,
            TagService tagService) {
        this.birthFieldsClobMaxLength = birthFieldsClobMaxLength;
        this.birthFieldsExtendedPropertyValueMaxLength = birthFieldsExtendedPropertyValueMaxLength;
        this.authorizationService = authorizationService;
        this.groupService = groupService;
        this.deviceConnectionService = deviceConnectionService;
        this.deviceEventService = deviceEventService;
        this.deviceRepository = deviceRepository;
        this.tagService = tagService;
    }

    /**
     * Validates the {@link DeviceCreator}.
     *
     * @param deviceCreator
     *         The {@link DeviceCreator} to validate.
     * @throws org.eclipse.kapua.KapuaIllegalArgumentException
     *         if one of the {@link DeviceCreator} fields is invalid.
     * @throws org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException
     *         if current {@link User} does not have sufficient {@link Permission}s
     * @throws KapuaException
     *         if there are other errors.
     * @since 1.0.0
     */
    @Override
    public void validateCreatePreconditions(DeviceCreator deviceCreator) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(deviceCreator, "deviceCreator");
        ArgumentValidator.notNull(deviceCreator.getScopeId(), "deviceCreator.scopeId");

        // .clientId
        ArgumentValidator.notEmptyOrNull(deviceCreator.getClientId(), "deviceCreator.clientId");
        ArgumentValidator.lengthRange(deviceCreator.getClientId(), 1, 255, "deviceCreator.clientId");
        ArgumentValidator.match(deviceCreator.getClientId(), DeviceValidationRegex.CLIENT_ID, "deviceCreator.clientId");

        // .groupId
        if (deviceCreator.getGroupId() != null) {
            ArgumentValidator.notNull(
                    KapuaSecurityUtils.doPrivileged(
                            () -> groupService.find(deviceCreator.getScopeId(), deviceCreator.getGroupId())
                    ), "deviceCreator.groupId");
        }

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
        // Check access
        authorizationService.checkPermission(new Permission(Domains.DEVICE, Actions.write, deviceCreator.getScopeId(), deviceCreator.getGroupId()));
    }

    /**
     * Validates the {@link Device} for {@link DeviceRegistryService#update(Object)} operation.
     *
     * @param device
     *         The {@link Device} to validate.
     * @throws org.eclipse.kapua.KapuaIllegalArgumentException
     *         if one of the {@link Device} fields is invalid.
     * @throws org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException
     *         if current {@link User} does not have sufficient {@link Permission}s
     * @throws KapuaException
     *         if there are other errors.
     * @since 1.0.0
     */
    @Override
    public void validateUpdatePreconditions(TxContext txContext, Device device) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(device, "device");
        ArgumentValidator.notNull(device.getScopeId(), "device.scopeId");
        ArgumentValidator.notNull(device.getId(), "device.id");

        // .clientId
        ArgumentValidator.notEmptyOrNull(device.getClientId(), "device.clientId");
        ArgumentValidator.lengthRange(device.getClientId(), 1, 255, "device.clientId");
        ArgumentValidator.match(device.getClientId(), DeviceValidationRegex.CLIENT_ID, "device.clientId");

        // .groupId
        // Check that current User can manage the current Group of the Device
        KapuaId currentGroupId = findCurrentGroupId(txContext, device.getScopeId(), device.getId());
        authorizationService.checkPermission(new Permission(Domains.DEVICE, Actions.write, device.getScopeId(), currentGroupId));

        // Check that current User can manage the target Group of the Device
        if (device.getGroupId() != null) {
            ArgumentValidator.notNull(
                    KapuaSecurityUtils.doPrivileged(
                            () -> groupService.find(device.getScopeId(), device.getGroupId())
                    ), "device.groupId");
        }
        authorizationService.checkPermission(new Permission(Domains.DEVICE, Actions.write, device.getScopeId(), device.getGroupId()));

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

        // .tagsIds
        for (KapuaId tagId : device.getTagIds()) {
            ArgumentValidator.notNull(
                    KapuaSecurityUtils.doPrivileged(
                            () -> tagService.find(device.getScopeId(), tagId))
                    , "device.tagsIds[].id"
            );
        }
    }

    /**
     * Validates the parameters for {@link DeviceRegistryService#find(KapuaId, KapuaId)} operation.
     *
     * @param scopeId
     *         The {@link Device#getScopeId()}
     * @param deviceId
     *         The {@link Device#getId()}
     * @throws org.eclipse.kapua.KapuaIllegalArgumentException
     *         if one of the parameters is invalid.
     * @throws org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException
     *         if current {@link User} does not have sufficient {@link Permission}s
     * @throws KapuaException
     *         if there are other errors.
     * @since 1.0.0
     */
    @Override
    public void validateFindPreconditions(TxContext txContext, KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(deviceId, "deviceId");
        // Check access
        KapuaId groupId = findCurrentGroupId(txContext, scopeId, deviceId);
        authorizationService.checkPermission(new Permission(Domains.DEVICE, Actions.read, scopeId, groupId));
    }

    /**
     * Validates the {@link KapuaQuery} for {@link DeviceRegistryService#query(KapuaQuery)} operation.
     *
     * @param query
     *         The {@link KapuaQuery} to validate.
     * @throws org.eclipse.kapua.KapuaIllegalArgumentException
     *         if one of the {@link KapuaQuery} fields is invalid.
     * @throws org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException
     *         if current {@link User} does not have sufficient {@link Permission}s
     * @throws KapuaException
     *         if there are other errors.
     * @since 1.0.0
     */
    @Override
    public void validateQueryPreconditions(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");

        // .fetchAttributes
        List<String> fetchAttributes = query.getFetchAttributes();
        if (fetchAttributes != null) {
            for (String fetchAttribute : fetchAttributes) {
                ArgumentValidator.match(fetchAttribute, DeviceValidationRegex.QUERY_FETCH_ATTRIBUTES, "fetchAttributes");
            }
        }
        // Check access
        authorizationService.checkPermission(new Permission(Domains.DEVICE, Actions.read, query.getScopeId(), Group.ANY));
    }

    /**
     * Validates the {@link KapuaQuery} for {@link DeviceRegistryService#count(KapuaQuery)} operation.
     *
     * @param query
     *         The {@link KapuaQuery} to validate.
     * @throws org.eclipse.kapua.KapuaIllegalArgumentException
     *         if one of the {@link KapuaQuery} fields is invalid.
     * @throws org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException
     *         if current {@link User} does not have sufficient {@link Permission}s
     * @throws KapuaException
     *         if there are other errors.
     * @since 1.0.0
     */
    @Override
    public void validateCountPreconditions(KapuaQuery query) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(query, "query");
        // Check access
        authorizationService.checkPermission(new Permission(Domains.DEVICE, Actions.read, query.getScopeId(), Group.ANY));
    }

    /**
     * Validates the parameters for {@link DeviceRegistryService#delete(KapuaId, KapuaId)} operation.
     *
     * @param scopeId
     *         The {@link Device#getScopeId()}
     * @param deviceId
     *         The {@link Device#getId()}
     * @throws org.eclipse.kapua.KapuaIllegalArgumentException
     *         if one of the parameters is invalid.
     * @throws org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException
     *         if current {@link User} does not have sufficient {@link Permission}s
     * @throws KapuaException
     *         if there are other errors.
     * @since 1.0.0
     */
    @Override
    public void validateDeletePreconditions(TxContext txContext, KapuaId scopeId, KapuaId deviceId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notNull(deviceId, "deviceId");
        // Check access
        KapuaId groupId = findCurrentGroupId(txContext, scopeId, deviceId);
        authorizationService.checkPermission(new Permission(Domains.DEVICE, Actions.delete, scopeId, groupId));
    }

    /**
     * Validates the parameters for {@link DeviceRegistryService#findByClientId(KapuaId, String)} operation.
     *
     * @param scopeId
     *         The {@link Device#getScopeId()}
     * @param clientId
     *         The {@link Device#getClientId()}
     * @throws org.eclipse.kapua.KapuaIllegalArgumentException
     *         if one of the parameters is invalid.
     * @throws org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException
     *         if current {@link User} does not have sufficient {@link Permission}s
     * @throws KapuaException
     *         if there are other errors.
     * @since 1.0.0
     */
    @Override
    public void validateFindByClientIdPreconditions(KapuaId scopeId, String clientId) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, KapuaEntityAttributes.SCOPE_ID);
        ArgumentValidator.notEmptyOrNull(clientId, "clientId");
        // Check access is performed by the query method.
    }

    /**
     * Finds the current {@link Group} id assigned to the given {@link Device#getId()}.
     *
     * @param scopeId
     *         The {@link Device#getScopeId()}
     * @param entityId
     *         The {@link Device#getId()}
     * @return The {@link Group} id found.
     * @throws KapuaException
     *         if any error occurs while looking for the Group.
     * @since 1.0.0
     */
    private KapuaId findCurrentGroupId(TxContext tx, KapuaId scopeId, KapuaId entityId) throws KapuaException {
        DeviceQuery query = new DeviceQuery(scopeId);
        query.setPredicate(query.attributePredicate(KapuaEntityAttributes.ENTITY_ID, entityId));

        DeviceListResult results;
        try {
            results = deviceRepository.query(tx, query);
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error while searching groupId");
        }

        KapuaId groupId = null;
        if (results != null && !results.isEmpty()) {
            groupId = results.getFirstItem().getGroupId();
        }

        return groupId;
    }
}
