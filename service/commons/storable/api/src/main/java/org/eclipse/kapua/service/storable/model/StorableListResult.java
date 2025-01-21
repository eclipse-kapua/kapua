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
package org.eclipse.kapua.service.storable.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.storable.model.query.StorableQuery;

/**
 * {@link StorableListResult} definition.
 * <p>
 * It is the base {@code interface} for all list of {@link Storable}s
 *
 * @param <E>
 *         The {@link Storable} for which this is a {@link StorableListResult} for.
 * @since 1.0.0
 */
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "limitExceeded", "size", "items", "nextKey", "totalCount" })
public abstract class StorableListResult<E extends Storable> implements KapuaSerializable {

    private static final long serialVersionUID = -6792613517586602315L;

    private boolean limitExceeded;
    private ArrayList<E> items;
    private Object nextKey;
    private Long totalCount;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public StorableListResult() {
    }

    /**
     * Constructor.
     *
     * @param storables
     *         The {@link Storable}s to add to the {@link StorableListResult}.
     * @param totalCount
     *         The total count of the {@link Storable}s matched.
     * @since 1.3.0
     */
    public StorableListResult(List<E> storables, Long totalCount) {
        this();

        addItems(storables);
        setTotalCount(totalCount);
    }

    /**
     * Constructors.
     *
     * @param nextKey
     *         The {@link StorableListResult#getNextKey()}.
     * @since 1.0.0
     * @deprecated Since 1.3.0, this is not used!
     */
    @Deprecated
    public StorableListResult(Object nextKey) {
        this();
        this.nextKey = nextKey;
    }

    /**
     * Constructor.
     *
     * @param nextKeyOffset
     *         The {@link StorableListResult#getNextKey()}.
     * @param totalCount
     *         The {@link StorableListResult#getTotalCount()}.
     * @since 1.0.0
     * @deprecated Since 1.3.0, this is not used!
     */
    @Deprecated
    public StorableListResult(Object nextKeyOffset, Long totalCount) {
        this(nextKeyOffset);
        this.totalCount = totalCount;
    }

    /**
     * Gets whether or not  the {@link StorableQuery#getLimit()} has been exceeded.
     * <p>
     * When {@code true} it means that there are more {@link Storable}s to fetch.
     *
     * @return {@code true} if the {@link StorableQuery#getLimit()} has been exceeded, {@code false} otherwise.
     * @since 1.0.0
     */
    @XmlElement(name = "limitExceeded")
    public boolean isLimitExceeded() {
        return limitExceeded;
    }

    /**
     * Sets whether or not  the {@link StorableQuery#getLimit()} has been exceeded.
     *
     * @param limitExceeded
     *         {@code true} if the {@link StorableQuery#getLimit()} has been exceeded, {@code false} otherwise.
     * @since 1.0.0
     */
    public void setLimitExceeded(boolean limitExceeded) {
        this.limitExceeded = limitExceeded;
    }

    /**
     * Gets the {@link Storable}s
     *
     * @return The {@link Storable}s
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
     * Gets the {@link Storable} at the given position in the {@link StorableListResult}.
     *
     * @param index
     *         The position in the {@link StorableListResult}
     * @return The {@link Storable} at the position
     * @throws IndexOutOfBoundsException
     *         If position is not available.
     * @see List#get(int)
     * @since 1.0.0
     */
    public E getItem(int index) {
        return getItems().get(index);
    }

    /**
     * Returns the first element in the {@link StorableListResult}.
     * <p>
     * It returns {@code null} if first element does not exist.
     *
     * @return The first element in the {@link Storable} or {@code null} if not present.
     * @since 1.0.0
     */
    public E getFirstItem() {
        return this.isEmpty() ? null : getItem(0);
    }

    /**
     * Gets the result {@link StorableListResult} size.
     *
     * @return The result list size.
     * @see List#size()
     * @since 1.0.0
     */
    @XmlElement(name = "size")
    public int getSize() {
        return getItems().size();
    }

    /**
     * Checks if the result {@link StorableListResult} is empty.
     *
     * @return {@code true} if the result list is empty, {@code false} otherwise.
     * @see List#isEmpty()
     * @since 1.0.0
     */
    public boolean isEmpty() {
        return getItems().isEmpty();
    }

    /**
     * Adds {@link Storable}s to the result {@link StorableListResult}
     *
     * @param items
     *         The {@link Storable}s to add.
     * @see List#addAll(Collection)
     * @since 1.0.0
     */
    public void addItems(Collection<? extends E> items) {
        getItems().addAll(items);
    }

    /**
     * Adds a {@link Storable} to the {@link StorableListResult}.
     *
     * @param item
     *         The {@link Storable} to add.
     * @since 1.3.0
     */
    public void addItem(E item) {
        getItems().add(item);
    }

    /**
     * Clears {@link Storable} result {@link StorableListResult}
     *
     * @see List#clear()
     * @since 1.0.0
     */
    public void clearItems() {
        getItems().clear();
    }

    /**
     * Get the next key.
     * <p>
     * If a limit is set into the query parameters (limit) and the messages count matching the query is higher than the limit, so the next key is the key of the first next object not included in the
     * result set.
     *
     * @return The next key.
     * @since 1.0.0
     */
    @XmlElement(name = "nextKey")
    public Object getNextKey() {
        return nextKey;
    }

    /**
     * Gets the total count of {@link Storable}s that match the {@link StorableQuery#getPredicate()}s regardless of {@link StorableQuery#getLimit()} and {@link StorableQuery#getOffset()}
     *
     * @return The total count
     * @since 1.0.0
     */
    public Long getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total count of {@link Storable}s that match the {@link KapuaQuery#getPredicate()}s regardless of {@code limit} and {@code offset}
     *
     * @param totalCount
     * @since 1.0.0
     */
    @XmlElement(name = "totalCount")
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

}
