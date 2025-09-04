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

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = UserProfileXmlRegistry.class, factoryMethod = "newUserProfile")
public interface UserProfile {
    String TYPE = "userProfile";

    @XmlElement(name = "displayName")
    String getDisplayName();

    void setDisplayName(String displayName);

    @XmlElement(name = "phoneNumber")
    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    @XmlElement(name = "email")
    String getEmail();

    void setEmail(String email);

    @XmlElement(name = "name")
    String getName();

    void setName(String name);

    @XmlElement(name = "optlock")
    int getOptlock();

    void setOptlock(int optlock);

}
