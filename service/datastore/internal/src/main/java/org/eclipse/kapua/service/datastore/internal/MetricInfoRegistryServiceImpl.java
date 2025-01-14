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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.service.internal.KapuaServiceDisabledException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.datastore.MetricInfoRegistryService;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageField;
import org.eclipse.kapua.service.datastore.internal.mediator.MetricInfoField;
import org.eclipse.kapua.service.datastore.internal.model.query.MessageQueryImpl;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.MetricInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.ExistsPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateFactory;
import org.eclipse.kapua.service.storable.model.query.predicate.TermPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Metric information registry implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class MetricInfoRegistryServiceImpl implements MetricInfoRegistryService {

    private static final Logger LOG = LoggerFactory.getLogger(MetricInfoRegistryServiceImpl.class);

    private final StorablePredicateFactory storablePredicateFactory;

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final MetricInfoRegistryFacade metricInfoRegistryFacade;
    private final DatastorePredicateFactory datastorePredicateFactory;
    private final MessageRepository messageRepository;
    private final DatastoreSettings datastoreSettings;
    protected final Integer maxResultWindowValue;

    private static final String QUERY = "query";
    private static final String QUERY_SCOPE_ID = "query.scopeId";

    @Inject
    public MetricInfoRegistryServiceImpl(
            StorablePredicateFactory storablePredicateFactory,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            DatastorePredicateFactory datastorePredicateFactory,
            MetricInfoRegistryFacade metricInfoRegistryFacade,
            MessageRepository messageRepository,
            DatastoreSettings datastoreSettings) {
        this.storablePredicateFactory = storablePredicateFactory;
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.datastorePredicateFactory = datastorePredicateFactory;
        this.metricInfoRegistryFacade = metricInfoRegistryFacade;
        this.messageRepository = messageRepository;
        this.datastoreSettings = datastoreSettings;
        maxResultWindowValue = datastoreSettings.getInt(DatastoreSettingsKey.MAX_RESULT_WINDOW_VALUE);
    }

    @Override
    public MetricInfo find(KapuaId scopeId, StorableId id) throws KapuaException {
        return find(scopeId, id, StorableFetchStyle.SOURCE_FULL);
    }

    @Override
    public MetricInfo find(KapuaId scopeId, StorableId id, StorableFetchStyle fetchStyle) throws KapuaException {
        if (!isServiceEnabled(scopeId)) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(id, "id");

        checkDataAccess(scopeId, Actions.read);
        try {
            // populate the lastMessageTimestamp
            MetricInfo metricInfo = metricInfoRegistryFacade.find(scopeId, id);
            if (metricInfo != null) {

                updateLastPublishedFields(metricInfo);
            }
            return metricInfo;
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public MetricInfoListResult query(MetricInfoQuery query)
            throws KapuaException {
        if (!isServiceEnabled(query.getScopeId())) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        checkDataAccess(query.getScopeId(), Actions.read);
        if (query.getLimit() != null && query.getOffset() != null) {
            ArgumentValidator.notNegative(query.getLimit(), "limit");
            ArgumentValidator.notNegative(query.getOffset(), "offset");
            ArgumentValidator.numLessThenOrEqual(query.getLimit() + query.getOffset(), maxResultWindowValue, "limit + offset");
        }

        try {
            MetricInfoListResult result = metricInfoRegistryFacade.query(query);
            if (result != null && query.getFetchAttributes().contains(MetricInfoField.TIMESTAMP_FULL.field())) {
                // populate the lastMessageTimestamp
                for (MetricInfo metricInfo : result.getItems()) {
                    updateLastPublishedFields(metricInfo);
                }
            }
            return result;
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public long count(MetricInfoQuery query)
            throws KapuaException {
        if (!isServiceEnabled(query.getScopeId())) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        checkDataAccess(query.getScopeId(), Actions.read);
        try {
            return metricInfoRegistryFacade.count(query);
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public void delete(MetricInfoQuery query)
            throws KapuaException {
        if (!isServiceEnabled(query.getScopeId())) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        checkDataAccess(query.getScopeId(), Actions.delete);
        try {
            metricInfoRegistryFacade.delete(query);
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public void delete(KapuaId scopeId, StorableId id)
            throws KapuaException {
        if (!isServiceEnabled(scopeId)) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(id, "id");

        checkDataAccess(scopeId, Actions.delete);
        try {
            metricInfoRegistryFacade.delete(scopeId, id);
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    private void checkDataAccess(KapuaId scopeId, Actions action)
            throws KapuaException {
        Permission permission = permissionFactory.newPermission(Domains.DATASTORE, action, scopeId);
        authorizationService.checkPermission(permission);
    }

    /**
     * Update the last published date and last published message identifier for the specified metric info, so it gets the timestamp and the message identifier of the last published message for the
     * account/clientId in the metric info
     *
     * @throws KapuaException
     */
    private void updateLastPublishedFields(MetricInfo metricInfo) throws KapuaException {
        List<SortField> sort = new ArrayList<>();
        sort.add(SortField.descending(MessageSchema.MESSAGE_TIMESTAMP));

        MessageQuery messageQuery = new MessageQueryImpl(metricInfo.getScopeId());
        messageQuery.setAskTotalCount(true);
        messageQuery.setFetchStyle(StorableFetchStyle.FIELDS);
        messageQuery.setLimit(1);
        messageQuery.setOffset(0);
        messageQuery.setSortFields(sort);

        RangePredicate messageIdPredicate = storablePredicateFactory.newRangePredicate(MetricInfoField.TIMESTAMP, metricInfo.getFirstMessageOn(), null);
        TermPredicate clientIdPredicate = datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, metricInfo.getClientId());
        ExistsPredicate metricPredicate = storablePredicateFactory.newExistsPredicate(MessageField.METRICS.field(), metricInfo.getName());

        AndPredicate andPredicate = storablePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(messageIdPredicate);
        andPredicate.getPredicates().add(clientIdPredicate);
        andPredicate.getPredicates().add(metricPredicate);
        messageQuery.setPredicate(andPredicate);

        MessageListResult messageList = messageRepository.query(messageQuery);

        StorableId lastPublishedMessageId = null;
        Date lastPublishedMessageTimestamp = null;
        final List<DatastoreMessage> messages = Optional.ofNullable(messageList).map(ml -> ml.getItems()).orElse(new ArrayList<>());
        if (messages.size() == 1) {
            lastPublishedMessageId = messages.get(0).getDatastoreId();
            lastPublishedMessageTimestamp = messages.get(0).getTimestamp();
        } else if (messages.isEmpty()) {
            // this condition could happens due to the ttl of the messages (so if it happens, it does not necessarily mean there has been an error!)
            LOG.warn("Cannot find last timestamp for the specified client id '{}' - account '{}'", metricInfo.getClientId(), metricInfo.getScopeId());
        } else {
            // this condition shouldn't never happens since the query has a limit 1
            // if happens it means than an elasticsearch internal error happens and/or our driver didn't set it correctly!
            LOG.error("Cannot find last timestamp for the specified client id '{}' - account '{}'. More than one result returned by the query!", metricInfo.getClientId(), metricInfo.getScopeId());
        }

        metricInfo.setLastMessageId(lastPublishedMessageId);
        metricInfo.setLastMessageOn(lastPublishedMessageTimestamp);
    }

    @Override
    public boolean isServiceEnabled(KapuaId scopeId) {
        return !datastoreSettings.getBoolean(DatastoreSettingsKey.DISABLE_DATASTORE, false);
    }

}
