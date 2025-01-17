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

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.kapua.PropertiesUtils;
import org.eclipse.kapua.entity.EntityPropertiesReadException;
import org.eclipse.kapua.entity.EntityPropertiesWriteException;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link KapuaUpdatableEntityCreator} definition.
 *
 * @param <E>
 *         entity type
 * @since 1.0.0
 */
public abstract class KapuaUpdatableEntityCreator<E extends KapuaEntity> extends KapuaEntityCreator<E> {

    protected String attributes;

    public KapuaUpdatableEntityCreator() {
    }

    public KapuaUpdatableEntityCreator(KapuaId scopeId) {
        super(scopeId);
    }

    protected KapuaUpdatableEntityCreator(KapuaEntityCreator<E> entityCreator) {
        super(entityCreator);
    }

    @XmlTransient
    public Properties getEntityAttributes() {
        try {
            return PropertiesUtils.readPropertiesFromString(attributes);
        } catch (IOException e) {
            throw new EntityPropertiesReadException(e, "attributes", attributes);
        }
    }

    public void setEntityAttributes(Properties attributes) {
        try {
            this.attributes = PropertiesUtils.writePropertiesToString(attributes);
        } catch (IOException e) {
            throw new EntityPropertiesWriteException(e, "attributes", attributes);
        }
    }
}
