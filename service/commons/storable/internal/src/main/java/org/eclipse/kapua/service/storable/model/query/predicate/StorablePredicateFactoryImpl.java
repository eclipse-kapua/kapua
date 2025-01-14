/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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

import javax.inject.Singleton;

import org.eclipse.kapua.service.storable.model.query.StorableField;

@Singleton
public class StorablePredicateFactoryImpl implements StorablePredicateFactory {

    @Override
    public AndPredicate newAndPredicate() {
        return new AndPredicateImpl();
    }

    @Override
    public OrPredicate newOrPredicate() {
        return new OrPredicateImpl();
    }

    @Override
    public ExistsPredicate newExistsPredicate(String... fields) {
        return new ExistsPredicateImpl(fields);
    }

    @Override
    public IdsPredicate newIdsPredicate() {
        return new IdsPredicateImpl();
    }

    @Override
    public MatchPredicate newMatchPredicate(StorableField field, String expression) {
        return new MatchPredicateImpl(field, expression);
    }

    @Override
    public <V extends Comparable<V>> RangePredicate newRangePredicate(StorableField fieldName, V minValue, V maxValue) {
        return new RangePredicateImpl(fieldName, minValue, maxValue);
    }

    @Override
    public <V> TermPredicate newTermPredicate(StorableField field, V value) {
        return new TermPredicateImpl(field, value);
    }
}
