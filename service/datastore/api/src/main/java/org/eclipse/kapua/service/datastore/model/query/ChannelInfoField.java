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

import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.storable.model.query.StorableField;

/**
 * This enumeration defines the fields names used in the {@link ChannelInfo} client schema
 *
 * @since 1.0.0
 */
public enum ChannelInfoField implements StorableField {
    /**
     * Channel
     *
     * @since 1.0.0
     */
    CHANNEL(ChannelInfoSchema.CHANNEL_NAME),

    /**
     * Client identifier
     *
     * @since 1.0.0
     */
    CLIENT_ID(ChannelInfoSchema.CHANNEL_CLIENT_ID),

    /**
     * Scope id
     *
     * @since 1.0.0
     */
    SCOPE_ID(ChannelInfoSchema.CHANNEL_SCOPE_ID),

    /**
     * Timestamp
     *
     * @since 1.0.0
     */
    TIMESTAMP(ChannelInfoSchema.CHANNEL_TIMESTAMP),

    /**
     * Message identifier
     *
     * @since 1.0.0
     */
    MESSAGE_ID(ChannelInfoSchema.CHANNEL_MESSAGE_ID);

    private String field;

    private ChannelInfoField(String name) {
        this.field = name;
    }

    @Override
    public String field() {
        return field;
    }

}
