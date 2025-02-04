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
package org.eclipse.kapua.service.tag.internal;

import org.eclipse.kapua.commons.model.mappers.KapuaBaseMapper;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagCreator;
import org.eclipse.kapua.service.tag.TagListResult;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(uses = KapuaBaseMapper.class, componentModel = MappingConstants.ComponentModel.JSR330, injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
public interface TagMapper {

    Tag map(TagImpl tag);

    @KapuaBaseMapper.IgnoreKapuaUpdatableEntityReadonlyFields
    TagImpl map(TagCreator tag);

    TagListResult map(TagImplListResult tagImplListResult);

    @KapuaBaseMapper.IgnoreKapuaUpdatableEntityReadonlyFields
    void merge(@MappingTarget TagImpl existingTag, Tag tag);
}
