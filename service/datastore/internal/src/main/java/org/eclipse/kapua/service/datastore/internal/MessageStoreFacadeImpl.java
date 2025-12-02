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
package org.eclipse.kapua.service.datastore.internal;

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.internal.device.data.KapuaDataChannelImpl;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.exception.DatastoreDisabledException;
import org.eclipse.kapua.service.datastore.internal.mediator.ChannelInfoField;
import org.eclipse.kapua.service.datastore.internal.mediator.ClientInfoField;
import org.eclipse.kapua.service.datastore.internal.mediator.ConfigurationException;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageInfo;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;
import org.eclipse.kapua.service.datastore.internal.mediator.Metric;
import org.eclipse.kapua.service.datastore.internal.mediator.MetricInfoField;
import org.eclipse.kapua.service.datastore.internal.model.ChannelInfoImpl;
import org.eclipse.kapua.service.datastore.internal.model.ClientInfoImpl;
import org.eclipse.kapua.service.datastore.internal.model.DataIndexBy;
import org.eclipse.kapua.service.datastore.internal.model.DatastoreMessageImpl;
import org.eclipse.kapua.service.datastore.internal.model.MessageListResultImpl;
import org.eclipse.kapua.service.datastore.internal.model.MetricInfoImpl;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.elasticsearch.client.exception.QueryMappingException;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Message store facade
 *
 * @since 1.0.0
 */
public final class MessageStoreFacadeImpl extends AbstractDatastoreFacade implements MessageStoreFacade {

    private static final Logger LOG = LoggerFactory.getLogger(MessageStoreFacadeImpl.class);

    private final StorableIdFactory storableIdFactory;
    private final ClientInfoRegistryFacade clientInfoRegistryFacade;
    private final ChannelInfoRegistryFacade channelInfoStoreFacade;
    private final MetricInfoRegistryFacade metricInfoStoreFacade;
    private final MessageRepository messageRepository;
    private final MetricInfoRepository metricInfoRepository;
    private final ChannelInfoRepository channelInfoRepository;
    private final ClientInfoRepository clientInfoRepository;
    private final DatastoreUtils datastoreUtils;

    private static final String QUERY = "query";
    private static final String QUERY_SCOPE_ID = "query.scopeId";
    private static final String SCOPE_ID = "scopeId";

    @Inject
    public MessageStoreFacadeImpl(
            ConfigurationProvider configProvider,
            StorableIdFactory storableIdFactory,
            ClientInfoRegistryFacade clientInfoRegistryFacade,
            ChannelInfoRegistryFacade channelInfoStoreFacade,
            MetricInfoRegistryFacade metricInfoStoreFacade,
            MessageRepository messageRepository,
            MetricInfoRepository metricInfoRepository,
            ChannelInfoRepository channelInfoRepository,
            ClientInfoRepository clientInfoRepository,
            DatastoreUtils datastoreUtils) {
        super(configProvider);
        this.storableIdFactory = storableIdFactory;
        this.clientInfoRegistryFacade = clientInfoRegistryFacade;
        this.channelInfoStoreFacade = channelInfoStoreFacade;
        this.metricInfoStoreFacade = metricInfoStoreFacade;
        this.messageRepository = messageRepository;
        this.metricInfoRepository = metricInfoRepository;
        this.channelInfoRepository = channelInfoRepository;
        this.clientInfoRepository = clientInfoRepository;
        this.datastoreUtils = datastoreUtils;
    }

