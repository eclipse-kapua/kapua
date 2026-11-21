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
package org.eclipse.kapua.service.device.registry.connection.internal;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.KapuaConfigurableServiceBase;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.RaiseServiceEvent;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.device.authentication.api.DeviceConnectionCredentialAdapter;
import org.eclipse.kapua.service.device.registry.common.DeviceValidationRegex;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionAttributes;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionCreator;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionListResult;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionRepository;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DeviceConnectionService} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class DeviceConnectionServiceImpl extends KapuaConfigurableServiceBase implements DeviceConnectionService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceConnectionServiceImpl.class);

    private final DeviceConnectionFactory entityFactory;
    private final DeviceConnectionRepository repository;
    private final Map<String, DeviceConnectionCredentialAdapter> availableDeviceConnectionAdapters;
    private final EventStorer eventStorer;

    /**
     * Constructor.
     *
     * @since 2.0.0
     */
    @Inject
    public DeviceConnectionServiceImpl(
            ServiceConfigurationManager serviceConfigurationManager,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DeviceConnectionFactory entityFactory,
            @Named("DeviceRegistryTransactionManager") TxManager txManager,
            DeviceConnectionRepository repository,
            Map<String, DeviceConnectionCredentialAdapter> availableDeviceConnectionAdapters,
            EventStorer eventStorer
    ) {
        super(
            txManager,
            serviceConfigurationManager,
            Domains.DEVICE_CONNECTION,
            authorizationService,
            permissionFactory
        );
        this.entityFactory = entityFactory;
        this.repository = repository;
        this.availableDeviceConnectionAdapters = availableDeviceConnectionAdapters;
        this.eventStorer = eventStorer;
    }

    @Override
    public DeviceConnection create(DeviceConnectionCreator deviceConnectionCreator)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(deviceConnectionCreator, "deviceConnectionCreator");
        ArgumentValidator.notNull(deviceConnectionCreator.getScopeId(), "deviceConnectionCreator.scopeId");
        ArgumentValidator.notNull(deviceConnectionCreator.getStatus(), "deviceConnectionCreator.status");
        ArgumentValidator.notEmptyOrNull(deviceConnectionCreator.getClientId(), "deviceConnectionCreator.clientId");
        ArgumentValidator.lengthRange(deviceConnectionCreator.getClientId(), 1, 255, "deviceCreator.clientId");
        ArgumentValidator.match(deviceConnectionCreator.getClientId(), DeviceValidationRegex.CLIENT_ID, "deviceCreator.clientId");
        ArgumentValidator.notNull(deviceConnectionCreator.getUserId(), "deviceConnectionCreator.userId");
        ArgumentValidator.notNull(deviceConnectionCreator.getUserCouplingMode(), "deviceConnectionCreator.userCouplingMode");
        ArgumentValidator.notNull(deviceConnectionCreator.getAuthenticationType(), "deviceConnectionCreator.authenticationType");
        if (!availableDeviceConnectionAdapters.containsKey(deviceConnectionCreator.getAuthenticationType())) {
            throw new KapuaIllegalArgumentException("deviceConnectionCreator.authenticationType", deviceConnectionCreator.getAuthenticationType());
        }
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_CONNECTION, Actions.write, null));
        return txManager.execute(tx -> {
            //TODO: check whether this is anywhere efficient
            // Check duplicate ClientId
            if (repository.countByClientId(tx, deviceConnectionCreator.getScopeId(), deviceConnectionCreator.getClientId()) > 0) {
                throw new KapuaDuplicateNameException(deviceConnectionCreator.getClientId());
            }
            final DeviceConnection deviceConnection = entityFactory.newEntity(deviceConnectionCreator.getScopeId());
            deviceConnection.setStatus(deviceConnectionCreator.getStatus());
            deviceConnection.setClientId(deviceConnectionCreator.getClientId());
            deviceConnection.setUserId(deviceConnectionCreator.getUserId());
            deviceConnection.setUserCouplingMode(deviceConnectionCreator.getUserCouplingMode());
            deviceConnection.setAllowUserChange(deviceConnectionCreator.getAllowUserChange());
            deviceConnection.setReservedUserId(deviceConnectionCreator.getReservedUserId());
            deviceConnection.setAuthenticationType(deviceConnectionCreator.getAuthenticationType());
            deviceConnection.setLastAuthenticationType(deviceConnectionCreator.getLastAuthenticationType());
            deviceConnection.setProtocol(deviceConnectionCreator.getProtocol());
            deviceConnection.setClientIp(deviceConnectionCreator.getClientIp());
            deviceConnection.setServerIp(deviceConnectionCreator.getServerIp());
            // Do create
            return repository.create(tx, deviceConnection);
        });

    }

    @Override
    public DeviceConnection update(DeviceConnection deviceConnection)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(deviceConnection, "deviceConnection");
        ArgumentValidator.notNull(deviceConnection.getId(), "deviceConnection.id");
        ArgumentValidator.notNull(deviceConnection.getScopeId(), "deviceConnection.scopeId");
        ArgumentValidator.notNull(deviceConnection.getStatus(), "deviceConnection.status");
        ArgumentValidator.notNull(deviceConnection.getUserId(), "deviceConnection.userId");
        ArgumentValidator.notNull(deviceConnection.getUserCouplingMode(), "deviceConnection.userCouplingMode");
        ArgumentValidator.notNull(deviceConnection.getAuthenticationType(), "deviceConnection.authenticationType");
        if (!availableDeviceConnectionAdapters.containsKey(deviceConnection.getAuthenticationType())) {
            throw new KapuaIllegalArgumentException("deviceConnection.authenticationType", deviceConnection.getAuthenticationType());
        }
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_CONNECTION, Actions.write, null));
        // Do Update
        return txManager.execute(tx -> repository.update(tx, deviceConnection));
    }

    @Override
    public DeviceConnection find(KapuaId scopeId, KapuaId entityId)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(entityId, "entityId");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_CONNECTION, Actions.read, scopeId));

        // Do find
        return txManager.execute(tx -> repository.find(tx, scopeId, entityId))
                .orElse(null);
    }

    @Override
    public DeviceConnection findByClientId(KapuaId scopeId, String clientId)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notEmptyOrNull(clientId, "clientId");

        // Build query
        DeviceConnectionQueryImpl query = new DeviceConnectionQueryImpl(scopeId);
        query.setPredicate(query.attributePredicate(DeviceConnectionAttributes.CLIENT_ID, clientId));

        // Do find
        return txManager.execute(tx -> repository.query(tx, query).getFirstItem());
    }

    @Override
    public DeviceConnectionListResult query(KapuaQuery query)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_CONNECTION, Actions.read, query.getScopeId()));

        // Do query
        return txManager.execute(tx -> repository.query(tx, query));
    }

    @Override
    public long count(KapuaQuery query)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_CONNECTION, Actions.read, query.getScopeId()));

        // Do count
        return txManager.execute(tx -> repository.count(tx, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId deviceConnectionId)
            throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(deviceConnectionId, "deviceConnection.id");
        ArgumentValidator.notNull(scopeId, "deviceConnection.scopeId");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_CONNECTION, Actions.write, null));

        txManager.execute(tx -> repository.delete(tx, scopeId, deviceConnectionId));
    }

    @Override
    public void connect(DeviceConnectionCreator creator)
            throws KapuaException {
        throw new NotImplementedException();
    }

    @Override
    public void disconnect(KapuaId scopeId, String clientId)
            throws KapuaException {
        throw new NotImplementedException();
    }

    @Override
    @RaiseServiceEvent
    public void disconnect(KapuaId scopeId, KapuaId deviceConnectionId) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceConnectionId, "deviceConnectionId");

        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.DEVICE_CONNECTION, Actions.write, scopeId));

        // Find the specified DeviceConnection
        DeviceConnection deviceConnection =
                txManager.execute(
                     tx -> repository.find(tx, scopeId, deviceConnectionId)
                                     .orElseThrow(() -> new KapuaEntityNotFoundException(DeviceConnection.TYPE, deviceConnectionId)),
                eventStorer::accept
        );
    }

    @Override
    public Set<String> getAvailableAuthTypes() {
        return availableDeviceConnectionAdapters.keySet();
    }

    // @ListenServiceEvent(fromAddress="account")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }
        LOG.info("DeviceConnectionService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteConnectionByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }
    // Private methods

    private void deleteConnectionByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {
        DeviceConnectionQuery query = entityFactory.newQuery(accountId);

        txManager.execute(tx -> {
            final DeviceConnectionListResult deviceConnectionsToDelete = repository.query(tx, query);

            for (DeviceConnection dc : deviceConnectionsToDelete.getItems()) {
                repository.delete(tx, dc.getScopeId(), dc.getId());
            }
            return null;
        });
    }
}
