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
package org.eclipse.kapua.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link KapuaEntityCreator} definition
 * <p>
 * All the {@link KapuaEntityCreator} will be an extension of this.
 *
 * @param <E> entity type
 * @since 1.0.0
 */
@Schema(description = "An object that contains the information needed to create an Updatable Entity")
public interface KapuaEntityCreator<E extends KapuaEntity> {

    /**
     * Gets the scope {@link KapuaId}
     *
     * @return the scope {@link KapuaId}
     * @since 1.0.0
     */
    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    KapuaId getScopeId();

    /**
     * Sets the scope {@link KapuaId}
     *
     * @param scopeId the scope {@link KapuaId}
     * @since 1.0.0
     */
    void setScopeId(KapuaId scopeId);
}
