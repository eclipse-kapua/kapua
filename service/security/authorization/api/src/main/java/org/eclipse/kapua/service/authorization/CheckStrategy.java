/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authorization;

import org.eclipse.kapua.service.authorization.permission.Permission;

import java.util.Collection;

/**
 * Defines the strategy to use when using {@link AuthorizationService#checkPermissions(Collection, CheckStrategy)}
 *
 * @since 2.1.0
 */
public enum CheckStrategy {

    /**
     * The current Subject must have given {@link Permission}
     *
     * @since 2.1.0
     */
    ALL_OF,

    /**
     * The current Subject must at least on of the given {@link Permission}
     *
     * @since 2.1.0
     */
    AT_LEAST_ONE_OF
}
