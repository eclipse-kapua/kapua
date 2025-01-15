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
import java.util.Properties;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.entity.EntityPropertiesReadException;
import org.eclipse.kapua.entity.EntityPropertiesWriteException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link KapuaUpdatableEntity} definition.
 *
 * @since 1.0.0
 */
@XmlType(propOrder = {
        "modifiedOn",
        "modifiedBy",
        "optlock"
})
@Schema(
    name = "KapuaEntity",
    description = "Represents an entity inside Kapua that can be updated"
)
public interface KapuaUpdatableEntity extends KapuaEntity {

    /**
     * Gets the last date that this {@link KapuaEntity} has been updated.
     *
     * @return the last date that this {@link KapuaEntity} has been updated.
     * @since 1.0.0
     */
    @XmlElement(name = "modifiedOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    @Schema(description = "Modified on date", example = "2024-11-28T08:38:57.102Z")
    Date getModifiedOn();

    /**
     * Get the last identity {@link KapuaId} that has updated this {@link KapuaEntity}
     *
     * @return the last identity {@link KapuaId} that has updated this {@link KapuaEntity}
     * @since 1.0.0
     */
    @XmlElement(name = "modifiedBy")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    @Schema(description = "Modified by", type = "string",
        example = "REk=TcEOmceVaRDVW5GIEmEgJG8isd9IWOGECrhFXLZS_lO3Uli79L-BqVTfdK8Rsw457P4rN3QiZ=6=x6")
    KapuaId getModifiedBy();

    /**
     * Gets the optlock
     *
     * @return the optlock
     * @since 1.0.0
     */
    @XmlElement(name = "optlock")
    @Schema(description = "Modified on date", example = "0")
    int getOptlock();

    /**
     * Sets the optlock
     *
     * @param optlock the optlock
     * @since 1.0.0
     */
    void setOptlock(int optlock);

    /**
     * Gets the attributes
     *
     * @return the attributes
     * @throws EntityPropertiesReadException If there are error while reading {@link Properties}
     */
    @XmlTransient
    @Schema(hidden = true)
    Properties getEntityAttributes() throws EntityPropertiesReadException;

    /**
     * Sets the attributes
     *
     * @param props the attributes
     * @throws EntityPropertiesWriteException If there are error while writing {@link Properties}
     * @since 1.0.0
     */
    void setEntityAttributes(Properties props) throws EntityPropertiesWriteException;

    /**
     * Gets the property entities
     *
     * @return the property entities
     * @throws EntityPropertiesReadException If there are error while reading {@link Properties}
     * @since 1.0.0
     */
    @XmlTransient
    @Schema(hidden = true)
    Properties getEntityProperties() throws EntityPropertiesReadException;

    /**
     * Sets the property entities
     *
     * @param props the property entities
     * @throws EntityPropertiesWriteException If there are error while writing {@link Properties}
     * @since 1.0.0
     */
    void setEntityProperties(Properties props) throws EntityPropertiesWriteException;
}
