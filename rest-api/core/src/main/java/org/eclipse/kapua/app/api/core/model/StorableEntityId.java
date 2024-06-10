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
package org.eclipse.kapua.app.api.core.model;

import javax.ws.rs.PathParam;

import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.id.StorableId;

/**
 * {@link StorableId} implementation to be used on REST API to parse the {@link PathParam} entityId.
 *
 * @since 1.0.0
 */
public class StorableEntityId implements StorableId {

    private String id;

    /**
     * Builds the {@link Storable} from the given {@link String} representation.
     *
     * @param id
     *         The string storable id to parse.
     * @since 1.0.0
     */
    public StorableEntityId(String id) {
        setId(id);
    }

    public StorableEntityId() {
    }

    @Override
    public String toString() {
        return id;
    }

    public void setId(String storableId) {
        this.id = storableId;
    }

    public String getId() {
        return id;
    }
}
