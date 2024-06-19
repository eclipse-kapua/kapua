/*******************************************************************************
 * Copyright (c) 2024 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.rest.model.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.KapuaMaxNumberOfItemsReachedException;

@XmlRootElement(name = "maxNumberOfItemsReachedExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaxNumberOfItemsReachedExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "entityType")
    private String entityType;


    /**
     * Constructor.
     *
     * @since 2.0.0
     */
    protected MaxNumberOfItemsReachedExceptionInfo() {
        super();
    }


    /**
     * Constructor.
     *
     * @param httpStatusCode               The http status code of the response containing this info
     * @param kapuaMaxNumberOfItemsReachedException The root exception.
     * @since 2.0.0
     */
    public MaxNumberOfItemsReachedExceptionInfo(int httpStatusCode, KapuaMaxNumberOfItemsReachedException kapuaMaxNumberOfItemsReachedException, boolean showStackTrace) {
        super(httpStatusCode, kapuaMaxNumberOfItemsReachedException, showStackTrace);
        this.entityType = kapuaMaxNumberOfItemsReachedException.getEntityType();
    }


    /**
     * Gets the {@link KapuaMaxNumberOfItemsReachedException#getEntityType()}.
     *
     * @return The {@link KapuaMaxNumberOfItemsReachedException#getEntityType()}.
     * @since 2.0.0
     */
    public String getEntityType() {
        return entityType;
    }

}
