/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.integration.service.datastoreJunit;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.internal.MessageStoreFacade;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreException;
import org.eclipse.kapua.service.datastore.model.query.MessageField;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.MessageSchema;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.TermPredicate;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(JUnitTests.class)
public class MessageStoreServiceSslTest extends AbstractMessageStoreServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(MessageStoreServiceSslTest.class);

    private final KapuaLocator locator = KapuaLocator.getInstance();

    private final DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);

    private final DatastorePredicateFactory datastorePredicateFactory = locator.getFactory(DatastorePredicateFactory.class);
    private final MessageStoreService messageStoreService = locator.getService(MessageStoreService.class);
    private final MessageStoreFacade messageStoreFacade = locator.getComponent(MessageStoreFacade.class);
    private final ElasticsearchClientProvider elasticsearchClientProvider = locator.getComponent(ElasticsearchClientProvider.class);

    /**
     * This method deletes all indices of the current ES instance
     * <p>
     * The method deletes all indices and resets the Kapua internal singleton state. This is required to ensure that each unit test, as it currently expects, starts with an empty ES setup
     * </p>
     *
     * @throws Exception
     *         any case anything goes wrong
     */
    // @Before
    // public void deleteAllIndices() throws Exception {
    // DatastoreMediator.getInstance().deleteAllIndexes();
    // }
    @Test(expected = DatastoreException.class)
    @Ignore
    public void connectNoSsl() throws InterruptedException, KapuaException, ParseException {
        // datastore.elasticsearch.ssl.enabled=false
        // datastore.elasticsearch.ssl.keystore.path=
        // datastore.elasticsearch.ssl.keystore.password=
        // datastore.elasticsearch.ssl.keystore.type=jks
        // datastore.elasticsearch.ssl.truststore.path=
        // datastore.elasticsearch.ssl.truststore.password=
        try {
            elasticsearchClientProvider.getElasticsearchClient();
            storeMessage("ssl_test/no_ssl");
            Assert.fail("ClientException should be thrown!");
        } catch (ClientException e) {
            // good
        } finally {
            elasticsearchClientProvider.close();
        }
    }

    @Test
    @Ignore
    public void connectSsl() throws InterruptedException, KapuaException, ParseException {
        // datastore.elasticsearch.ssl.enabled=true
        // datastore.elasticsearch.ssl.keystore.path=
        // datastore.elasticsearch.ssl.keystore.password=
        // datastore.elasticsearch.ssl.keystore.type=jks
        // datastore.elasticsearch.ssl.truststore.path=
        // datastore.elasticsearch.ssl.truststore.password=
        try {
            elasticsearchClientProvider.getElasticsearchClient();
            storeMessage("ssl_test/ssl");
        } catch (ClientException e) {
            Assert.fail("No ClientException should be thrown!");
        } finally {
            elasticsearchClientProvider.close();
        }
    }

    @Test
    @Ignore
    public void connectSslTrustServerNoTrustStoreSet() throws InterruptedException, KapuaException, ParseException {
        // datastore.elasticsearch.ssl.enabled=true
        // datastore.elasticsearch.ssl.keystore.path=
        // datastore.elasticsearch.ssl.keystore.password=
        // datastore.elasticsearch.ssl.keystore.type=jks
        // datastore.elasticsearch.ssl.truststore.path=
        // datastore.elasticsearch.ssl.truststore.password=
        try {
            elasticsearchClientProvider.getElasticsearchClient();
            storeMessage("ssl_test/ssl_trust_server_no_trust_store_set");
        } catch (ClientException e) {
            Assert.fail("No ClientException should be thrown!");
        } finally {
            elasticsearchClientProvider.close();
        }
    }

    @Test
    @Ignore
    public void connectSslTrustServerTrustStoreSet() throws InterruptedException, KapuaException, ParseException {
        // datastore.elasticsearch.ssl.enabled=false
        // datastore.elasticsearch.ssl.keystore.path=
        // datastore.elasticsearch.ssl.keystore.password=
        // datastore.elasticsearch.ssl.keystore.type=jks
        // datastore.elasticsearch.ssl.truststore.path=some valid truststore path
        // datastore.elasticsearch.ssl.truststore.password=trust store password
        try {
            elasticsearchClientProvider.getElasticsearchClient();
            storeMessage("ssl_test/ssl_trust_server_default_trust_store_set");
        } catch (ClientException e) {
            Assert.fail("No ClientException should be thrown!");
        } finally {
            elasticsearchClientProvider.close();
        }
    }

    @Test(expected = DatastoreException.class)
    @Ignore
    public void connectSslTrustServerSelfSignedTrustStore() throws InterruptedException, KapuaException, ParseException {
        // datastore.elasticsearch.ssl.enabled=false
        // datastore.elasticsearch.ssl.keystore.path=
        // datastore.elasticsearch.ssl.keystore.password=
        // datastore.elasticsearch.ssl.keystore.type=jks
        // datastore.elasticsearch.ssl.truststore.path=self signed trust store
        // datastore.elasticsearch.ssl.truststore.password=password
        try {
            elasticsearchClientProvider.getElasticsearchClient();
            storeMessage("ssl_test/ssl_trust_server_self_signed_tust");
        } catch (ClientException e) {
            Assert.fail("No ClientException should be thrown!");
        } finally {
            elasticsearchClientProvider.close();
        }
    }

    private void storeMessage(String semanticTopic) throws InterruptedException, KapuaException, ParseException {
        Account account = getTestAccountCreator(adminScopeId);

        String clientId = String.format("device-%d", new Date().getTime());
        DeviceCreator deviceCreator = new DeviceCreator(account.getId(), clientId);
        Device device = deviceRegistryService.create(deviceCreator);

        // leave the message index by as default (DEVICE_TIMESTAMP)
        String stringPayload = "Hello delete by date message!";
        byte[] payload = stringPayload.getBytes();

        Instant currentInstant = KapuaDateUtils.getKapuaSysDate();
        Date date = Date.from(currentInstant);
        int messagesCount = 1;
        insertMessage(account, clientId, device.getId(), semanticTopic, payload, date);

        // do a first query count
        messageStoreFacade.refreshAllIndexes();
        MessageQuery messageQuery = getBaseMessageQuery(KapuaEid.ONE, 10);
        setMessageQueryBaseCriteria(messageQuery, clientId, new DateRange(Date.from(currentInstant.minusSeconds(3600)), date));
        long count = messageStoreService.count(messageQuery);
        Assert.assertEquals(messagesCount, count);

    }

    private KapuaDataMessage insertMessage(Account account, String clientId, KapuaId deviceId, String semanticTopic, byte[] payload, Date sentOn) throws InterruptedException, KapuaException {
        KapuaDataPayload messagePayload = new KapuaDataPayload();
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("float", new Float((float) 0.01));
        messagePayload.setMetrics(metrics);
        messagePayload.setBody(payload);
        Date receivedOn = Date.from(KapuaDateUtils.getKapuaSysDate());
        Date capturedOn = sentOn;
        KapuaDataMessage message = createMessage(clientId, account.getId(), deviceId, receivedOn, capturedOn, sentOn);
        setChannel(message, semanticTopic);
        updatePayload(message, messagePayload);
        List<StorableId> messageStoredIds = insertMessages(message);
        return message;
    }

    // ===========================================================
    // ===========================================================
    // utility methods
    // ===========================================================
    // ===========================================================

    private List<StorableId> insertMessages(KapuaDataMessage... messages) throws InterruptedException, KapuaException {
        Objects.requireNonNull(messages);

        List<StorableId> storableIds = new ArrayList<>(messages.length);
        for (KapuaDataMessage message : messages) {
            storableIds.add(messageStoreService.store(message));
        }
        return storableIds;
    }

    /**
     * Creates a new KapuaMessage setting the provided parameters
     *
     * @param clientId
     * @param scopeId
     * @param deviceId
     * @param capturedOn
     * @param sentOn
     * @return
     */
    private KapuaDataMessage createMessage(String clientId, KapuaId scopeId, KapuaId deviceId, Date receivedOn, Date capturedOn, Date sentOn) {
        KapuaDataMessage message = new KapuaDataMessage();
        message.setReceivedOn(receivedOn);
        message.setCapturedOn(capturedOn);
        message.setSentOn(sentOn);
        message.setChannel(new KapuaDataChannel());
        message.setClientId(clientId);
        message.setDeviceId(deviceId);
        message.setScopeId(scopeId);
        return message;
    }

    /**
     * Update the KapuaMessage channel with the provided semantic part
     *
     * @param message
     * @param semanticPart
     */
    private void setChannel(KapuaDataMessage message, String semanticPart) {
        KapuaDataChannel channel = new KapuaDataChannel();
        channel.setSemanticParts(new ArrayList<>(Arrays.asList(semanticPart.split("/"))));

        message.setChannel(channel);
    }

    /**
     * Update the KapuaMessage payload with the provided payload
     *
     * @param message
     * @param messagePayload
     */
    private void updatePayload(KapuaDataMessage message, KapuaDataPayload messagePayload) {
        message.setPayload(messagePayload);
    }
    // Utility methods to help to to create message queries

    /**
     * Creates a new query setting the default base parameters (fetch style, sort, limit, offset, ...) for the Message schema
     *
     * @return
     */
    private MessageQuery getBaseMessageQuery(KapuaId scopeId, int limit) {
        MessageQuery query = new MessageQuery(scopeId);

        query.setAskTotalCount(true);
        query.setFetchStyle(StorableFetchStyle.SOURCE_FULL);
        query.setLimit(limit);
        query.setOffset(0);
        List<SortField> order = new ArrayList<>();
        order.add(SortField.descending(MessageSchema.MESSAGE_TIMESTAMP));
        query.setSortFields(order);
        return query;
    }

    /**
     * Set the query account, message timestamp and client id filter
     *
     * @param messageQuery
     * @param clientId
     * @param dateRange
     */
    private void setMessageQueryBaseCriteria(MessageQuery messageQuery, String clientId, DateRange dateRange) {
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();

        if (!StringUtils.isEmpty(clientId)) {
            TermPredicate clientPredicate = datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, clientId);
            andPredicate.getPredicates().add(clientPredicate);
        }
        if (dateRange != null) {
            RangePredicate timestampPredicate = datastorePredicateFactory.newRangePredicate(MessageField.TIMESTAMP, dateRange.getLowerBound(), dateRange.getUpperBound());

            andPredicate.getPredicates().add(timestampPredicate);
        }
        messageQuery.setPredicate(andPredicate);
    }

    private class DateRange {

        private final Date lowerBound;
        private final Date upperBound;

        public DateRange(Date lowerBound, Date upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public Date getLowerBound() {
            return lowerBound;
        }

        public Date getUpperBound() {
            return upperBound;
        }

    }

    /**
     * Return a new account created just for the test.<br>
     * <b>WARNING!!!!!!! Current implementation is not compliance with that since it is a temporary implementation that returns the default kapua-sys account</b>
     *
     * @param scopeId
     * @return
     * @throws KapuaException
     */
    private Account getTestAccountCreator(KapuaId scopeId) throws KapuaException {
        KapuaLocator locator = KapuaLocator.getInstance();
        Account account = locator.getService(AccountService.class).findByName("kapua-sys");
        return account;
    }

}
