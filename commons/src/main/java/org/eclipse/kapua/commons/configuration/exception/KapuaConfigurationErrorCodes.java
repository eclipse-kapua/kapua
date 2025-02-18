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
package org.eclipse.kapua.commons.configuration.exception;

import org.eclipse.kapua.KapuaErrorCode;

/**
 * {@link KapuaErrorCode}s for {@link KapuaConfigurationException}
 *
 * @since 1.0.0
 */
public enum KapuaConfigurationErrorCodes implements KapuaErrorCode {

    /**
     * Internal error.
     *
     * @since 1.0.0
     */
    INTERNAL_ERROR,

    /**
     * @see ServiceConfigurationLimitExceededException
     * @since 1.0.0
     */
    LIMIT_EXCEEDED,

    /**
     * @see ServiceConfigurationLimitExceededException
     * @since 2.0.0
     */
    LIMIT_EXCEEDED_BY,

    /**
     * @see ServiceConfigurationParentLimitExceededException
     * @since 1.0.0
     */
    PARENT_LIMIT_EXCEEDED,

    /**
     * @see ServiceConfigurationParentLimitExceededException
     * @since 2.0.0
     */
    PARENT_LIMIT_EXCEEDED_BY,

    /**
     * @see ServiceConfigurationForbiddenException
     * @since 2.1.0
     */
    UPDATE_PROPERTY_FORBIDDEN,
}
