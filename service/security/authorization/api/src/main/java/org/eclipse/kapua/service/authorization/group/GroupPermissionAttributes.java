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
package org.eclipse.kapua.service.authorization.group;

import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionAttributes;

/**
 * Query predicate attribute name for {@link GroupPermission} entity.
 *
 * @since 2.1.0
 */
public class GroupPermissionAttributes extends KapuaEntityAttributes {

    /**
     * Predicate for field {@link GroupPermission#getGroupId()}
     *
     * @since 2.1.0
     */
    public static final String GROUP_ID = "groupId";

    /**
     * Predicate for field {@link GroupPermission#getPermission()}
     *
     * @since 2.1.0
     */
    public static final String PERMISSION = "permission";

    /**
     * Predicate for field {@link GroupPermission#getPermission()}.{@link Permission#getDomain()}
     *
     * @since 2.1.0
     */
    public static final String PERMISSION_DOMAIN = PERMISSION + "." + PermissionAttributes.DOMAIN;

    /**
     * Predicate for field {@link GroupPermission#getPermission()}.{@link Permission#getAction()}
     *
     * @since 2.1.0
     */
    public static final String PERMISSION_ACTION = PERMISSION + "." + PermissionAttributes.ACTION;

    /**
     * Predicate for field {@link GroupPermission#getPermission()}.{@link Permission#getTargetScopeId()}
     *
     * @since 2.1.0
     */
    public static final String PERMISSION_TARGET_SCOPE_ID = PERMISSION + "." + PermissionAttributes.TARGET_SCOPE_ID;

    /**
     * Predicate for field {@link GroupPermission#getPermission()}.{@link Permission#getGroupId()}
     *
     * @since 2.1.0
     */
    public static final String PERMISSION_GROUP_ID = PERMISSION + "." + PermissionAttributes.GROUP_ID;

    /**
     * Predicate for field {@link GroupPermission#getPermission()}.{@link Permission#getForwardable()}
     *
     * @since 2.1.0
     */
    public static final String PERMISSION_FORWARDABLE = PERMISSION + "." + PermissionAttributes.FORWARDABLE;

}
