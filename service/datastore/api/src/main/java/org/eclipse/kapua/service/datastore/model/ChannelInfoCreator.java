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
package org.eclipse.kapua.service.datastore.model;

import java.util.Date;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.storable.model.StorableCreator;
import org.eclipse.kapua.service.storable.model.id.StorableId;

/**
 * Channel information schema creator definition
 *
 * @since 1.0.0
 */
public class ChannelInfoCreator extends StorableCreator<ChannelInfo> {

    private String clientId;
    private String name;
    private StorableId messageId;
    private Date messageTimestamp;

    public ChannelInfoCreator() {
    }

    public ChannelInfoCreator(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Get the client identifier
     *
     * @return
     * @since 1.0.0
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * @param clientId
     * @since 1.3.0
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Get the name
     *
     * @return
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Set the channel name
     *
     * @param name
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the message identifier (of the first message published on this channel)
     *
     * @return
     * @since 1.0.0
     */
    public StorableId getMessageId() {
        return messageId;
    }

    /**
     * Set the message identifier (of the first message published on this channel)
     *
     * @param messageId
     * @since 1.0.0
     */
    public void setMessageId(StorableId messageId) {
        this.messageId = messageId;
    }

    /**
     * Get the message timestamp (of the first message published on this channel)
     *
     * @return
     * @since 1.0.0
     */
    public Date getMessageTimestamp() {
        return messageTimestamp;
    }

    /**
     * Set the message timestamp (of the first message published on this channel)
     *
     * @param messageTimestamp
     * @since 1.0.0
     */
    public void setMessageTimestamp(Date messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

}
