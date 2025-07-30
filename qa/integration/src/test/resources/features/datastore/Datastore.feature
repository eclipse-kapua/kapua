###############################################################################
# Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Eurotech - initial API and implementation
#
###############################################################################
@datastore
@env_docker

Feature: Datastore tests

  @setup
  Scenario: Start full docker environment
    Given Init Security Context
    And Start full docker environment

  Scenario: Delete items by date ranges
  Delete previously stored messages based on a time interval. The number of deleted items should
  depend on the index window. Only the items in whole hours/days/weeks of th einterval should be deleted.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-client-1"
    And I set the database to device timestamp indexing

    Given System property "datastore.index.prefix" with value ""
    And I set the datastore indexing window to "week"

    When I prepare a number of messages in the specified ranges and remember the list as "TestMessages"
      | clientId      | topic               | count | startDate                | endDate                  |
      | test-client-1 | delete/by/date/test | 92    | 2018-10-01T12:00:00.000Z | 2018-12-31T12:00:00.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices

    When I create message query for current account from "2018-10-01T00:00:00.000Z" to "2018-12-31T23:59:59.999Z" with limit 1000
    And I count for data message
    Then I get message count 92

    When I delete the indexes from "2018-10-10T00:00:00.000Z" to "2018-10-31T23:59:59.999Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 78
    When I delete the indexes from "2018-12-11T00:00:00.000Z" to "2018-12-22T23:59:59.999Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 78
    When I delete the indexes from "2018-11-16T00:00:00.000Z" to "2018-12-17T23:59:59.999Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 50

    Then I delete all indices

    When I set the datastore indexing window to "day"

    When I prepare a number of messages in the specified ranges and remember the list as "TestMessages"
      | clientId      | topic               | count | startDate                | endDate                  |
      | test-client-1 | delete/by/date/test | 744   | 2018-12-01T00:30:00.000Z | 2018-12-31T23:30:00.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices

    When I create message query for current account from "2018-12-01T00:00:00.000Z" to "2018-12-31T23:59:59.999Z" with limit 1000
    And I count for data message
    Then I get message count 744

    When I delete the indexes from "2018-12-03T12:00:00.000Z" to "2018-12-08T12:00:00.000Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 648
    When I delete the indexes from "2018-12-20T12:00:00.000Z" to "2018-12-21T12:00:00.000Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 648
    When I delete the indexes from "2018-12-20T12:00:00.000Z" to "2018-12-24T12:00:00.000Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 576

    Then I delete all indices

    When I set the datastore indexing window to "hour"

    When I prepare a number of messages in the specified ranges and remember the list as "TestMessages"
      | clientId      | topic               | count | startDate                | endDate                  |
      | test-client-1 | delete/by/date/test | 1440  | 2018-12-01T00:00:30.000Z | 2018-12-01T23:59:30.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices

    When I create message query for current account from "2018-12-01T00:00:00.000Z" to "2018-12-01T23:59:59.999Z" with limit 1000
    And I count for data message
    Then I get message count 1440

    When I delete the indexes from "2018-12-01T05:00:00.000Z" to "2018-12-01T08:30:00.000Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 1260
    When I delete the indexes from "2018-12-01T12:01:00.000Z" to "2018-12-01T13:59:00.000Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 1260
    When I delete the indexes from "2018-12-01T15:10:00.000Z" to "2018-12-01T18:50:00.000Z"
    And I refresh all indices
    And I count for data message
    Then I get message count 1140

    Then I logout

  Scenario: Delete items by the datastore ID
  Delete a previously stored message and verify that it is not in the store any more. Also delete and check the
  message related channel, metric and client info entries.

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I prepare a random message and save it as "RandomDataMessage"
    And I store the message "RandomDataMessage" and remember its ID as "RandomDataMessageId"
    And I refresh all indices
    When I search for a data message with ID "RandomDataMessageId" and remember it as "DataStoreMessage"
    Then The datastore message "DataStoreMessage" matches the prepared message "RandomDataMessage"
    When I delete the datastore message with ID "RandomDataMessageId"
    And I refresh all indices
    When I search for a data message with ID "RandomDataMessageId" and remember it as "ShouldBeNull"
    Then Message "ShouldBeNull" is null
    When I query for the current account channels and store them as "AccountChannelList"
    Then There is exactly 1 channel in the list "AccountChannelList"
    When I delete all channels from the list "AccountChannelList"
    And I refresh all indices
    When I count the current account channels and store the count as "AccountChannelCount"
    Then The value of "AccountChannelCount" is exactly 0
    When I query for the current account metrics and store them as "AccountMetriclList"
    Then There is exactly 12 metrics in the list "AccountMetriclList"
    When I delete all metrics from the list "AccountMetriclList"
    And I refresh all indices
    When I count the current account metrics and store the count as "AccountMetricCount"
    Then The value of "AccountMetricCount" is exactly 0
    When I query for the current account clients and store them as "AccountClientlList"
    Then There is exactly 1 client in the list "AccountClientlList"
    When I delete all clients from the list "AccountClientlList"
    And I refresh all indices
    When I count the current account clients and store the count as "AccountClientCount"
    Then The value of "AccountClientCount" is exactly 0
    And I delete all indices

  Scenario: Delete items based on query results

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    Given I prepare a number of messages with the following details and remember the list as "TestMessages1"
      | topic                  |
      | delete/by/query/test/1 |
    And The device "test-device-2"
    Given I prepare a number of messages with the following details and remember the list as "TestMessages2"
      | topic                   |
      | delete/by/query/tests/2 |
    And The device "test-device-3"
    Given I prepare a number of messages with the following details and remember the list as "TestMessages3"
      | topic                  |
      | delete/by/query/test/3 |
    Then I store the messages from list "TestMessages1" and remember the IDs as "StoredMessageIDs1"
    Then I store the messages from list "TestMessages2" and remember the IDs as "StoredMessageIDs2"
    Then I store the messages from list "TestMessages3" and remember the IDs as "StoredMessageIDs3"
    And I refresh all indices
    When I search for messages with IDs from the list "StoredMessageIDs1" and store them in the list "StoredMessagesList"
    Then The datastore messages in list "StoredMessagesList" matches the prepared messages in list "TestMessages1"
    When I pick the ID number 0 from the list "StoredMessageIDs1" and remember it as "SelectedMessageId"
    When I delete the datastore message with ID "SelectedMessageId"
    And I refresh all indices
    When I search for a data message with ID "SelectedMessageId" and remember it as "ShouldBeNull"
    Then Message "ShouldBeNull" is null
    When I query for the current account channels and store them as "AccountChannelList"
    Then There are exactly 3 channels in the list "AccountChannelList"
    When I pick the ID of the channel number 0 in the list "AccountChannelList" and remember it as "FirstAccountId"
    And I delete the channel with the ID "FirstAccountId"
    And I refresh all indices
    When I query for the current account channels and store them as "AccountChannelList"
    Then There are exactly 2 channels in the list "AccountChannelList"
    When I query for the current account metrics and store them as "AccountMetriclList"
    Then There are exactly 36 metrics in the list "AccountMetriclList"
    When I query for the metrics in topic "delete/by/query/test/1" and store them as "TopicMetricList"
    Then There are exactly 12 metrics in the list "TopicMetricList"
    When I delete the metric info data based on the last query
    And I refresh all indices
    When I query for the metrics in topic "delete/by/query/test/1" and store them as "TopicMetricList2x"
    Then There are exactly 0 metrics in the list "TopicMetricList2x"
    When I query for the current account clients and store them as "AccountClientlList"
    Then There are exactly 3 clients in the list "AccountClientlList"
    And I delete client number 1 from the list "AccountClientlList"
    And I refresh all indices
    When I query for the current account clients and store them as "AccountClientlList2"
    Then There are exactly 2 clients in the list "AccountClientlList2"
    And I delete all indices

  Scenario: Check the mapping for message semantic topics

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    Given I prepare a number of messages with the following details and remember the list as "TestMessages"
      | topic                             |
      | same/metric/name/different/type/1 |
      | same/metric/name/different/type/2 |
      | same/metric/name/different/type/3 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I count the current account messages and store the count as "AccountMessageCount"
    Then The value of "AccountMessageCount" is exactly 3
    When I count the current account metrics and store the count as "AccountMetricCount"
    Then The value of "AccountMetricCount" is exactly 36
    When I search for messages with IDs from the list "StoredMessageIDs" and store them in the list "StoredMessagesList"
    Then The datastore messages in list "StoredMessagesList" matches the prepared messages in list "TestMessages"
    And I delete all indices

  Scenario: Query before schema search
  Before schema is created methods that search with find for messages, channel info,
  metric info and client info, should return null values.
  Query methods on messages, channel info, metric info and client info should return empty
  results and count on the same services should return 0.
  Delete based on query or parametrized delete on non existent data should not fail. If data
  doesn't exist it is not deleted.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    Given I select account "kapua-sys"
    When I search for data message with id "fake-id"
    Then I don't find message
    When I search for channel info with id "fake-id"
    Then I don't find channel info
    When I search for metric info with id "fake-id"
    Then I don't find metric info
    When I search for client info with id "fake-id"
    Then I don't find client info
    Given I create message query for current account with limit 100
    When I query for data message
    Then I get empty message list result
    When I count for data message
    Then I get message count 0
    And I delete the the message with the ID "fake-id" from the current account
    Given I create channel info query for current account with limit 10
    When I query for channel info
    Then I get empty channel info list result
    When I count for channel info
    Then I get channel info count 0
    Then I delete the the channel info data with the ID "fake-id" from the current account
    Given I create metric info query for current account with limit 10
    When I query for metric info
    Then I get empty metric info list result
    When I count for metric info
    Then I get metric info count 0
    Then I delete the the metric info data with the ID "fake-id" from the current account
    Given I create client info query for current account with limit 10
    When I query for client info
    Then I get empty client info list result
    When I count for client info
    Then I get client info count 0
    Then I delete the the client info data with the ID "fake-id" from the current account
    Then I delete all indices
    And I logout

  Scenario: Check the database cache coherency
  This test checks the coherence of the registry cache for the metrics info (so if, once the
  cache is erased, after a new metric insert the firstMessageId and firstMessageOn contain
  the previous value)

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I prepare a random message and save it as "RandomDataMessage1"
    Then I store the message "RandomDataMessage1" and remember its ID as "RandomDataMessage1Id"
    And I refresh all indices
    When I search for a data message with ID "RandomDataMessage1Id" and remember it as "DataStoreMessage"
    Then The datastore message "DataStoreMessage" matches the prepared message "RandomDataMessage1"
    Then I clear all the database caches
    And I store the message "RandomDataMessage1" and remember its ID as "RandomDataMessage2Id"
    When I refresh all indices
    And I search for a data message with ID "RandomDataMessage1Id" and remember it as "DataStoreMessageNew"
    Then The datastore messages "DataStoreMessage" and "DataStoreMessageNew" match
    And I delete all indices

  Scenario: Check the message store
  Store few messages with few metrics, position and body (partially randomly generated) and check
  if the stored message (retrieved by id) has all the fields correctly set.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    When I prepare 15 random messages and remember the list as "RandomMessagesList"
    And I store the messages from list "RandomMessagesList" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I search for messages with IDs from the list "StoredMessageIDs" and store them in the list "StoredMessagesList"
    Then The datastore messages in list "StoredMessagesList" matches the prepared messages in list "RandomMessagesList"
    And I delete all indices

  Scenario: Query based on message ordering
  Test the correctness of the query filtering order (3 fields: date descending, date ascending,
  string descending) for messages

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I set the database to device timestamp indexing
    Given I prepare 100 randomly ordered messages and remember the list as "TestMessageList"
      | topic           |
      | bus/route/one   |
      | bus/route/one   |
      | bus/route/two/a |
      | bus/route/two/b |
      | tram/route/one  |
      | car/one         |
    And I store the messages from list "TestMessageList" and remember the IDs as "StoredMessageIDs"
    Then I refresh all indices
    When I perform an ordered query for messages and store the results as "QueriedMessageList"
    Then The messages in the list "QueriedMessageList" are stored in the default order
    And I delete all indices

  Scenario: Test the message store with timestamp indexing
  Test the correctness of the storage process with a basic message (no metrics, payload and position)
  indexing message date by device timestamp (as default).

    And I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    When I prepare a random message with null payload and save it as "TestMessageWithNullPayload"
    And I set the database to device timestamp indexing
    And I store the message "TestMessageWithNullPayload" and remember its ID as "TestMessageId"
    Then I refresh all indices
    When I perform a default query for the account messages and store the results as "AccountMessages"
    And I pick message number 0 from the list "AccountMessages" and remember it as "QueriedMessage"
    Then The datastore message "QueriedMessage" matches the prepared message "TestMessageWithNullPayload"
    And I delete all indices

  Scenario: Test the message store with server timestamp indexing
  Test the correctness of the storage process with a basic message (no metrics, payload and position)
  indexing message date by server timestamp (as default).

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    When I prepare a random message with null payload and save it as "TestMessageWithNullPayload"
    And I set the database to server timestamp indexing
    And I store the message "TestMessageWithNullPayload" with the server time and remember its ID as "TestMessageId"
    Then I refresh all indices
    When I perform a default query for the account messages and store the results as "AccountMessages"
    And I pick message number 0 from the list "AccountMessages" and remember it as "QueriedMessage"
    Then The datastore message "QueriedMessage" matches the prepared message "TestMessageWithNullPayload"
    And I delete all indices

  Scenario: ChannelInfo client ID based on the account id
  Check the correctness of the client ids info stored in the channel info data by retrieving the
  channel info by account.

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic                      |
      | test-client-1 | ci_client_by_account/1/2/3 |
      | test-client-2 | ci_client_by_account/1/2/3 |
      | test-client-3 | ci_client_by_account/1/2/3 |
      | test-client-4 | ci_client_by_account/1/2/3 |
      | test-client-4 | ci_client_by_account/1/2/3 |
      | test-client-4 | ci_client_by_account/1/2/3 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account channels and store them as "AccountChannelList"
    Then There are exactly 4 channels in the list "AccountChannelList"
    And The channel info items "AccountChannelList" match the prepared messages in "TestMessages"
    And I delete all indices

  Scenario: ChannelInfo last published date
  Check the correctness of the channel info last publish date stored by retrieving the
  channel info by account and time window.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic                            | captured                 |
      | test-client-1 | ci_client_by_pd_by_account/1/2/3 | 2017-07-03T09:00:00.000Z |
      | test-client-2 | ci_client_by_pd_by_account/1/2/3 | 2017-07-03T09:00:00.000Z |
      | test-client-2 | ci_client_by_pd_by_account/1/2/3 | 2017-07-03T09:00:10.000Z |
      | test-client-2 | ci_client_by_pd_by_account/1/2/3 | 2017-07-03T09:00:20.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account channels in the date range "2017-07-03T09:00:00.000Z" to "2017-07-03T09:00:20.000Z" and store them as "ChannelList"
    Then There are exactly 2 channels in the list "ChannelList"
    And Client "test-client-1" first published on a channel in the list "ChannelList" on "2017-07-03T09:00:00.000Z"
    And Client "test-client-1" last published on a channel in the list "ChannelList" on "2017-07-03T09:00:00.000Z"
    And Client "test-client-2" first published on a channel in the list "ChannelList" on "2017-07-03T09:00:00.000Z"
    And Client "test-client-2" last published on a channel in the list "ChannelList" on "2017-07-03T09:00:20.000Z"
    And I delete all indices

  Scenario: ChannelInfo topic data based on the account id
  Check the correctness of the topic info stored in the channel info data by retrieving the channel
  info by account.

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic                     |
      | test-client-1 | ci_topic_by_account/1/2/3 |
      | test-client-1 | ci_topic_by_account/1/2/4 |
      | test-client-1 | ci_topic_by_account/1/2/5 |
      | test-client-1 | ci_topic_by_account/1/2/6 |
      | test-client-2 | ci_topic_by_account/1/2/3 |
      | test-client-2 | ci_topic_by_account/1/2/4 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account channels and store them as "AccountChannelList"
    Then There are exactly 6 channels in the list "AccountChannelList"
    And The channel info items "AccountChannelList" match the prepared messages in "TestMessages"
    And I delete all indices

  Scenario: ChannelInfo client ID and topic data based on the client id
  Check the correctness of the client ids and topics stored in the channel info data by retrieving the
  channel info by client id

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I set the database to device timestamp indexing
    And The device "test-device-1"
    When I prepare a number of messages with the following details and remember the list as "TestMessages1"
      | clientId      | topic           |
      | test-client-1 | bus/route/one   |
      | test-client-1 | bus/route/one/a |
      | test-client-1 | bus/route/two/a |
      | test-client-1 | bus/route/two/b |
    Then I store the messages from list "TestMessages1" and remember the IDs as "StoredMessageIDs1"
    When I prepare a number of messages with the following details and remember the list as "TestMessages2"
      | clientId      | topic            |
      | test-client-2 | bus/route/three  |
      | test-client-2 | bus/route/four   |
      | test-client-2 | bus/route/four/a |
    Then I store the messages from list "TestMessages2" and remember the IDs as "StoredMessageIDs2"
    And I refresh all indices
    When I query for the channel info of the client "test-client-1" and store the result as "ClientInfoList"
    Then There are exactly 4 channels in the list "ClientInfoList"
    And The channel info items "ClientInfoList" match the prepared messages in "TestMessages1"
    And I delete all indices

  Scenario: Account wide metrics check
  Check the correctness of the metric info data stored by retrieving the metrics information by account.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
    And I set the following metrics to the message 0 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-1 | double | 123   |
      | tst-metric-2 | int    | 123   |
    And I set the following metrics to the message 1 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics and store them as "AccountMetrics"
    Then There are exactly 4 metrics in the list "AccountMetrics"
    And The metric info items "AccountMetrics" match the prepared messages in "TestMessages"
    And I delete all indices

  Scenario: MetricsInfo last published date
  Check the correctness of the metrics info last publish date stored by retrieving the
  metrics info published in a defined time window.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            | captured                 |
      | test-client-1 | test_topic/1/2/3 | 2017-07-03T09:00:00.000Z |
      | test-client-2 | test_topic/1/2/3 | 2017-07-03T09:00:00.000Z |
      | test-client-2 | test_topic/1/2/3 | 2017-07-03T09:00:10.000Z |
      | test-client-2 | test_topic/1/2/3 | 2017-07-03T09:00:20.000Z |
    And I set the following metrics to the message 0 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-1 | double | 123   |
      | tst-metric-2 | int    | 123   |
    And I set the following metrics to the message 1 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    And I set the following metrics to the message 2 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    And I set the following metrics to the message 3 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics in the date range "2017-07-03T09:00:00.000Z" to "2017-07-03T09:00:20.000Z" and store them as "AccountMetrics"
    Then There are exactly 4 metrics in the list "AccountMetrics"
    And The metric info items "AccountMetrics" match the prepared messages in "TestMessages"
    And Client "test-client-1" first published a metric in the list "AccountMetrics" on "2017-07-03T09:00:00.000Z"
    And Client "test-client-1" last published a metric in the list "AccountMetrics" on "2017-07-03T09:00:00.000Z"
    And Client "test-client-2" first published a metric in the list "AccountMetrics" on "2017-07-03T09:00:00.000Z"
    And Client "test-client-2" last published a metric in the list "AccountMetrics" on "2017-07-03T09:00:20.000Z"
    And The metric "tst-metric-1" was last published in the list "AccountMetrics" on "2017-07-03T09:00:00.000Z"
    And The metric "tst-metric-2" was last published in the list "AccountMetrics" on "2017-07-03T09:00:00.000Z"
    And The metric "tst-metric-3" was last published in the list "AccountMetrics" on "2017-07-03T09:00:20.000Z"
    And The metric "tst-metric-4" was last published in the list "AccountMetrics" on "2017-07-03T09:00:20.000Z"
    And I delete all indices

  Scenario: MetricsInfo client ID and topic data based on the client id
  Check the correctness of the client ids and metrics stored in the metrics info data by retrieving the
  metric info by client id.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
    And I set the following metrics to the message 0 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-1 | double | 123   |
      | tst-metric-2 | int    | 123   |
    And I set the following metrics to the message 1 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    Then I prepare a number of messages with the following details and remember the list as "TestMessages2"
      | clientId      | topic            |
      | test-client-2 | test_topic/1/2/3 |
    And I set the following metrics to the message 0 from the list "TestMessages2"
      | metric       | type   | value |
      | tst-metric-3 | double | 123   |
      | tst-metric-4 | int    | 123   |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    Then I store the messages from list "TestMessages2" and remember the IDs as "StoredMessageIDs2"
    And I refresh all indices
    When I query for the metrics from client "test-client-1" and store them as "Client1Metrics"
    Then There are exactly 4 metrics in the list "Client1Metrics"
    And The metric info items "Client1Metrics" match the prepared messages in "TestMessages"
    And I delete all indices

  Scenario: Query based on metrics ordering
  Test the correctness of the query filtering order (3 fields: date descending, date ascending,
  string descending) for the metrics

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And I set the database to device timestamp indexing
    Given I prepare 100 randomly ordered messages and remember the list as "TestMessageList"
      | topic           |
      | bus/route/one   |
      | bus/route/one   |
      | bus/route/two/a |
      | bus/route/two/b |
      | tram/route/one  |
      | car/one         |
    And I store the messages from list "TestMessageList" and remember the IDs as "StoredMessageIDs"
    Then I refresh all indices
    When I perform an ordered query for metrics and store the results as "QueriedMetricList"
    Then The metrics in the list "QueriedMetricList" are ordered by name
    And I delete all indices

  Scenario: Account based ClientInfo data check
  Check the correctness of the client info data stored by retrieving the client information by account.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account clients and store them as "AccountClients"
    Then There are exactly 2 clients in the list "AccountClients"
    And The client info items "AccountClients" match the prepared messages in "TestMessages"
    And I delete all indices

  Scenario: Captured date based ClientInfo data check
  Check the correctness of the client info data stored by retrieving the client info with a query based
  on the Captured date.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            | captured                 |
      | test-client-1 | test_topic/1/2/3 | 2017-07-03T09:00:00.000Z |
      | test-client-2 | test_topic/1/2/3 | 2017-07-03T09:00:00.000Z |
      | test-client-2 | test_topic/1/2/3 | 2017-07-03T09:00:10.000Z |
      | test-client-2 | test_topic/1/2/3 | 2017-07-03T09:00:20.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for current account clients in the date range "2017-07-03T09:00:00.000Z" to "2017-07-03T09:00:20.000Z" and store them as "ClientInfos"
    Then There are exactly 2 clients in the list "ClientInfos"
    And Client "test-client-1" first message in the list "ClientInfos" is on "2017-07-03T09:00:00.000Z"
    And Client "test-client-1" last message in the list "ClientInfos" is on "2017-07-03T09:00:00.000Z"
    And Client "test-client-2" first message in the list "ClientInfos" is on "2017-07-03T09:00:00.000Z"
    And Client "test-client-2" last message in the list "ClientInfos" is on "2017-07-03T09:00:20.000Z"
    And I delete all indices

  Scenario: Client Id based ClientInfo data check
  Check the correctness of the client info data stored by retrieving the client info with a query based
  on the Client Id.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages1"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
    Then I store the messages from list "TestMessages1" and remember the IDs as "StoredMessageIDs1"
    When I prepare a number of messages with the following details and remember the list as "TestMessages2"
      | clientId      | topic            |
      | test-client-2 | test_topic/1/2/4 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/4 |
    Then I store the messages from list "TestMessages2" and remember the IDs as "StoredMessageIDs2"
    And I refresh all indices
    When I query for the current account client with the Id "test-client-1" and store it as "ClientInfo"
    Then There is exactly 1 client in the list "ClientInfo"
    And The client info items "ClientInfo" match the prepared messages in "TestMessages1"
    And I delete all indices

  Scenario: Channel info queries based on datastore channel filters
  Query for account channels and use specific topic filters to narrow the range of retrieved
  channel info items.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic       |
      | test-client-1 | tba_1/1/1/1 |
      | test-client-1 | tba_1/1/1/2 |
      | test-client-1 | tba_1/1/1/3 |
      | test-client-1 | tba_1/1/2/1 |
      | test-client-1 | tba_1/1/2/2 |
      | test-client-1 | tba_1/1/2/3 |
      | test-client-1 | tba_1/2/1/1 |
      | test-client-1 | tba_1/2/1/2 |
      | test-client-1 | tba_1/2/1/3 |
      | test-client-1 | tba_1/2/2/1 |
      | test-client-1 | tba_1/2/2/2 |
      | test-client-1 | tba_1/2/2/3 |
      | test-client-1 | tba_2/1/1/1 |
      | test-client-1 | tba_2/1/1/2 |
      | test-client-1 | tba_2/1/1/3 |
      | test-client-1 | tba_2/1/2/1 |
      | test-client-1 | tba_2/1/2/2 |
      | test-client-1 | tba_2/1/2/3 |
      | test-client-1 | tba_2/2/1/1 |
      | test-client-1 | tba_2/2/1/2 |
      | test-client-1 | tba_2/2/1/3 |
      | test-client-1 | tba_2/2/2/1 |
      | test-client-1 | tba_2/2/2/2 |
      | test-client-1 | tba_2/2/2/3 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account channels and store them as "AccountChannelList"
    Then There are exactly 24 channels in the list "AccountChannelList"
    When I query for the current account channels with the filter "1/#" and store them as "QueryResult1"
    Then There are exactly 0 channels in the list "QueryResult1"
    When I query for the current account channels with the filter "tba_1/#" and store them as "QueryResult2"
    Then There are exactly 12 channels in the list "QueryResult2"
    And The channel list "QueryResult2" contains the following topics
      | topic       |
      | tba_1/1/1/1 |
      | tba_1/1/1/2 |
      | tba_1/1/1/3 |
      | tba_1/1/2/1 |
      | tba_1/1/2/2 |
      | tba_1/1/2/3 |
      | tba_1/2/1/1 |
      | tba_1/2/1/2 |
      | tba_1/2/1/3 |
      | tba_1/2/2/1 |
      | tba_1/2/2/2 |
      | tba_1/2/2/3 |
    When I query for the current account channels with the filter "tba_2/#" and store them as "QueryResult3"
    Then There are exactly 12 channels in the list "QueryResult3"
    And The channel list "QueryResult3" contains the following topics
      | topic       |
      | tba_2/1/1/1 |
      | tba_2/1/1/2 |
      | tba_2/1/1/3 |
      | tba_2/1/2/1 |
      | tba_2/1/2/2 |
      | tba_2/1/2/3 |
      | tba_2/2/1/1 |
      | tba_2/2/1/2 |
      | tba_2/2/1/3 |
      | tba_2/2/2/1 |
      | tba_2/2/2/2 |
      | tba_2/2/2/3 |
    When I query for the current account channels with the filter "tba_1/1/#" and store them as "QueryResult4"
    Then There are exactly 6 channels in the list "QueryResult4"
    And The channel list "QueryResult4" contains the following topics
      | topic       |
      | tba_1/1/1/1 |
      | tba_1/1/1/2 |
      | tba_1/1/1/3 |
      | tba_1/1/2/1 |
      | tba_1/1/2/2 |
      | tba_1/1/2/3 |
    When I query for the current account channels with the filter "tba_2/1/1/#" and store them as "QueryResult5"
    Then There are exactly 3 channels in the list "QueryResult5"
    And The channel list "QueryResult5" contains the following topics
      | topic       |
      | tba_2/1/1/1 |
      | tba_2/1/1/2 |
      | tba_2/1/1/3 |
    And I delete all indices

  Scenario: Find correct number of messages by corresponding metric
  Checking of the number of messages that are associated with the correct metrics.
  Searching for messages is done by one metric.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    When Device is connected within 10 seconds
    Then Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    Then I find device with clientId "rpione3"
    And I get the KuraMock device after 5 seconds
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/3 |
    And I set the following metrics with messages from the list "TestMessages"
      | message | metric       | type   | value |
      | 0       | tst-metric-1 | double | 123   |
      | 1       | tst-metric-1 | double | 123   |
      | 2       | tst-metric-1 | double | 123   |
      | 3       | tst-metric-2 | int    | 123   |
      | 4       | tst-metric-2 | int    | 123   |
      | 5       | tst-metric-2 | int    | 123   |
      | 6       | tst-metric-3 | bool   | 123   |
      | 7       | tst-metric-3 | bool   | 123   |
      | 8       | tst-metric-4 | string | 123   |
      | 9       | tst-metric-4 | string | 123   |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics and store them as "AccountMetrics"
    Then There are exactly 4 metrics in the list "AccountMetrics"
    When I create message query for metric "tst-metric-1"
    And I count for data message
    Then I get message count 3
    When I create message query for metric "tst-metric-2"
    And I count for data message
    Then I get message count 3
    When I create message query for metric "tst-metric-3"
    And I count for data message
    Then I get message count 2
    When I create message query for metric "tst-metric-4"
    And I count for data message
    Then I get message count 2
    And No assertion error was thrown
    Then I logout

  Scenario: Finding correct number of messages by corresponding two metrics
  Checking of the number of messages that are associated with the correct metrics.
  Searching for messages is done by two metrics.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    When Device is connected within 10 seconds
    Then Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I find device with clientId "rpione3"
    And I get the KuraMock device after 5 seconds
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/3 |
    And I set the following metrics with messages from the list "TestMessages"
      | message | metric       | type   | value |
      | 0       | tst-metric-1 | double | 123   |
      | 1       | tst-metric-1 | double | 123   |
      | 2       | tst-metric-1 | double | 123   |
      | 3       | tst-metric-2 | int    | 123   |
      | 4       | tst-metric-2 | int    | 123   |
      | 5       | tst-metric-2 | int    | 123   |
      | 6       | tst-metric-3 | bool   | 123   |
      | 7       | tst-metric-3 | bool   | 123   |
      | 8       | tst-metric-4 | string | 123   |
      | 9       | tst-metric-4 | string | 123   |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics and store them as "AccountMetrics"
    Then There are exactly 4 metrics in the list "AccountMetrics"
    When I create message query for following metrics
      | tst-metric-1 |
      | tst-metric-2 |
    And I count data messages for more metrics
    Then I count 6 data messages
    When I create message query for following metrics
      | tst-metric-3 |
      | tst-metric-4 |
    And I count data messages for more metrics
    Then I count 4 data messages
    When I create message query for following metrics
      | tst-metric-1 |
      | tst-metric-3 |
    And I count data messages for more metrics
    Then I count 5 data messages
    And No assertion error was thrown
    And I logout

  Scenario: Finding all messages by selecting all metrics
  Checking of the number of messages that are associated with the correct metrics.
  Searching for messages is done by all metrics.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    When Device is connected within 10 seconds
    Then Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/3 |
    And I set the following metrics with messages from the list "TestMessages"
      | message | metric       | type   | value |
      | 0       | tst-metric-1 | double | 123   |
      | 1       | tst-metric-1 | double | 123   |
      | 2       | tst-metric-1 | double | 123   |
      | 3       | tst-metric-2 | int    | 123   |
      | 4       | tst-metric-2 | int    | 123   |
      | 5       | tst-metric-2 | int    | 123   |
      | 6       | tst-metric-3 | bool   | 123   |
      | 7       | tst-metric-3 | bool   | 123   |
      | 8       | tst-metric-4 | string | 123   |
      | 9       | tst-metric-4 | string | 123   |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics and store them as "AccountMetrics"
    Then There are exactly 4 metrics in the list "AccountMetrics"
    When I create message query for following metrics
      | tst-metric-1 |
      | tst-metric-2 |
      | tst-metric-3 |
      | tst-metric-4 |
    And I count data messages for more metrics
    Then I count 10 data messages
    And No assertion error was thrown
    And I logout

  Scenario: Dotted metrics
  Checking of the number of messages that are associated with the correct metrics which contains dots (special chars parsed to other set of chars).
  Searching for messages is done by all metrics.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    When Device is connected within 10 seconds
    Then Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
    And I set the following metrics with messages from the list "TestMessages"
      | message | metric               | type   | value |
      | 0       | dotted.metric.first  | double | 123   |
      | 1       | dotted.metric.second | double | 123   |
      | 2       | dotted.metric.third  | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics and store them as "AccountMetrics"
    Then There are exactly 3 metrics in the list "AccountMetrics"
    When I create message query for following metrics
      | dotted.metric.first  |
      | dotted.metric.second |
      | dotted.metric.third  |
    And I count data messages for more metrics
    Then I count 3 data messages
    And No assertion error was thrown
    And I logout

  Scenario: Dotted metrics considering different types
  Checking of the number of messages that are associated with the correct metrics which contains dots (special chars parsed to other set of chars).
  Searching for messages is done by all metrics and considering the particular type.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    When Device is connected within 10 seconds
    Then Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
    And I set the following metrics with messages from the list "TestMessages"
      | message | metric               | type   | value |
      | 0       | dotted.metric.first  | double | 123   |
      | 1       | dotted.metric.second | double | 123   |
      | 2       | dotted.metric.third  | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics and store them as "AccountMetrics"
    Then There are exactly 3 metrics in the list "AccountMetrics"
    When I create message query for metric type "double" for following metrics
      | dotted.metric.first  |
      | dotted.metric.second |
      | dotted.metric.third  |
    And I count data messages for more metrics
    Then I count 2 data messages
    And No assertion error was thrown
    When I create message query for metric type "boolean" for following metrics
      | dotted.metric.first  |
      | dotted.metric.second |
      | dotted.metric.third  |
    And I count data messages for more metrics
    Then I count 1 data messages
    And No assertion error was thrown
    And I logout

  Scenario: Metrics containing combination of special chars
  Checking of the number of messages that are associated with the correct metrics which contains dots and dollars (special chars parsed to other set of chars for the insertion to DB).
  Searching for messages is done by all metrics.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    When Device is connected within 10 seconds
    Then Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
    And I set the following metrics with messages from the list "TestMessages"
      | message | metric                 | type   | value |
      | 0       | dotted.$metric$.first  | double | 123   |
      | 1       | dotted.metric$$.second | double | 123   |
      | 2       | dotted.$$$metric.third | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics and store them as "AccountMetrics"
    Then There are exactly 3 metrics in the list "AccountMetrics"
    When I create message query for following metrics
      | dotted.$metric$.first  |
      | dotted.metric$$.second |
      | dotted.$$$metric.third |
    And I count data messages for more metrics
    Then I count 3 data messages
    And No assertion error was thrown
    And I logout

  Scenario: Finding messages with incorrect metric parameters
  Checking of the number of messages that are associated with the incorrect metrics.
  Searching for messages is done by one metric.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I start the Kura Mock
    When Device is connected within 10 seconds
    Then Device status is "CONNECTED" within 10 seconds
    And I select account "kapua-sys"
    And I get the KuraMock device after 5 seconds
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/3 |
    And I set the following metrics with messages from the list "TestMessages"
      | message | metric       | type   | value |
      | 0       | tst-metric-1 | double | 123   |
      | 1       | tst-metric-1 | double | 123   |
      | 2       | tst-metric-1 | double | 123   |
      | 3       | tst-metric-2 | int    | 123   |
      | 4       | tst-metric-2 | int    | 123   |
      | 5       | tst-metric-2 | int    | 123   |
      | 6       | tst-metric-3 | bool   | 123   |
      | 7       | tst-metric-3 | bool   | 123   |
      | 8       | tst-metric-4 | string | 123   |
      | 9       | tst-metric-4 | string | 123   |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics and store them as "AccountMetrics"
    Then There are exactly 4 metrics in the list "AccountMetrics"
    When I create message query for metric "tst-metric-1"
    And I count for data message
    And I expect the assertion error "AssertionError" with the text "This two values are not equal"
    And I get message count 2
    Then An assertion error was thrown
    When I create message query for metric "tst-metric-2"
    And I count for data message
    And I expect the assertion error "AssertionError" with the text "This two values are not equal"
    And I get message count 2
    Then An assertion error was thrown
    When I create message query for metric "tst-metric-3"
    And I count for data message
    And I expect the assertion error "AssertionError" with the text "This two values are not equal"
    And I get message count 1
    Then An assertion error was thrown
    When I create message query for metric "tst-metric-4"
    And I count for data message
    And I expect the assertion error "AssertionError" with the text "This two values are not equal"
    And I get message count 1
    Then An assertion error was thrown
    And I logout

  Scenario: Create 4 clients, query all clients with offset 0 and limit 1
  Check if value of limitExceed is true.

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic                     |
      | test-client-1 | ci_topic_by_account/1/2/3 |
      | test-client-1 | ci_topic_by_account/1/2/4 |
      | test-client-1 | ci_topic_by_account/1/2/5 |
      | test-client-1 | ci_topic_by_account/1/2/6 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account channels with limit 1 and offset 0 and store them as "AccountChannelList"
    Then The channel list "AccountChannelList" have limitExceed value true
    And I delete all indices
    And I logout

  Scenario: Create 4 clients, query all clients with offset 0 and limit 4
  Check if value of limitExceed is false.

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic                     |
      | test-client-1 | ci_topic_by_account/1/2/3 |
      | test-client-1 | ci_topic_by_account/1/2/4 |
      | test-client-1 | ci_topic_by_account/1/2/5 |
      | test-client-1 | ci_topic_by_account/1/2/6 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account channels with limit 4 and offset 0 and store them as "AccountChannelList"
    Then The channel list "AccountChannelList" have limitExceed value false
    And I delete all indices
    And I logout

  Scenario: Create 4 clients, query all clients with offset 2 and limit 1
  Check if value of limitExceed is true.

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic                     |
      | test-client-1 | ci_topic_by_account/1/2/3 |
      | test-client-1 | ci_topic_by_account/1/2/4 |
      | test-client-1 | ci_topic_by_account/1/2/5 |
      | test-client-1 | ci_topic_by_account/1/2/6 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account channels with limit 1 and offset 2 and store them as "AccountChannelList"
    Then The channel list "AccountChannelList" have limitExceed value true
    And I delete all indices
    And I logout

  Scenario: Create 4 clients, query all clients with offset 1 and limit 3
  Check if value of limitExceed is false.

    Given I delete all indices
    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic                     |
      | test-client-1 | ci_topic_by_account/1/2/3 |
      | test-client-1 | ci_topic_by_account/1/2/4 |
      | test-client-1 | ci_topic_by_account/1/2/5 |
      | test-client-1 | ci_topic_by_account/1/2/6 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account channels with limit 3 and offset 1 and store them as "AccountChannelList"
    Then The channel list "AccountChannelList" have limitExceed value false
    And I delete all indices
    And I logout

  Scenario: Create 4 metrics, query all metrics with offset 0 and limit 1
  Check if value of limitExceed is true.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
    And I set the following metrics to the message 0 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-1 | double | 123   |
      | tst-metric-2 | int    | 123   |
    And I set the following metrics to the message 1 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics with limit 1 and offset 0 and store them as "AccountMetrics"
    Then The metric list "AccountMetrics" have limitExceed value true
    And I delete all indices

  Scenario: Create 4 metrics, query all metrics with offset 0 and limit 4
  Check if value of limitExceed is false.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
    And I set the following metrics to the message 0 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-1 | double | 123   |
      | tst-metric-2 | int    | 123   |
    And I set the following metrics to the message 1 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics with limit 4 and offset 0 and store them as "AccountMetrics"
    Then The metric list "AccountMetrics" have limitExceed value false
    And I delete all indices

  Scenario: Create 4 metrics, query all metrics with offset 2 and limit 1
  Check if value of limitExceed is true.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
    And I set the following metrics to the message 0 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-1 | double | 123   |
      | tst-metric-2 | int    | 123   |
    And I set the following metrics to the message 1 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics with limit 1 and offset 2 and store them as "AccountMetrics"
    Then The metric list "AccountMetrics" have limitExceed value true
    And I delete all indices

  Scenario: Create 4 metrics, query all metrics with offset 3 and limit 1
  Check if value of limitExceed is false.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    Then I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/3 |
    And I set the following metrics to the message 0 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-1 | double | 123   |
      | tst-metric-2 | int    | 123   |
    And I set the following metrics to the message 1 from the list "TestMessages"
      | metric       | type   | value |
      | tst-metric-3 | string | 123   |
      | tst-metric-4 | bool   | true  |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account metrics with limit 1 and offset 3 and store them as "AccountMetrics"
    Then The metric list "AccountMetrics" have limitExceed value false
    And I delete all indices

  Scenario: Create 4 clients, query all clients with offset 0 and limit 1
  Check if value of limitExceed is true.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/4 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/4 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account clients with limit 1 and offset 0 and store them as "ClientInfo"
    Then The client list "ClientInfo" have limitExceed value true
    And I delete all indices

  Scenario: Create 4 clients, query all clients with offset 0 and limit 4
  Check if value of limitExceed is false.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/4 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/4 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account clients with limit 4 and offset 0 and store them as "ClientInfo"
    Then The client list "ClientInfo" have limitExceed value false
    And I delete all indices

  Scenario: Create 4 clients, query all clients with offset 2 and limit 1
  Check if value of limitExceed is true.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/4 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/4 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account clients with limit 1 and offset 2 and store them as "ClientInfo"
    Then The client list "ClientInfo" have limitExceed value true
    And I delete all indices

  Scenario: Create 4 clients, query all clients with offset 3 and limit 1
  Check if value of limitExceed is false.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-device-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages with the following details and remember the list as "TestMessages"
      | clientId      | topic            |
      | test-client-1 | test_topic/1/2/3 |
      | test-client-2 | test_topic/1/2/4 |
      | test-client-3 | test_topic/1/2/3 |
      | test-client-4 | test_topic/1/2/4 |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account clients with limit 1 and offset 3 and store them as "ClientInfo"
    Then The client list "ClientInfo" have limitExceed value false
    And I delete all indices

  Scenario: Create 4 messages, query all messages with offset 0 and limit 1
  Check if value of limitExceed is true.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-client-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages in the specified ranges and remember the list as "TestMessages"
      | clientId      | topic               | count | startDate                | endDate                  |
      | test-client-1 | delete/by/date/test | 4     | 2018-10-01T12:00:00.000Z | 2018-12-31T12:00:00.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account messages with limit 1 and offset 0 and store them as "MessageInfo"
    Then The message list "MessageInfo" have limitExceed value true
    And I delete all indices

  Scenario: Create 4 messages, query all messages with offset 0 and limit 4
  Check if value of limitExceed is false.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-client-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages in the specified ranges and remember the list as "TestMessages"
      | clientId      | topic               | count | startDate                | endDate                  |
      | test-client-1 | delete/by/date/test | 4     | 2018-10-01T12:00:00.000Z | 2018-12-31T12:00:00.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account messages with limit 4 and offset 0 and store them as "MessageInfo"
    Then The message list "MessageInfo" have limitExceed value false
    And I delete all indices

  Scenario: Create 4 messages, query all messages with offset 2 and limit 1
  Check if value of limitExceed is true.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-client-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages in the specified ranges and remember the list as "TestMessages"
      | clientId      | topic               | count | startDate                | endDate                  |
      | test-client-1 | delete/by/date/test | 4     | 2018-10-01T12:00:00.000Z | 2018-12-31T12:00:00.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account messages with limit 1 and offset 2 and store them as "MessageInfo"
    Then The message list "MessageInfo" have limitExceed value true
    And I delete all indices

  Scenario: Create 4 messages, query all messages with offset 3 and limit 1
  Check if value of limitExceed is false.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-client-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages in the specified ranges and remember the list as "TestMessages"
      | clientId      | topic               | count | startDate                | endDate                  |
      | test-client-1 | delete/by/date/test | 4     | 2018-10-01T12:00:00.000Z | 2018-12-31T12:00:00.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    When I query for the current account messages with limit 1 and offset 3 and store them as "MessageInfo"
    Then The message list "MessageInfo" have limitExceed value false
    And I delete all indices

  Scenario: Create some messages, query all messages with not allowed offset and limit values
  Check if value of limitExceed is false.

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    And The device "test-client-1"
    And I set the database to device timestamp indexing
    When I prepare a number of messages in the specified ranges and remember the list as "TestMessages"
      | clientId      | topic               | count | startDate                | endDate                  |
      | test-client-1 | delete/by/date/test | 2     | 2018-10-01T12:00:00.000Z | 2018-12-31T12:00:00.000Z |
    Then I store the messages from list "TestMessages" and remember the IDs as "StoredMessageIDs"
    And I refresh all indices
    Given I expect the exception "KapuaIllegalNullArgumentException" with the text "*"
    When I query for the current account messages with limit -1 and offset 3 and store them as "MessageInfo"
    Then An exception was thrown
    Given I expect the exception "KapuaIllegalNullArgumentException" with the text "*"
    When I query for the current account messages with limit 5 and offset -3 and store them as "MessageInfo"
    Then An exception was thrown
    Given I expect the exception "KapuaIllegalArgumentException" with the text "*"
    When I query for the current account messages with limit 9999 and offset 3 and store them as "MessageInfo"
    Then An exception was thrown
    Given I expect the exception "KapuaIllegalArgumentException" with the text "*"
    When I query for the current account messages with limit 3 and offset 9999 and store them as "MessageInfo"
    Then An exception was thrown
    And I delete all indices

  Scenario: Create 10k messages and more, test if limitExceeded parameter is right when doing queries

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    When I store 10000 messages in bulk mode to the index "1-data-message-2023-1"
    And I refresh all indices
    When I query for the current account messages with limit 10000 and offset 0 and store them as "MessageInfo"
    Then The message list "MessageInfo" have limitExceed value false
    When I store 1 messages in bulk mode to the index "1-data-message-2023-1"
    And I refresh all indices
    When I query for the current account messages with limit 10000 and offset 0 and store them as "MessageInfo"
    Then The message list "MessageInfo" have limitExceed value true
    And I delete all indices

  Scenario: Create 10k messages and more, test if counting messages works despite internal limit on ES to search more than 10k docs

    Given I login as user with name "kapua-sys" and password "kapua-password"
    And I select account "kapua-sys"
    When I store 10005 messages in bulk mode to the index "1-data-message-2023-1"
    And I refresh all indices
    When I create message query for current account with limit 1
    And I count for data message
    Then I get message count 10005
    And I delete all indices

  @teardown
  Scenario: Stop full docker environment
    Given Stop full docker environment
    And Clean Locator Instance
