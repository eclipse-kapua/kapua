/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.client.filter;

/**
 * List of headers name used by the job engine client and read by the job engine service to rebuild the session
 *
 * @since 1.5.0
 */
public interface SessionInfoHttpHeaders {

    String SCOPE_ID_HTTP_HEADER = "X-Kapua-Scope-ID";
    String USER_ID_HTTP_HEADER = "X-Kapua-User-ID";
    String AUTH_MODE = "X-Kapua-Auth-Mode";
}
