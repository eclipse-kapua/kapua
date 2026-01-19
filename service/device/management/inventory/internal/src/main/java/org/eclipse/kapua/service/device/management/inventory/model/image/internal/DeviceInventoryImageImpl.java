/*******************************************************************************
 * Copyright (c) 2021, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.inventory.model.image.internal;

import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImage;

/**
 * {@link DeviceInventoryImage} implementation.
 *
 * @since 2.0.0
 */
public class DeviceInventoryImageImpl implements DeviceInventoryImage {

    private String name;
    private String version;
    private String imageType;

    /**
     * Constructor.
     *
     * @since 2.0.0
     */
    public DeviceInventoryImageImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getImageType() {
        return imageType;
    }

    @Override
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
