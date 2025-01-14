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
package org.eclipse.kapua.model.query.predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;

/**
 * {@link OrPredicate} definition.
 * <p>
 * Used to link multiple {@link QueryPredicate}s in OR clause.
 *
 * @since 1.0.0
 */
public class OrPredicate implements QueryPredicate {

    private List<QueryPredicate> predicates;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public OrPredicate() {
        setPredicates(new ArrayList<>());
    }

    /**
     * Constructor which accepts a not {@code null} array of {@link QueryPredicate}s.
     *
     * @param predicates
     *         the {@link QueryPredicate}s to add.
     * @throws NullPointerException
     *         if the given parameter is {@code null}.
     * @since 1.0.0
     */
    public OrPredicate(@NotNull QueryPredicate... predicates) {
        Objects.requireNonNull(predicates);

        setPredicates(Lists.newArrayList(predicates));
    }

    /**
     * Adds the given {@link QueryPredicate} to the {@link OrPredicate}.
     *
     * @param predicate
     *         The {@link OrPredicate} to concatenate
     * @return {@code this} {@link OrPredicate}.
     * @throws NullPointerException
     *         if the given parameter is {@code null}.
     * @since 1.0.0
     */
    public OrPredicate or(@NotNull QueryPredicate predicate) {
        Objects.requireNonNull(predicates);

        getPredicates().add(predicate);

        return this;
    }

    /**
     * Gets all {@link QueryPredicate} set for this {@link OrPredicate}
     *
     * @return The {@link List} of {@link QueryPredicate}s
     * @since 1.0.0
     */
    public List<QueryPredicate> getPredicates() {
        return this.predicates;
    }

    /**
     * Sets a {@link List} of {@link QueryPredicate}s in OR clause
     *
     * @param predicates
     *         The {@link List} of {@link QueryPredicate}s
     * @throws NullPointerException
     *         if the given parameter is {@code null}.
     * @since 1.1.0
     */
    public void setPredicates(List<QueryPredicate> predicates) {
        this.predicates = predicates;
    }

}
