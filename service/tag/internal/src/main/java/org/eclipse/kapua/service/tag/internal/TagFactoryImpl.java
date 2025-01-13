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
package org.eclipse.kapua.service.tag.internal;

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagCreator;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagQuery;

/**
 * {@link TagFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class TagFactoryImpl implements TagFactory {

    @Override
    public TagCreator newCreator(KapuaId scopeId, String name) {
        TagCreator creator = newCreator(scopeId);

        creator.setName(name);

        return creator;
    }

    @Override
    public Tag newEntity(KapuaId scopeId) {
        return new TagImpl(scopeId);
    }

    @Override
    public TagQuery newQuery(KapuaId scopeId) {
        return new TagQueryImpl(scopeId);
    }

    @Override
    public TagCreator newCreator(KapuaId scopeId) {
        return new TagCreatorImpl(scopeId);
    }

    @Override
    public Tag clone(Tag tag) {
        try {
            return new TagImpl(tag);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, Tag.TYPE, tag);
        }
    }
}
