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
 *******************************************************************************/
package org.eclipse.kapua.commons.security;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.KapuaPrincipal;
import org.eclipse.kapua.service.authentication.token.AccessToken;

/**
 * Kapua session
 *
 * @since 1.0
 */
public class KapuaSession implements Serializable {

    private static final long serialVersionUID = -3831904230950408142L;

    public static final String KAPUA_SESSION_KEY = "KapuaSession";

    private static final List<String> TRUSTED_CLASSES = new ArrayList<>();
    private static final String TRUST_CLASS_METHOD_PATTERN = "{0}.{1}";

    // TODO to be moved inside configuration service or something like that "fully.qualified.classname.methodname" (<init> for the constructor)
    static {
        TRUSTED_CLASSES.add("org.eclipse.kapua.commons.security.KapuaSecurityUtils.doPrivileged");
        TRUSTED_CLASSES.add("org.eclipse.kapua.commons.event.jms.EventListener.onMessage");
        TRUSTED_CLASSES.add("org.eclipse.kapua.job.engine.app.core.filter.RebuildTrustedSessionFilter.isAccessAllowed");
    }

    /**
     * Access token that identify the logged in session.
     */
    private AccessToken accessToken;

    /**
     * User scope identifier
     */
    private KapuaId scopeId;

    /**
     * User identifier
     */
    private KapuaId userId;

    /**
     * Trusted mode.<br> If true every rights check will be skipped, in other word <b>the user is trusted so he is allowed to execute every operation</b> defined in the system.
     */
    private boolean trustedMode;

    /**
     * OpenID Connect idToken obtained with an OpenID Connect login, contains user information, used for the OpenID Connect logout
     */
    private String openIDidToken;

    /**
     * Set to true when the logout from the current session is triggered by the user
     */
    private boolean userInitiatedLogout;

    /**
     * Default constructor
     */
    public KapuaSession() {
        super();
    }

    private KapuaSession(KapuaId scopeId, KapuaId userId, boolean trustedMode) {
        this.scopeId = scopeId;
        this.userId = userId;
        this.trustedMode = trustedMode;
    }

    /**
     * Creates a {@link KapuaSession} copy with trusted mode flag set to true (to be used only from trusted classes)
     *
     * @return
     */
    public static KapuaSession createFrom() {
        if (isCallerClassTrusted()) {
            KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
            KapuaSession kapuaSessionCopy = new KapuaSession(kapuaSession.getAccessToken(),
                    kapuaSession.getScopeId(),
                    kapuaSession.getUserId());
            kapuaSessionCopy.trustedMode = true;
            return kapuaSessionCopy;
        } else {
            // TODO to be replaced with a security exception
            throw new RuntimeException("Method not allowed for the caller class");
        }
    }

    /**
     * Creates a new {@link KapuaSession} with trusted mode flag set to true (to be used only from trusted classes)
     *
     * @return
     */
    public static KapuaSession createFrom(KapuaId scopeId, KapuaId userId) {
        if (isCallerClassTrusted()) {
            KapuaSession session = new KapuaSession(scopeId, userId, true);
            KapuaSecurityUtils.setSession(session);
            return session;
        } else {
            // TODO to be replaced with a security exception
            throw new RuntimeException("Method not allowed for the caller class");
        }
    }

    /**
     * Check if the caller is included in the caller list allowed to change the trusted mode flag.
     *
     * @return
     */
    private static boolean isCallerClassTrusted() {
        // the stack trace should be like
        // 0 ---> Thread
        // 1 ---> KapuaSession -> isCallerClassTrusted()
        // 2 ---> KapuaSession -> createFrom()
        // 3 ---> "outside" caller class that should be checked
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements != null && stackTraceElements.length > 4) {
            return TRUSTED_CLASSES.contains(MessageFormat.format(TRUST_CLASS_METHOD_PATTERN, stackTraceElements[3].getClassName(), stackTraceElements[3].getMethodName()));
        } else {
            return false;
        }
    }

    /**
     * Constructs a {@link KapuaSession} with given parameters
     *
     * @param accessToken
     * @param scopeId
     * @param userId
     */
    public KapuaSession(AccessToken accessToken,
            KapuaId scopeId,
            KapuaId userId) {
        this();
        this.accessToken = accessToken;
        this.scopeId = scopeId;
        this.userId = userId;
    }

    /**
     * Constructs a {@link KapuaSession} with given parameters
     *
     * @param accessToken
     * @param scopeId
     * @param userId
     * @param openIDidToken
     *         the idToken obtained with an OpenID Connect login, contains user information, used for the logout
     */
    public KapuaSession(AccessToken accessToken, KapuaId scopeId, KapuaId userId, String openIDidToken) {
        this();
        this.accessToken = accessToken;
        this.scopeId = scopeId;
        this.userId = userId;
        this.openIDidToken = openIDidToken;
    }

    /**
     * Constructs a {@link KapuaSession} with given parameter
     *
     * @param principal
     */
    public KapuaSession(KapuaPrincipal principal) {
        trustedMode = true;
        scopeId = principal.getAccountId();
        userId = principal.getUserId();
    }

    /**
     * Get the access token
     *
     * @return
     */
    public AccessToken getAccessToken() {
        return accessToken;
    }

    /**
     * Get the scope identifier
     *
     * @return
     */
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Get the user identifier
     *
     * @return
     */
    public KapuaId getUserId() {
        return userId;
    }

    /**
     * Get the OpenID Connect idToken
     *
     * @return
     */
    public String getOpenIDidToken() {
        return openIDidToken;
    }

    /**
     * Set the trusted mode status.<br> If true every rights check will be skipped, in other word <b>the user is trusted so he is allowed to execute every operation</b> defined in the system.
     */
    final void setTrustedMode(boolean trustedMode) {
        this.trustedMode = trustedMode;
    }

    /**
     * Return the trusted mode status.<br> If true every rights check will be skipped, in other word <b>the user is trusted so he is allowed to execute every operation</b> defined in the system.
     *
     * @return
     */
    public final boolean isTrustedMode() {
        return trustedMode;
    }

    /**
     * Get the `userInitiatedLogout` value.
     *
     * @return 'true' if user initiated logout, 'false' otherwise
     */
    public boolean isUserInitiatedLogout() {
        return userInitiatedLogout;
    }

    /**
     * Set the logout as 'user initiated'. This will allow to avoid logging out from an OpenID session by using the OpenIDLogoutListener.
     *
     * @param userInitiatedLogout
     *         'true' if user initiated logout, 'false' otherwise
     */
    public void setUserInitiatedLogout(boolean userInitiatedLogout) {
        this.userInitiatedLogout = userInitiatedLogout;
    }
}
