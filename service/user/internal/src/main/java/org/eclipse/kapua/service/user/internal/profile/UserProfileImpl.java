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
package org.eclipse.kapua.service.user.internal.profile;

import org.eclipse.kapua.service.user.profile.UserProfile;

public class UserProfileImpl implements UserProfile {

    private String displayName;
    private String phoneNumber;
    private String email;
    private int optlock;


    @Override
    public String getDisplayName() {
        return displayName;
    }


    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }


    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    @Override
    public String getEmail() {
        return email;
    }


    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public int getOptlock() {
        return optlock;
    }

    public void setOptlock(int optlock) {
        this.optlock = optlock;
    }

}
