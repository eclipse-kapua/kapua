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
package org.eclipse.kapua.commons.jersey.rest.model.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;

@XmlRootElement(name = "illegalNullArgumentExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class IllegalNullArgumentExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "argumentName")
    private String argumentName;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public IllegalNullArgumentExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param httpStatusCode
     *         The http status code of the response containing this info
     * @param kapuaIllegalNullArgumentException
     *         The root exception.
     * @since 1.0.0
     */
    public IllegalNullArgumentExceptionInfo(int httpStatusCode, KapuaIllegalNullArgumentException kapuaIllegalNullArgumentException, boolean showStackTrace) {
        super(httpStatusCode, kapuaIllegalNullArgumentException, showStackTrace);

        this.argumentName = kapuaIllegalNullArgumentException.getArgumentName();
    }

    /**
     * Gets the {@link KapuaIllegalNullArgumentException#getArgumentName()}.
     *
     * @return The {@link KapuaIllegalNullArgumentException#getArgumentName()}.
     * @since 1.0.0
     */
    public String getArgumenName() {
        return argumentName;
    }
}
