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

/**
 * {@link AttributePredicate} definition.
 *
 * @param <T>
 *         Attribute value type.
 * @since 1.3.0
 */
public class AttributePredicate<T> implements QueryPredicate {

    /**
     * {@link AttributePredicate}s operators
     * <p>
     * Determines how the values of the result set are compared with the given {@link AttributePredicate#getAttributeValue()}
     *
     * @since 1.3.0
     */
    public static enum Operator {
        /**
         * {@literal #EQUAL} {@link Operator}
         * <p>
         * Matches results with the same value.
         *
         * @since 1.3.0
         */
        EQUAL,

        /**
         * {@literal #NOT_EQUAL} {@link Operator}
         * <p>
         * Matches results with not the same value.
         *
         * @since 1.3.0
         */
        NOT_EQUAL,

        /**
         * {@literal #IS_NULL} {@link Operator}
         * <p>
         * Matches results with value {@code null}.
         *
         * @since 1.3.0
         */
        IS_NULL,
        /**
         * {@literal #NOT_NULL} {@link Operator}
         * <p>
         * Matches results with value NOT {@code null}.
         *
         * @since 1.3.0
         */
        NOT_NULL,

        /**
         * {@literal #STARTS_WITH} {@link Operator}
         * <p>
         * Matches results with value that starts with the given value. To be used with {@link String} {@link org.eclipse.kapua.model.KapuaEntityAttributes}.
         *
         * @since 1.3.0
         */
        STARTS_WITH,

        /**
         * The same of {@link #STARTS_WITH} {@link Operator} but case insensitive
         * <p>
         * Matches results with value that starts with the given value, case insensitive. To be used with {@link String} {@link org.eclipse.kapua.model.KapuaEntityAttributes}.
         *
         * @since 1.3.0
         */
        STARTS_WITH_IGNORE_CASE,

        /**
         * {@literal #LIKE} {@link Operator}
         * <p>
         * Matches results with value that are like (in SQL fashion) the given value. To be used with {@link String} {@link org.eclipse.kapua.model.KapuaEntityAttributes}.
         * <p>
         * If you want to match only the beginning of the {@link String} please consider using {@link #STARTS_WITH}.
         *
         * @since 1.3.0
         */
        LIKE,

        /**
         * {@link #LIKE} {@link Operator} but case insensitive
         * <p>
         * Matches results with value that are like (in SQL fashion) the given value case insensitive. To be used with {@link String} {@link org.eclipse.kapua.model.KapuaEntityAttributes}.
         * <p>
         * If you want to match only the beginning of the {@link String} please consider using {@link #STARTS_WITH_IGNORE_CASE}.
         *
         * @since 1.3.0
         */
        LIKE_IGNORE_CASE,

        /**
         * {@literal #GREATER_THAN} {@link Operator}
         * <p>
         * Matches result with value that is greater but not equal. To be used with {@link Comparable} types.
         *
         * @since 1.3.0
         */
        GREATER_THAN,

        /**
         * {@literal #GREATER_THAN_OR_EQUAL} {@link Operator}
         * <p>
         * Matches result with value that is greater or equal. To be used with {@link Comparable} types.
         *
         * @since 1.3.0
         */
        GREATER_THAN_OR_EQUAL,

        /**
         * {@literal #LESS_THAN} {@link Operator}
         * <p>
         * Matches result with value that is less but not equal. To be used with {@link Comparable} types.
         *
         * @since 1.3.0
         */
        LESS_THAN,

        /**
         * {@literal #LESS_THAN_OR_EQUAL} {@link Operator}
         * <p>
         * Matches result with value that is less or equal. To be used with {@link Comparable} types.
         *
         * @since 1.3.0
         */
        LESS_THAN_OR_EQUAL
    }

    private String attributeName;
    private T attributeValue;
    private Operator operator;

    /**
     * Constructor.
     * <p>
     * Defaults ot {@link org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator#EQUAL}
     *
     * @param attributeName
     *         The name of {@link org.eclipse.kapua.model.KapuaEntityAttributes} to set into the {@link AttributePredicate}.
     * @param attributeValue
     *         The value of {@link org.eclipse.kapua.model.KapuaEntityAttributes} to set into the {@link AttributePredicate}.
     * @since 1.0.0
     */
    public AttributePredicate(String attributeName, T attributeValue) {
        this(attributeName, attributeValue, Operator.EQUAL);
    }

    /**
     * Constructor.
     *
     * @param attributeName
     *         The name of {@link org.eclipse.kapua.model.KapuaEntityAttributes} to set into the {@link AttributePredicate}.
     * @param attributeValue
     *         The value of {@link org.eclipse.kapua.model.KapuaEntityAttributes} to set into the {@link AttributePredicate}.
     * @param operator
     *         The {@link org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator} to set into the {@link AttributePredicate}.
     * @since 1.0.0
     */
    public AttributePredicate(String attributeName, T attributeValue, Operator operator) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.operator = operator;
    }

    /**
     * Gets the name of the {@link org.eclipse.kapua.model.KapuaEntityAttributes} to compare.
     *
     * @return The name name of the {@link org.eclipse.kapua.model.KapuaEntityAttributes} to compare.
     * @since 1.3.0
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Gets the value to compare the results.
     *
     * @return The value to compare the results.
     * @since 1.3.0
     */
    public T getAttributeValue() {
        return attributeValue;
    }

    /**
     * Get the {@link Operator} used to compare results.
     *
     * @return The {@link Operator} used to compare results.
     * @since 1.3.0
     */
    public Operator getOperator() {
        return operator;
    }
}
