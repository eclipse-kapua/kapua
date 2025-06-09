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
package org.eclipse.kapua.service.storable.model.query;

import org.eclipse.kapua.service.storable.model.Storable;

/**
 * {@link StorableField }definition.
 * <p>
 * It identifies one of the fields of {@link Storable}
 *
 * @since 1.0.0
 */
public interface StorableField {

    /**
     * Gets the {@link Storable} field name.
     *
     * @return The {@link Storable} field name.
     * @since 1.0.0
     */
    String field();

    /**
     * Watch out using this to get the {@link StorableField} actual name. Maybe {@link #field()} is what you are looking for!
     *
     * @return The {@link Enum#name()}
     * @since 1.3.0
     * @deprecated Since the beginning.
     */
    @Deprecated
    String name();

}
