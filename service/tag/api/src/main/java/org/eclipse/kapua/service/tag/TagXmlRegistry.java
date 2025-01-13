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

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

@XmlRegistry
public class TagXmlRegistry {

    private final TagFactory tagFactory = KapuaLocator.getInstance().getFactory(TagFactory.class);

    /**
     * Creates a new tag instance
     *
     * @return
     */
    public Tag newTag() {
        return tagFactory.newEntity(null);
    }

    /**
     * Creates a new tag creator instance
     *
     * @return
     */
    public TagCreator newTagCreator() {
        return tagFactory.newCreator(null, null);
    }

    public TagQuery newQuery() {
        return tagFactory.newQuery(null);
    }
}
