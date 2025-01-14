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
package org.eclipse.kapua.service.tag;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link TagFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface TagFactory extends KapuaEntityFactory<Tag, TagCreator> {

    /**
     * Instantiates a new {@link TagCreator}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to set into the {@link TagCreator}
     * @param name
     *         The name to set into the {@link TagCreator}.
     * @return The newly instantiated {@link TagCreator}
     * @since 1.0.0
     */
    TagCreator newCreator(KapuaId scopeId, String name);
}
