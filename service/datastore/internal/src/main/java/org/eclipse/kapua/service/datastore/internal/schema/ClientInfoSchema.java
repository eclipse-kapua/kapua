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
package org.eclipse.kapua.service.datastore.internal.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.storeengine.client.SchemaKeys;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

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

    /**
     * Create and return the Json representation of the client info schema
     *
     * @return
     * @throws MappingException
     * @since 1.0.0
     */
    public static JsonNode getClientTypeSchema() throws MappingException {

        ObjectNode clientNode = MappingUtils.newObjectNode();
        {
            ObjectNode sourceClient = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_ENABLED, true)});
            clientNode.set(SchemaKeys.KEY_SOURCE, sourceClient);

            ObjectNode propertiesNode = MappingUtils.newObjectNode();
            {
                ObjectNode clientId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(CLIENT_ID, clientId);

                ObjectNode clientTimestamp = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                propertiesNode.set(CLIENT_TIMESTAMP, clientTimestamp);

                ObjectNode clientScopeId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(CLIENT_SCOPE_ID, clientScopeId);

                ObjectNode clientMessageId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(CLIENT_MESSAGE_ID, clientMessageId);
            }
            clientNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, propertiesNode);
        }

        return clientNode;
    }

}
