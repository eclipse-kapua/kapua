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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.id.KapuaId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link Tag} {@link org.eclipse.kapua.model.KapuaEntity} definition
 * <p>
 * {@link Tag}s serve as tag for entities marked as {@link Taggable}.
 * It is possible to assign a {@link Tag} to a {@link org.eclipse.kapua.model.KapuaEntity}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = TagXmlRegistry.class, factoryMethod = "newTag")
public interface Tag extends KapuaNamedEntity {

    @XmlTransient
    @JsonIgnore
    KapuaId ANY = KapuaId.ANY;

    String TYPE = "tag";

    @Override
    @Schema(example = "tag")
    default String getType() {
        return TYPE;
    }
}
