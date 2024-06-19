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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.KapuaDuplicateNameException;

@XmlRootElement(name = "duplicateNameExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class DuplicateNameExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "duplicatedName")
    private String duplicatedName;


    public DuplicateNameExceptionInfo() {
        super();
    }


    public DuplicateNameExceptionInfo(KapuaDuplicateNameException kapuaDuplicateNameException, boolean showStackTrace) {
        super(409, kapuaDuplicateNameException, showStackTrace);
        this.duplicatedName = kapuaDuplicateNameException.getDuplicateName();
    }


    /**
     * Gets the {@link KapuaDuplicateNameException#getDuplicateName()}.
     * @return The {@link KapuaDuplicateNameException#getDuplicateName()}.
     */
    public String getName() {
        return duplicatedName;
    }
}
