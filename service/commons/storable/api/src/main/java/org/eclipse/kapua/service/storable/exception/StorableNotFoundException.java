/*******************************************************************************
 * Copyright (c) 2024, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.storable.exception;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.id.StorableId;

/**
 * {@link StorableNotFoundException} is thrown when an {@link Storable} could not be loaded from the database.
 *
 * @since 2.0.0
 */
public class StorableNotFoundException extends KapuaException {

    private final String storableType;
    private final StorableId storableId;

    /**
     * Constructor.
     *
     * @param storableType The {@link Storable#getType()}.
     * @param storableId   The {@link StorableId}.
     * @since 2.0.0
     */
    public StorableNotFoundException(String storableType, StorableId storableId) {
        super(StorableErrorCodes.STORABLE_NOT_FOUND, storableType, storableId);

        this.storableType = storableType;
        this.storableId = storableId;
    }

    /**
     * Gets the {@link Storable#getType()}.
     *
     * @return The {@link Storable#getType()}.
     * @since 2.0.0
     */
    public String getStorableType() {
        return storableType;
    }

    /**
     * Gets the {@link StorableId}.
     *
     * @return The {@link StorableId}.
     * @since 2.0.0
     */
    public StorableId getStorableId() {
        return storableId;
    }
}
