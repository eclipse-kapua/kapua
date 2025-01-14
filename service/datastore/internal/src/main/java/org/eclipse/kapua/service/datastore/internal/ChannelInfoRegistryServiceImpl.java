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
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.datastore.ChannelInfoRegistryService;
import org.eclipse.kapua.service.datastore.internal.mediator.ChannelInfoField;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageField;
import org.eclipse.kapua.service.datastore.internal.model.query.MessageQueryImpl;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoListResult;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.TermPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Channel info registry implementation
 *
 * @since 1.0.0
 */
@Singleton
public class ChannelInfoRegistryServiceImpl implements ChannelInfoRegistryService {

    private static final Logger LOG = LoggerFactory.getLogger(ChannelInfoRegistryServiceImpl.class);
    private final DatastorePredicateFactory datastorePredicateFactory;
    protected final Integer maxResultWindowValue;
    private final AccountService accountService;
    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final ChannelInfoRegistryFacade channelInfoRegistryFacade;
    private final MessageRepository messageRepository;
    private final DatastoreSettings datastoreSettings;
    private static final String QUERY = "query";
    private static final String QUERY_SCOPE_ID = "query.scopeId";

    /**
     * Default constructor
     *
     * @since 1.0.0
     */
    @Inject
    public ChannelInfoRegistryServiceImpl(
            DatastorePredicateFactory datastorePredicateFactory,
            AccountService accountService,
            AuthorizationService authorizationService,
            PermissionFactory permissionFactory,
            MessageRepository messageStoreService,
            ChannelInfoRegistryFacade channelInfoRegistryFacade,
            DatastoreSettings datastoreSettings) {
        this.datastorePredicateFactory = datastorePredicateFactory;
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.messageRepository = messageStoreService;
        this.channelInfoRegistryFacade = channelInfoRegistryFacade;
        this.datastoreSettings = datastoreSettings;
        this.accountService = accountService;
        this.maxResultWindowValue = datastoreSettings.getInt(DatastoreSettingsKey.MAX_RESULT_WINDOW_VALUE);
    }

    @Override
    public ChannelInfo find(KapuaId scopeId, StorableId id) throws KapuaException {
        return find(scopeId, id, StorableFetchStyle.SOURCE_FULL);
    }

    @Override
    public ChannelInfo find(KapuaId scopeId, StorableId id, StorableFetchStyle fetchStyle)
            throws KapuaException {
        if (!isServiceEnabled(scopeId)) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(id, "id");

        checkDataAccess(scopeId, Actions.read);
        try {
            ChannelInfo channelInfo = channelInfoRegistryFacade.find(scopeId, id);
            if (channelInfo != null) {
                // populate the lastMessageTimestamp
                updateLastPublishedFields(channelInfo);
            }
            return channelInfo;
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public ChannelInfoListResult query(ChannelInfoQuery query)
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
            ChannelInfoListResult result = channelInfoRegistryFacade.query(query);
            if (result != null && query.getFetchAttributes().contains(ChannelInfoField.TIMESTAMP.field())) {
                // populate the lastMessageTimestamp
                for (ChannelInfo channelInfo : result.getItems()) {
                    updateLastPublishedFields(channelInfo);
                }
            }
            return result;
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public long count(ChannelInfoQuery query)
            throws KapuaException {
        if (!isServiceEnabled(query.getScopeId())) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        checkDataAccess(query.getScopeId(), Actions.read);
        try {
            return channelInfoRegistryFacade.count(query);
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
            channelInfoRegistryFacade.delete(scopeId, id);
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public void delete(ChannelInfoQuery query)
            throws KapuaException {
        if (!isServiceEnabled(query.getScopeId())) {
            throw new KapuaServiceDisabledException(this.getClass().getName());
        }

        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        checkDataAccess(query.getScopeId(), Actions.delete);
        try {
            channelInfoRegistryFacade.delete(query);
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
     * Update the last published date and last published message identifier for the specified channel info, so it gets the timestamp and the message id of the last published message for the
     * account/clientId in the channel info
     *
     * @param channelInfo
     * @throws KapuaException
     * @since 1.0.0
     */
    private void updateLastPublishedFields(ChannelInfo channelInfo) throws KapuaException {
        List<SortField> sort = new ArrayList<>();
        sort.add(SortField.descending(MessageSchema.MESSAGE_TIMESTAMP));

        MessageQuery messageQuery = new MessageQueryImpl(channelInfo.getScopeId());
        messageQuery.setAskTotalCount(true);
        messageQuery.setFetchStyle(StorableFetchStyle.FIELDS);
        messageQuery.setLimit(1);
        messageQuery.setOffset(0);
        messageQuery.setSortFields(sort);

        RangePredicate messageIdPredicate = datastorePredicateFactory.newRangePredicate(ChannelInfoField.TIMESTAMP, channelInfo.getFirstMessageOn(), null);
        TermPredicate clientIdPredicate = datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, channelInfo.getClientId());
        TermPredicate channelPredicate = datastorePredicateFactory.newTermPredicate(MessageField.CHANNEL, channelInfo.getName());

        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(messageIdPredicate);
        andPredicate.getPredicates().add(clientIdPredicate);
        andPredicate.getPredicates().add(channelPredicate);
        messageQuery.setPredicate(andPredicate);

        MessageListResult messageList = messageRepository.query(messageQuery);
        final List<DatastoreMessage> messages = Optional.ofNullable(messageList).map(ml -> ml.getItems()).orElse(new ArrayList<>());

        StorableId lastPublishedMessageId = null;
        Date lastPublishedMessageTimestamp = null;
        if (messages.size() == 1) {
            lastPublishedMessageId = messages.get(0).getDatastoreId();
            lastPublishedMessageTimestamp = messages.get(0).getTimestamp();
        } else if (messages.isEmpty()) {
            // this condition could happens due to the ttl of the messages (so if it happens, it does not necessarily mean there has been an error!)
            LOG.warn("Cannot find last timestamp for the specified client id '{}' - account '{}'", channelInfo.getScopeId(), channelInfo.getClientId());
        } else {
            // this condition shouldn't never happens since the query has a limit 1
            // if happens it means than an elasticsearch internal error happens and/or our driver didn't set it correctly!
            LOG.error("Cannot find last timestamp for the specified client id '{}' - account '{}'. More than one result returned by the query!", channelInfo.getScopeId(), channelInfo.getClientId());
        }

        channelInfo.setLastMessageId(lastPublishedMessageId);
        channelInfo.setLastMessageOn(lastPublishedMessageTimestamp);
    }

    @Override
    public boolean isServiceEnabled(KapuaId scopeId) {
        return !datastoreSettings.getBoolean(DatastoreSettingsKey.DISABLE_DATASTORE, false);
    }

}
