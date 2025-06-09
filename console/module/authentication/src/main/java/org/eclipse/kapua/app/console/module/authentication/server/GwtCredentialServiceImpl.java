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
package org.eclipse.kapua.app.console.module.authentication.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authentication.shared.model.GwtCredential;
import org.eclipse.kapua.app.console.module.authentication.shared.model.GwtCredentialCreator;
import org.eclipse.kapua.app.console.module.authentication.shared.model.GwtCredentialQuery;
import org.eclipse.kapua.app.console.module.authentication.shared.service.GwtCredentialService;
import org.eclipse.kapua.app.console.module.authentication.shared.util.GwtKapuaAuthenticationModelConverter;
import org.eclipse.kapua.app.console.module.authentication.shared.util.KapuaGwtAuthenticationModelConverter;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.shiro.utils.AuthenticationUtils;
import org.eclipse.kapua.service.authentication.user.PasswordChangeRequest;
import org.eclipse.kapua.service.authentication.user.PasswordResetRequest;
import org.eclipse.kapua.service.authentication.user.UserCredentialsService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtCredentialServiceImpl extends KapuaRemoteServiceServlet implements GwtCredentialService {

    private static final long serialVersionUID = 7323313459749361320L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AuthenticationService AUTHENTICATION_SERVICE = LOCATOR.getService(AuthenticationService.class);

    private static final CredentialService CREDENTIAL_SERVICE = LOCATOR.getService(CredentialService.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);

    private static final UserCredentialsService USER_CREDENTIALS_SERVICE = LOCATOR.getService(UserCredentialsService.class);
    private static final AuthenticationUtils AUTHENTICATION_UTILS = LOCATOR.getComponent(AuthenticationUtils.class);

    // this should be removed due to the refactoring in fixPasswordValidationBypass method
    private static final int SYSTEM_MAXIMUM_PASSWORD_LENGTH = 255;

    @Override
    public PagingLoadResult<GwtCredential> query(PagingLoadConfig loadConfig, final GwtCredentialQuery gwtCredentialQuery) throws GwtKapuaException {
        int totalLength = 0;
        List<GwtCredential> gwtCredentials = new ArrayList<GwtCredential>();
        try {

            // Convert from GWT entity
            KapuaQuery credentialQuery = GwtKapuaAuthenticationModelConverter.convertCredentialQuery(loadConfig, gwtCredentialQuery);

            // query
            CredentialListResult credentials = CREDENTIAL_SERVICE.query(credentialQuery);
            credentials.sort(credentialComparator);
            totalLength = credentials.getTotalCount().intValue();

            // If there are results
            if (!credentials.isEmpty()) {
                //TODO: #LAYER_VIOLATION - user lookup should not be done here
                UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(GwtKapuaCommonsModelConverter.convertKapuaId(gwtCredentialQuery.getScopeId())));
                    }
                });

                HashMap<String, String> usernameMap = new HashMap<String, String>();
                for (User user : userListResult.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                // Convert to GWT entity
                for (Credential credential : credentials.getItems()) {
                    GwtCredential gwtCredential = KapuaGwtAuthenticationModelConverter.convertCredential(credential);
                    gwtCredential.setUsername(usernameMap.get(credential.getUserId().toCompactId()));
                    gwtCredential.setCreatedByName(usernameMap.get(credential.getCreatedBy().toCompactId()));
                    gwtCredential.setModifiedByName(usernameMap.get(credential.getModifiedBy().toCompactId()));
                    gwtCredentials.add(gwtCredential);
                }
            }

        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }

        return new BasePagingLoadResult<GwtCredential>(gwtCredentials, loadConfig.getOffset(), totalLength);
    }

    @Override
    public void delete(GwtXSRFToken xsrfToken, String stringAccountId, String gwtCredentialId) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(stringAccountId);
            KapuaId credentialId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtCredentialId);

            CREDENTIAL_SERVICE.delete(scopeId, credentialId);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public GwtCredential create(GwtXSRFToken xsrfToken, GwtCredentialCreator gwtCredentialCreator) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(xsrfToken);
        // Do create
        GwtCredential gwtCredential = null;
        try {
            // Convert from GWT Entity
            CredentialCreator credentialCreator = GwtKapuaAuthenticationModelConverter.convertCredentialCreator(gwtCredentialCreator);

            // Create
            Credential credential = CREDENTIAL_SERVICE.create(credentialCreator);
            User user = USER_SERVICE.find(GwtKapuaCommonsModelConverter.convertKapuaId(gwtCredentialCreator.getScopeId()), credential.getUserId());

            // Convert
            gwtCredential = KapuaGwtAuthenticationModelConverter.convertCredential(credential, user);
            gwtCredential.setCredentialKey(credential.getCredentialKey());

        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
        // Return result
        return gwtCredential;
    }

    @Override
    public GwtCredential update(GwtXSRFToken gwtXsrfToken, GwtCredential gwtCredential) throws GwtKapuaException {
        // Checking XSRF token
        checkXSRFToken(gwtXsrfToken);

        // Do update
        try {
            Credential credential = GwtKapuaAuthenticationModelConverter.convertCredential(gwtCredential);

            // Update
            Credential credentialUpdated = CREDENTIAL_SERVICE.update(credential);

            // Convert
            User user = USER_SERVICE.find(credentialUpdated.getScopeId(), credentialUpdated.getUserId());

            // Return result
            return KapuaGwtAuthenticationModelConverter.convertCredential(credentialUpdated, user);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public void changePassword(GwtXSRFToken gwtXsrfToken, String oldPassword, final String newPassword, String mfaCode, String stringUserId, String stringScopeId) throws GwtKapuaException {
        String username;
        try {
            final KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(stringScopeId);
            final KapuaId userId = GwtKapuaCommonsModelConverter.convertKapuaId(stringUserId);

            User user = KapuaSecurityUtils.doPrivileged(new Callable<User>() {

                @Override
                public User call() throws Exception {
                    return USER_SERVICE.find(scopeId, userId);
                }
            });
            if (user == null) {
                SecurityUtils.getSubject().logout();

                throw new AuthenticationException();
            }
            username = user.getName();
            final String finalUsername = username;
            UsernamePasswordCredentials loginCredentials = new UsernamePasswordCredentials(finalUsername, oldPassword);
            loginCredentials.setAuthenticationCode(mfaCode);
            AUTHENTICATION_SERVICE.verifyCredentials(loginCredentials);

            PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
            passwordChangeRequest.setCurrentPassword(oldPassword);
            passwordChangeRequest.setNewPassword(newPassword);
            USER_CREDENTIALS_SERVICE.changePassword(scopeId, userId, passwordChangeRequest);

        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public void resetPassword(GwtXSRFToken gwtXsrfToken, String stringScopeId, String gwtCredentialId, final String newPassword) throws GwtKapuaException {
        checkXSRFToken(gwtXsrfToken);

        try {
            final KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(stringScopeId);
            final KapuaId credentialId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtCredentialId);

            PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
            passwordResetRequest.setNewPassword(newPassword);
            USER_CREDENTIALS_SERVICE.resetPassword(scopeId, credentialId, passwordResetRequest);
        } catch (KapuaException e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public void unlock(GwtXSRFToken xsrfToken, String stringScopeId, String gwtCredentialId) throws GwtKapuaException {

        try {
            // //
            // Checking XSRF token
            checkXSRFToken(xsrfToken);

            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(stringScopeId);
            KapuaId credentialId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtCredentialId);

            CREDENTIAL_SERVICE.unlock(scopeId, credentialId);
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
    }

    @Override
    public Integer getMinPasswordLength(final String scopeId) throws GwtKapuaException {
        try {
            AUTHENTICATION_SERVICE.isAuthenticated();

            // Do privileged because the request may come from a user with no permission who just wants to change his own password
            return KapuaSecurityUtils.doPrivileged(new Callable<Integer>() {

                @Override
                public Integer call() throws Exception {
                    return CREDENTIAL_SERVICE.getMinimumPasswordLength(GwtKapuaCommonsModelConverter.convertKapuaId(scopeId));
                }

            });
        } catch (KapuaException ex) {
            throw KapuaExceptionHandler.buildExceptionFromError(ex);
        }
    }

    @Override
    public List<String> getAvailableCredentialTypes() throws GwtKapuaException {
        try {
            return new ArrayList<String>(CREDENTIAL_SERVICE.getAvailableCredentialTypes());
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    Comparator<Credential> credentialComparator = new Comparator<Credential>() {

        @Override
        public int compare(Credential credential1, Credential credential2) {
            return credential1.getId().toString().compareTo(credential2.getId().toString());
        }
    };
}
