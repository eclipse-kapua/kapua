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
package org.eclipse.kapua.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;

/**
 * {@link KapuaEntityBase} definition.
 * <p>
 * All the {@link KapuaEntityBase}s will be an extension of this entity.
 *
 * @since 1.0.0
 */
@XmlType(propOrder = {
        "id",
        "scopeId",
        "createdOn",
        "createdBy" })
public abstract class KapuaEntityBase implements KapuaSerializable {

    private KapuaId id;
    private KapuaId scopeId;
    private Date createdOn;
    private KapuaId createdBy;

    /**
     * Gets the unique {@link KapuaId}
     *
     * @return the unique {@link KapuaId}
     * @since 1.0.0
     */
    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getId() {
        return id;
    }

    /**
     * Sets the unique {@link KapuaId}
     *
     * @param id
     *         the unique {@link KapuaId}
     * @since 1.0.0
     */
    public void setId(KapuaId id) {
        this.id = id;
    }

    /**
     * Gets the scope {@link KapuaId}
     *
     * @return the scope {@link KapuaId}
     * @since 1.0.0
     */
    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getScopeId() {
        return this.scopeId;
    }

    /**
     * Sets the scope {@link KapuaId}
     *
     * @param scopeId
     *         the scope {@link KapuaId}
     * @since 1.0.0
     */
    public void setScopeId(KapuaId scopeId) {
        this.scopeId = scopeId;
    }

    /**
     * Gets the creation date.
     *
     * @return the creation date.
     * @since 1.0.0
     */
    @XmlElement(name = "createdOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getCreatedOn() {
        return this.createdOn;
    }

    /**
     * Gets the identity {@link KapuaId} who has created this {@link KapuaEntityBase}
     *
     * @return the identity {@link KapuaId} who has created this {@link KapuaEntityBase}
     * @since 1.0.0
     */
    @XmlElement(name = "createdBy")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getCreatedBy() {
        return this.createdBy;
    }
}
