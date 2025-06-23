/*******************************************************************************
 * Copyright (c) 2017, 2025 Eurotech and/or its affiliates and others
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

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.server.util.UserCreatedByModifiedByUtils;
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
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.CredentialsFactory;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.credential.CredentialCreator;
import org.eclipse.kapua.service.authentication.credential.CredentialFactory;
import org.eclipse.kapua.service.authentication.credential.CredentialListResult;
import org.eclipse.kapua.service.authentication.credential.CredentialQuery;
import org.eclipse.kapua.service.authentication.credential.CredentialService;
import org.eclipse.kapua.service.authentication.user.PasswordChangeRequest;
import org.eclipse.kapua.service.authentication.user.PasswordResetRequest;
import org.eclipse.kapua.service.authentication.user.UserCredentialsFactory;
import org.eclipse.kapua.service.authentication.user.UserCredentialsService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class GwtCredentialServiceImpl extends KapuaRemoteServiceServlet implements GwtCredentialService {

    private static final long serialVersionUID = 7323313459749361320L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AuthenticationService AUTHENTICATION_SERVICE = LOCATOR.getService(AuthenticationService.class);

    private static final CredentialService CREDENTIAL_SERVICE = LOCATOR.getService(CredentialService.class);
    private static final CredentialFactory CREDENTIAL_FACTORY = LOCATOR.getFactory(CredentialFactory.class);

    private static final CredentialsFactory CREDENTIALS_FACTORY = LOCATOR.getFactory(CredentialsFactory.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);

    private static final UserCredentialsService USER_CREDENTIALS_SERVICE = LOCATOR.getService(UserCredentialsService.class);
    private static final UserCredentialsFactory USER_CREDENTIALS_FACTORY = LOCATOR.getFactory(UserCredentialsFactory.class);

    @Override
    public PagingLoadResult<GwtCredential> query(PagingLoadConfig loadConfig, final GwtCredentialQuery gwtCredentialQuery) throws GwtKapuaException {
        try {

            // Convert from GWT entity
            CredentialQuery credentialQuery = GwtKapuaAuthenticationModelConverter.convertCredentialQuery(loadConfig, gwtCredentialQuery);

            // query
            final CredentialListResult credentials = CREDENTIAL_SERVICE.query(credentialQuery);
            credentials.sort(credentialComparator);

            // If there are results
            List<GwtCredential> gwtCredentials = new ArrayList<GwtCredential>();
            if (!credentials.isEmpty()) {

                Map<KapuaId, String> idUsernameMap = UserCreatedByModifiedByUtils.resolveFromListResultAndFields(
                        credentials,
                        new Callable<Set<KapuaId>>() {
                            @Override
                            public Set<KapuaId> call() {
                                Set<KapuaId> additionalKapuaIds = new HashSet<KapuaId>();
                                for (Credential credential : credentials.getItems()) {
                                    additionalKapuaIds.add(credential.getUserId());
                                }

                                return additionalKapuaIds;
                            }
                });

                // Convert to GWT entity
                for (Credential credential : credentials.getItems()) {
                    GwtCredential gwtCredential = KapuaGwtAuthenticationModelConverter.convertCredential(credential);

                    gwtCredential.setUsername(idUsernameMap.get(credential.getUserId()));
                    gwtCredential.setCreatedByName(idUsernameMap.get(credential.getCreatedBy()));
                    gwtCredential.setModifiedByName(idUsernameMap.get(credential.getModifiedBy()));

                    gwtCredentials.add(gwtCredential);
                }
            }

            return new BasePagingLoadResult<GwtCredential>(gwtCredentials, loadConfig.getOffset(), credentials.getTotalCount().intValue());
        } catch (Throwable t) {
            throw KapuaExceptionHandler.buildExceptionFromError(t);
        }
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
            UsernamePasswordCredentials loginCredentials = CREDENTIALS_FACTORY.newUsernamePasswordCredentials(finalUsername, oldPassword);
            loginCredentials.setAuthenticationCode(mfaCode);
            AUTHENTICATION_SERVICE.verifyCredentials(loginCredentials);

            PasswordChangeRequest passwordChangeRequest = USER_CREDENTIALS_FACTORY.newPasswordChangeRequest();
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

            PasswordResetRequest passwordResetRequest = USER_CREDENTIALS_FACTORY.newPasswordResetRequest();
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