    /**
     * Store a message
     *
     * @param message
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    @Override
    public StorableId store(KapuaMessage<?, ?> message, String messageId) throws KapuaIllegalArgumentException, DatastoreDisabledException, ConfigurationException, ClientException, MappingException {
        ArgumentValidator.notNull(message, "message");
        ArgumentValidator.notNull(message.getScopeId(), SCOPE_ID);
        ArgumentValidator.notNull(message.getReceivedOn(), "receivedOn");
        ArgumentValidator.notNull(messageId, "messageId");

        // Define data TTL
        if (!isDatastoreServiceEnabled(message.getScopeId())) {
            throw new DatastoreDisabledException(message.getScopeId());
        }

        Date capturedOn = message.getCapturedOn();
        // Overwrite timestamp if necessary
        // Use the account service plan to determine whether we will give
        // precede to the device time
        MessageStoreConfiguration accountServicePlan = configProvider.getConfiguration(message.getScopeId());
        long indexedOn = KapuaDateUtils.getKapuaSysDate().toEpochMilli();
        if (DataIndexBy.DEVICE_TIMESTAMP.equals(accountServicePlan.getDataIndexBy())) {
            if (capturedOn != null) {
                indexedOn = capturedOn.getTime();
            } else {
                LOG.debug("The account is set to use, as date indexing, the device timestamp but the device timestamp is null! Current system date will be used to indexing the message by date!");
            }
        }
        // Extract schema metadata
        Date indexedOnDate = new Date(indexedOn);

        // Save message (the big one)
        final DatastoreMessage messageToStore = convertTo(message, messageId);
        messageToStore.setTimestamp(indexedOnDate);
        // Possibly update the schema with new metric mappings
        Map<String, Metric> metrics = new HashMap<>();
        if (message.getPayload() != null && message.getPayload().getMetrics() != null && !message.getPayload().getMetrics().isEmpty()) {
            Map<String, Object> messageMetrics = message.getPayload().getMetrics();
            for (Map.Entry<String, Object> messageMetric : messageMetrics.entrySet()) {
                String metricName = datastoreUtils.normalizeMetricName(messageMetric.getKey());
                String clientMetricType = datastoreUtils.getClientMetricFromType(messageMetric.getValue().getClass());
                Metric metric = new Metric(metricName, clientMetricType);

                // each metric is potentially a dynamic field so report it a new mapping
                String mappedName = datastoreUtils.getMetricValueQualifier(metricName, clientMetricType);
                metrics.put(mappedName, metric);
            }
        }

        final String storedId = messageRepository.store(messageToStore, metrics);
        messageToStore.setDatastoreId(storableIdFactory.newStorableId(storedId));

        MessageInfo messageInfo = configProvider.getInfo(message.getScopeId());
        this.onAfterMessageStore(messageInfo, messageToStore);

        return storableIdFactory.newStorableId(storedId);
    }

    /**
     * This constructor should be used for wrapping Kapua message into datastore message for insert purpose
     *
     * @param message
     */
    @Override
    public DatastoreMessage convertTo(KapuaMessage<?, ?> message, String messageId) {
        KapuaDataChannel datastoreChannel = new KapuaDataChannelImpl();
        datastoreChannel.setSemanticParts(message.getChannel().getSemanticParts());

        DatastoreMessage datastoreMessage = new DatastoreMessageImpl();
        datastoreMessage.setCapturedOn(message.getCapturedOn());
        datastoreMessage.setChannel(datastoreChannel);
        datastoreMessage.setClientId(message.getClientId());
        datastoreMessage.setDeviceId(message.getDeviceId());
        datastoreMessage.setId(message.getId());
        datastoreMessage.setPayload(message.getPayload());
        datastoreMessage.setPosition(message.getPosition());
        datastoreMessage.setReceivedOn(message.getReceivedOn());
        datastoreMessage.setScopeId(message.getScopeId());
        datastoreMessage.setSentOn(message.getSentOn());

        // generate uuid
        datastoreMessage.setId(message.getId());
        datastoreMessage.setDatastoreId(storableIdFactory.newStorableId(messageId));
        return datastoreMessage;
    }

    /**
     * Delete message by identifier.<br>
     * <b>Be careful using this function since it doesn't guarantee the datastore consistency.<br>
     * It just deletes the message by id without checking the consistency of the registries.</b>
     *
     * @param id
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws ClientException
     */
    @Override
    public void delete(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(id, "id");

        if (!isDatastoreServiceEnabled(scopeId)) {
            LOG.debug("Storage not enabled for account {}, return", scopeId);
            return;
        }

        // get the index by finding the object by id
        DatastoreMessage messageToBeDeleted = messageRepository.find(scopeId, id);
        if (messageToBeDeleted != null) {
            messageRepository.delete(scopeId, id, messageToBeDeleted.getTimestamp().getTime());
        } else {
            LOG.warn("Cannot find the message to be deleted. scopeId: '{}' - id: '{}'", scopeId, id);
        }
        // otherwise no message to be deleted found
    }

