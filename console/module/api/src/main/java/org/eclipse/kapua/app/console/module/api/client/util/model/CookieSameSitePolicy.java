/*******************************************************************************
 * Copyright (c) 2026, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.api.client.util.model;

/**
 * Representation of SameSite Cookie attribute values.
 *
 * @since 2.1.0
 */
public interface CookieSameSitePolicy {

    /**
     * @since 2.1.0
     */
    String STRICT = "Strict";

    /**
     * @since 2.1.0
     */
    String LAX = "Lax";

    /**
     * @since 2.1.0
     */
    String NONE = "None";
}
