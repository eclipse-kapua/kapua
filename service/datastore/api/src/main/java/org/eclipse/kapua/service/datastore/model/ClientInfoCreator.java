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
 * Client information schema creator definition
 *
 * @since 1.0.0
 */
public class ClientInfoCreator extends StorableCreator<ClientInfo> {

    private String clientId;
    private StorableId messageId;
    private Date messageTimestamp;

    public ClientInfoCreator() {
    }

    /**
     * Construct a client information creator for the given account
     *
     * @param scopeId
     *         The scope {@link KapuaId}
     * @since 1.0.0
     */
    public ClientInfoCreator(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Get the client identifier
     *
     * @return
     * @since 1.0.0
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Set the client identifier
     *
     * @param clientId
     * @since 1.0.0
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Get the message identifier (of the first message published by this client)
     *
     * @return
     * @since 1.0.0
     */
    public StorableId getMessageId() {
        return messageId;
    }

    /**
     * Set the message identifier (of the first message published by this client)
     *
     * @param messageId
     * @since 1.0.0
     */
    public void setMessageId(StorableId messageId) {
        this.messageId = messageId;
    }

    /**
     * Get the message timestamp (of the first message published by this client)
     *
     * @return
     * @since 1.0.0
     */
    public Date getMessageTimestamp() {
        return messageTimestamp;
    }

    /**
     * Set the message timestamp (of the first message published by this client)
     *
     * @param messageTimestamp
     * @since 1.0.0
     */
    public void setMessageTimestamp(Date messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

}
