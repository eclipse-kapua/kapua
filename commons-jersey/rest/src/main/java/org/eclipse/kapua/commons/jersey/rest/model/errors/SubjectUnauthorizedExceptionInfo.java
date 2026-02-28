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
import org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException;
import org.eclipse.kapua.service.authorization.permission.Permission;

@XmlRootElement(name = "subjectUnauthorizedExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubjectUnauthorizedExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "permission")
    private Permission permission;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public SubjectUnauthorizedExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param httpStatusCode
     *         The http status code of the response containing this info
     * @param subjectUnauthorizedException
     *         The root exception.
     * @since 1.0.0
     */
    public SubjectUnauthorizedExceptionInfo(int httpStatusCode, SubjectUnauthorizedException subjectUnauthorizedException, boolean showStackTrace) {
        super(httpStatusCode, subjectUnauthorizedException, showStackTrace);

        this.permission = subjectUnauthorizedException.getPermission();
    }

    /**
     * Gets the {@link SubjectUnauthorizedException#getPermission()}.
     *
     * @return The {@link SubjectUnauthorizedException#getPermission()}.
     * @since 1.0.0
     */
    public Permission getPermission() {
        return permission;
    }
}
