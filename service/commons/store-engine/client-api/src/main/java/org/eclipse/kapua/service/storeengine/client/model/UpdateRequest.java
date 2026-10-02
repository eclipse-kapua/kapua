/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.storeengine.client.model;

import org.eclipse.kapua.service.storable.model.Storable;

/**
 * Update {@link Request} definition.
 *
 * @since 1.0.0
 */
public class UpdateRequest extends Request {

    /**
     * Constructor.
     *
     * @param id             The Object identifier.
     * @param index          The index.
     * @param storable       The Object to update.
     * @since 1.0.0
     */
    public UpdateRequest(String id, String index, Storable storable) {
        super(id, index, storable);
    }

}
