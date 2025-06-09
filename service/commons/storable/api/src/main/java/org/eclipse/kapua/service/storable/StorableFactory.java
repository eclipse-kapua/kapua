/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.storable;

import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.storable.model.Storable;

/**
 * {@link StorableFactory} definition.
 * <p>
 * Is the base for all {@link StorableFactory}es
 *
 * @since 1.3.0
 */
public interface StorableFactory<S extends Storable> extends KapuaObjectFactory {

    /**
     * Instantiates a new {@link Storable}.
     *
     * @return The newly instantiated {@link Storable}.
     * @since 1.3.0
     */
    S newStorable();

}
