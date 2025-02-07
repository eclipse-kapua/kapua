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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaSerializable;

/**
 * {@link DeviceKeystores} definition.
 *
 * @since 1.5.0
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "deviceKeystores")
@XmlType
public class DeviceKeystores implements KapuaSerializable {

    private static final long serialVersionUID = 1318691706440015345L;

    private List<DeviceKeystore> keystores;

    /**
     * Gets the {@link List} of {@link DeviceKeystore}s
     *
     * @return The {@link List} of {@link DeviceKeystore}s
     * @since 1.5.0
     */
    @XmlElement(name = "keystores")
    public List<DeviceKeystore> getKeystores() {
        if (keystores == null) {
            keystores = new ArrayList<>();
        }

        return keystores;
    }

    /**
     * Adds a {@link DeviceKeystore} to the {@link List}
     *
     * @param keystore
     *         The {@link DeviceKeystore} to add.
     * @since 1.5.0
     */
    public void addKeystore(DeviceKeystore keystore) {
        getKeystores().add(keystore);
    }

    /**
     * Sets the {@link List} of {@link DeviceKeystore}s
     *
     * @param keystores
     *         The {@link List} of {@link DeviceKeystore}s
     * @since 1.5.0
     */
    public void setKeystores(List<DeviceKeystore> keystores) {
        this.keystores = keystores;
    }

}
