/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.jersey.rest;

import org.eclipse.kapua.KapuaErrorCode;

public enum RestApiErrorCodes implements KapuaErrorCode {
    /**
     * When a resource receive a request, but the {@link org.eclipse.kapua.commons.security.KapuaSession} is not populated. This is likely to happen when the `shiro.ini` does not map the
     * `kapuaAuthcAccessToken` as a request filter chain.
     */
    SESSION_NOT_POPULATED
}
