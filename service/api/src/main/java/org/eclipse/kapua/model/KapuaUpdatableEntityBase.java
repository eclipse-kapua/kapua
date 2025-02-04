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

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;

/**
 * {@link KapuaUpdatableEntityBase} definition.
 *
 * @since 1.0.0
 */
@XmlType(propOrder = {
        "modifiedOn",
        "modifiedBy",
        "optlock"
})
public abstract class KapuaUpdatableEntityBase extends KapuaEntityBase {

    private Date modifiedOn;
    private KapuaId modifiedBy;
    private int optLock;

    /**
     * Gets the last date that this {@link KapuaEntity} has been updated.
     *
     * @return the last date that this {@link KapuaEntity} has been updated.
     * @since 1.0.0
     */
    @XmlElement(name = "modifiedOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    public Date getModifiedOn() {
        return this.modifiedOn;
    }

    /**
     * Get the last identity {@link KapuaId} that has updated this {@link KapuaEntity}
     *
     * @return the last identity {@link KapuaId} that has updated this {@link KapuaEntity}
     * @since 1.0.0
     */
    @XmlElement(name = "modifiedBy")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getModifiedBy() {
        return this.modifiedBy;
    }

    /**
     * Gets the optlock
     *
     * @return the optlock
     * @since 1.0.0
     */
    @XmlElement(name = "optlock")
    public int getOptlock() {
        return this.optLock;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public void setModifiedBy(KapuaId modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setOptlock(int optLock) {
        this.optLock = optLock;
    }
}
