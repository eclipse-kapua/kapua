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
package org.eclipse.kapua.service.storable.model.query.predicate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.service.storable.exception.MappingException;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * {@link AndPredicateImpl} implementation.
 *
 * @since 1.0.0
 */
public class AndPredicateImpl extends StorablePredicateImpl implements AndPredicate {

    private List<StorablePredicate> predicates;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public AndPredicateImpl() {
    }

    /**
     * Constructor.
     *
     * @param storablePredicates
     *         The {@link StorablePredicate}s to add.
     * @since 1.0.0
     */
    public AndPredicateImpl(StorablePredicate... storablePredicates) {
        this();

        setPredicates(Lists.newArrayList(storablePredicates));
    }

    public List<StorablePredicate> getPredicates() {
        if (predicates == null) {
            predicates = new ArrayList<>();
        }

        return this.predicates;
    }

    public AndPredicateImpl addPredicate(StorablePredicate storablePredicate) {
        getPredicates().add(storablePredicate);
        return this;
    }

    public void setPredicates(List<StorablePredicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public ObjectNode toSerializedMap() throws MappingException {
        ArrayNode conditionsNode = newArrayNodeFromPredicates(getPredicates());

        ObjectNode termNode = newObjectNode();
        termNode.set(PredicateConstants.MUST_KEY, conditionsNode);

        ObjectNode rootNode = newObjectNode();
        rootNode.set(PredicateConstants.BOOL_KEY, termNode);
        return rootNode;
    }

}
