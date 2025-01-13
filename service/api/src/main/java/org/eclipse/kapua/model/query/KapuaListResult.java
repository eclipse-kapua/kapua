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
package org.eclipse.kapua.model.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.model.KapuaEntity;

/**
 * {@link KapuaListResult} definition.
 *
 * @param <E>
 *         {@link KapuaEntity} type.
 * @since 1.0.0
 */
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "limitExceeded", "size", "items", "totalCount" })
public class KapuaListResult<E> implements KapuaSerializable {

    private static final long serialVersionUID = 8939666089540269261L;

    private ArrayList<E> items;
    private boolean limitExceeded;
    private Long totalCount;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public KapuaListResult() {
        limitExceeded = false;
    }

    /**
     * Gets the limit exceeded flag.
     * <p>
     * This flag is {@code true} if there are more results that exceded the {@link KapuaQuery#getLimit()}. Increasing the {@link KapuaQuery#getLimit()} or moving the {@link KapuaQuery#getOffset()}
     * will return more results
     *
     * @return The limit exceeded flag
     * @since 1.0.0
     */
    @XmlElement(name = "limitExceeded")
    public boolean isLimitExceeded() {
        return limitExceeded;
    }

    /**
     * Sets the limit exceeded flag.
     * <p>
     * To be set to {@code true} if the {@link KapuaQuery} matching elements are more than {@link KapuaQuery#getLimit()}.
     *
     * @param limitExceeded
     *         The limit exceeded flag.
     * @since 1.0.0
     */
    public void setLimitExceeded(boolean limitExceeded) {
        this.limitExceeded = limitExceeded;
    }

    /**
     * Gets the {@link KapuaEntity}s that matched the {@link KapuaQuery#getPredicate()}.
     * <p>
     * The result are ordered according to the {@link KapuaQuery#getSortCriteria()}
     *
     * @return The {@link KapuaEntity}s that matched the {@link KapuaQuery#getPredicate()}.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    public List<E> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }

        return items;
    }

    /**
     * Gets the {@link KapuaEntity}s that matched the {@link KapuaQuery#getPredicate()} and applies even more filtering.
     * <p>
     * This is meant to be used to filter result when is not possible to do so with {@link KapuaQuery#getPredicate()}s.
     *
     * @param filter
     *         The filter to apply to select results.
     * @return The filtered {@link KapuaEntity}s that matched the {@link KapuaQuery#getPredicate()}.
     * @since 2.0.0
     */
    public List<E> getItems(Predicate<E> filter) {
        return getItems().stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * Gets a {@link Map} whose {@link Map#keySet()} are generated from the given {@link Map.Entry#getKey()} mapper {@link Function}. The {@link Map#values()} are the {@link KapuaEntity}s themself.
     * <p>
     * This is like invoking {@link #getItemsAsMap(Function, Function)} whose value mapper returns the {@link KapuaEntity} itself.
     *
     * @param keyMapper
     *         The {@link Function} which defines the {@link Map.Entry#getKey()} for each {@link Map.Entry}
     * @param <K>
     *         The type of the {@link Map.Entry#getKey()}
     * @return The {@link Map} generated according to the mapping {@link Map.Entry#getKey()} {@link Function}.
     * @since 2.0.0
     */
    public <K> Map<K, E> getItemsAsMap(Function<E, K> keyMapper) {
        return getItems().stream().collect(Collectors.toMap(keyMapper, e -> e));
    }

    /**
     * Gets a {@link Map} whose {@link Map#keySet()} are generated from the given {@link Map.Entry#getKey()} mapper {@link Function}. The {@link Map#values()} are generated from the given
     * {@link Map.Entry#getValue()} mapper {@link Function}.
     *
     * @param keyMapper
     *         The {@link Function} which defines the {@link Map.Entry#getKey()} for each {@link Map.Entry}
     * @param valueMapper
     *         The {@link Function} which defines the {@link Map.Entry#getValue()} for each {@link Map.Entry}
     * @param <K>
     *         The type of the {@link Map.Entry#getKey()}
     * @param <V>
     *         The type of the {@link Map.Entry#getValue()}
     * @return The {@link Map} generated according to the mapping {@link Function}s.
     * @since 2.0.0
     */
    public <K, V> Map<K, V> getItemsAsMap(Function<E, K> keyMapper, Function<E, V> valueMapper) {
        return getItems().stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * Gets the {@link KapuaEntity} at the given position in the {@link KapuaListResult}.
     *
     * @param index
     *         The position in the {@link KapuaListResult}
     * @return The {@link KapuaEntity} at the position
     * @throws IndexOutOfBoundsException
     *         If position is not available.
     * @see List#get(int)
     * @since 1.0.0
     */
    public E getItem(int index) {
        return getItems().get(index);
    }

    /**
     * Gets the first {@link KapuaEntity} in the {@link KapuaListResult}.
     * <p>
     * It returns {@code null} if {@link KapuaListResult#isEmpty()}.
     *
     * @return The first element in the {@link KapuaListResult} or {@code null} if not present.
     * @since 1.0.0
     */
    public E getFirstItem() {
        return this.isEmpty() ? null : getItem(0);
    }

    /**
     * Gets the result {@link KapuaListResult} size
     *
     * @return The result list size
     * @see List#size()
     * @since 1.0.0
     */
    @XmlElement(name = "size")
    public int getSize() {
        return getItems().size();
    }

    /**
     * Checks if the result {@link KapuaListResult} is empty
     *
     * @return {@code true} if the result list is empty, {@code false} otherwise
     * @see List#isEmpty()
     * @since 1.0.0
     */
    public boolean isEmpty() {
        return getItems().isEmpty();
    }

    /**
     * Adds {@link KapuaEntity}s to the result {@link KapuaListResult}
     *
     * @param items
     *         The {@link KapuaEntity}s to add.
     * @see List#addAll(Collection)
     * @since 1.0.0
     */
    public void addItems(Collection<? extends E> items) {
        getItems().addAll(items);
    }

    /**
     * Adds a {@link KapuaEntity} to the {@link KapuaListResult}.
     *
     * @param item
     *         The {@link KapuaEntity} to add.
     */
    public void addItem(E item) {
        getItems().add(item);
    }

    /**
     * Clears {@link KapuaEntity} result {@link KapuaListResult}
     *
     * @see List#clear()
     * @since 1.0.0
     */
    public void clearItems() {
        getItems().clear();
    }

    /**
     * Sorts the result {@link List} according to the given {@link Comparator}.
     *
     * @param comparator
     *         The {@link Comparator} used to compare items.
     * @see List#sort(Comparator)
     * @see Comparator
     * @since 1.0.0
     */
    public void sort(Comparator<E> comparator) {
        getItems().sort(comparator);
    }

    /**
     * Gets the total count of entries that match the {@link KapuaQuery#getPredicate()}s regardless of {@link KapuaQuery#getLimit()} and {@link KapuaQuery#getOffset()}
     *
     * @return The total count
     * @since 1.2.0
     */
    public Long getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total count of entries that match the {@link KapuaQuery#getPredicate()}s regardless of {@link KapuaQuery#getLimit()} and {@link KapuaQuery#getOffset()}.
     *
     * @since 1.2.0
     */
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
