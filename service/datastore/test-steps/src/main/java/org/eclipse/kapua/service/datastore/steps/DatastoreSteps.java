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
 *     Red Hat Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.steps;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaMessageFactory;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.message.device.data.KapuaDataMessageFactory;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.type.ObjectTypeConverter;
import org.eclipse.kapua.qa.common.Session;
import org.eclipse.kapua.qa.common.SimulatedDevice;
import org.eclipse.kapua.qa.common.SimulatedDeviceApplication;
import org.eclipse.kapua.qa.common.StepData;
import org.eclipse.kapua.qa.common.TestBase;
import org.eclipse.kapua.qa.common.With;
import org.eclipse.kapua.qa.common.cucumber.CucMessageRange;
import org.eclipse.kapua.qa.common.cucumber.CucMetric;
import org.eclipse.kapua.qa.common.cucumber.CucTopic;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.datastore.ChannelInfoFactory;
import org.eclipse.kapua.service.datastore.ChannelInfoRegistryService;
import org.eclipse.kapua.service.datastore.ClientInfoFactory;
import org.eclipse.kapua.service.datastore.ClientInfoRegistryService;
import org.eclipse.kapua.service.datastore.MessageStoreFactory;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.MetricInfoFactory;
import org.eclipse.kapua.service.datastore.MetricInfoRegistryService;
import org.eclipse.kapua.service.datastore.internal.DatastoreCacheManager;
import org.eclipse.kapua.service.datastore.internal.MessageStoreFacade;
import org.eclipse.kapua.service.datastore.internal.MetricEntry;
import org.eclipse.kapua.service.datastore.internal.mediator.ChannelInfoField;
import org.eclipse.kapua.service.datastore.internal.mediator.ClientInfoField;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreChannel;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageField;
import org.eclipse.kapua.service.datastore.internal.mediator.MessageStoreConfiguration;
import org.eclipse.kapua.service.datastore.internal.mediator.MetricInfoField;
import org.eclipse.kapua.service.datastore.internal.model.DataIndexBy;
import org.eclipse.kapua.service.datastore.internal.model.metric.MetricsIndexBy;
import org.eclipse.kapua.service.datastore.internal.model.query.MessageQueryImpl;
import org.eclipse.kapua.service.datastore.internal.schema.ChannelInfoSchema;
import org.eclipse.kapua.service.datastore.internal.schema.ClientInfoSchema;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.internal.schema.MetricInfoSchema;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoListResult;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfoListResult;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.MetricInfoListResult;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.ChannelMatchPredicate;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClient;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.exception.ClientException;
import org.eclipse.kapua.service.elasticsearch.client.model.IndexRequest;
import org.eclipse.kapua.service.elasticsearch.client.rest.ElasticsearchResourcePaths;
import org.eclipse.kapua.service.storable.model.StorableListResult;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdFactory;
import org.eclipse.kapua.service.storable.model.query.SortDirection;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.TermPredicate;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Singleton
public class DatastoreSteps extends TestBase {

    private static final Logger logger = LoggerFactory.getLogger(DatastoreSteps.class);

    private static final String MESSAGE_COUNT_RESULT = "messageCountResult";
    private static final String DOUBLE = "double";
    private static final String LAST_ACCOUNT = "LastAccount";
    private static final String MESSAGE_QUERY = "messageQuery";
    private static final String DEVICE = "Device";
    private static final String DATE_FORMAT_HH_MM_SS = "HH:mm:ss dd/MM/yyyy";
    private static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy HH:mm:ss";
    private static final String RANDOM_DATE_1 = "01/04/2017 03:00:00";
    private static final String METRIC_INFO_QUERY = "metricInfoQuery";
    private static final String CHANNEL_INFO_QUERY = "channelInfoQuery";
    private static final String CLIENT_INFO_QUERY = "clientInfoQuery";
    private static final String CANNOT_FIND_CHANNEL_INFO_REGISTRY = "Cannot find the channel info registry!";
    private static final String CANNOT_FIND_CLIENT_INFO_REGISTRY = "Cannot find the client info registry!";
    private static final String CANNOT_FIND_METRIC_INFO_REGISTRY = "Cannot find the metric info registry!";
    private static final String NO_MESSAGES_TO_COMPARE_TO = "No messages to compare to!";
    private static final String NUMBER_OF_CLIENTS_DOES_NOT_MATCH = "The number of clients does not match!";
    private static final String CLIENT_ID_NOT_FOUND = "The client id [%s] is not found in the info list!";
    private DatastoreCacheManager datastoreCacheManager;

    @Then("Number of received data messages is different than {int}")
    public void numberOfReceivedDataMessagesIsDifferentThan(int numberOfMessages) {
        long count = (long) stepData.get(MESSAGE_COUNT_RESULT);
        Assert.assertNotEquals(numberOfMessages, count);
    }

    @And("I set the following metrics with messages from the list {string}")
    public void iSetTheFollowingMetricsFromTheList(String lstKey, List<CucMetric> metLst) throws Exception {
        List<KapuaDataMessage> tmpMsgLst = (List<KapuaDataMessage>) stepData.get(lstKey);

        for (CucMetric tmpMet : metLst) {
            KapuaDataMessage tmpMsg = tmpMsgLst.get(tmpMet.getMessage());
            tmpMsg.setPayload(dataMessageFactory.newKapuaDataPayload());

            switch (tmpMet.getType().trim().toLowerCase()) {
            case "string": {
                tmpMsg.getPayload().getMetrics().put(tmpMet.getMetric(), tmpMet.getValue());
                break;
            }
            case "int": {
                tmpMsg.getPayload().getMetrics().put(tmpMet.getMetric(), Integer.valueOf(tmpMet.getValue()));
                break;
            }
            case DOUBLE: {
                tmpMsg.getPayload().getMetrics().put(tmpMet.getMetric(), Double.valueOf(tmpMet.getValue()));
                break;
            }
            case "bool": {
                tmpMsg.getPayload().getMetrics().put(tmpMet.getMetric(), Boolean.valueOf(tmpMet.getValue().trim().toUpperCase()));
                break;
            }
            default: {
                Assert.fail(String.format("Unknown metric type [%s]", tmpMet.getType()));
                break;
            }
            }
        }
    }

    @When("I create message query for following metrics")
    public void iCreateMessageQueryForMetrics(List<String> metricList) throws Exception {
        iCreateMessageQueryForMetrics(null, metricList);
    }

    @When("I create message query for metric type {string} for following metrics")
    public void iCreateMessageQueryForMetrics(String type, List<String> metricList) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        List<MessageQuery> listOfMessageQueries = new ArrayList<>();

        try {
            for (String metricName : metricList) {
                MessageQuery messageQuery = createBaseMessageQuery(account.getId(), 100);
                messageQuery.setPredicate(datastorePredicateFactory.newMetricExistsPredicate(metricName, (Class<? extends Comparable>) ObjectTypeConverter.fromString(type)));

                stepData.put(MESSAGE_QUERY, messageQuery);
                listOfMessageQueries.add(messageQuery);
            }
            stepData.put("ListOfMessageQueries", listOfMessageQueries);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @And("I count data messages for more metrics")
    public void iCountDataMessagesForMoreMetrics() throws Exception {
        List<Long> metricCountList = new ArrayList<>();
        List<MessageQuery> messageQueries = (List<MessageQuery>) stepData.get("ListOfMessageQueries");

        for (MessageQuery messageQuery : messageQueries) {
            long countMetric = messageStoreService.count(messageQuery);
            metricCountList.add(countMetric);
        }
        stepData.put("MetricCountList", metricCountList);
    }

    @Then("I count {int} data messages")
    public void iCountMessages(long metricCount) throws AssertionError {
        List<Long> listOfCounts = (List<Long>) (stepData.get("MetricCountList"));

        try {
            long endMetricCount = 0;

            for (Long num : listOfCounts) {
                endMetricCount += num;
            }
            Assert.assertEquals("This two values are not equal", metricCount, endMetricCount);
        } catch (AssertionError assertError) {
            verifyAssertionError(assertError);
        }
    }

    @DataTableType
    public MetricEntry metricEntry(Map<String, String> entry) {
        return new MetricEntry(
                entry.get("key"),
                entry.get("type"),
                entry.get("value")
        );
    }

    protected KapuaLocator locator;
    private DeviceRegistryService deviceRegistryService;

    private DeviceFactory deviceFactory;

    private ElasticsearchClient<?> elasticsearchClient;

    private ChannelInfoFactory channelInfoFactory;
    private ClientInfoFactory clientInfoFactory;
    private MessageStoreFactory messageStoreFactory;
    private MetricInfoFactory metricInfoFactory;
    private DatastorePredicateFactory datastorePredicateFactory;
    private StorableIdFactory storableIdFactory;

    private ChannelInfoRegistryService channelInfoRegistryService;
    private MetricInfoRegistryService metricInfoRegistryService;
    private ClientInfoRegistryService clientInfoRegistryService;

    private MessageStoreService messageStoreService;
    private KapuaMessageFactory messageFactory;
    private KapuaDataMessageFactory dataMessageFactory;
    private MessageStoreFacade messageStoreFacade;

    private SimulatedDevice currentDevice;
    private Session session;
    private String currentApplication;

    @Inject
    public DatastoreSteps(SimulatedDevice currentDevice, Session session, StepData stepData) {
        super(stepData);
        this.currentDevice = currentDevice;
        this.session = session;
    }

    @After(value = "@setup")
    public void setServices() throws Exception {
        locator = KapuaLocator.getInstance();
        // Get instance of services used in different scenarios
        deviceRegistryService = locator.getService(DeviceRegistryService.class);
        deviceFactory = locator.getFactory(DeviceFactory.class);
        messageStoreService = locator.getService(MessageStoreService.class);
        messageFactory = locator.getFactory(KapuaMessageFactory.class);
        storableIdFactory = locator.getFactory(StorableIdFactory.class);
        channelInfoRegistryService = locator.getService(ChannelInfoRegistryService.class);
        elasticsearchClient = locator.getComponent(ElasticsearchClientProvider.class).getElasticsearchClient();
        channelInfoFactory = locator.getFactory(ChannelInfoFactory.class);
        clientInfoFactory = locator.getFactory(ClientInfoFactory.class);
        messageStoreFactory = locator.getFactory(MessageStoreFactory.class);
        metricInfoFactory = locator.getFactory(MetricInfoFactory.class);
        datastorePredicateFactory = locator.getFactory(DatastorePredicateFactory.class);
        metricInfoRegistryService = locator.getService(MetricInfoRegistryService.class);
        clientInfoRegistryService = locator.getService(ClientInfoRegistryService.class);
        messageFactory = locator.getFactory(KapuaMessageFactory.class);
        dataMessageFactory = locator.getFactory(KapuaDataMessageFactory.class);
        messageStoreFacade = locator.getComponent(MessageStoreFacade.class);
        datastoreCacheManager = locator.getComponent(DatastoreCacheManager.class);
    }

    // *************************************
    // Definition of Cucumber scenario steps
    // *************************************

    @Before
    public void beforeScenario(Scenario scenario) {
        updateScenario(scenario);

    }

    @After(value = "not (@setup or @teardown)", order = 10)
    public void afterScenario() {
        try {
            deleteIndices();
        } catch (Exception e) {
            //do nothing. The datastore container may be destroyed during shutdown
            logger.warn("Error while deleting ES indeces: {}", e.getMessage());
        }
    }

    @Given("I delete indexes {string}")
    public void deleteIndexes(String indexExp) throws Exception {
        messageStoreFacade.deleteIndexes(indexExp);
    }

    @Given("I delete all indices")
    public void deleteIndices() throws Exception {
        messageStoreFacade.refreshAllIndexes();
        messageStoreFacade.deleteAllIndexes();
    }

    @When("I refresh all indices")
    public void refreshIndeces() throws Throwable {
        messageStoreFacade.refreshAllIndexes();
        datastoreCacheManager.getMetricsCache().invalidateAll();
        datastoreCacheManager.getChannelsCache().invalidateAll();
        datastoreCacheManager.getClientsCache().invalidateAll();
    }

    @When("I clear all the database caches")
    public void clearDatabaseCaches() {
        datastoreCacheManager.getChannelsCache().invalidateAll();
        datastoreCacheManager.getClientsCache().invalidateAll();
        datastoreCacheManager.getMetricsCache().invalidateAll();
        datastoreCacheManager.getMetadataCache().invalidateAll();
    }

    @Given("I have a mock data application named {string}")
    public void addMockDataApplication(String applicationId) {
        Assert.assertFalse(currentDevice.isStarted());
        SimulatedDeviceApplication app = new SimulatedDeviceApplication(applicationId);
        currentDevice.getMockApplications().put(applicationId, app);
    }

    @Given("I publish for the application {string}")
    public void selectPublishApplication(String applicationId) {
        currentApplication = applicationId;
    }

    @When("I publish on the topic {string} timestamped now")
    public void publishMetric(String topic, List<MetricEntry> metrics) {
        Map<String, Object> data = toData(metrics);
        SimulatedDeviceApplication app = getMockApplication(currentApplication);
        app.publishData(topic, Instant.now(), data);
    }

    @Then("I expect the number of messages for this device to be {long}")
    public void expectNumberOfMessages(long numberOfMessages) throws Exception {
        session.withLogin(() -> With.withUserAccount(currentDevice.getAccountName(), account -> {
            MessageQuery query = messageStoreFactory.newQuery(account.getId());
            query.setPredicate(datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, currentDevice.getClientId()));
            query.setAskTotalCount(true);
            query.setLimit((int) numberOfMessages);
            MessageListResult result = messageStoreService.query(query);

            Assert.assertEquals(numberOfMessages, result.getSize());
            Assert.assertEquals(Long.valueOf(numberOfMessages), result.getTotalCount());
            Assert.assertEquals(numberOfMessages, messageStoreService.count(query));
        }));
    }

