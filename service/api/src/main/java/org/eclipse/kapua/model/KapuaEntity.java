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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link KapuaEntity} definition.
 * <p>
 * All the {@link KapuaEntity}s will be an extension of this entity.
 *
 * @since 1.0.0
 */
@XmlType(propOrder = {
        "id",
        "scopeId",
        "createdOn",
        "createdBy"})
@Schema(
    name = "KapuaEntity",
    description = "Represents an entity inside Kapua"
)
public interface KapuaEntity extends KapuaSerializable {

    @XmlTransient
    @Schema(hidden = true)
    String getType();

    /**
     * Gets the unique {@link KapuaId}
     *
     * @return the unique {@link KapuaId}
     * @since 1.0.0
     */
    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(type = "string", example = "AJOgM70X35Sle3eZ1OVPMwLHEOk1A=jeQq7kVSfu5==apsDfLoRPC44hz8gBlN6YQVzox0w")
    KapuaId getId();

    /**
     * Sets the unique {@link KapuaId}
     *
     * @param id the unique {@link KapuaId}
     * @since 1.0.0
     */
    void setId(KapuaId id);

    /**
     * Gets the scope {@link KapuaId}
     *
     * @return the scope {@link KapuaId}
     * @since 1.0.0
     */
    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(type = "string", example = "AS5sTGPCGQEKqgQMyyOOG3AIKep09=Lw6bN")
    KapuaId getScopeId();

    /**
     * Sets the scope {@link KapuaId}
     *
     * @param scopeId the scope {@link KapuaId}
     * @since 1.0.0
     */
    void setScopeId(KapuaId scopeId);

    /**
     * Gets the creation date.
     *
     * @return the creation date.
     * @since 1.0.0
     */
    @XmlElement(name = "createdOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getCreatedOn();

    /**
     * Gets the identity {@link KapuaId} who has created this {@link KapuaEntity}
     *
     * @return the identity {@link KapuaId} who has created this {@link KapuaEntity}
     * @since 1.0.0
     */
    @XmlElement(name = "createdBy")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(type = "string",
        example = "5_77YiomzLRvcZIhaCDWrOCjeOBJ2DD4-i6pm")
    KapuaId getCreatedBy();
}
