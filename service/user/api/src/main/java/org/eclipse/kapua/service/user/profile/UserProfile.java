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
package org.eclipse.kapua.service.user.profile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = UserProfileXmlRegistry.class, factoryMethod = "newUserProfile")
@Schema(description = "The user profile")
public interface UserProfile {
    String TYPE = "userProfile";

    @XmlElement(name = "displayName")
    @Schema(example = "Foo42")
    String getDisplayName();

    void setDisplayName(String displayName);

    @XmlElement(name = "phoneNumber")
    @Schema(example = "424202424")
    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    @XmlElement(name = "email")
    @Schema(example = "foo@bar.com")
    String getEmail();

    void setEmail(String email);
}
