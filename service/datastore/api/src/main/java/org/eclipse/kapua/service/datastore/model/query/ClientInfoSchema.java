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
package org.eclipse.kapua.service.datastore.model.query;

import org.eclipse.kapua.service.datastore.model.ClientInfo;

/**
 * {@link ClientInfo} schema definition.
 *
 * @since 1.0.0
 */
public class ClientInfoSchema {

    /**
     * @since 1.0.0
     */
    private ClientInfoSchema() {
    }

    /**
     * Client information schema name.
     *
     * @since 1.0.0
     */
    public static final String CLIENT_TYPE_NAME = "client";

    /**
     * Client information - client identifier
     *
     * @since 1.0.0
     */
    public static final String CLIENT_ID = "client_id";

    /**
     * Client information - scope id
     *
     * @since 1.0.0
     */
    public static final String CLIENT_SCOPE_ID = "scope_id";

    /**
     * Client information - message timestamp (of the first message published in this channel)
     *
     * @since 1.0.0
     */
    public static final String CLIENT_TIMESTAMP = "timestamp";

    /**
     * Client information - message identifier (of the first message published in this channel)
     *
     * @since 1.0.0
     */
    public static final String CLIENT_MESSAGE_ID = "message_id";
}