    @Then("I delete the messages for this device")
    public void deleteMessages() throws Exception {
        session.withLogin(() -> With.withUserAccount(currentDevice.getAccountName(), account -> {
            MessageQuery query = messageStoreFactory.newQuery(account.getId());
            query.setPredicate(datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, currentDevice.getClientId()));
            query.setAskTotalCount(true);
            query.setLimit(100);
            messageStoreService.delete(query);
        }));
    }

    @Then("I expect the latest captured message on channel {string} to have the metrics")
    public void testMessageData(String topic, List<MetricEntry> expectedMetrics) throws Exception {
        session.withLogin(() -> With.withUserAccount(currentDevice.getAccountName(), account -> {
            MessageQuery query = messageStoreFactory.newQuery(account.getId());
            AndPredicate and = datastorePredicateFactory.newAndPredicate();
            and.getPredicates().add(datastorePredicateFactory.newTermPredicate(MessageField.CLIENT_ID, currentDevice.getClientId()));
            and.getPredicates().add(datastorePredicateFactory.newTermPredicate(MessageField.CHANNEL, topic));
            query.setPredicate(and);
            query.setSortFields(Arrays.asList(SortField.descending(MessageField.CAPTURED_ON.field())));
            MessageListResult result = messageStoreService.query(query);
            Assert.assertEquals(1, result.getSize());
            DatastoreMessage message = result.getFirstItem();
            Assert.assertEquals(currentDevice.getClientId(), message.getClientId());
            KapuaPayload payload = message.getPayload();
            final Map<String, Object> properties = payload.getMetrics();
            Assert.assertEquals(toData(expectedMetrics), properties);
        }));
    }

    @When("I set the datastore indexing window to {string}")
    public void setDatastoreIndexingWindowTo(String window) {
        setDatastoreIndexingWindowOption(window);
        stepData.put("IndexingWindow", window);
    }

    @Then("The index name is {string}")
    public void checkCurrentDateIndexName(String name) throws Exception {
        Date currentDate = (Date) stepData.get("Date");
        Instant instant = currentDate.toInstant();
        String window = (String) stepData.get("IndexingWindow");
        String indexName = "";

        primeException();
        indexName = KapuaLocator.getInstance().getComponent(DatastoreUtils.class).getDataIndexName(SYS_SCOPE_ID, instant.toEpochMilli(), window);

        Assert.assertEquals(name, indexName);
    }

    @Given("I prepare a random message and save it as {string}")
    public void prepareAndRememberARandomMessage(String msgKey) throws Exception {
        KapuaDataMessage tmpMessage = createTestMessage(((Account) stepData.get(LAST_ACCOUNT)).getId(),
                ((Device) stepData.get(DEVICE)).getId(),
                ((Device) stepData.get(DEVICE)).getClientId(),
                "", "");
        stepData.put(msgKey, tmpMessage);
    }

    @Given("I prepare a random message with capture date {string} and save it as {string}")
    public void prepareAndRememberARandomMessage(String captureDate, String msgKey) throws Exception {
        KapuaDataMessage tmpMessage = createTestMessage(((Account) stepData.get(LAST_ACCOUNT)).getId(),
                ((Device) stepData.get(DEVICE)).getId(),
                ((Device) stepData.get(DEVICE)).getClientId(),
                null, captureDate);
        stepData.put(msgKey, tmpMessage);
    }

    @Given("I prepare a random message with null payload and save it as {string}")
    public void prepareRandomMessageWithNullPayload(String msgKey) throws Exception {
        KapuaDataMessage tmpMessage = createTestMessage(
                ((Account) stepData.get(LAST_ACCOUNT)).getId(),
                ((Device) stepData.get(DEVICE)).getId(),
                ((Device) stepData.get(DEVICE)).getClientId(),
                "", "");
        tmpMessage.setPayload(null);
        stepData.put(msgKey, tmpMessage);
    }

    @Given("I prepare a number of messages with the following details and remember the list as {string}")
    public void prepareAListOfMessagesWithDifferentTopics(String listKey, List<CucTopic> topics) throws Exception {
        List<KapuaDataMessage> tmpList = new ArrayList<>();
        KapuaDataMessage tmpMsg = null;
        for (CucTopic tmpTopic : topics) {
            tmpMsg = createTestMessage(
                    ((Account) stepData.get(LAST_ACCOUNT)).getId(),
                    ((Device) stepData.get(DEVICE)).getId(),
                    tmpTopic.getClientId(),
                    tmpTopic.getTopic(),
                    tmpTopic.getCaptured());
            tmpList.add(tmpMsg);
        }

        stepData.put(listKey, tmpList);
    }

    @Given("I prepare a number of messages in the specified ranges and remember the list as {string}")
    public void prepareAListOfMessagesInTheSpecifiedRanges(String listKey, List<CucMessageRange> messages) throws Exception {
        List<KapuaDataMessage> tmpList = new ArrayList<>();
        KapuaDataMessage tmpMsg;
        Calendar tmpCal = Calendar.getInstance();
        for (CucMessageRange tmpMessage : messages) {
            Date startDate = KapuaDateUtils.parseDate(tmpMessage.getStartDate());
            Date endDate = KapuaDateUtils.parseDate(tmpMessage.getEndDate());
            long stepSeconds = (endDate.getTime() - startDate.getTime()) / (1000L * (tmpMessage.getCount().longValue() - 1));
            tmpCal.setTime(startDate);
            for (int cnt = 0; cnt < tmpMessage.getCount(); cnt++) {
                tmpMsg = createTestMessage(
                        ((Account) stepData.get(LAST_ACCOUNT)).getId(),
                        ((Device) stepData.get(DEVICE)).getId(),
                        tmpMessage.getClientId(),
                        tmpMessage.getTopic(),
                        tmpCal.getTime());
                tmpList.add(tmpMsg);
                tmpCal.add(Calendar.SECOND, (int) stepSeconds);
            }
        }

        stepData.put(listKey, tmpList);
    }

    @And("I set the following metrics to the message {int} from the list {string}")
    public void appendMetricsToSelectedMessage(int idx, String lstKey, List<CucMetric> metLst) throws Exception {
        List<KapuaDataMessage> tmpMsgLst = (List<KapuaDataMessage>) stepData.get(lstKey);
        KapuaDataMessage tmpMsg = tmpMsgLst.get(idx);
        tmpMsg.setPayload(dataMessageFactory.newKapuaDataPayload());
        for (CucMetric tmpMet : metLst) {
            switch (tmpMet.getType().trim().toLowerCase()) {
            case "string": {
                tmpMsg.getPayload().getMetrics().put(tmpMet.getMetric(), tmpMet.getValue());
                break;
            }
            case "int": {
                tmpMsg.getPayload().getMetrics().put(tmpMet.getMetric(), Integer.valueOf(tmpMet.getValue()));
                break;
            }
            case DOUBLE: {
                tmpMsg.getPayload().getMetrics().put(tmpMet.getMetric(), Double.valueOf(tmpMet.getValue()));
                break;
            }
            case "bool": {
                tmpMsg.getPayload().getMetrics().put(tmpMet.getMetric(), Boolean.valueOf(tmpMet.getValue().trim().toUpperCase()));
                break;
            }
            default: {
                Assert.fail(String.format("Unknown metric type [%s]", tmpMet.getType()));
                break;
            }
            }
        }
    }

    @Given("I prepare {int} random messages and remember the list as {string}")
    public void prepareAndRememberANumberOfRandomMessages(int number, String lstKey) throws Exception {
        KapuaDataMessage tmpMsg;
        List<KapuaDataMessage> msgList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            tmpMsg = createTestMessage(((Account) stepData.get(LAST_ACCOUNT)).getId(),
                    ((Device) stepData.get(DEVICE)).getId(),
                    ((Device) stepData.get(DEVICE)).getClientId(),
                    "", "");
            msgList.add(tmpMsg);
        }
        stepData.put(lstKey, msgList);
    }

    @Given("I prepare {int} randomly ordered messages and remember the list as {string}")
    public void prepareUnorderedRandomMessagesWithTopics(int number, String lstKey, List<CucTopic> topics) throws Exception {
        Account tmpAccount = (Account) stepData.get(LAST_ACCOUNT);
        KapuaDataMessage tmpMsg;
        List<KapuaDataMessage> tmpList = new ArrayList<>();
        String clientId1 = "test-device-1";
        Date sentOn1 = new Date(new Date().getTime() - 10000);
        Date capturedOn1 = new Date(new Date().getTime() - 500);
        DeviceCreator tmpDevCr1 = new DeviceCreator(tmpAccount.getId(), clientId1);
        Device device1 = deviceRegistryService.create(tmpDevCr1);
        String clientId2 = "test-device-2";
        Date sentOn2 = new Date(sentOn1.getTime() + 1000);
        Date capturedOn2 = new Date(new Date().getTime());
        DeviceCreator tmpDevCr2 = new DeviceCreator(tmpAccount.getId(), clientId2);
        Device device2 = deviceRegistryService.create(tmpDevCr2);
        Device tmpDev;
        Date sentOn;
        Date capturedOn;

        String[] metrics = new String[] {
                "m_order_metric1",
                "m_order_metric2",
                "m_order_metric3",
                "m_order_metric4",
                "m_order_metric5",
                "m_order_metric6" };
        String[] metricsValuesString = new String[] {
                "string_metric_1",
                "string_metric_2",
                "string_metric_3",
                "string_metric_4",
                "string_metric_5",
                "string_metric_6" };
        Date[] metricsValuesDate = new Date[] {
                new Date(new SimpleDateFormat(DATE_FORMAT_HH_MM_SS).parse("10:10:01 01/01/2017").getTime()),
                new Date(new SimpleDateFormat(DATE_FORMAT_HH_MM_SS).parse("10:10:02 01/01/2017").getTime()),
                new Date(new SimpleDateFormat(DATE_FORMAT_HH_MM_SS).parse("10:10:03 01/01/2017").getTime()),
                new Date(new SimpleDateFormat(DATE_FORMAT_HH_MM_SS).parse("10:10:04 01/01/2017").getTime()),
                new Date(new SimpleDateFormat(DATE_FORMAT_HH_MM_SS).parse("10:10:05 01/01/2017").getTime()),
                new Date(new SimpleDateFormat(DATE_FORMAT_HH_MM_SS).parse("10:10:06 01/01/2017").getTime()) };
        int[] metricsValuesInt = new int[] { 10, 20, 30, 40, 50, 60 };
        float[] metricsValuesFloat = new float[] { 0.002f, 10.12f, 20.22f, 33.33f, 44.44f, 55.66f };
        double[] metricsValuesDouble = new double[] { 1.002d, 11.12d, 21.22d, 34.33d, 45.44d, 56.66d };
        boolean[] metricsValuesBoolean = new boolean[] { true, true, false, true, false, false };

        for (int i = 0; i < number; i++) {
            if (i < number / 4) {
                sentOn = sentOn1;
                capturedOn = capturedOn1;
            } else if (i < number / 2) {
                sentOn = sentOn1;
                capturedOn = capturedOn2;
            } else if (i < number * 3 / 4) {
                sentOn = sentOn2;
                capturedOn = capturedOn1;
            } else {
                sentOn = sentOn2;
                capturedOn = capturedOn2;
            }
            if (i % 2 == 0) {
                tmpDev = device1;
            } else {
                tmpDev = device2;
            }

            Date receivedOn = new Date();
            CucTopic tmpTopic = topics.get(i % topics.size());
            tmpMsg = createTestMessage(
                    tmpAccount.getId(),
                    tmpDev.getId(),
                    tmpDev.getClientId(),
                    tmpTopic.getTopic(),
                    "");
            tmpMsg.setCapturedOn(capturedOn);
            tmpMsg.setSentOn(sentOn);
            tmpMsg.setReceivedOn(receivedOn);
            tmpMsg.getPayload().getMetrics().put(metrics[0], metricsValuesDate[i % metricsValuesDate.length]);
            tmpMsg.getPayload().getMetrics().put(metrics[1], metricsValuesString[i % metricsValuesString.length]);
            tmpMsg.getPayload().getMetrics().put(metrics[2], metricsValuesInt[i % metricsValuesInt.length]);
            tmpMsg.getPayload().getMetrics().put(metrics[3], metricsValuesFloat[i % metricsValuesFloat.length]);
            tmpMsg.getPayload().getMetrics().put(metrics[4], metricsValuesBoolean[i % metricsValuesBoolean.length]);
            tmpMsg.getPayload().getMetrics().put(metrics[5], metricsValuesDouble[i % metricsValuesDouble.length]);
            tmpList.add(tmpMsg);
        }
        stepData.put(lstKey, tmpList);
    }

    @Given("I store the message {string} and remember its ID as {string}")
    public void insertRandomMessageIntoDatastore(String msgKey, String idKey) throws KapuaException {
        KapuaDataMessage tmpMessage = (KapuaDataMessage) stepData.get(msgKey);
        StorableId storeId = insertMessageInStore(tmpMessage);
        stepData.put(idKey, storeId);
    }

    @Given("I store the message {string} with the server time and remember its ID as {string}")
    public void insertRandomMessageIntoDatastoreWithCurrentTimestamp(String msgKey, String idKey) throws KapuaException {
        KapuaDataMessage tmpMessage = (KapuaDataMessage) stepData.get(msgKey);
        tmpMessage.setReceivedOn(new Date());
        StorableId storeId = insertMessageInStore(tmpMessage);
        stepData.put(idKey, storeId);
    }

    @Given("I store the messages from list {string} and remember the IDs as {string}")
    public void insertRandomMessagesIntoDatastore(String msgListKey, String idListKey) throws KapuaException {
        List<KapuaDataMessage> tmpMsgList = (List<KapuaDataMessage>) stepData.get(msgListKey);
        List<StorableId> tmpIdList = insertMessagesInStore(tmpMsgList);
        stepData.put(idListKey, tmpIdList);
    }

    @Given("I store the messages from list {string} with the server time and remember the IDs as {string}")
    public void insertRandomMessagesIntoDatastoreWithCurrentTimestamps(String msgListKey, String idListKey) throws KapuaException {
        List<KapuaDataMessage> tmpMsgList = (List<KapuaDataMessage>) stepData.get(msgListKey);
        StorableId tmpId = null;
        List<StorableId> tmpList = new ArrayList<>();
        for (KapuaDataMessage tmpMsg : tmpMsgList) {
            tmpMsg.setReceivedOn(new Date());
            tmpId = insertMessageInStore(tmpMsg);
            tmpList.add(tmpId);
        }
        stepData.put(idListKey, tmpList);
    }

    //with this method I insert in bulk mode an hard-coded message, in order to speed up the insertion process
    @When("I store {int} messages in bulk mode to the index {string}")
    public void bulkInsert(int nMessages, String index) throws IOException {
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < nMessages; i++) {
            body.append("{ \"index\":{}}\n");
            body.append(
                    "{\"device_id\":\"6782593496741240747\",\"channel_parts\":[\"genericMetric\"],\"scope_id\":\"1\",\"channel\":\"genericMetric\",\"received_on\":\"2018-10-01T16:43:04.115Z\",\"ip_address\":\"127.0.0.1\",\"metrics\":{\"metric\":{\"int\":2}},\"sent_on\":null,\"body\":null,\"captured_on\":null,\"client_id\":\"samSulekBulkingHeavy\",\"timestamp\":\"2018-10-01T13:48:36.946Z\",\"sort\":[1701784116946]}\n");
        }
        Request request = new Request("POST", index + ElasticsearchResourcePaths.getBulkPath());
        request.setJsonEntity(body.toString());
        RestClient cl = (RestClient) elasticsearchClient.getClient();
        cl.performRequest(request);
    }

    @Given("I set the database to device timestamp indexing")
    public void setDatabaseToDeviceTimestampIndexing() throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        updateConfiguration(messageStoreService, account.getId(), account.getScopeId(),
                DataIndexBy.DEVICE_TIMESTAMP, MetricsIndexBy.TIMESTAMP, 30, true);
    }

    @Given("I set the database to server timestamp indexing")
    public void setDatabaseToServerTimestampIndexing() throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        updateConfiguration(messageStoreService, account.getId(), account.getScopeId(),
                DataIndexBy.SERVER_TIMESTAMP, MetricsIndexBy.TIMESTAMP, 30, true);
    }

    @When("I delete the datastore message with ID {string}")
    public void deleteDatastoreMessage(String idKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        StorableId msgId = (StorableId) stepData.get(idKey);
        messageStoreService.delete(account.getId(), msgId);
    }

    @When("I pick the ID number {int} from the list {string} and remember it as {string}")
    public void pickAMessageIdFromAList(int index, String lstKey, String idKey) {
        List<StorableId> msgIds = (List<StorableId>) stepData.get(lstKey);
        StorableId tmpId = msgIds.get(index);
        stepData.put(idKey, tmpId);
    }

    @When("I pick the ID of the channel number {int} in the list {string} and remember it as {string}")
    public void pickAChannelIdFromAList(int index, String lstKey, String idKey) {
        ChannelInfoListResult tmpLst = (ChannelInfoListResult) stepData.get(lstKey);
        StorableId tmpId = tmpLst.getItem(index).getId();
        stepData.put(idKey, tmpId);
    }

    @When("I pick message number {int} from the list {string} and remember it as {string}")
    public void pickADatastoreMessageFromList(int index, String lstKey, String msgKey) {
        MessageListResult tmpList = (MessageListResult) stepData.get(lstKey);
        DatastoreMessage tmpMsg = tmpList.getItem(index);
        stepData.put(msgKey, tmpMsg);
    }

    @When("I search for the last inserted message")
    public void findLastInsertedMessage() throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        StorableId msgId = (StorableId) stepData.get("LastStoredMessageId");
        DatastoreMessage tmpMsg = messageStoreService.find(account.getId(), msgId, StorableFetchStyle.SOURCE_FULL);
        stepData.put("LastMessageInDatastore", tmpMsg);
    }

    @When("I search for the last inserted message and save it as {string}")
    public void findLastInsertedMessage(String keyName) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        StorableId msgId = (StorableId) stepData.get("LastStoredMessageId");
        DatastoreMessage tmpMsg = messageStoreService.find(account.getId(), msgId, StorableFetchStyle.SOURCE_FULL);
        stepData.put(keyName, tmpMsg);
    }

    @When("I search for a data message with ID {string} and remember it as {string}")
    public void searchForMessageInDatastore(String idKey, String msgKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        StorableId tmpId = (StorableId) stepData.get(idKey);
        DatastoreMessage tmpMsg = messageStoreService.find(account.getId(), tmpId, StorableFetchStyle.SOURCE_FULL);
        stepData.put(msgKey, tmpMsg);
    }

    @When("I search for messages with IDs from the list {string} and store them in the list {string}")
    public void searchForMessagesInTheDatastore(String idListKey, String msgListKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        List<StorableId> msgIdLst = (List<StorableId>) stepData.get(idListKey);
        List<DatastoreMessage> tmpMsgLst = new ArrayList<>();
        DatastoreMessage tmpMsg;
        for (StorableId tmpId : msgIdLst) {
            tmpMsg = messageStoreService.find(account.getId(), tmpId, StorableFetchStyle.SOURCE_FULL);
            tmpMsgLst.add(tmpMsg);
        }
        stepData.put(msgListKey, tmpMsgLst);
    }

    @When("I perform a default query for the account messages and store the results as {string}")
    public void performADefaultMessageQuery(String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MessageQuery query = createBaseMessageQuery(account.getId(), 100);
        MessageListResult msgList = messageStoreService.query(query);
        stepData.put(lstKey, msgList);
    }

    @When("I perform an ordered query for messages and store the results as {string}")
    public void performAnOrderedMessageQuery(String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MessageQuery query = getBaseMessageQuery(account.getId(), 100, getDefaultListOrdering());
        MessageListResult msgList = messageStoreService.query(query);
        stepData.put(lstKey, msgList);
    }

    @When("I count the current account messages and store the count as {string}")
    public void countAccountMessages(String countKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MessageQuery msgQuery = createBaseMessageQuery(account.getId(), 100);
        long messageCount = messageStoreService.count(msgQuery);
        stepData.put(countKey, messageCount);
    }

    @When("I query for the current account channel(s) (again )and store (it/them) as {string}")
    public void queryForAccountChannelInfo(String chnKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ChannelInfoQuery tmpQuery = createBaseChannelInfoQuery(account.getId(), 100);
        tmpQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        ChannelInfoListResult tmpList = channelInfoRegistryService.query(tmpQuery);
        stepData.put(chnKey, tmpList);
    }

    @When("I query for the current account channels in the date range {string} to {string} and store them as {string}")
    public void queryForAccountChannelInfoByDateRange(String start, String end, String lstKey) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ChannelInfoQuery tmpQuery = createBaseChannelInfoQuery(account.getId(), 100);
        RangePredicate timestampPredicate = datastorePredicateFactory.newRangePredicate(ChannelInfoField.TIMESTAMP, KapuaDateUtils.parseDate(start), KapuaDateUtils.parseDate(end));
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(timestampPredicate);
        tmpQuery.setPredicate(andPredicate);
        tmpQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        ChannelInfoListResult tmpList = channelInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @When("I query for the current account channels with the filter {string} and store them as {string}")
    public void queryForAccountChannelInfoWithFiltering(String filter, String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        DatastoreChannel datastoreChannel = new DatastoreChannel(filter);
        ChannelInfoQuery tmpQuery = createBaseChannelInfoQuery(account.getId(), 100);
        ChannelMatchPredicate channelMatchPredicate = datastorePredicateFactory.newChannelMatchPredicate(datastoreChannel.getChannelCleaned());
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(channelMatchPredicate);
        tmpQuery.setPredicate(andPredicate);
        tmpQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        ChannelInfoListResult tmpList = channelInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @Then("The channel list {string} contains the following topics")
    public void checkThattheChannelInfoContainsTheSpecifiedTopics(String lstKey, List<CucTopic> topics) {
        ChannelInfoListResult tmpList = (ChannelInfoListResult) stepData.get(lstKey);
        Set<String> infoTopics = new HashSet<>();
        Assert.assertEquals("Wrong number of topics found!", tmpList.getSize(), topics.size());
        for (ChannelInfo tmpInfo : tmpList.getItems()) {
            infoTopics.add(tmpInfo.getName());
        }
        for (CucTopic tmpTop : topics) {
            Assert.assertTrue(String.format("The topic [%s] was not found!", tmpTop.getTopic()), infoTopics.contains(tmpTop.getTopic()));
        }
    }

    @Then("The channel list {string} have limitExceed value {booleanValue}")
    public void theChannelListHaveLimitExceedValue(String lstKey, boolean limitExceedVal) {
        theListHaveLimitExceedValue(ChannelInfoListResult.class, lstKey, limitExceedVal);
    }

    @Then("The metric list {string} have limitExceed value {booleanValue}")
    public void theMetricListHaveLimitExceedValue(String lstKey, boolean limitExceedVal) {
        theListHaveLimitExceedValue(MetricInfoListResult.class, lstKey, limitExceedVal);
    }

    @Then("The message list {string} have limitExceed value {booleanValue}")
    public void theMessageListHaveLimitExceedValue(String lstKey, boolean limitExceedVal) {
        theListHaveLimitExceedValue(MessageListResult.class, lstKey, limitExceedVal);
    }

    @Then("The client list {string} have limitExceed value {booleanValue}")
    public void theClientListHaveLimitExceedValue(String lstKey, boolean limitExceedVal) {
        theListHaveLimitExceedValue(ClientInfoListResult.class, lstKey, limitExceedVal);
    }

    @When("I query for the current account channels with limit {int} and offset {int} and store them as {string}")
    public void queryChannelWithOffsetAndLimit(int limit, int offset, String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ChannelInfoQuery tmpQuery = createBaseChannelInfoQuery(account.getId(), limit);
        tmpQuery.setOffset(offset);
        tmpQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        ChannelInfoListResult tmpList = channelInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @When("I query for the current account metrics with limit {int} and offset {int} and store them as {string}")
    public void queryMetricWithOffsetAndLimit(int limit, int offset, String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery tmpQuery = createBaseMetricInfoQuery(account.getId(), limit);
        tmpQuery.setOffset(offset);
        tmpQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        MetricInfoListResult tmpList = metricInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @When("I query for the current account messages with limit {int} and offset {int} and store them as {string}")
    public void queryMessageWithOffsetAndLimit(int limit, int offset, String lstKey) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MessageQuery tmpQuery = createBaseMessageQuery(account.getId(), limit);
        tmpQuery.setOffset(offset);
        tmpQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        primeException();
        try {
            MessageListResult tmpList = messageStoreService.query(tmpQuery);
            stepData.put(lstKey, tmpList);
        } catch (KapuaException e) {
            verifyException(e);
        }
    }

    @When("I query for the current account clients with limit {int} and offset {int} and store them as {string}")
    public void queryClientWithOffsetAndLimit(int limit, int offset, String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfoQuery tmpQuery = createBaseClientInfoQuery(account.getId(), limit);
        tmpQuery.setOffset(offset);
        tmpQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        ClientInfoListResult tmpList = clientInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @When("I count the current account channels and store the count as {string}")
    public void countAccountChannels(String countKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ChannelInfoQuery tmpQuery = createBaseChannelInfoQuery(account.getId(), 100);
        long channelCount = channelInfoRegistryService.count(tmpQuery);
        stepData.put(countKey, channelCount);
    }

    @Then("There (is/are) exactly {int} channel(s) in the list {string}")
    public void checkNumberOfQueriedChannels(int cnt, String lstKey) {
        Assert.assertEquals(cnt, ((ChannelInfoListResult) stepData.get(lstKey)).getSize());
    }

    @Then("Client {string} first published on a channel in the list {string} on {string}")
    public void checkFirstPublishDateOnChannel(String clientId, String lstKey, String date) throws Exception {
        ChannelInfoListResult chnList = (ChannelInfoListResult) stepData.get(lstKey);
        Date tmpCaptured = KapuaDateUtils.parseDate(date);
        for (ChannelInfo tmpInfo : chnList.getItems()) {
            if (tmpInfo.getClientId().equals(clientId)) {
                Assert.assertEquals(tmpCaptured, tmpInfo.getFirstMessageOn());
                return;
            }
        }
        Assert.fail(String.format("No channel matches the client id [%s]", clientId));
    }

    @Then("Client {string} last published on a channel in the list {string} on {string}")
    public void checkLastPublishDateOnChannel(String clientId, String lstKey, String date) throws Exception {
        ChannelInfoListResult chnList = (ChannelInfoListResult) stepData.get(lstKey);
        Date tmpCaptured = KapuaDateUtils.parseDate(date);
        for (ChannelInfo tmpInfo : chnList.getItems()) {
            if (tmpInfo.getClientId().equals(clientId)) {
                Assert.assertEquals(tmpCaptured, tmpInfo.getLastMessageOn());
                return;
            }
        }
        Assert.fail(String.format("No channel matches the client id [%s]", clientId));
    }

    @When("I delete all channels from the list {string}")
    public void deleteAllChannelsFromList(String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ChannelInfoListResult tmpList = (ChannelInfoListResult) stepData.get(lstKey);
        for (ChannelInfo tmpItem : tmpList.getItems()) {
            channelInfoRegistryService.delete(account.getId(), tmpItem.getId());
        }
    }

    @When("I delete the channel with the ID {string}")
    public void deleteChannelWithId(String idKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        StorableId tmpId = (StorableId) stepData.get(idKey);
        channelInfoRegistryService.delete(account.getId(), tmpId);
    }

    @When("I query for the current account metrics (again )and store (it/them) as {string}")
    public void queryForAccountMetrics(String metricKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery tmpQuery = createBaseMetricInfoQuery(account.getId(), 100);
        tmpQuery.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());
        stepData.put(METRIC_INFO_QUERY, tmpQuery);
        MetricInfoListResult tmpList = metricInfoRegistryService.query(tmpQuery);
        stepData.put(metricKey, tmpList);
    }

    @When("I query for the current account metrics in the date range {string} to {string} and store them as {string}")
    public void queryForAccountMetricsInfoByDateRange(String start, String end, String lstKey) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery tmpQuery = createBaseMetricInfoQuery(account.getId(), 100);
        RangePredicate timestampPredicate = datastorePredicateFactory.newRangePredicate(MetricInfoField.TIMESTAMP_FULL, KapuaDateUtils.parseDate(start), KapuaDateUtils.parseDate(end));
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(timestampPredicate);
        tmpQuery.setPredicate(andPredicate);
        tmpQuery.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());
        stepData.put(METRIC_INFO_QUERY, tmpQuery);
        MetricInfoListResult tmpList = metricInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @When("I perform an ordered query for the current account metrics in the date range {string} to {string} and store them as {string}")
    public void queryForAccountMetricsInfoByDateRangeOrdered(String start, String end, String lstKey) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery tmpQuery = createBaseMetricInfoQuery(account.getId(), 100);
        RangePredicate timestampPredicate = datastorePredicateFactory.newRangePredicate(MetricInfoField.TIMESTAMP_FULL, KapuaDateUtils.parseDate(start), KapuaDateUtils.parseDate(end));
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(timestampPredicate);
        tmpQuery.setPredicate(andPredicate);
        tmpQuery.setSortFields(getNamedMetricOrdering().stream().map(OrderConstraint::getField).collect(Collectors.toList()));
        tmpQuery.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());
        stepData.put(METRIC_INFO_QUERY, tmpQuery);
        MetricInfoListResult tmpList = metricInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @When("I perform an ordered query for metrics and store the results as {string}")
    public void performAnOrderedMetricQuery(String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery query = getBaseMetricQuery(account.getId(), 100, getNamedMetricOrdering());
        query.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());
        MetricInfoListResult metList = metricInfoRegistryService.query(query);
        stepData.put(lstKey, metList);
    }

    @When("I count the current account metrics and store the count as {string}")
    public void countAccountMetrics(String countKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery tmpQuery = createBaseMetricInfoQuery(account.getId(), 100);
        stepData.put(METRIC_INFO_QUERY, tmpQuery);
        stepData.remove(countKey);
        long metricCount = metricInfoRegistryService.count(tmpQuery);
        stepData.put(countKey, metricCount);
    }

    @When("I query for the metrics in topic {string} and store them as {string}")
    public void queryForTopicMetrics(String topic, String lstKey) throws KapuaException {
        stepData.remove(METRIC_INFO_QUERY);
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery tmpQuery = createBaseMetricInfoQuery(account.getId(), 100);
        tmpQuery.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        TermPredicate tmpPred = datastorePredicateFactory.newTermPredicate(MetricInfoField.CHANNEL, topic);
        MetricInfoListResult tmpList = null;
        andPredicate.getPredicates().add(tmpPred);
        tmpQuery.setPredicate(andPredicate);
        stepData.put(METRIC_INFO_QUERY, tmpQuery);
        stepData.remove(lstKey);
        tmpList = metricInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @When("I query for the metrics from client {string} and store them as {string}")
    public void queryForClientMetrics(String clientId, String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery tmpQuery = createBaseMetricInfoQuery(account.getId(), 100);
        tmpQuery.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        TermPredicate tmpPred = datastorePredicateFactory.newTermPredicate(MetricInfoField.CLIENT_ID, clientId);
        MetricInfoListResult tmpList = null;
        andPredicate.getPredicates().add(tmpPred);
        tmpQuery.setPredicate(andPredicate);
        stepData.put(METRIC_INFO_QUERY, tmpQuery);
        tmpList = metricInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @Then("There is/are exactly {int} metric(s) in the list {string}")
    public void checkNumberOfQueriedMetrics(int cnt, String lstKey) {
        MetricInfoListResult tmpResults = (MetricInfoListResult) stepData.get(lstKey);
        Assert.assertEquals(cnt, tmpResults.getSize());
    }

    @Then("Client {string} first published a metric in the list {string} on {string}")
    public void checkFirstPublishDateOfClientMetric(String clientId, String lstKey, String date) throws Exception {
        MetricInfoListResult metList = (MetricInfoListResult) stepData.get(lstKey);
        Date tmpCaptured = KapuaDateUtils.parseDate(date);
        for (MetricInfo tmpMet : metList.getItems()) {
            if (tmpMet.getClientId().equals(clientId)) {
                Assert.assertEquals(tmpMet.getFirstMessageOn(), tmpCaptured);
                return;
            }
        }
        Assert.fail(String.format("No metric matches the client id [%s]", clientId));
    }

    @Then("Client {string} last published a metric in the list {string} on {string}")
    public void checkLastPublishDateOfClientMetric(String clientId, String lstKey, String date) throws Exception {
        MetricInfoListResult metList = (MetricInfoListResult) stepData.get(lstKey);
        Date tmpCaptured = KapuaDateUtils.parseDate(date);
        for (MetricInfo tmpMet : metList.getItems()) {
            if (tmpMet.getClientId().equals(clientId)) {
                Assert.assertEquals(tmpMet.getLastMessageOn(), tmpCaptured);
                return;
            }
        }
        Assert.fail(String.format("No metric matches the client id [%s]", clientId));
    }

    @Then("The metric {string} was first published in the list {string} on {string}")
    public void checkFirstPublishDateOfMetricInList(String metric, String lstKey, String date) throws Exception {
        MetricInfoListResult metList = (MetricInfoListResult) stepData.get(lstKey);
        Date tmpCaptured = KapuaDateUtils.parseDate(date);
        for (MetricInfo tmpMet : metList.getItems()) {
            if (tmpMet.getName().equals(metric)) {
                Assert.assertEquals(tmpMet.getFirstMessageOn(), tmpCaptured);
                return;
            }
        }
        Assert.fail(String.format("There is no metric [%s]", metric));
    }

    @Then("The metric {string} was last published in the list {string} on {string}")
    public void checkLastPublishDateOfMetricInList(String metric, String lstKey, String date) throws Exception {
        MetricInfoListResult metList = (MetricInfoListResult) stepData.get(lstKey);
        Date tmpCaptured = KapuaDateUtils.parseDate(date);
        for (MetricInfo tmpMet : metList.getItems()) {
            if (tmpMet.getName().equals(metric)) {
                Assert.assertEquals(tmpMet.getLastMessageOn(), tmpCaptured);
                return;
            }
        }
        Assert.fail(String.format("There is no metric [%s]", metric));
    }

    @When("I delete all metrics from the list {string}")
    public void deleteAllMetricsFromList(String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoListResult tmpList = (MetricInfoListResult) stepData.get(lstKey);
        for (MetricInfo tmpItem : tmpList.getItems()) {
            metricInfoRegistryService.delete(account.getId(), tmpItem.getId());
        }
    }

    @When("I query for the current account client(s) (again )and store it/them as {string}")
    public void queryForAccountClientInfo(String clientKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfoQuery tmpQuery = createBaseClientInfoQuery(account.getId(), 100);
        tmpQuery.addFetchAttributes(ClientInfoField.TIMESTAMP.field());
        ClientInfoListResult tmpList = clientInfoRegistryService.query(tmpQuery);
        stepData.put(clientKey, tmpList);
    }

    @When("I query for current account clients in the date range {string} to {string} and store them as {string}")
    public void queryForAccountClientInfoByDateRange(String start, String end, String lstKey) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfoQuery tmpQuery = createBaseClientInfoQuery(account.getId(), 100);
        RangePredicate timestampPredicate = datastorePredicateFactory.newRangePredicate(ClientInfoField.TIMESTAMP, KapuaDateUtils.parseDate(start), KapuaDateUtils.parseDate(end));
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(timestampPredicate);
        tmpQuery.setPredicate(andPredicate);
        tmpQuery.addFetchAttributes(ClientInfoField.TIMESTAMP.field());
        ClientInfoListResult tmpList = clientInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @When("I query for the current account client with the Id {string} and store it as {string}")
    public void queryForAccountClientInfoByClientId(String clientId, String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfoQuery tmpQuery = createBaseClientInfoQuery(account.getId(), 100);
        TermPredicate clientIdPredicate = datastorePredicateFactory.newTermPredicate(MetricInfoField.CLIENT_ID, clientId);
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(clientIdPredicate);
        tmpQuery.setPredicate(andPredicate);
        tmpQuery.addFetchAttributes(ClientInfoField.TIMESTAMP.field());
        ClientInfoListResult tmpList = clientInfoRegistryService.query(tmpQuery);
        stepData.put(lstKey, tmpList);
    }

    @And("I create message query for metric {string}")
    public void iQueryForMessages(String metricName) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        try {
            MessageQuery messageQuery = createBaseMessageQuery(account.getId(), 100);
            messageQuery.setPredicate(datastorePredicateFactory.newExistsPredicate(String.format(MessageSchema.MESSAGE_METRICS + ".%s", metricName)));
            stepData.put(MESSAGE_QUERY, messageQuery);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @When("I count the current account clients and store the count as {string}")
    public void countAccountClients(String countKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfoQuery tmpQuery = createBaseClientInfoQuery(account.getId(), 100);
        long clientCount = clientInfoRegistryService.count(tmpQuery);
        stepData.put(countKey, clientCount);
    }

    @Then("There (is/are) exactly {int} client(s) in the list {string}")
    public void checkNumberOfQueriedClients(int cnt, String lstKey) {
        Assert.assertEquals(cnt, ((ClientInfoListResult) stepData.get(lstKey)).getSize());
    }

    @Then("Client {string} first message in the list {string} is on {string}")
    public void checkFirstPublishDateForClient(String clientId, String lstKey, String date) throws Exception {
        ClientInfoListResult cliList = (ClientInfoListResult) stepData.get(lstKey);
        Date tmpCaptured = KapuaDateUtils.parseDate(date);
        for (ClientInfo tmpInfo : cliList.getItems()) {
            if (tmpInfo.getClientId().equals(clientId)) {
                Assert.assertEquals(tmpInfo.getFirstMessageOn(), tmpCaptured);
                return;
            }
        }
        Assert.fail(String.format("No client info item matches the client id [%s]", clientId));
    }

    @Then("Client {string} last message in the list {string} is on {string}")
    public void checkLastPublishDateForClient(String clientId, String lstKey, String date) throws Exception {
        ClientInfoListResult cliList = (ClientInfoListResult) stepData.get(lstKey);
        Date tmpCaptured = KapuaDateUtils.parseDate(date);
        for (ClientInfo tmpInfo : cliList.getItems()) {
            if (tmpInfo.getClientId().equals(clientId)) {
                Assert.assertEquals(tmpInfo.getLastMessageOn(), tmpCaptured);
                return;
            }
        }
        Assert.fail(String.format("No client info item matches the client id [%s]", clientId));
    }

    @When("I delete all clients from the list {string}")
    public void deleteAllClientsFromList(String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfoListResult tmpList = (ClientInfoListResult) stepData.get(lstKey);
        for (ClientInfo tmpItem : tmpList.getItems()) {
            clientInfoRegistryService.delete(account.getId(), tmpItem.getId());
        }
    }

    @When("I delete client number {int} from the list {string}")
    public void deleteClientFromList(int index, String lstKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfoListResult tmpList = (ClientInfoListResult) stepData.get(lstKey);
        clientInfoRegistryService.delete(account.getId(), tmpList.getItem(index).getId());
    }

    @When("I search for data message with id {string}")
    public void messageStoreFind(String storeId) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        try {
            primeException();
            DatastoreMessage tmpMsg = messageStoreService.find(account.getId(), storableIdFactory.newStorableId(storeId), StorableFetchStyle.SOURCE_FULL);
            stepData.put("message", tmpMsg);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @Then("I don't find message")
    public void dontFindMessage() {
        DatastoreMessage message = (DatastoreMessage) stepData.get("message");
        Assert.assertNull(message);
    }

    @Then("Message {string} is null")
    public void checkThatTheMessageIsActuallyNull(String msgKey) {
        Assert.assertNull(stepData.get(msgKey));
    }

    @Then("The datastore message {string} matches the prepared message {string}")
    public void checkThatTheStoredMessageMatchesTheOriginal(String datastoreMsgKey, String originalMsgKey) throws KapuaException {
        KapuaDataMessage origMsg = (KapuaDataMessage) stepData.get(originalMsgKey);
        DatastoreMessage foundMsg = (DatastoreMessage) stepData.get(datastoreMsgKey);
        checkThatTheInsertedMessageMatchesTheOriginal(origMsg, foundMsg);
        isChannelForFirstMessageInStoreOK(foundMsg.getDatastoreId(), foundMsg.getTimestamp());
        isClientForFirstMessageInStoreOK(foundMsg.getDatastoreId(), foundMsg.getTimestamp());
        if (origMsg.getPayload() != null) {
            isMetricForFirstMessageInStoreOK(foundMsg.getDatastoreId(), foundMsg.getTimestamp());
        }
    }

    @Then("The datastore messages in list {string} matches the prepared messages in list {string}")
    public void checkThatTheStoredMessagesMatchTheOriginals(String datastoreMsgLstKey, String originalMsgLstKey) {
        List<KapuaDataMessage> origMsgs = (List<KapuaDataMessage>) stepData.get(originalMsgLstKey);
        List<DatastoreMessage> dataMsg = (List<DatastoreMessage>) stepData.get(datastoreMsgLstKey);
        for (int i = 0; i < origMsgs.size(); i++) {
            checkThatTheInsertedMessageMatchesTheOriginal(origMsgs.get(i), dataMsg.get(i));
        }
    }

    @Then("The datastore messages {string} and {string} match")
    public void checkSavedMessages(String firstMsg, String secondMsg) throws KapuaException {
        DatastoreMessage origMsg = (DatastoreMessage) stepData.get(firstMsg);
        DatastoreMessage foundMsg = (DatastoreMessage) stepData.get(secondMsg);
        checkThatDatastoreMessagesMatch(origMsg, foundMsg);
        isChannelForFirstMessageInStoreOK(foundMsg.getDatastoreId(), foundMsg.getTimestamp());
        isClientForFirstMessageInStoreOK(foundMsg.getDatastoreId(), foundMsg.getTimestamp());
        if ((origMsg.getPayload() != null) || (foundMsg.getPayload() != null)) {
            isMetricForFirstMessageInStoreOK(foundMsg.getDatastoreId(), foundMsg.getTimestamp());
        }
    }

    @When("I search for channel info with id {string}")
    public void channelInfoFind(String storableId) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ChannelInfo channelInfo = channelInfoRegistryService.find(account.getId(), storableIdFactory.newStorableId(storableId));
        stepData.put("channelInfo", channelInfo);
    }

    @Then("I don't find channel info")
    public void dontFindChannelInfo() {
        ChannelInfo channelInfo = (ChannelInfo) stepData.get("channelInfo");
        Assert.assertNull(channelInfo);
    }

    @Then("The channel info items {string} match the prepared messages in {string}")
    public void checkThatChannelInfoMatchesTheMessageData(String channelKey, String msgKey) {
        ChannelInfoListResult tmpChn = (ChannelInfoListResult) stepData.get(channelKey);
        List<KapuaDataMessage> tmpMsgs = (List<KapuaDataMessage>) stepData.get(msgKey);
        checkChannelInfoAgainstPreparedMessages(tmpChn, tmpMsgs);
    }

    @When("I search for metric info with id {string}")
    public void metricInfoFind(String storableId) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfo tmpInfo = metricInfoRegistryService.find(account.getId(), storableIdFactory.newStorableId(storableId));
        stepData.put("metricInfo", tmpInfo);
    }

    @Then("I don't find metric info")
    public void dontFindMetricInfo() {
        MetricInfo metricInfo = (MetricInfo) stepData.get("metricInfo");
        Assert.assertNull(metricInfo);
    }

    @Then("The metric info items {string} match the prepared messages in {string}")
    public void checkThatMetricInfoMatchesTheMessageData(String metricKey, String msgKey) {
        MetricInfoListResult tmpMet = (MetricInfoListResult) stepData.get(metricKey);
        List<KapuaDataMessage> tmpMsgs = (List<KapuaDataMessage>) stepData.get(msgKey);
        checkMetricInfoAgainstPreparedMessages(tmpMet, tmpMsgs);
    }

    @When("I search for client info with id {string}")
    public void clientInfoFind(String storableId) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfo tmpInfo = clientInfoRegistryService.find(account.getId(), storableIdFactory.newStorableId(storableId));
        stepData.put("clientInfo", tmpInfo);
    }

    @Then("I don't find client info")
    public void dontFindClientInfo() {
        ClientInfo clientInfo = (ClientInfo) stepData.get("clientInfo");
        Assert.assertNull(clientInfo);
    }

    @Then("The client info items {string} match the prepared messages in {string}")
    public void checkThatClientInfoMatchesTheMessageData(String infoKey, String msgKey) {
        ClientInfoListResult tmpList = (ClientInfoListResult) stepData.get(infoKey);
        List<KapuaDataMessage> tmpMsgs = (List<KapuaDataMessage>) stepData.get(msgKey);
        checkClientInfoAgainstPreparedMessages(tmpList, tmpMsgs);
    }

    @Given("I create message query for current account with limit {int}")
    public void createMessageQueryForAccount(int limit) {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MessageQuery messageQuery = createBaseMessageQuery(account.getId(), limit);
        stepData.put(MESSAGE_QUERY, messageQuery);
    }

    @Given("I create message query for current account from {string} to {string} with limit {int}")
    public void createMessageQueryForAccount(String fromDate, String toDate, Integer limit) throws Exception {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        try {
            Date startDate = KapuaDateUtils.parseDate(fromDate);
            Date endDate = KapuaDateUtils.parseDate(toDate);
            MessageQuery messageQuery = createBaseMessageQuery(account.getId(), limit);
            messageQuery.setPredicate(datastorePredicateFactory.newRangePredicate(MessageField.TIMESTAMP, startDate, endDate));
            stepData.put(MESSAGE_QUERY, messageQuery);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    @When("I query for data message")
    public void queryForDataMessage() throws KapuaException {
        MessageQuery messageQuery = (MessageQuery) stepData.get(MESSAGE_QUERY);
        MessageListResult result = messageStoreService.query(messageQuery);
        stepData.put("messageListResult", result);
    }

    @When("I delete the messages based on the last query")
    public void deleteMessagesByQuery() throws KapuaException {
        MessageQuery messageQuery = (MessageQuery) stepData.get(MESSAGE_QUERY);
        messageStoreService.delete(messageQuery);
    }

    @When("I delete the the message with the ID {string} from the current account")
    public void deleteMessageWithId(String id) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        messageStoreService.delete(account.getId(), storableIdFactory.newStorableId(id));
    }

    @Then("I get empty message list result")
    public void getEmptyMessageListResult() {
        MessageListResult result = (MessageListResult) stepData.get("messageListResult");
        Assert.assertTrue(result.isEmpty());
    }

    @When("I count for data message(s)")
    public void countForDataMessage() throws Exception {
        primeException();
        try {
            MessageQuery messageQuery = (MessageQuery) stepData.get(MESSAGE_QUERY);
            long count = messageStoreService.count(messageQuery);
            stepData.put(MESSAGE_COUNT_RESULT, count);
        } catch (KapuaException e) {
            verifyException(e);
        }
    }

    @Then("I get message count {int}")
    public void getDesiredMessageCountResult(int desiredCount) throws AssertionError {
        try {
            primeException();
            long count = (long) stepData.get(MESSAGE_COUNT_RESULT);
            Assert.assertEquals("This two values are not equal", desiredCount, count);
        } catch (AssertionError assertError) {
            verifyAssertionError(assertError);
        }
    }

    @Given("I create channel info query for current account with limit {int}")
    public void createChannelInofQueryForAccount(int limit) {

        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ChannelInfoQuery channelInfoQuery = createBaseChannelInfoQuery(account.getId(), limit);
        stepData.put(CHANNEL_INFO_QUERY, channelInfoQuery);
    }

    @Given("I query for the channel info of the client {string} and store the result as {string}")
    public void createChannelInofQueryForClientId(String clientId, String resKey) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ChannelInfoQuery channelInfoQuery = createBaseChannelInfoQuery(account.getId(), 100);
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        TermPredicate clientPredicate = datastorePredicateFactory.newTermPredicate(ChannelInfoField.CLIENT_ID, clientId);
        andPredicate.getPredicates().add(clientPredicate);
        channelInfoQuery.setPredicate(andPredicate);
        channelInfoQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        ChannelInfoListResult infoRes = channelInfoRegistryService.query(channelInfoQuery);
        stepData.put(resKey, infoRes);
    }

    @When("I query for channel info")
    public void queryForChannelInfo() throws KapuaException {
        ChannelInfoQuery channelInfoQuery = (ChannelInfoQuery) stepData.get(CHANNEL_INFO_QUERY);
        channelInfoQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        ChannelInfoListResult result = channelInfoRegistryService.query(channelInfoQuery);
        stepData.put("channelInfoListResult", result);
    }

    @When("I delete the channel info data based on the last query")
    public void deleteChannelInfoByQuery() throws KapuaException {
        ChannelInfoQuery tmpQuery = (ChannelInfoQuery) stepData.get(CHANNEL_INFO_QUERY);
        channelInfoRegistryService.delete(tmpQuery);
    }

    @When("I delete the the channel info data with the ID {string} from the current account")
    public void deleteChannelInfoWithId(String id) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        channelInfoRegistryService.delete(account.getId(), storableIdFactory.newStorableId(id));
    }

    @Then("I get empty channel info list result")
    public void getEmptyChannelInfoListResult() {
        ChannelInfoListResult result = (ChannelInfoListResult) stepData.get("channelInfoListResult");
        Assert.assertTrue(result.isEmpty());
    }

    @When("I count for channel info")
    public void countForChannelInfo() throws KapuaException {
        ChannelInfoQuery channelInfoQuery = (ChannelInfoQuery) stepData.get(CHANNEL_INFO_QUERY);
        long count = channelInfoRegistryService.count(channelInfoQuery);
        stepData.put("channelInfoCountResult", count);
    }

    @Then("I get channel info count {int}")
    public void getDesiredChannelInfoCountResult(int desiredCount) {
        long count = (long) stepData.get("channelInfoCountResult");
        Assert.assertEquals(desiredCount, count);
    }

    @Given("I create metric info query for current account with limit {int}")
    public void createMetricInfoQueryForAccount(int limit) {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        MetricInfoQuery metricInfoQuery = createBaseMetricInfoQuery(account.getId(), limit);
        stepData.put(METRIC_INFO_QUERY, metricInfoQuery);
    }

    @When("I query for metric info")
    public void queryForMetricInfo() throws KapuaException {
        MetricInfoQuery metricInfoQuery = (MetricInfoQuery) stepData.get(METRIC_INFO_QUERY);
        metricInfoQuery.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());
        MetricInfoListResult result = metricInfoRegistryService.query(metricInfoQuery);
        stepData.put("metricInfoListResult", result);
    }

    @Then("I get empty metric info list result")
    public void getEmptyMetricInfoListResult() {
        MetricInfoListResult result = (MetricInfoListResult) stepData.get("metricInfoListResult");
        Assert.assertTrue(result.isEmpty());
    }

    @When("I delete the metric info data based on the last query")
    public void deleteMetricsInfoByQuery() throws KapuaException {
        MetricInfoQuery tmpQuery = (MetricInfoQuery) stepData.get(METRIC_INFO_QUERY);
        metricInfoRegistryService.delete(tmpQuery);
    }

    @When("I delete the the metric info data with the ID {string} from the current account")
    public void deleteMetricsInfoWithId(String id) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        metricInfoRegistryService.delete(account.getId(), storableIdFactory.newStorableId(id));
    }

    @When("I count for metric info")
    public void countForMetricInfo() throws KapuaException {
        MetricInfoQuery metricInfoQuery = (MetricInfoQuery) stepData.get(METRIC_INFO_QUERY);
        stepData.put("metricInfoCountResult", (int) metricInfoRegistryService.count(metricInfoQuery));
    }

    @Then("I get metric info count {int}")
    public void getDesiredMetricInfoCountResult(int desiredCount) {
        int count = (int) stepData.get("metricInfoCountResult");
        Assert.assertEquals(desiredCount, count);
    }

    @Given("I create client info query for current account with limit {int}")
    public void createClientInfoQueryForAccount(int limit) {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        ClientInfoQuery clientInfoQuery = createBaseClientInfoQuery(account.getId(), limit);
        stepData.put(CLIENT_INFO_QUERY, clientInfoQuery);
    }

    @When("I query for client info")
    public void queryForClientInfo() throws KapuaException {
        ClientInfoQuery clientInfoQuery = (ClientInfoQuery) stepData.get(CLIENT_INFO_QUERY);
        clientInfoQuery.addFetchAttributes(ClientInfoField.TIMESTAMP.field());
        ClientInfoListResult result = clientInfoRegistryService.query(clientInfoQuery);
        stepData.put("clientInfoListResult", result);
    }

    @Then("I get empty client info list result")
    public void getEmptyClientInfoListResult() {
        ClientInfoListResult result = (ClientInfoListResult) stepData.get("clientInfoListResult");
        Assert.assertTrue(result.isEmpty());
    }

    @When("I delete the client info data based on the last query")
    public void deleteClientInfoByQuery() throws KapuaException {
        ClientInfoQuery tmpQuery = (ClientInfoQuery) stepData.get(CLIENT_INFO_QUERY);
        clientInfoRegistryService.delete(tmpQuery);
    }

    @When("I delete the the client info data with the ID {string} from the current account")
    public void deleteClientInfoWithId(String id) throws KapuaException {
        Account account = (Account) stepData.get(LAST_ACCOUNT);
        clientInfoRegistryService.delete(account.getId(), storableIdFactory.newStorableId(id));
    }

    @When("I count for client info")
    public void countForClientInfo() throws KapuaException {
        ClientInfoQuery clientInfoQuery = (ClientInfoQuery) stepData.get(CLIENT_INFO_QUERY);
        long count = clientInfoRegistryService.count(clientInfoQuery);
        stepData.put("clientInfoCountResult", count);
    }

    @Then("I get client info count {int}")
    public void getDesiredClientInfoCountResult(int desiredCount) {
        long count = (long) stepData.get("clientInfoCountResult");
        Assert.assertEquals(desiredCount, count);
    }

    @Then("The value of {string} is exactly {int}")
    public void checkValueOfIntegerItem(String valKey, long value) {
        Assert.assertEquals(value, (long) stepData.get(valKey));
    }

    @Then("The messages in the list {string} are stored in the default order")
    public void checkListForDefaultMessageOrdering(String lstKey) {
        MessageListResult msgLst = (MessageListResult) stepData.get(lstKey);
        checkListOrder(msgLst, getDefaultListOrdering());
    }

    @Then("The metrics in the list {string} are ordered by name")
    public void checkListForNamedMetricOrdering(String lstKey) {
        MetricInfoListResult metLst = (MetricInfoListResult) stepData.get(lstKey);
        checkListOrder(metLst, getNamedMetricOrdering());
    }

    @Given("Dataservice config enabled {string}, dataTTL {int}, rxByteLimit {int}, dataIndexBy {string}, messageUniquenessCheck {string}")
    public void configureDatastoreService(String enabled, int dataTTL, int rxByteLimit, String dataIndexBy, String messageUniquenessCheck) throws Exception {
        Map<String, Object> settings = messageStoreService.getConfigValues(KapuaId.ONE);
        settings.put("enabled", enabled.equalsIgnoreCase("TRUE"));
        settings.put("dataTTL", dataTTL);
        settings.put("rxByteLimit", rxByteLimit);
        settings.put("dataIndexBy", dataIndexBy);
        settings.put("messageUniquenessCheck", messageUniquenessCheck);
        messageStoreService.setConfigValues(KapuaId.ONE, null, settings);
    }

    @When("I delete the indexes from {string} to {string}")
    public void deleteIndexesBetweenDates(String fromDate, String toDate) throws Exception {
        primeException();
        try {
            String[] indexes = KapuaLocator.getInstance().getComponent(DatastoreUtils.class)
                    .filterIndexesTemporalWindow(getDataIndexesByAccount(getCurrentScopeId()), KapuaDateUtils.parseDate(fromDate).toInstant(),
                            KapuaDateUtils.parseDate(toDate).toInstant(), null);
            elasticsearchClient.deleteIndexes(indexes);
        } catch (Exception ex) {
            verifyException(ex);
        }
    }

    // *******************
    // * Private Helpers *
    // *******************

    private static Map<String, Object> toData(List<MetricEntry> metrics) {
        final Map<String, Object> data = new HashMap<>();

        for (final MetricEntry entry : metrics) {

            final String key = entry.getKey();
            final String stringValue = entry.getValue();
            final String type = entry.getType();

            switch (type.toUpperCase()) {
            case "STRING":
                data.put(key, stringValue);
                break;
            case "INT32":
                data.put(key, Integer.valueOf(stringValue));
                break;
            case "INT64":
                data.put(key, Long.valueOf(stringValue));
                break;
            case "DOUBLE":
                data.put(key, Double.parseDouble(stringValue));
                break;
            case "BOOLEAN":
                data.put(key, Boolean.valueOf(stringValue));
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown type: %s", type));
            }
        }
        return data;
    }

    private SimulatedDeviceApplication getMockApplication(final String applicationId) {
        final SimulatedDeviceApplication app = currentDevice.getMockApplications().get(applicationId);
        if (app == null) {
            throw new IllegalStateException(String.format("Application '%s' not found in current setup", applicationId));
        }
        return app;
    }

    private KapuaDataPayload createRandomTestPayload() throws Exception {

        KapuaDataPayload tmpTestPayload = dataMessageFactory.newKapuaDataPayload();

        byte[] randomPayload = new byte[128];
        random.nextBytes(randomPayload);
        String stringPayload = "Hello first message on!";
        byte[] tmpByteArrayPayload = ArrayUtils.addAll(randomPayload, stringPayload.getBytes());
        tmpTestPayload.setBody(tmpByteArrayPayload);

        DateFormat dfBr = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
        dfBr.setTimeZone(TimeZone.getTimeZone("Europe/Brussels"));
        Date dateBr = dfBr.parse(RANDOM_DATE_1);
        DateFormat dfLA = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
        dfLA.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        Date dateLA = dfLA.parse(RANDOM_DATE_1);
        DateFormat dfHK = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
        dfHK.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
        Date dateHK = dfHK.parse(RANDOM_DATE_1);
        DateFormat dfSyd = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
        dfSyd.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
        Date dateSyd = dfSyd.parse(RANDOM_DATE_1);

        Map<String, Object> tmpMetrics = new HashMap<>();
        tmpMetrics.put("float", random.nextFloat() * 100);
        tmpMetrics.put("float_int", Float.valueOf(random.nextInt()));
        tmpMetrics.put(DOUBLE, random.nextDouble() * 100);
        tmpMetrics.put("double_int", Float.valueOf(random.nextInt()));
        tmpMetrics.put("integer", random.nextInt());
        tmpMetrics.put("long", random.nextLong());
        tmpMetrics.put("long_int", (long) random.nextInt());
        tmpMetrics.put("string_value", Integer.toString(random.nextInt()));
        tmpMetrics.put("date_value_brussels", dateBr);
        tmpMetrics.put("date_value_la", dateLA);
        tmpMetrics.put("date_value_hk", dateHK);
        tmpMetrics.put("date_value_sydney", dateSyd);
        tmpTestPayload.setMetrics(tmpMetrics);

        return tmpTestPayload;
    }

    private KapuaPosition createRandomTestPosition(Date timeStamp) {

        KapuaPosition tmpPosition = messageFactory.newPosition();
        tmpPosition.setAltitude(20000.0 * random.nextDouble()); // 0 .. 20000
        tmpPosition.setHeading(360.0 * random.nextDouble()); // 0 .. 360
        tmpPosition.setLatitude(180.0 * (random.nextDouble() - 0.5)); // -90 .. 90
        tmpPosition.setLongitude(360.0 * (random.nextDouble() - 0.5)); // -180 .. 180
        tmpPosition.setPrecision(100.0 * random.nextDouble()); // 0 .. 100
        tmpPosition.setSatellites(random.nextInt(30)); // 0 .. 30
        tmpPosition.setSpeed(500.0 * random.nextDouble()); // 0 .. 500
        tmpPosition.setStatus(random.nextInt(10)); // 0 .. 10
        tmpPosition.setTimestamp(timeStamp);

        return tmpPosition;
    }

    private KapuaDataMessage createTestMessage(KapuaId scopeId, KapuaId deviceId, String clientId, String topic, String captured)
            throws Exception {

        String tmpTopic = ((topic != null) && !topic.isEmpty()) ? topic : "default/test/topic";
        String tmpClientId = (clientId != null) ? clientId : ((Device) stepData.get(DEVICE)).getClientId();
        KapuaDataMessage tmpMessage = dataMessageFactory.newKapuaDataMessage();

        Date tmpRecDate = new Date();
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.add(Calendar.MINUTE, -10);
        Date tmpSentDate = tmpCal.getTime();

        Date tmpCaptured = tmpRecDate;
        if ((captured != null) && !captured.isEmpty()) {
            tmpCaptured = KapuaDateUtils.parseDate(captured);
        }

        tmpMessage.setReceivedOn(tmpRecDate);
        tmpMessage.setSentOn(tmpSentDate);
        tmpMessage.setCapturedOn(tmpCaptured);
        tmpMessage.setClientId(tmpClientId);
        tmpMessage.setDeviceId(deviceId);
        tmpMessage.setScopeId(scopeId);

        KapuaDataChannel tmpChannel = dataMessageFactory.newKapuaDataChannel();
        tmpChannel.setSemanticParts(new ArrayList<>(Arrays.asList(tmpTopic.split("/"))));
        tmpMessage.setChannel(tmpChannel);

        tmpMessage.setPayload(createRandomTestPayload());
        tmpMessage.setPosition(createRandomTestPosition(tmpSentDate));

        return tmpMessage;
    }

    private KapuaDataMessage createTestMessage(KapuaId scopeId, KapuaId deviceId, String clientId, String topic, Date captured)
            throws Exception {

        String tmpTopic = (topic != null) ? topic : "default/test/topic";
        String tmpClientId = (clientId != null) ? clientId : ((Device) stepData.get(DEVICE)).getClientId();
        KapuaDataMessage tmpMessage = dataMessageFactory.newKapuaDataMessage();

        Date tmpCaptured = (captured != null) ? captured : new Date();
        Calendar tmpCal = Calendar.getInstance();
        tmpCal.setTime(tmpCaptured);
        tmpCal.add(Calendar.MINUTE, -10);
        Date tmpSentDate = tmpCal.getTime();

        tmpMessage.setReceivedOn(tmpCaptured);
        tmpMessage.setSentOn(tmpSentDate);
        tmpMessage.setCapturedOn(tmpCaptured);
        tmpMessage.setClientId(tmpClientId);
        tmpMessage.setDeviceId(deviceId);
        tmpMessage.setScopeId(scopeId);

        KapuaDataChannel tmpChannel = dataMessageFactory.newKapuaDataChannel();
        tmpChannel.setSemanticParts(new ArrayList<>(Arrays.asList(tmpTopic.split("/"))));
        tmpMessage.setChannel(tmpChannel);

        tmpMessage.setPayload(createRandomTestPayload());
        tmpMessage.setPosition(createRandomTestPosition(tmpSentDate));

        return tmpMessage;
    }

    private StorableId insertMessageInStore(KapuaDataMessage message) throws KapuaException {
        return messageStoreService.store(message);
    }

    private List<StorableId> insertMessagesInStore(List<KapuaDataMessage> messages) throws KapuaException {

        StorableId tmpId = null;
        List<StorableId> tmpList = new ArrayList<>();

        for (KapuaDataMessage tmpMsg : messages) {
            tmpId = insertMessageInStore(tmpMsg);
            tmpList.add(tmpId);
        }

        return tmpList;
    }

    private boolean areSemanticPartsEqual(List<String> parts1, List<String> parts2) {

        if ((parts1 == null) && (parts2 == null)) {
            return true;
        }

        if ((parts1 != null) && (parts2 != null)) {
            if (parts1.size() != parts2.size()) {
                return false;
            }
            for (int i = 0; i < parts1.size(); i++) {
                if (!parts1.get(i).equals(parts2.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean areMetricsEqual(Map<String, Object> properties1, Map<String, Object> properties2) {

        Assert.assertEquals(properties1.size(), properties2.size());

        if (properties1.size() > 0) {
            for (Map.Entry<String, Object> entry : properties1.entrySet()) {
                Assert.assertTrue(properties2.containsKey(entry.getKey()));
                Assert.assertEquals(entry.getValue(), properties2.get(entry.getKey()));
            }
        }
        return true;
    }

    private void isChannelForFirstMessageInStoreOK(StorableId msgId, Date storedOn) throws KapuaException {

        KapuaId tmpAccId = ((Account) stepData.get(LAST_ACCOUNT)).getId();
        String tmpClId = ((Device) stepData.get(DEVICE)).getClientId();
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(datastorePredicateFactory.newTermPredicate(ChannelInfoField.CLIENT_ID, tmpClId));
        ChannelInfoQuery channelInfoQuery = createBaseChannelInfoQuery(tmpAccId, 100);
        channelInfoQuery.setPredicate(andPredicate);
        channelInfoQuery.addFetchAttributes(ChannelInfoField.TIMESTAMP.field());
        ChannelInfoListResult channelInfoList = channelInfoRegistryService.query(channelInfoQuery);

        Assert.assertNotNull(CANNOT_FIND_CHANNEL_INFO_REGISTRY, channelInfoList);
        Assert.assertNotEquals(CANNOT_FIND_CHANNEL_INFO_REGISTRY, channelInfoList.getSize(), 0);
        Assert.assertNotNull(CANNOT_FIND_CHANNEL_INFO_REGISTRY, channelInfoList.getFirstItem());
        Assert.assertEquals("Wrong channel info message id!", channelInfoList.getFirstItem().getFirstMessageId(), msgId);
        Assert.assertEquals("Wrong channel info message on!", channelInfoList.getFirstItem().getFirstMessageOn(), storedOn);
    }

    private void isClientForFirstMessageInStoreOK(StorableId msgId, Date storedOn) throws KapuaException {

        KapuaId tmpAccId = ((Account) stepData.get(LAST_ACCOUNT)).getId();
        String tmpClId = ((Device) stepData.get(DEVICE)).getClientId();
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(datastorePredicateFactory.newTermPredicate(ClientInfoField.CLIENT_ID, tmpClId));
        ClientInfoQuery clientInfoQuery = createBaseClientInfoQuery(tmpAccId, 100);
        clientInfoQuery.setPredicate(andPredicate);
        clientInfoQuery.addFetchAttributes(ClientInfoField.TIMESTAMP.field());

        ClientInfoListResult clientInfoList = clientInfoRegistryService.query(clientInfoQuery);

        Assert.assertNotNull(CANNOT_FIND_CLIENT_INFO_REGISTRY, clientInfoList);
        Assert.assertNotEquals(CANNOT_FIND_CLIENT_INFO_REGISTRY, clientInfoList.getSize(), 0);
        Assert.assertNotNull(CANNOT_FIND_CLIENT_INFO_REGISTRY, clientInfoList.getFirstItem());
        Assert.assertEquals("Wrong client info message id!", clientInfoList.getFirstItem().getFirstMessageId(), msgId);
        Assert.assertEquals("Wrong client info message on!", clientInfoList.getFirstItem().getFirstMessageOn(), storedOn);
    }

    private void isMetricForFirstMessageInStoreOK(StorableId msgId, Date storedOn) throws KapuaException {

        KapuaId tmpAccId = ((Account) stepData.get(LAST_ACCOUNT)).getId();
        String tmpClId = ((Device) stepData.get(DEVICE)).getClientId();
        AndPredicate andPredicate = datastorePredicateFactory.newAndPredicate();
        andPredicate.getPredicates().add(datastorePredicateFactory.newTermPredicate(MetricInfoField.CLIENT_ID, tmpClId));
        MetricInfoQuery metricInfoQuery = createBaseMetricInfoQuery(tmpAccId, 100);
        metricInfoQuery.setPredicate(andPredicate);
        metricInfoQuery.addFetchAttributes(MetricInfoField.TIMESTAMP_FULL.field());

        MetricInfoListResult metricInfoList = metricInfoRegistryService.query(metricInfoQuery);

        Assert.assertNotNull(CANNOT_FIND_METRIC_INFO_REGISTRY, metricInfoList);
        Assert.assertNotEquals(CANNOT_FIND_METRIC_INFO_REGISTRY, metricInfoList.getSize(), 0);
        Assert.assertNotNull(CANNOT_FIND_METRIC_INFO_REGISTRY, metricInfoList.getFirstItem());
        Assert.assertEquals("Wrong metric info message id!", metricInfoList.getFirstItem().getFirstMessageId(), msgId);
        Assert.assertEquals("Wrong metric info message on!", metricInfoList.getFirstItem().getFirstMessageOn(), storedOn);
    }

    private boolean arePositionsEqual(KapuaPosition position1, KapuaPosition position2) {

        Assert.assertEquals(position1.getAltitude(), position2.getAltitude());
        Assert.assertEquals(position1.getHeading(), position2.getHeading());
        Assert.assertEquals(position1.getLatitude(), position2.getLatitude());
        Assert.assertEquals(position1.getLongitude(), position2.getLongitude());
        Assert.assertEquals(position1.getPrecision(), position2.getPrecision());
        Assert.assertEquals(position1.getSatellites(), position2.getSatellites());
        Assert.assertEquals(position1.getSpeed(), position2.getSpeed());
        Assert.assertEquals(position1.getStatus(), position2.getStatus());
        Assert.assertEquals(position1.getTimestamp(), position2.getTimestamp());
        return true;
    }

    // create a basic message query with default parameters. Also set the requested ordering.
    private MessageQuery getBaseMessageQuery(KapuaId scopeId, int limit, List<OrderConstraint<?>> order) {

        return createBaseMessageQuery(scopeId, limit, order.stream().map(OrderConstraint::getField).collect(Collectors.toList()));
    }

    // create a basic metric query with default parameters. Also set the requested ordering.
    private MetricInfoQuery getBaseMetricQuery(KapuaId scopeId, int limit, List<OrderConstraint<?>> order) {

        MetricInfoQuery tmpQuery = createBaseMetricInfoQuery(scopeId, limit);
        tmpQuery.setSortFields(order.stream().map(OrderConstraint::getField).collect(Collectors.toList()));

        return tmpQuery;
    }

    // Check whether the message that was inserted into the message store matches the originally prepared message
    private boolean checkThatTheInsertedMessageMatchesTheOriginal(KapuaDataMessage origMsg, DatastoreMessage foundMsg) {

        Assert.assertTrue(areSemanticPartsEqual(origMsg.getChannel().getSemanticParts(), foundMsg.getChannel().getSemanticParts()));
        if (origMsg.getPayload() != null) {
            Assert.assertArrayEquals(origMsg.getPayload().getBody(), foundMsg.getPayload().getBody());
            Assert.assertTrue(areMetricsEqual(origMsg.getPayload().getMetrics(), foundMsg.getPayload().getMetrics()));
        }
        Assert.assertTrue(arePositionsEqual(origMsg.getPosition(), foundMsg.getPosition()));
        Assert.assertTrue(foundMsg.getTimestamp().compareTo(origMsg.getReceivedOn()) >= 0);
        Assert.assertTrue(foundMsg.getTimestamp().compareTo(new Date(origMsg.getReceivedOn().getTime() + 10000)) <= 0);
        Assert.assertEquals(origMsg.getCapturedOn(), foundMsg.getCapturedOn());
        Assert.assertEquals(origMsg.getSentOn(), foundMsg.getSentOn());
        Assert.assertEquals(origMsg.getReceivedOn(), foundMsg.getReceivedOn());

        return true;
    }

    // Check that two datastore messages match
    private boolean checkThatDatastoreMessagesMatch(DatastoreMessage firstMsg, DatastoreMessage secondMsg) {

        Assert.assertEquals(firstMsg.getDatastoreId().toString(), secondMsg.getDatastoreId().toString());
        Assert.assertTrue(areSemanticPartsEqual(firstMsg.getChannel().getSemanticParts(), secondMsg.getChannel().getSemanticParts()));
        if ((firstMsg.getPayload() != null) || (secondMsg.getPayload() != null)) {
            Assert.assertArrayEquals(firstMsg.getPayload().getBody(), secondMsg.getPayload().getBody());
            Assert.assertTrue(areMetricsEqual(firstMsg.getPayload().getMetrics(), secondMsg.getPayload().getMetrics()));
        }
        Assert.assertTrue(arePositionsEqual(firstMsg.getPosition(), secondMsg.getPosition()));
        Assert.assertEquals(firstMsg.getTimestamp(), secondMsg.getTimestamp());
        Assert.assertEquals(firstMsg.getCapturedOn(), secondMsg.getCapturedOn());
        Assert.assertEquals(firstMsg.getSentOn(), secondMsg.getSentOn());
        Assert.assertEquals(firstMsg.getReceivedOn(), secondMsg.getReceivedOn());

        return true;
    }

    // Check whether the supplied channel info list contains all the client ids and topic names that were
    // defined by the messages
    private void checkChannelInfoAgainstPreparedMessages(ChannelInfoListResult chnInfo, List<KapuaDataMessage> msgLst) {

        Set<String> msgTopics = new HashSet<>();
        Set<String> msgClients = new HashSet<>();
        Set<String> infoTopics = new HashSet<>();
        Set<String> infoClients = new HashSet<>();

        Assert.assertNotNull("No channel info data!", chnInfo);
        Assert.assertNotNull(NO_MESSAGES_TO_COMPARE_TO, msgLst);

        for (KapuaDataMessage tmpMsg : msgLst) {
            msgClients.add(tmpMsg.getClientId());
            msgTopics.add(tmpMsg.getChannel().toString());
        }
        for (ChannelInfo tmpInfo : chnInfo.getItems()) {
            infoClients.add(tmpInfo.getClientId());
            infoTopics.add(tmpInfo.getName());
        }

        Assert.assertEquals(NUMBER_OF_CLIENTS_DOES_NOT_MATCH, msgClients.size(), infoClients.size());
        Assert.assertEquals("The number of topics does not match!", msgTopics.size(), infoTopics.size());

        for (String tmpTopic : msgTopics) {
            Assert.assertTrue(String.format("The topic [%s] is not found in the info list!", tmpTopic), infoTopics.contains(tmpTopic));
        }

        for (String tmpClient : msgClients) {
            Assert.assertTrue(String.format(CLIENT_ID_NOT_FOUND, tmpClient), infoClients.contains(tmpClient));
        }
    }

    // Check whether the supplied metric info list contains all the client ids and metric names that were
    // defined by the messages
    private void checkMetricInfoAgainstPreparedMessages(MetricInfoListResult metInfo, List<KapuaDataMessage> msgLst) {

        Set<String> msgMetrics = new HashSet<>();
        Set<String> msgClients = new HashSet<>();
        Set<String> infoMetrics = new HashSet<>();
        Set<String> infoClients = new HashSet<>();

        Assert.assertNotNull("No channel info data!", metInfo);
        Assert.assertNotNull(NO_MESSAGES_TO_COMPARE_TO, msgLst);

        for (KapuaDataMessage tmpMsg : msgLst) {
            msgClients.add(tmpMsg.getClientId());
            for (String tmpMet : tmpMsg.getPayload().getMetrics().keySet()) {
                msgMetrics.add(tmpMet);
            }
        }
        for (MetricInfo tmpMet : metInfo.getItems()) {
            infoClients.add(tmpMet.getClientId());
            infoMetrics.add(tmpMet.getName());
        }

        Assert.assertEquals(NUMBER_OF_CLIENTS_DOES_NOT_MATCH, msgClients.size(), infoClients.size());
        Assert.assertEquals("The number of topics does not match!", msgMetrics.size(), infoMetrics.size());

        for (String tmpMetric : msgMetrics) {
            Assert.assertTrue(String.format("The topic [%s] is not found in the info list!", tmpMetric), infoMetrics.contains(tmpMetric));
        }

        for (String tmpClient : msgClients) {
            Assert.assertTrue(String.format(CLIENT_ID_NOT_FOUND, tmpClient), infoClients.contains(tmpClient));
        }
    }

    // Check whether the supplied metric info list contains all the client ids and metric names that were
    // defined by the messages
    private void checkClientInfoAgainstPreparedMessages(ClientInfoListResult cliInfo, List<KapuaDataMessage> msgLst) {

        Set<String> msgClients = new HashSet<>();
        Set<String> infoClients = new HashSet<>();

        Assert.assertNotNull("No client info data!", cliInfo);
        Assert.assertNotNull(NO_MESSAGES_TO_COMPARE_TO, msgLst);

        for (KapuaDataMessage tmpMsg : msgLst) {
            msgClients.add(tmpMsg.getClientId());
        }
        for (ClientInfo tmpClient : cliInfo.getItems()) {
            infoClients.add(tmpClient.getClientId());
        }

        Assert.assertEquals(NUMBER_OF_CLIENTS_DOES_NOT_MATCH, msgClients.size(), infoClients.size());

        for (String tmpClient : msgClients) {
            Assert.assertTrue(String.format(CLIENT_ID_NOT_FOUND, tmpClient), infoClients.contains(tmpClient));
        }
    }

    private List<OrderConstraint<?>> getDefaultListOrdering() {

        List<OrderConstraint<?>> sort = new ArrayList<>();

        sort.add(orderConstraint(SortField.descending(MessageSchema.MESSAGE_SENT_ON), Date.class));
        sort.add(orderConstraint(SortField.ascending(MessageSchema.MESSAGE_TIMESTAMP), Date.class));
        sort.add(orderConstraint(SortField.descending(MessageSchema.MESSAGE_CLIENT_ID), String.class));

        List<SortField> order = new ArrayList<>();
        order.add(SortField.descending(MessageSchema.MESSAGE_TIMESTAMP));
        return sort;
    }

    private List<OrderConstraint<?>> getNamedMetricOrdering() {
        List<OrderConstraint<?>> sort = new ArrayList<>();
        sort.add(orderConstraint(SortField.ascending(MetricInfoSchema.METRIC_MTR_NAME_FULL), String.class));
        return sort;
    }

    private static class OrderConstraint<T extends Comparable<T>> {

        private final SortField field;
        private final Function<Object, T> valueFunction;

        public OrderConstraint(final SortField field, final Function<Object, T> valueFunction) {
            this.field = field;
            this.valueFunction = valueFunction;
        }

        public SortField getField() {
            return field;
        }

        public boolean validate(final Object previousItem, final Object currentItem) {
            final T v1 = valueFunction.apply(previousItem);
            final T v2 = valueFunction.apply(currentItem);

            if (!v2.equals(v1)) {
                checkNextValueCoherence(field, v2, v1);
                return true;
            }
            return false;
        }
    }

    private static <T extends Comparable<T>> OrderConstraint<T> orderConstraint(SortField field, Class<T> clazz) {

        return new OrderConstraint<>(field, fieldFunction(field, clazz));
    }

    private static <T extends Comparable<T>> Function<Object, T> fieldFunction(SortField field, Class<T> clazz) {

        return item -> getValue(item, field.getField(), clazz);
    }

    /**
     * Check if the message result list is correctly ordered by the provided criteria (list of fields and ordering)
     *
     * @param result
     * @param sortFieldList
     */
    private static void checkListOrder(StorableListResult<?> result, List<OrderConstraint<?>> sortFieldList) {

        Object previousItem = null;
        for (Object item : result.getItems()) {
            for (OrderConstraint<?> field : sortFieldList) {
                if (previousItem != null) {
                    if (field.validate(previousItem, item)) {
                        break;
                    }
                } else {
                    break;
                }
            }
            previousItem = item;
        }
    }

    /**
     * Check if the next value (it must be not equals, so the equals condition must be checked before calling this method) is coherent with its ordering criteria
     *
     * @param field
     * @param currentValue
     * @param previousValue
     */
    private static <T extends Comparable<T>> void checkNextValueCoherence(final SortField field, final T currentValue, final T previousValue) {

        if (SortDirection.ASC.equals(field.getSortDirection())) {
            Assert.assertTrue(String.format("The field [%s] is not correctly ordered as [%s] (%s -> %s)!", field.getField(), field.getSortDirection(), currentValue, previousValue),
                    currentValue.compareTo(previousValue) > 0);
        } else {
            Assert.assertTrue(String.format("The field [%s] is not correctly ordered as [%s] (%s -> %s)!", field.getField(), field.getSortDirection(), currentValue, previousValue),
                    currentValue.compareTo(previousValue) < 0);
        }
    }

    /**
     * Return the value of the field name provided (assuming that this value is a Comparable)
     *
     * @param field
     * @return
     */
    private static <T> T getValue(Object object, String field, Class<T> clazz) {

        try {
            Class<?> objetcClass = object.getClass();
            String getterFieldName = getFieldName(field, true);
            Method getMethod = getMethod(objetcClass, getterFieldName, "get");
            if (getMethod != null) {
                return clazz.cast(getMethod.invoke(object, new Object[0]));
            }
            getMethod = getMethod(objetcClass, getterFieldName, "is");
            if (getMethod != null) {
                return clazz.cast(getMethod.invoke(object, new Object[0]));
            }
            // else try by field access
            String fieldName = getFieldName(field, false);
            Field objField = getField(objetcClass, fieldName);
            if (objField != null) {
                return clazz.cast(objField.get(object));
            } else {
                throw new IllegalArgumentException(String.format("Cannot find getter for field [%s] or field [%s] or the field value is not a Comparable value!", field, field));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Cannot find getter for field [%s] or field [%s] or the field value is not a Comparable value!", field, field));
        }
    }

    /**
     * Return the suffix field name to compose (in a different method) the getter name. It removes the _ and append the remaining part capitalizing the first letter (if capitalizeFirstLetter = true)
     *
     * @param field
     * @return
     */
    private static String getFieldName(String field, boolean capitalizeFirstLetter) {

        String[] str = cleanupFieldName(field);

        StringBuilder fieldNameSb = new StringBuilder();
        if (capitalizeFirstLetter) {
            fieldNameSb.append(str[0].substring(0, 1).toUpperCase()).append(str[0].substring(1));
        } else {
            fieldNameSb.append(str[0]);
        }

        for (int i = 1; i < str.length; i++) {
            fieldNameSb.append(str[i].substring(0, 1).toUpperCase()).append(str[i].substring(1));
        }

        return fieldNameSb.toString();
    }

    private static String[] cleanupFieldName(String field) {

        int lastDot = field.lastIndexOf('.');
        if (lastDot > -1) {
            field = field.substring(lastDot + 1, field.length());
        }

        String[] str = field.split("_");
        if (str.length <= 0) {
            throw new IllegalArgumentException(String.format("Invalid field name [%s]", field));
        }
        return str;
    }

    /**
     * Return the method combining the prefix and the field name provided
     *
     * @param objetcClass
     * @param field
     * @param prefix
     * @return
     */
    private static Method getMethod(Class<?> objetcClass, String field, String prefix) {

        String fieldName = prefix + field.substring(0, 1).toUpperCase() + field.substring(1);

        Method objMethod = null;
        do {
            try {
                objMethod = objetcClass.getMethod(fieldName, new Class[0]);
            } catch (NoSuchMethodException e) {
                objetcClass = objetcClass.getSuperclass();
            }
        } while (objMethod == null && objetcClass != null);

        return objMethod;
    }

    /**
     * Return the field combining the prefix and the field name provided
     *
     * @param objectClass
     * @param field
     * @return
     */
    private static Field getField(Class<?> objectClass, final String field) {

        Field objField = null;
        do {
            try {
                objField = objectClass.getDeclaredField(field);
                objField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                objectClass = objectClass.getSuperclass();
            }
        } while (objField == null && objectClass != null);

        return objField;
    }

    /**
     * Update the store service configuration with the provided values
     *
     * @param messageStoreService
     * @param scopeId
     * @param dataIndexBy
     * @param metricsIndexBy
     * @param dataTTL
     * @param storageEnabled
     * @throws KapuaException
     */
    private void updateConfiguration(MessageStoreService messageStoreService, KapuaId scopeId, KapuaId parentId, DataIndexBy dataIndexBy, MetricsIndexBy metricsIndexBy, int dataTTL,
            boolean storageEnabled)
            throws KapuaException {

        Map<String, Object> config = messageStoreService.getConfigValues(scopeId);
        if (config == null) {
            config = new HashMap<>();
        }
        if (dataIndexBy != null) {
            config.put(MessageStoreConfiguration.CONFIGURATION_DATA_INDEX_BY_KEY, dataIndexBy.name());
        }
        if (metricsIndexBy != null) {
            config.put(MessageStoreConfiguration.CONFIGURATION_METRICS_INDEX_BY_KEY, metricsIndexBy.name());
        }
        config.put(MessageStoreConfiguration.CONFIGURATION_DATA_TTL_KEY, dataTTL);
        config.put(MessageStoreConfiguration.CONFIGURATION_DATA_STORAGE_ENABLED_KEY, storageEnabled);
        messageStoreService.setConfigValues(scopeId, parentId, config);
    }

    private String[] getDataIndexesByAccount(KapuaId scopeId) throws ClientException {
        return elasticsearchClient.findIndexes(new IndexRequest(scopeId.toStringId() + "-data-message-*")).getIndexes();
    }

    private void setDatastoreIndexingWindowOption(String windowOption) {
        System.setProperty(DatastoreSettingsKey.INDEXING_WINDOW_OPTION.key(), windowOption.trim().toLowerCase());
    }

    private <T extends StorableListResult> void theListHaveLimitExceedValue(Class<T> bar, String lstKey, boolean limitExceedVal) {
        T tmpList = (T) stepData.get(lstKey);
        Assert.assertEquals(tmpList.isLimitExceeded(), limitExceedVal);
    }

    /**
     * Creating query for data messages with reasonable defaults.
     *
     * @param scopeId
     *         scope
     * @param limit
     *         limit results
     * @return query
     */
    public MessageQuery createBaseMessageQuery(KapuaId scopeId, int limit) {

        MessageQuery query = new MessageQueryImpl(scopeId);
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
     * Creating query for data messages with reasonable defaults and user specified ordering.
     *
     * @param scopeId
     *         scope
     * @param limit
     *         limit results
     * @param order
     *         the required result ordering
     * @return query
     */
    public MessageQuery createBaseMessageQuery(KapuaId scopeId, int limit, List<SortField> order) {

        MessageQuery query = new MessageQueryImpl(scopeId);
        query.setAskTotalCount(true);
        query.setFetchStyle(StorableFetchStyle.SOURCE_FULL);
        query.setLimit(limit);
        query.setOffset(0);
        query.setSortFields(order);

        return query;
    }

    /**
     * Creating query for channel info with reasonable defaults.
     *
     * @param scopeId
     *         scope
     * @param limit
     *         limit results
     * @return query
     */
    public ChannelInfoQuery createBaseChannelInfoQuery(KapuaId scopeId, int limit) {

        ChannelInfoQuery query = channelInfoFactory.newQuery(scopeId);
        query.setAskTotalCount(true);
        query.setFetchStyle(StorableFetchStyle.SOURCE_FULL);
        query.setLimit(limit);
        query.setOffset(0);

        List<SortField> order = new ArrayList<>();
        order.add(SortField.descending(ChannelInfoSchema.CHANNEL_TIMESTAMP));
        query.setSortFields(order);

        return query;
    }

    /**
     * Creating query for metric info with reasonable defaults.
     *
     * @param scopeId
     *         scope
     * @param limit
     *         limit results
     * @return query
     */
    public MetricInfoQuery createBaseMetricInfoQuery(KapuaId scopeId, int limit) {

        MetricInfoQuery query = metricInfoFactory.newQuery(scopeId);
        query.setAskTotalCount(true);
        query.setFetchStyle(StorableFetchStyle.SOURCE_FULL);
        query.setLimit(limit);
        query.setOffset(0);

        List<SortField> order = new ArrayList<>();
        order.add(SortField.descending(MetricInfoSchema.METRIC_MTR_TIMESTAMP_FULL));
        query.setSortFields(order);

        return query;
    }

    /**
     * Creating query for client info with reasonable defaults.
     *
     * @param scopeId
     *         scope
     * @param limit
     *         limit results
     * @return query
     */
    public ClientInfoQuery createBaseClientInfoQuery(KapuaId scopeId, int limit) {

        ClientInfoQuery query = clientInfoFactory.newQuery(scopeId);
        query.setAskTotalCount(true);
        query.setFetchStyle(StorableFetchStyle.SOURCE_FULL);
        query.setLimit(limit);
        query.setOffset(0);

        List<SortField> order = new ArrayList<>();
        order.add(SortField.descending(ClientInfoSchema.CLIENT_TIMESTAMP));
        query.setSortFields(order);

        return query;
    }
}
