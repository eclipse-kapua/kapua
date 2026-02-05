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
package org.eclipse.kapua.service.authorization.exception;

import org.eclipse.kapua.service.authorization.permission.Permission;

import javax.validation.constraints.NotNull;

/**
 * {@link KapuaAuthorizationException} to {@code throw} when a {@link javax.security.auth.Subject} does not have the required {@link Permission}.
 *
 * @since 1.0.0
 */
public class SubjectUnauthorizedException extends KapuaAuthorizationException {

    private final Permission permission;

    /**
     * Constructor.
     *
     * @param permission The {@link Permission} that the {@link javax.security.auth.Subject} is missing.
     * @since 1.0.0
     */
    public SubjectUnauthorizedException(@NotNull Permission permission) {
        super(KapuaAuthorizationErrorCodes.SUBJECT_UNAUTHORIZED, null, permission);

        this.permission = permission;
    }

    /**
     * Gets the {@link Permission} that the {@link javax.security.auth.Subject} is missing.
     *
     * @return The {@link Permission} that the {@link javax.security.auth.Subject} is missing.
     * @since 1.0.0
     */
    public Permission getPermission() {
        return permission;
    }
}
