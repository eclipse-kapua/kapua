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

import java.util.ArrayList;
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

/**
 * {@link StorableQuery} definition.
 *
 * @since 1.0.0
 */
public abstract class StorableQuery {

    private StorablePredicate predicate;

    private KapuaId scopeId;
    private Integer limit;
    private Integer indexOffset;
    private boolean askTotalCount;
    private List<SortField> sortFields;
    private StorableFetchStyle fetchStyle;
    private List<String> fetchAttributes;

    /**
     * Constructor.
     * <p>
     * Forces the {@link StorableFetchStyle} to {@link StorableFetchStyle#SOURCE_FULL}
     *
     * @since 1.0.0
     */
    public StorableQuery() {
        super();

        setFetchStyle(StorableFetchStyle.SOURCE_FULL);
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The scope KapuaId.
     * @since 1.0.0
     */
    public StorableQuery(KapuaId scopeId) {
        this();

        setScopeId(scopeId);
    }

    /**
     * Gets the {@link StorableField}s.
     *
     * @return The {@link StorableField}s.
     * @since 1.0.0
     */
    public abstract String[] getFields();

    /**
     * Gets the fetch attribute names list.
     *
     * @return The fetch attribute names list.
     */
    @XmlElementWrapper(name = "fetchAttributeName")
    @XmlElement(name = "fetchAttributeName")
    public List<String> getFetchAttributes() {
        if (fetchAttributes == null) {
            fetchAttributes = new ArrayList<>();
        }

        return fetchAttributes;
    }

    /**
     * Adds an attribute to the fetch attribute names list
     *
     * @param fetchAttribute
     *         The fetch attribute to add to the list.
     * @since 1.0.0
     */
    public void addFetchAttributes(String fetchAttribute) {
        getFetchAttributes().add(fetchAttribute);
    }

    /**
     * Sets the fetch attribute names list.<br> This list is a list of optional attributes of a {@link KapuaEntity} that can be fetched when querying.
     *
     * @param fetchAttributeNames
     *         The fetch attribute names list.
     * @since 1.0.0
     */
    public void setFetchAttributes(List<String> fetchAttributeNames) {
        fetchAttributes = fetchAttributeNames;
    }

    /**
     * Gets the scope {@link KapuaId}.
     *
     * @return The scope {@link KapuaId}.
     * @since 1.0.0
     */
    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Sets the scope id
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @since 1.0.0
     */
    public void setScopeId(KapuaId scopeId) {
        this.scopeId = scopeId;
    }

    /**
     * Gets the {@link StorablePredicate}s
     *
     * @return The {@link StorablePredicate}s
     * @since 1.0.0
     */
    @XmlTransient
    public StorablePredicate getPredicate() {
        return this.predicate;
    }

    /**
     * Sets the {@link StorablePredicate}s
     *
     * @param predicate
     *         The {@link StorablePredicate}s
     * @since 1.0.0
     */
    public void setPredicate(StorablePredicate predicate) {
        this.predicate = predicate;
    }

    /**
     * Gets the {@link StorableQuery} offset.
     *
     * @return The {@link StorableQuery} offset.
     * @since 1.0.0
     */
    @XmlElement(name = "offset")
    public Integer getOffset() {
        return indexOffset;
    }

    /**
     * Set the {@link StorableQuery} offset in the result set from which start query.
     * <p>
     * If set to {@code null} the {@link StorableQuery} will start from the first result found. This also mean that {@link #setOffset(Integer)} with {@code 0} or {@code null} will produce the same
     * result.
     * <p>
     * This method and {@link #setLimit(Integer)} are meant to be used to paginate through the result set.
     *
     * @param offset
     *         The {@link StorableQuery} offset.
     * @since 1.0.0
     */
    public void setOffset(Integer offset) {
        this.indexOffset = offset;
    }

    /**
     * Gets the {@link StorableQuery} limit.
     *
     * @return The {@link StorableQuery} limit.
     * @since 1.0.0
     */
    @XmlElement(name = "limit")
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets max number of result that will be fetched by this {@link KapuaEntity}.
     * <p>
     * If set to {@code null} the {@link StorableQuery} will be unlimited.
     * <p>
     * This method and {@link #setOffset(Integer)} are meant to be used to paginate through the result set.
     *
     * @param limit
     *         The max number of result that will be fetched by this {@link KapuaEntity}.
     * @since 1.0.0
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Whether or not add the {@link StorableListResult#getTotalCount()} when processing the {@link StorableQuery}.
     *
     * @return {@code true} to include the StorableListResult#getTotalCount(), {@code false} otherwise.
     * @since 1.0.0
     */
    @XmlElement(name = "askTotalCount")
    public boolean isAskTotalCount() {
        return askTotalCount;
    }

    /**
     * Sets whether or not add the {@link StorableListResult#getTotalCount()} when processing the {@link StorableQuery}.
     *
     * @param askTotalCount
     *         {@code true} to include the StorableListResult#getTotalCount(), {@code false} otherwise.
     * @since 1.0.0
     */
    public void setAskTotalCount(boolean askTotalCount) {
        this.askTotalCount = askTotalCount;
    }

    /**
     * Gets the {@link StorableFetchStyle}.
     *
     * @return The {@link StorableFetchStyle}.
     * @since 1.0.0
     */
    @XmlTransient
    public StorableFetchStyle getFetchStyle() {
        return this.fetchStyle;
    }

    /**
     * Sets the {@link StorableFetchStyle}.
     *
     * @param fetchStyle
     *         The {@link StorableFetchStyle}.
     * @since 1.0.0
     */
    public void setFetchStyle(StorableFetchStyle fetchStyle) {
        this.fetchStyle = fetchStyle;
    }

    /**
     * Gets the {@link List} of {@link SortField}s
     *
     * @return The {@link List} of {@link SortField}s
     * @since 1.0.0
     */
    @XmlJavaTypeAdapter(SortFieldXmlAdapter.class)
    public List<SortField> getSortFields() {
        if (sortFields == null) {
            sortFields = new ArrayList<>();
        }

        return sortFields;
    }

    /**
     * Sets the {@link List} of {@link SortField}s
     *
     * @param sortFields
     *         The {@link List} of {@link SortField}s
     * @since 1.0.0
     */
    public void setSortFields(List<SortField> sortFields) {
        this.sortFields = sortFields;
    }

    /**
     * Gets the included {@link StorableField}s according to the {@link StorableFetchStyle}.
     *
     * @param fetchStyle
     *         The {@link StorableFetchStyle}.
     * @return The included {@link StorableField}s according to the {@link StorableFetchStyle}.
     * @since 1.0.0
     */
    public abstract String[] getIncludes(StorableFetchStyle fetchStyle);

    /**
     * Gets the excluded {@link StorableField}s according to the {@link StorableFetchStyle}.
     *
     * @param fetchStyle
     *         The {@link StorableFetchStyle}.
     * @return The excluded {@link StorableField}s according to the {@link StorableFetchStyle}.
     * @since 1.0.0
     */
    public abstract String[] getExcludes(StorableFetchStyle fetchStyle);
}
