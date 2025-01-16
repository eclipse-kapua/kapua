/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = UserCredentialsXmlRegistry.class, factoryMethod = "newPasswordChangeRequest")
public interface PasswordChangeRequest {
    @XmlElement(name = "currentPassword")
    @Schema(example = "Welcome1234!")
    String getCurrentPassword();


    void setCurrentPassword(String currentPassword);


    @XmlElement(name = "newPassword")
    @Schema(example = "NewWelcome1234!")
    String getNewPassword();


    void setNewPassword(String newPassword);

}
