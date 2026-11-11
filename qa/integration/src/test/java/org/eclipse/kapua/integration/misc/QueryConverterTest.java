/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.integration.misc;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.datastore.internal.model.query.MessageQueryImpl;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.storeengine.client.QueryConverter;
import org.eclipse.kapua.service.storeengine.client.SchemaKeys;
import org.eclipse.kapua.service.storeengine.client.rest.QueryConverterImpl;
import org.eclipse.kapua.service.storable.model.query.AggregationField;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(JUnitTests.class)
public class QueryConverterTest {

    private final KapuaLocator locator = KapuaLocator.getInstance();
    DatastorePredicateFactory datastorePredicateFactory;
    QueryConverter qc;
    MessageQuery mq;

    @Before
    public void initialize() {
        qc = new QueryConverterImpl();
        mq = createBaseMessageQuery(KapuaId.ONE, 100);
        datastorePredicateFactory = locator.getFactory(DatastorePredicateFactory.class);
    }

    @Test
    public void queryConversionTest1() throws Exception {
        JsonNode esQuery = qc.convertCountQuery(mq);
        Assert.assertEquals("Expected and actual values should be the same!", "{}", esQuery.toString());
    }

    @Test
    public void queryConversionTest2() throws Exception {
        mq.setPredicate(datastorePredicateFactory.newExistsPredicate(String.format(MessageSchema.MESSAGE_METRICS + ".%s", "thisMustBePresentInTheEsQuery")));
        JsonNode esQuery = qc.convertCountQuery(mq);
        //Now I must check that only the "query" field is present in the json. Set with the given predicate
        Assert.assertTrue(esQuery.has("query"));
        Assert.assertEquals(1, esQuery.size());
        Assert.assertEquals("Expected and actual values should be the same!", "metrics.thisMustBePresentInTheEsQuery", esQuery.findValue(SchemaKeys.KEY_QUERY).findValue("exists").findValue("field").asText());
    }

    @Test
    public void queryConversionTest3() throws Exception {
        mq.setPredicate(datastorePredicateFactory.newExistsPredicate(String.format(MessageSchema.MESSAGE_METRICS + ".%s", "thisMustBePresentInTheEsQuery")));
        JsonNode esQuery = qc.convertDeleteQuery(mq);
        //Now I must check that only the "query" field is present in the json. Set with the given predicate
        Assert.assertTrue(esQuery.has("query"));
        Assert.assertEquals(1, esQuery.size());
        Assert.assertEquals("Expected and actual values should be the same!", "metrics.thisMustBePresentInTheEsQuery", esQuery.findValue(SchemaKeys.KEY_QUERY).findValue("exists").findValue("field").asText());
    }

    @Test
    public void queryConversionTest5() throws Exception {
        JsonNode esQuery = qc.convertQuery(mq);
        //now I must check that the query converted all the parameters, given the "search" actiontype
        Assert.assertEquals(4, esQuery.size());
        Assert.assertTrue(esQuery.has("_source"));
        Assert.assertTrue(esQuery.has("sort"));
        Assert.assertTrue(esQuery.has("from"));
        Assert.assertTrue(esQuery.has("size"));;
    }

    @Test
    public void aggregationConversionTest() throws Exception {
        mq.setAggregationField(new AggregationField("distinct_channels", "channel", 50));
        JsonNode composite = qc.convertQuery(mq).path(SchemaKeys.KEY_AGGREGATION_NODE).path("distinct_channels");
        //composite aggregation (not terms) on the single field, with the requested page size
        Assert.assertFalse(composite.has(SchemaKeys.KEY_AGGREGATION_TERMS));
        Assert.assertEquals(50, composite.path(SchemaKeys.KEY_AGGREGATION_COMPOSITE).path(SchemaKeys.KEY_AGGREGATION_SIZE).asInt());
        JsonNode sources = composite.path(SchemaKeys.KEY_AGGREGATION_COMPOSITE).path(SchemaKeys.KEY_AGGREGATION_SOURCES);
        Assert.assertEquals(1, sources.size());
        Assert.assertEquals("channel", sources.get(0).path("channel").path(SchemaKeys.KEY_AGGREGATION_TERMS).path(SchemaKeys.KEY_AGGREGATION_FIELD).asText());
        //first page: no cursor and no sub-aggregation
        Assert.assertFalse(composite.path(SchemaKeys.KEY_AGGREGATION_COMPOSITE).has(SchemaKeys.KEY_AGGREGATION_AFTER));
        Assert.assertFalse(composite.has(SchemaKeys.KEY_AGGREGATION_NODE));
    }

    @Test
    public void aggregationAfterConversionTest() throws Exception {
        mq.setAggregationField(new AggregationField("distinct_channels", "channel", 50, "bus/route/one"));
        JsonNode compositeNode = qc.convertQuery(mq).path(SchemaKeys.KEY_AGGREGATION_NODE).path("distinct_channels").path(SchemaKeys.KEY_AGGREGATION_COMPOSITE);
        //with a single source the composite key is just the value of the field
        JsonNode after = compositeNode.path(SchemaKeys.KEY_AGGREGATION_AFTER);
        Assert.assertEquals(1, after.size());
        Assert.assertEquals("bus/route/one", after.path("channel").asText());
        Assert.assertEquals(50, compositeNode.path(SchemaKeys.KEY_AGGREGATION_SIZE).asInt());
    }

    @Test
    public void aggregationMinSubAggregationConversionTest() throws Exception {
        mq.setAggregationField(new AggregationField("distinct_channels", "channel", 50, null, "timestamp"));
        JsonNode aggregation = qc.convertQuery(mq).path(SchemaKeys.KEY_AGGREGATION_NODE).path("distinct_channels");
        Assert.assertFalse(aggregation.path(SchemaKeys.KEY_AGGREGATION_COMPOSITE).has(SchemaKeys.KEY_AGGREGATION_AFTER));
        //the min sub-aggregation is a sibling of the composite one, so it is computed on each bucket
        JsonNode minAggregation = aggregation.path(SchemaKeys.KEY_AGGREGATION_NODE).path(AggregationField.MIN_AGGREGATION_NAME);
        Assert.assertEquals("timestamp", minAggregation.path(SchemaKeys.KEY_AGGREGATION_MIN).path(SchemaKeys.KEY_AGGREGATION_FIELD).asText());
    }

    @Test
    public void aggregationKeepsOtherQueryPartsConversionTest() throws Exception {
        mq.setPredicate(datastorePredicateFactory.newExistsPredicate("channel"));
        mq.setLimit(0);
        mq.setAggregationField(new AggregationField("distinct_channels", "channel", 10, "a", "timestamp"));
        JsonNode esQuery = qc.convertQuery(mq);
        Assert.assertTrue(esQuery.has(SchemaKeys.KEY_AGGREGATION_NODE));
        //the aggregation runs on the documents matched by the predicate, and no hit is returned
        Assert.assertTrue(esQuery.has(SchemaKeys.KEY_QUERY));
        Assert.assertEquals(0, esQuery.path(SchemaKeys.KEY_SIZE).asInt());
    }

    @Test
    public void aggregationIsIgnoredByCountAndDeleteConversionTest() throws Exception {
        mq.setAggregationField(new AggregationField("distinct_channels", "channel", 10, "a", "timestamp"));
        Assert.assertEquals("{}", qc.convertCountQuery(mq).toString());
        Assert.assertEquals("{}", qc.convertDeleteQuery(mq).toString());
    }

    /**
     * Creating query for data messages with reasonable defaults.
     *
     * @param scopeId scope
     * @param limit   limit results
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



}
