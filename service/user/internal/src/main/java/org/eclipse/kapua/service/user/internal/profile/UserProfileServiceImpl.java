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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.CommonsValidationRegex;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;
import org.eclipse.kapua.service.user.profile.UserProfile;
import org.eclipse.kapua.service.user.profile.UserProfileService;

import com.google.common.base.Strings;

@Singleton
public class UserProfileServiceImpl implements UserProfileService {

    private final UserService userService;

    @Inject
    public UserProfileServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void changeUserProfile(UserProfile userProfile) throws KapuaException {
        if (!Strings.isNullOrEmpty(userProfile.getEmail())) {
            ArgumentValidator.match(userProfile.getEmail(), CommonsValidationRegex.EMAIL_REGEXP, "userProfile.email");
        }

        KapuaSecurityUtils.doPrivileged(() -> {
            User user = userService.find(KapuaSecurityUtils.getSession().getScopeId(), KapuaSecurityUtils.getSession().getUserId());
            if (user == null) {
                throw new KapuaEntityNotFoundException(User.TYPE, KapuaSecurityUtils.getSession().getUserId());
            }
            user.setEmail(userProfile.getEmail());
            user.setDisplayName(userProfile.getDisplayName());
            user.setPhoneNumber(userProfile.getPhoneNumber());

            userService.update(user);
        });
    }

    @Override
    public UserProfile getUserProfile() throws KapuaException {
        return KapuaSecurityUtils.doPrivileged(() -> {
            User user = userService.find(KapuaSecurityUtils.getSession().getScopeId(), KapuaSecurityUtils.getSession().getUserId());
            if (user == null) {
                throw new KapuaEntityNotFoundException(User.TYPE, KapuaSecurityUtils.getSession().getUserId());
            }

            UserProfile userProfile = new UserProfile();
            userProfile.setDisplayName(user.getDisplayName());
            userProfile.setEmail(user.getEmail());
            userProfile.setPhoneNumber(user.getPhoneNumber());
            return userProfile;
        });
    }
}
