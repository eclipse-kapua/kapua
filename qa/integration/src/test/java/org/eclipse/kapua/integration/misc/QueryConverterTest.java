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
import org.eclipse.kapua.service.datastore.internal.converter.QueryConverterImpl;
import org.eclipse.kapua.service.datastore.internal.model.query.MessageQueryImpl;
import org.eclipse.kapua.service.datastore.internal.schema.MessageSchema;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.elasticsearch.client.QueryConverter;
import org.eclipse.kapua.service.elasticsearch.client.SchemaKeys;
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
