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

import java.util.Collection;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.storable.model.query.StorableQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link StorableListResult} definition.
 * <p>
 * It is the base {@code interface} for all list of {@link Storable}s
 *
 * @param <E> The {@link Storable} for which this is a {@link StorableListResult} for.
 * @since 1.0.0
 */
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"limitExceeded", "size", "items", "nextKey", "totalCount"})
@Schema(description = "A container for all the Entities List")
public interface StorableListResult<E extends Storable> extends KapuaSerializable {

    /**
     * Gets whether or not  the {@link StorableQuery#getLimit()} has been exceeded.
     * <p>
     * When {@code true} it means that there are more {@link Storable}s to fetch.
     *
     * @return {@code true} if the {@link StorableQuery#getLimit()} has been exceeded, {@code false} otherwise.
     * @since 1.0.0
     */
    @XmlElement(name = "limitExceeded")
    @Schema(description = "A flag indicating if more results are available but were not returned because of the limit imposed in the query")
    boolean isLimitExceeded();

    /**
     * Sets whether or not  the {@link StorableQuery#getLimit()} has been exceeded.
     *
     * @param limitExceeded {@code true} if the {@link StorableQuery#getLimit()} has been exceeded, {@code false} otherwise.
     * @since 1.0.0
     */
    void setLimitExceeded(boolean limitExceeded);

    /**
     * Gets the {@link Storable}s
     *
     * @return The {@link Storable}s
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    List<E> getItems();

    /**
     * Gets the {@link Storable} at the given position in the {@link StorableListResult}.
     *
     * @param i The position in the {@link StorableListResult}
     * @return The {@link Storable} at the position
     * @throws IndexOutOfBoundsException If position is not available.
     * @see List#get(int)
     * @since 1.0.0
     */
    E getItem(int i);

    /**
     * Returns the first element in the {@link StorableListResult}.
     * <p>
     * It returns {@code null} if first element does not exist.
     *
     * @return The first element in the {@link Storable} or {@code null} if not present.
     * @since 1.0.0
     */
    E getFirstItem();

    /**
     * Gets the result {@link StorableListResult} size.
     *
     * @return The result list size.
     * @see List#size()
     * @since 1.0.0
     */
    @XmlElement(name = "size")
    @Schema(description = "The size of the list containing the items")
    int getSize();

    /**
     * Checks if the result {@link StorableListResult} is empty.
     *
     * @return {@code true} if the result list is empty, {@code false} otherwise.
     * @see List#isEmpty()
     * @since 1.0.0
     */
    boolean isEmpty();

    /**
     * Adds {@link Storable}s to the result {@link StorableListResult}
     *
     * @param items The {@link Storable}s to add.
     * @see List#addAll(Collection)
     * @since 1.0.0
     */
    void addItems(Collection<? extends E> items);

    /**
     * Adds a {@link Storable} to the {@link StorableListResult}.
     *
     * @param item The {@link Storable} to add.
     * @since 1.3.0
     */
    void addItem(@NotNull E item);

    /**
     * Clears {@link Storable} result {@link StorableListResult}
     *
     * @see List#clear()
     * @since 1.0.0
     */
    void clearItems();

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
    Object getNextKey();

    /**
     * Gets the total count of {@link Storable}s that match the {@link StorableQuery#getPredicate()}s regardless of {@link StorableQuery#getLimit()} and {@link StorableQuery#getOffset()}
     *
     * @return The total count
     * @since 1.0.0
     */
    @Schema(description = "The total count of the elements that matched the query. Available only if `askTotalCount` was `true` in the query object.")
    Long getTotalCount();

    /**
     * Sets the total count of {@link Storable}s that match the {@link KapuaQuery#getPredicate()}s regardless of {@code limit} and {@code offset}
     *
     * @param totalCount
     * @since 1.0.0
     */
    @XmlElement(name = "totalCount")
    void setTotalCount(Long totalCount);

}
