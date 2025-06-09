/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core.model.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.datastore.model.query.MessageField;
import org.eclipse.kapua.service.datastore.model.query.MessageSchema;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.XmlAdaptedSortField;
import org.eclipse.kapua.service.storable.model.query.predicate.StorablePredicate;

/**
 * Message query implementation
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class JsonMessageQuery {

    private StorablePredicate predicate;

    private KapuaId scopeId;
    private Integer limit;
    private Integer indexOffset;
    private boolean askTotalCount;
    private List<XmlAdaptedSortField> sortFields;
    private StorableFetchStyle fetchStyle;
    private List<String> fetchAttributes;

    /**
     * Default constructor
     *
     * @since 1.0.0
     */
    public JsonMessageQuery() {
        super();

        fetchStyle = StorableFetchStyle.SOURCE_FULL;
        fetchAttributes = new ArrayList<>();
        askTotalCount = false;
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The scopeId of the query
     * @since 1.0.0
     */
    public JsonMessageQuery(KapuaId scopeId) {
        this();

        setScopeId(scopeId);
    }

    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getScopeId() {
        return scopeId;
    }

    public void setScopeId(KapuaId scopeId) {
        this.scopeId = KapuaEid.parseKapuaId(scopeId);
    }

    @XmlTransient
    public StorablePredicate getPredicate() {
        return this.predicate;
    }

    public void setPredicate(StorablePredicate predicate) {
        this.predicate = predicate;
    }

    @XmlElement(name = "offset")
    public Integer getOffset() {
        return indexOffset;
    }

    public void setOffset(Integer offset) {
        this.indexOffset = offset;
    }

    @XmlElement(name = "limit")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit;
    }

    @XmlElement(name = "askTotalCount")
    public boolean isAskTotalCount() {
        return askTotalCount;
    }

    public void setAskTotalCount(boolean askTotalCount) {
        this.askTotalCount = askTotalCount;
    }

    @XmlTransient
    public StorableFetchStyle getFetchStyle() {
        return this.fetchStyle;
    }

    public void setFetchStyle(StorableFetchStyle fetchStyle) {
        this.fetchStyle = fetchStyle;
    }

    @XmlElementWrapper(name = "fetchAttributeName")
    @XmlElement(name = "fetchAttributeName")
    public List<String> getFetchAttributes() {
        return fetchAttributes;
    }

    public void addFetchAttributes(String fetchAttribute) {
        fetchAttributes.add(fetchAttribute);
    }

    public void setFetchAttributes(List<String> fetchAttributeNames) {
        fetchAttributes = fetchAttributeNames;
    }

    @XmlElementWrapper(name = "sortFields")
    @XmlElement(name = "sortField")
    public List<XmlAdaptedSortField> getSortFields() {
        return sortFields;
    }

    public void setSortFields(List<XmlAdaptedSortField> sortFields) {
        this.sortFields = sortFields;
    }

    @XmlTransient
    public String[] getIncludes(StorableFetchStyle fetchStyle) {
        // Fetch mode
        String[] includeSource = null;
        switch (fetchStyle) {
        case FIELDS:
            includeSource = getFields();
            break;
        case SOURCE_SELECT:
            includeSource = new String[] { MessageSchema.MESSAGE_CAPTURED_ON, MessageSchema.MESSAGE_POSITION + ".*", MessageSchema.MESSAGE_METRICS + ".*" };
            break;
        case SOURCE_FULL:
            includeSource = new String[] { "*" };
        }
        return includeSource;
    }

    @XmlTransient
    public String[] getExcludes(StorableFetchStyle fetchStyle) {
        // Fetch mode
        String[] excludeSource = null;
        switch (fetchStyle) {
        case FIELDS:
            excludeSource = new String[] { "" };
            break;
        case SOURCE_SELECT:
            excludeSource = new String[] { MessageSchema.MESSAGE_BODY };
            break;
        case SOURCE_FULL:
            excludeSource = new String[] { "" };
        }
        return excludeSource;
    }

    @XmlTransient
    public String[] getFields() {
        return new String[] {
                MessageField.MESSAGE_ID.field(),
                MessageField.SCOPE_ID.field(),
                MessageField.DEVICE_ID.field(),
                MessageField.CLIENT_ID.field(),
                MessageField.CHANNEL.field(),
                MessageField.TIMESTAMP.field() };
    }

}
