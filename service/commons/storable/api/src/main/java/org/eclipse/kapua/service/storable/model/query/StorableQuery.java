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
package org.eclipse.kapua.service.storable.model.query;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.storable.model.StorableListResult;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicate;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link StorableQuery} definition.
 *
 * @since 1.0.0
 */
@Schema(description = "An object to specify Query options")
public interface StorableQuery {

    /**
     * Gets the fetch attribute names list.
     *
     * @return The fetch attribute names list.
     */
    @XmlElementWrapper(name = "fetchAttributeName")
    @XmlElement(name = "fetchAttributeName")
    public List<String> getFetchAttributes();

    /**
     * Adds an attribute to the fetch attribute names list
     *
     * @param fetchAttribute The fetch attribute to add to the list.
     * @since 1.0.0
     */
    void addFetchAttributes(String fetchAttribute);

    /**
     * Sets the fetch attribute names list.<br>
     * This list is a list of optional attributes of a {@link KapuaEntity} that can be fetched when querying.
     *
     * @param fetchAttributeNames The fetch attribute names list.
     * @since 1.0.0
     */
    void setFetchAttributes(List<String> fetchAttributeNames);

    /**
     * Gets the scope {@link KapuaId}.
     *
     * @return The scope {@link KapuaId}.
     * @since 1.0.0
     */
    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getScopeId();

    /**
     * Sets the scope id
     *
     * @param scopeId The scope {@link KapuaId}.
     * @since 1.0.0
     */
    void setScopeId(KapuaId scopeId);

    /**
     * Gets the {@link StorablePredicate}s
     *
     * @return The {@link StorablePredicate}s
     * @since 1.0.0
     */
    @XmlTransient
    StorablePredicate getPredicate();

    /**
     * Sets the {@link StorablePredicate}s
     *
     * @param predicate The {@link StorablePredicate}s
     * @since 1.0.0
     */
    void setPredicate(StorablePredicate predicate);

    /**
     * Gets the {@link StorableQuery} offset.
     *
     * @return The {@link StorableQuery} offset.
     * @since 1.0.0
     */
    @XmlElement(name = "offset")
    Integer getOffset();

    /**
     * Set the {@link StorableQuery} offset in the result set from which start query.
     * <p>
     * If set to {@code null} the {@link StorableQuery} will start from the first result found.
     * This also mean that {@link #setOffset(Integer)} with {@code 0} or {@code null} will produce the same result.
     * <p>
     * This method and {@link #setLimit(Integer)} are meant to be used to paginate through the result set.
     *
     * @param offset The {@link StorableQuery} offset.
     * @since 1.0.0
     */
    void setOffset(Integer offset);


    /**
     * Gets the {@link StorableQuery} limit.
     *
     * @return The {@link StorableQuery} limit.
     * @since 1.0.0
     */
    @XmlElement(name = "limit")
    Integer getLimit();

    /**
     * Sets max number of result that will be fetched by this {@link KapuaEntity}.
     * <p>
     * If set to {@code null} the {@link StorableQuery} will be unlimited.
     * <p>
     * This method and {@link #setOffset(Integer)} are meant to be used to paginate through the result set.
     *
     * @param limit The max number of result that will be fetched by this {@link KapuaEntity}.
     * @since 1.0.0
     */
    void setLimit(Integer limit);

    /**
     * Whether or not add the {@link StorableListResult#getTotalCount()} when processing the {@link StorableQuery}.
     *
     * @return {@code true} to include the StorableListResult#getTotalCount(), {@code false} otherwise.
     * @since 1.0.0
     */
    @XmlElement(name = "askTotalCount")
    boolean isAskTotalCount();

    /**
     * Sets whether or not add the {@link StorableListResult#getTotalCount()} when processing the {@link StorableQuery}.
     *
     * @param askTotalCount {@code true} to include the StorableListResult#getTotalCount(), {@code false} otherwise.
     * @since 1.0.0
     */
    void setAskTotalCount(boolean askTotalCount);

    /**
     * Gets the {@link StorableFetchStyle}.
     *
     * @return The {@link StorableFetchStyle}.
     * @since 1.0.0
     */
    @XmlTransient
    StorableFetchStyle getFetchStyle();

    /**
     * Sets the {@link StorableFetchStyle}.
     *
     * @param fetchStyle The {@link StorableFetchStyle}.
     * @since 1.0.0
     */
    void setFetchStyle(StorableFetchStyle fetchStyle);

    /**
     * Gets the {@link List} of {@link SortField}s
     *
     * @return The {@link List} of {@link SortField}s
     * @since 1.0.0
     */
    @XmlJavaTypeAdapter(SortFieldXmlAdapter.class)
    List<SortField> getSortFields();

    /**
     * Sets the {@link List} of {@link SortField}s
     *
     * @param sortFields The {@link List} of {@link SortField}s
     * @since 1.0.0
     */
    void setSortFields(List<SortField> sortFields);

    /**
     * Gets the included {@link StorableField}s according to the {@link StorableFetchStyle}.
     *
     * @param fetchStyle The {@link StorableFetchStyle}.
     * @return The included {@link StorableField}s according to the {@link StorableFetchStyle}.
     * @since 1.0.0
     */
    String[] getIncludes(StorableFetchStyle fetchStyle);

    /**
     * Gets the excluded {@link StorableField}s according to the {@link StorableFetchStyle}.
     *
     * @param fetchStyle The {@link StorableFetchStyle}.
     * @return The excluded {@link StorableField}s according to the {@link StorableFetchStyle}.
     * @since 1.0.0
     */
    String[] getExcludes(StorableFetchStyle fetchStyle);
}
