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
package org.eclipse.kapua.commons.web.rest.model.errors;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.KapuaEntityUniquenessException;

@XmlRootElement(name = "entityUniquenessExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityUniquenessExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "entityType")
    private String entityType;

    @XmlElement(name = "uniquesFieldValues")
    private List<Map.Entry<String, Object>> uniquesFieldValues;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected EntityUniquenessExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param httpStatusCode
     *         The http status code of the response containing this info
     * @param kapuaEntityUniquenessException
     *         The root exception.
     * @since 1.0.0
     */
    public EntityUniquenessExceptionInfo(int httpStatusCode, KapuaEntityUniquenessException kapuaEntityUniquenessException, boolean showStackTrace) {
        super(httpStatusCode, kapuaEntityUniquenessException, showStackTrace);

        this.entityType = kapuaEntityUniquenessException.getEntityType();
        this.uniquesFieldValues = kapuaEntityUniquenessException.getUniquesFieldValues();
    }

    /**
     * Gets the {@link KapuaEntityUniquenessException#getEntityType()}.
     *
     * @return The {@link KapuaEntityUniquenessException#getEntityType()}.
     * @since 1.0.0
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Gets the {@link KapuaEntityUniquenessException#getUniquesFieldValues()}.
     *
     * @return The {@link KapuaEntityUniquenessException#getUniquesFieldValues()}.
     * @since 2.0.0
     */
    public List<Map.Entry<String, Object>> getUniquesFieldValues() {
        return uniquesFieldValues;
    }
}
