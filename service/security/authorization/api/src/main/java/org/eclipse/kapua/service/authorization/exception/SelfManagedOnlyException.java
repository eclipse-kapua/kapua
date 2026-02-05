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
 * {@link KapuaAuthorizationException} to {@code throw} when an operation can only be performed on the User which is currently logged.
 *
 * @since 1.4.0
 */
public class SelfManagedOnlyException extends KapuaAuthorizationException {

    private static final long serialVersionUID = -3116117876370203330L;

    /**
     * Constructor.
     *
     * @since 1.4.0
     */
    public SelfManagedOnlyException() {
        super(KapuaAuthorizationErrorCodes.SELF_MANAGED_ONLY);
    }
}
