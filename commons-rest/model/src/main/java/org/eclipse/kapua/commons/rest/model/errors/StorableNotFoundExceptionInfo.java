/*******************************************************************************
 * Copyright (c) 2024, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.storable.exception.StorableNotFoundException;
import org.eclipse.kapua.service.storable.model.id.StorableId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "storableNotFoundExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class StorableNotFoundExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "storableType")
    private String storableType;

    @XmlElement(name = "storableId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private StorableId storableId;

    /**
     * Constructor.
     *
     * @since 2.0.0
     */
    protected StorableNotFoundExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param httpStatusCode            The http status code of the response containing this info
     * @param storableNotFoundException The {@link StorableNotFoundException}.
     * @since 2.0.0
     */
    public StorableNotFoundExceptionInfo(int httpStatusCode, StorableNotFoundException storableNotFoundException, boolean showStackTrace) {
        super(httpStatusCode, storableNotFoundException, showStackTrace);

        this.storableType = storableNotFoundException.getStorableType();
        this.storableId = storableNotFoundException.getStorableId();
    }

    /**
     * Gets the {@link StorableNotFoundException#getStorableType()}
     *
     * @return The {@link StorableNotFoundException#getStorableType()}.
     * @since 2.0.0
     */
    public String getEntityType() {
        return storableType;
    }

    /**
     * Gets the {@link StorableNotFoundException#getStorableId()}.
     *
     * @return The {@link StorableNotFoundException#getStorableId()}.
     * @since 2.0.0
     */
    public StorableId getStorableId() {
        return storableId;
    }
}
