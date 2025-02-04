/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.model.mappers;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KapuaBaseMapper {

    /**
     * Use this annotation for merge-mappers between dtos and KapuaEntities, to ignore all read-only fields of the target entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scopeId", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    public @interface IgnoreKapuaEntityReadonlyFields {

    }

    /**
     * Use this annotation for merge-mappers between update request dtos and KapuaUpdatableEntities, to ignore all read-only fields of the target entity
     */
    @Mapping(target = "modifiedOn", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "optlock", ignore = true)
    @Mapping(target = "entityProperties", ignore = true)
    @Mapping(target = "entityAttributes", ignore = true)
    @IgnoreKapuaEntityReadonlyFields
    public @interface IgnoreKapuaUpdatableEntityReadonlyFields {

    }

    @Mapping(target = "name", ignore = true)
    @IgnoreKapuaUpdatableEntityReadonlyFields
    public @interface IgnoreKapuaNamedEntityReadonlyFields {

    }

    /**
     * The default method chosen by mapstruct to map properties (something along the line of new Properties(properties)) returns an empty property object with defaults, which is not the same as we
     * need. Hence the need to specify a custom mapping
     *
     * @param properties
     *         The properties object to map
     * @return A copy of the properties object passed
     */
    default Properties mapProperties(Properties properties) {
        if (properties == null) {
            return null;
        }
        final Properties res = new Properties();
        res.putAll(properties);
        return res;
    }

    default String map(String value) {
        return Optional.ofNullable(value).map(String::trim).filter(s -> !s.isEmpty()).orElse(null);
    }

    default String map(int value) {
        return Integer.toString(value);
    }

    default String map(long value) {
        return Long.toString(value);
    }

    default String map(double value) {
        return Double.toString(value);
    }

    default String map(float value) {
        return Float.toString(value);
    }

    default KapuaId map(BigInteger value) {
        return new KapuaIdImpl(value);
    }
}
