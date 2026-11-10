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
package org.eclipse.kapua.service.storable.exception;

import org.eclipse.kapua.KapuaErrorCode;

/**
 * {@link StorableException} {@link KapuaErrorCode}s
 *
 * @since 1.3.0
 */
public enum StorableErrorCodes implements KapuaErrorCode {

    /**
     * See {@link InvalidValueMappingException}
     *
     * @since 1.3.0
     */
    INVALID_VALUE,

    /**
     * See {@link UnsupportedTypeMappingException}
     *
     * @since 1.3.0
     */
    UNSUPPORTED_TYPE,

    /**
     * See {@link StorableNotFoundException}
     *
     * @since 2.0.0
     */
    STORABLE_NOT_FOUND
}