    @Override
    public void delete(MessageQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);

        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, skipping delete", query.getScopeId());
            return;
        }

        messageRepository.delete(query);
    }

    @Override
    public DatastoreMessage find(KapuaId scopeId, StorableId id) throws KapuaIllegalArgumentException, ClientException {
        ArgumentValidator.notNull(scopeId, SCOPE_ID);
        ArgumentValidator.notNull(id, "id");
        return messageRepository.find(scopeId, id);
    }

    @Override
    public long count(MessageQuery query) throws KapuaIllegalArgumentException, ConfigurationException, ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);
        if (!isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, returning empty result", query.getScopeId());
            return 0;
        }

        return messageRepository.count(query);
    }


    @Override
    public void onAfterMessageStore(MessageInfo messageInfo, DatastoreMessage message)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            MappingException,
            ClientException {

        // convert semantic channel to String
        final String semanticChannel = Optional.ofNullable(message.getChannel()).map(c -> c.toString()).orElse("");

        ClientInfoImpl clientInfo = new ClientInfoImpl(message.getScopeId());
        clientInfo.setClientId(message.getClientId());
        clientInfo.setId(storableIdFactory.newStorableId(ClientInfoField.getOrDeriveId(null, message.getScopeId(), message.getClientId())));
        clientInfo.setFirstMessageId(message.getDatastoreId());
        clientInfo.setFirstMessageOn(message.getTimestamp());
        clientInfoRegistryFacade.upstore(clientInfo);

        ChannelInfoImpl channelInfo = new ChannelInfoImpl(message.getScopeId());
        channelInfo.setClientId(message.getClientId());
        channelInfo.setName(semanticChannel);
        channelInfo.setFirstMessageId(message.getDatastoreId());
        channelInfo.setFirstMessageOn(message.getTimestamp());
        channelInfo.setId(storableIdFactory.newStorableId(ChannelInfoField.getOrDeriveId(null, channelInfo)));
        channelInfoStoreFacade.upstore(channelInfo);

        KapuaPayload payload = message.getPayload();
        if (payload == null) {
            return;
        }

        Map<String, Object> metrics = payload.getMetrics();
        if (metrics == null) {
            return;
        }

        int i = 0;
        MetricInfoImpl[] messageMetrics = new MetricInfoImpl[metrics.size()];
        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            MetricInfoImpl metricInfo = new MetricInfoImpl(message.getScopeId());
            metricInfo.setClientId(message.getClientId());
            metricInfo.setChannel(semanticChannel);
            metricInfo.setName(entry.getKey());
            metricInfo.setMetricType(entry.getValue().getClass());
            metricInfo.setId(storableIdFactory.newStorableId(MetricInfoField.getOrDeriveId(null, metricInfo)));
            metricInfo.setFirstMessageId(message.getDatastoreId());
            metricInfo.setFirstMessageOn(message.getTimestamp());
            messageMetrics[i++] = metricInfo;
        }

        metricInfoStoreFacade.upstore(messageMetrics);
    }

    /**
     * Find messages matching the given query
     *
     * @param query
     * @return
     * @throws KapuaIllegalArgumentException
     * @throws ConfigurationException
     * @throws QueryMappingException
     * @throws ClientException
     */
    @Override
    public MessageListResult query(MessageQuery query)
            throws KapuaIllegalArgumentException,
            ConfigurationException,
            ClientException {
        ArgumentValidator.notNull(query, QUERY);
        ArgumentValidator.notNull(query.getScopeId(), QUERY_SCOPE_ID);
        if (!this.isDatastoreServiceEnabled(query.getScopeId())) {
            LOG.debug("Storage not enabled for account {}, returning empty result", query.getScopeId());
            return new MessageListResultImpl();
        }

        return messageRepository.query(query);
    }

    // Utility methods
    @Override
    public void refreshAllIndexes() throws ClientException {
        messageRepository.refreshAllIndexes();
        clientInfoRepository.refreshAllIndexes();
        channelInfoRepository.refreshAllIndexes();
        metricInfoRepository.refreshAllIndexes();
    }

    @Override
    public void deleteAllIndexes() throws ClientException {
        messageRepository.deleteAllIndexes();
        clientInfoRepository.deleteAllIndexes();
        channelInfoRepository.deleteAllIndexes();
        metricInfoRepository.deleteAllIndexes();
    }


    @Override
    public void deleteIndexes(String indexExp) throws ClientException {
        messageRepository.deleteIndexes(indexExp);
        clientInfoRepository.deleteIndexes(indexExp);
        channelInfoRepository.deleteIndexes(indexExp);
        metricInfoRepository.deleteIndexes(indexExp);
    }

}
