/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.service.authorization.exception.InternalUserOnlyException;

@XmlRootElement(name = "internalUserOnlyExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class InternalUserOnlyExceptionInfo extends ExceptionInfo {

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public InternalUserOnlyExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param httpStatusCode
     *         The http status code of the response containing this info
     * @since 1.0.0
     */
    public InternalUserOnlyExceptionInfo(int httpStatusCode, InternalUserOnlyException internalUserOnlyException, boolean showStackTrace) {
        super(httpStatusCode, internalUserOnlyException, showStackTrace);
    }

}
