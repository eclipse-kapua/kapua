/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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

/**
 * {@link KapuaAuthorizationException} to {@code throw} when an operation is reserved only to User with User type is UserType INTERNAL.
 *
 * @since 1.4.0
 */
public class InternalUserOnlyException extends KapuaAuthorizationException {

    private static final long serialVersionUID = 2018380306732864218L;

    /**
     * Constructor.
     *
     * @since 1.4.0
     */
    public InternalUserOnlyException() {
        super(KapuaAuthorizationErrorCodes.INTERNAL_USER_ONLY);
    }
}
