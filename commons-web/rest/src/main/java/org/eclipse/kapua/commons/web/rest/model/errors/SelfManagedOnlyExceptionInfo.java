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
package org.eclipse.kapua.commons.web.rest.model.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.service.authorization.exception.SelfManagedOnlyException;

@XmlRootElement(name = "selfManagedOnlyExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SelfManagedOnlyExceptionInfo extends ExceptionInfo {

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public SelfManagedOnlyExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param httpStatusCode
     *         The http status code of the response containing this info
     * @param selfManagedOnlyException
     *         The root exception.
     * @since 1.0.0
     */
    public SelfManagedOnlyExceptionInfo(int httpStatusCode, SelfManagedOnlyException selfManagedOnlyException, boolean showStackTrace) {
        super(httpStatusCode, selfManagedOnlyException, showStackTrace);
    }

}
