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
package org.eclipse.kapua.service.datastore.internal.model.query.predicate;

import org.eclipse.kapua.service.datastore.model.query.MessageField;
import org.eclipse.kapua.service.datastore.model.query.predicate.ChannelMatchPredicate;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.query.StorableField;
import org.eclipse.kapua.service.storable.model.query.predicate.MatchPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.PredicateConstants;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicateImpl;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * {@link ChannelMatchPredicate} implementation.
 *
 * @since 1.0.0
 */
public class ChannelMatchPredicateImpl extends StorablePredicateImpl implements ChannelMatchPredicate {

    private StorableField field;
    private String expression;

    /**
     * Constructor.
     *
     * @param expression
     *         the channel expression (may use wildcard)
     * @since 1.0.0
     */
    public ChannelMatchPredicateImpl(String expression) {
        this(MessageField.CHANNEL, expression);
    }

    /**
     * Constructor.
     *
     * @param field
     *         the field name
     * @param expression
     *         the channel expression (may use wildcard)
     * @since 1.0.0
     */
    public ChannelMatchPredicateImpl(StorableField field, String expression) {
        setField(field);
        setExpression(expression);
    }

    @Override
    public StorableField getField() {
        return field;
    }

    @Override
    public MatchPredicate setField(StorableField field) {
        this.field = field;

        return this;
    }

    @Override
    public String getExpression() {
        return this.expression;
    }

    @Override
    public MatchPredicate setExpression(String expression) {
        this.expression = expression;

        return this;
    }

    @Override
    public ObjectNode toSerializedMap() throws MappingException {
        ObjectNode expressionNode = newObjectNode(new KeyValueEntry[] { new KeyValueEntry(getField().field(), getExpression()) });

        ObjectNode rootNode = newObjectNode();
        rootNode.set(PredicateConstants.PREFIX_KEY, expressionNode);
        return rootNode;
    }

}
