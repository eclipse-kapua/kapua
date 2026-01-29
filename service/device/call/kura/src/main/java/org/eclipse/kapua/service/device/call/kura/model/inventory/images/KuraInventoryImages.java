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
package org.eclipse.kapua.service.device.call.kura.model.inventory.images;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.checkerframework.checker.nullness.qual.Nullable;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link KuraInventoryImages} definition.
 *
 * @since 2.0.0
 */
@JsonRootName("inventoryImages")
public class KuraInventoryImages {

    @JsonProperty("images")
    public List<KuraInventoryImage> inventoryImages;

    /**
     * Gets the {@link KuraInventoryImage}s {@link List}.
     *
     * @return The {@link KuraInventoryImage}s {@link List}.
     * @since 2.0.0
     */
    public List<KuraInventoryImage> getInventoryImages() {
        if (inventoryImages == null) {
            inventoryImages = new ArrayList<>();
        }

        return inventoryImages;
    }

    /**
     * Adds a {@link KuraInventoryImage} to the {@link List}
     *
     * @param inventoryImage The {@link KuraInventoryImage} to add.
     * @since 2.0.0
     */
    public void addInventoryImage(@NotNull KuraInventoryImage inventoryImage) {
        getInventoryImages().add(inventoryImage);
    }

    /**
     * Sets the {@link KuraInventoryImage}s {@link List}.
     *
     * @param inventoryImages The {@link KuraInventoryImage}s {@link List}.
     * @since 2.0.0
     */
    public void setInventoryImages(@Nullable List<KuraInventoryImage> inventoryImages) {
        this.inventoryImages = inventoryImages;
    }
}
