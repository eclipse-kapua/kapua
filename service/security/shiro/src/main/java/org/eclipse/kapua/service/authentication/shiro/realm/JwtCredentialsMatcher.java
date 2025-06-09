/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.authentication.shiro.realm;

import javax.validation.constraints.NotNull;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.eclipse.kapua.plugin.sso.openid.JwtProcessor;
import org.eclipse.kapua.service.authentication.credential.Credential;
import org.eclipse.kapua.service.authentication.shiro.JwtCredentialsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link JwtCredentialsMatcher} credential matcher implementation
 *
 * @since 1.0
 */
public class JwtCredentialsMatcher implements CredentialsMatcher {

    private static final Logger logger = LoggerFactory.getLogger(JwtCredentialsMatcher.class);
    private JwtProcessor jwtProcessor;

    public JwtCredentialsMatcher(@NotNull final JwtProcessor jwtProcessor) {
        this.jwtProcessor = jwtProcessor;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {

        final String idToken = ((JwtCredentialsImpl) authenticationToken).getIdToken();
        if (idToken == null) {
            // we don't have a JWT
            return false;
        }

        // check for correct credentials type

        final Object credentialsValue = authenticationInfo.getCredentials();
        if (!(credentialsValue instanceof Credential)) {
            return false;
        }

        // extract credentials

        final Credential credentials = (Credential) credentialsValue;

        // Match token with info

        if (!idToken.equals(credentials.getCredentialKey())) {
            return false;
        }

        try {
            // validate the JWT
            return this.jwtProcessor.validate(idToken);
        } catch (Exception e) {
            logger.error("Error while validating JWT credentials", e);
        }

        return false;
    }

}
