/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.datastore.internal;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.configuration.AbstractKapuaConfigurableService;
import org.eclipse.kapua.commons.metric.MetricServiceFactory;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.datastore.DatastoreDomains;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreCommunicationException;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreException;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreMediator;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientCommunicationException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Message store service implementation.
 *
 * @since 1.0.0
 */
@KapuaProvider
public class MessageStoreServiceImpl extends AbstractKapuaConfigurableService implements MessageStoreService {

    private static final Logger logger = LoggerFactory.getLogger(MessageStoreServiceImpl.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    // metrics
    private final Counter metricMessageCount;
    private final Counter metricCommunicationErrorCount;
    private final Counter metricConfigurationErrorCount;
    private final Counter metricGenericErrorCount;
    private final Counter metricValidationErrorCount;
    // store timers
    private final Timer metricDataSaveTime;
    // queues counters
    private final Counter metricQueueCommunicationErrorCount;
    private final Counter metricQueueConfigurationErrorCount;
    private final Counter metricQueueGenericErrorCount;

    protected final AccountService accountService = LOCATOR.getService(AccountService.class);
    protected final AuthorizationService authorizationService = LOCATOR.getService(AuthorizationService.class);
    protected final PermissionFactory permissionFactory = LOCATOR.getFactory(PermissionFactory.class);
    protected static final Integer MAX_ENTRIES_ON_DELETE = DatastoreSettings.getInstance().getInt(DatastoreSettingsKey.CONFIG_MAX_ENTRIES_ON_DELETE);
    protected static final Integer MAX_LIMIT_VALUE = DatastoreSettings.getInstance().getInt(DatastoreSettingsKey.MAX_LIMIT_VALUE);

    protected final MessageStoreFacade messageStoreFacade;

    /**
     * Constructor.
     * <p>
     * Initializes {@link ConfigurationProvider} and {@link MetricsService}
     *
     * @since 1.0.0
     */
    public MessageStoreServiceImpl() {
        super(MessageStoreService.class.getName(), DatastoreDomains.DATASTORE_DOMAIN, DatastoreEntityManagerFactory.getInstance());
        ConfigurationProviderImpl configurationProvider = new ConfigurationProviderImpl(this, accountService, DatastoreCacheManager.getInstance().getConfigurationCache());
        messageStoreFacade = new MessageStoreFacade(configurationProvider, DatastoreMediator.getInstance());
        DatastoreMediator.getInstance().setMessageStoreFacade(messageStoreFacade);

        // data message
        MetricsService metricService = MetricServiceFactory.getInstance();
        metricMessageCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_MESSAGES, DataStoreDriverMetrics.METRIC_COUNT);
        metricCommunicationErrorCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_MESSAGES, DataStoreDriverMetrics.METRIC_COMMUNICATION, DataStoreDriverMetrics.METRIC_ERROR, DataStoreDriverMetrics.METRIC_COUNT);
        metricConfigurationErrorCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_MESSAGES, DataStoreDriverMetrics.METRIC_CONFIGURATION, DataStoreDriverMetrics.METRIC_ERROR, DataStoreDriverMetrics.METRIC_COUNT);
        metricGenericErrorCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_MESSAGES, DataStoreDriverMetrics.METRIC_GENERIC, DataStoreDriverMetrics.METRIC_ERROR, DataStoreDriverMetrics.METRIC_COUNT);
        metricValidationErrorCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_MESSAGES, DataStoreDriverMetrics.METRIC_VALIDATION, DataStoreDriverMetrics.METRIC_ERROR, DataStoreDriverMetrics.METRIC_COUNT);

        // error messages queues size
        metricQueueCommunicationErrorCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_QUEUE, DataStoreDriverMetrics.METRIC_COMMUNICATION, DataStoreDriverMetrics.METRIC_ERROR, DataStoreDriverMetrics.METRIC_COUNT);
        metricQueueConfigurationErrorCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_QUEUE, DataStoreDriverMetrics.METRIC_CONFIGURATION, DataStoreDriverMetrics.METRIC_ERROR, DataStoreDriverMetrics.METRIC_COUNT);
        metricQueueGenericErrorCount = metricService.getCounter(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_QUEUE, DataStoreDriverMetrics.METRIC_GENERIC, DataStoreDriverMetrics.METRIC_ERROR, DataStoreDriverMetrics.METRIC_COUNT);

        // store timers
        metricDataSaveTime = metricService.getTimer(DataStoreDriverMetrics.METRIC_MODULE_NAME, DataStoreDriverMetrics.METRIC_COMPONENT_NAME, DataStoreDriverMetrics.METRIC_STORE, DataStoreDriverMetrics.METRIC_MESSAGES, DataStoreDriverMetrics.METRIC_TIME, DataStoreDriverMetrics.METRIC_S);
    }

    @Override
    public StorableId store(KapuaMessage<?, ?> message)
            throws KapuaException {
        String datastoreId = UUID.randomUUID().toString();
        Context metricDataSaveTimeContext = metricDataSaveTime.time();
        try {
            checkDataAccess(message.getScopeId(), Actions.write);
            metricMessageCount.inc();
            return messageStoreFacade.store(message, datastoreId, true);
        } catch (ConfigurationException e) {
            metricConfigurationErrorCount.inc();
            metricQueueConfigurationErrorCount.inc();
            throw e;
        } catch (KapuaIllegalArgumentException e) {
            metricValidationErrorCount.inc();
            metricQueueGenericErrorCount.inc();
            throw e;
        } catch (ClientCommunicationException e) {
            metricCommunicationErrorCount.inc();
            metricQueueCommunicationErrorCount.inc();
            throw new DatastoreCommunicationException(datastoreId, e);
        } catch (Exception e) {
            metricGenericErrorCount.inc();
            metricQueueGenericErrorCount.inc();
            logException(e);
            throw new DatastoreException(KapuaErrorCodes.INTERNAL_ERROR, e, e.getMessage());
        } finally {
            metricDataSaveTimeContext.stop();
        }
    }

    @Override
    public StorableId store(KapuaMessage<?, ?> message, String datastoreId)
            throws KapuaException {
        ArgumentValidator.notEmptyOrNull(datastoreId, "datastoreId");
        Context metricDataSaveTimeContext = metricDataSaveTime.time();
        try {
            checkDataAccess(message.getScopeId(), Actions.write);
            metricMessageCount.inc();
            return messageStoreFacade.store(message, datastoreId, false);
        } catch (ConfigurationException e) {
            metricConfigurationErrorCount.inc();
            metricQueueConfigurationErrorCount.inc();
            throw e;
        } catch (KapuaIllegalArgumentException e) {
            metricValidationErrorCount.inc();
            metricQueueGenericErrorCount.inc();
            throw e;
        } catch (ClientCommunicationException e) {
            metricCommunicationErrorCount.inc();
            metricQueueCommunicationErrorCount.inc();
            throw new DatastoreCommunicationException(datastoreId, e);
        } catch (Exception e) {
            metricGenericErrorCount.inc();
            metricQueueGenericErrorCount.inc();
            logException(e);
            throw new DatastoreException(KapuaErrorCodes.INTERNAL_ERROR, e, e.getMessage());
        } finally {
            metricDataSaveTimeContext.stop();
        }
    }

    @Override
    public DatastoreMessage find(KapuaId scopeId, StorableId id) throws KapuaException {
        return find(scopeId, id, StorableFetchStyle.SOURCE_FULL);
    }

    @Override
    public DatastoreMessage find(KapuaId scopeId, StorableId id, StorableFetchStyle fetchStyle) throws KapuaException {
        checkDataAccess(scopeId, Actions.read);
        try {
            return messageStoreFacade.find(scopeId, id, fetchStyle);
        } catch (Exception e) {
            logException(e);
            throw new DatastoreException(KapuaErrorCodes.INTERNAL_ERROR, e, e.getMessage());
        }
    }

    @Override
    public MessageListResult query(MessageQuery query)
            throws KapuaException {
        checkDataAccess(query.getScopeId(), Actions.read);
        if (query.getLimit() != null) {
            ArgumentValidator.numRange(query.getLimit(), 0, MAX_LIMIT_VALUE, "limit");
        }
        try {
            return messageStoreFacade.query(query);
        } catch (Exception e) {
            logException(e);
            throw new DatastoreException(
                KapuaErrorCodes.INTERNAL_ERROR,
                e,
                e.getCause().getMessage() != null ? e.getCause().getMessage() : e.getMessage()
            );
        }
    }

    @Override
    public long count(MessageQuery query)
            throws KapuaException {
        checkDataAccess(query.getScopeId(), Actions.read);
        try {
            return messageStoreFacade.count(query);
        } catch (Exception e) {
            logException(e);
            throw new DatastoreException(KapuaErrorCodes.INTERNAL_ERROR, e, e.getMessage());
        }
    }

    @Override
    public void delete(KapuaId scopeId, StorableId id)
            throws KapuaException {
        checkDataAccess(scopeId, Actions.delete);
        try {
            messageStoreFacade.delete(scopeId, id);
        } catch (Exception e) {
            logException(e);
            throw new DatastoreException(KapuaErrorCodes.INTERNAL_ERROR, e, e.getMessage());
        }
    }

    @Override
    public void delete(MessageQuery query)
            throws KapuaException {
        ArgumentValidator.numRange(query.getLimit(), 0, MAX_ENTRIES_ON_DELETE, "limit");

        checkDataAccess(query.getScopeId(), Actions.delete);
        try {
            messageStoreFacade.delete(query);
        } catch (Exception e) {
            logException(e);
            throw new DatastoreException(KapuaErrorCodes.INTERNAL_ERROR, e, e.getMessage());
        }
    }

    @Override
    protected boolean isServiceEnabled(KapuaId scopeId) {
        return !DatastoreSettings.getInstance().getBoolean(DatastoreSettingsKey.DISABLE_DATASTORE, false);
    }

    protected void checkDataAccess(KapuaId scopeId, Actions action)
            throws KapuaException {
        Permission permission = permissionFactory.newPermission(DatastoreDomains.DATASTORE_DOMAIN, action, scopeId);
        authorizationService.checkPermission(permission);
    }

    private void logException(Exception e) {
        if (e instanceof RuntimeException) {
            logger.debug("", e);
        }
    }
}
