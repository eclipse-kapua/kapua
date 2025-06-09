/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.keystore.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

import com.google.common.base.Strings;

/**
 * {@link DeviceKeystoreItemQuery} definition.
 * <p>
 * Is used to filter result from the keystore. Only one of the filter can be used at a time.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "deviceKeystoreItemQuery")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class DeviceKeystoreItemQuery implements KapuaSerializable {

    private String keystoreId;
    private String alias;

    /**
     * Gets the keystore id.
     *
     * @return The keystore id.
     * @since 1.5.0
     */
    @XmlElement(name = "keystoreId")
    public String getKeystoreId() {
        return keystoreId;
    }

    /**
     * Sets the keystore id.
     *
     * @param keystoreId
     *         The keystore id.
     * @since 1.5.0
     */
    public void setKeystoreId(String keystoreId) {
        this.keystoreId = keystoreId;
    }

    /**
     * Gets the alias.
     *
     * @return The alias.
     * @since 1.5.0
     */
    @XmlElement(name = "alias")
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias.
     *
     * @param alias
     *         The alias.
     * @since 1.5.0
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Gets whether or not there is a filter specified.
     *
     * @return {@code true} if there is at least one filter, {@code false} otherwise.
     * @since 1.5.0
     */
    @XmlTransient
    public boolean hasFilters() {
        return !Strings.isNullOrEmpty(getKeystoreId()) ||
                !Strings.isNullOrEmpty(getAlias());
    }
}
