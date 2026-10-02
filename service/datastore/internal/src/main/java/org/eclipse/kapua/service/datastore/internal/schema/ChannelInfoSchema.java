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
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.storeengine.client.SchemaKeys;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

/**
 * {@link ChannelInfo} schema definition.
 *
 * @since 1.0.0
 */
public class ChannelInfoSchema {

    /**
     * @since 1.0.0
     */
    private ChannelInfoSchema() {
    }

    /**
     * Channel information schema name
     *
     * @since 1.0.0
     */
    public static final String CHANNEL_TYPE_NAME = "channel";
    /**
     * Channel information - channel
     *
     * @since 1.0.0
     */
    public static final String CHANNEL_NAME = "channel";
    /**
     * Channel information - client identifier
     *
     * @since 1.0.0
     */
    public static final String CHANNEL_CLIENT_ID = "client_id";
    /**
     * Channel information - scope id
     *
     * @since 1.0.0
     */
    public static final String CHANNEL_SCOPE_ID = "scope_id";
    /**
     * Channel information - message timestamp (of the first message published in this channel)
     *
     * @since 1.0.0
     */
    public static final String CHANNEL_TIMESTAMP = "timestamp";
    /**
     * Channel information - message identifier (of the first message published in this channel)
     *
     * @since 1.0.0
     */
    public static final String CHANNEL_MESSAGE_ID = "message_id";

    /**
     * Create and return the Json representation of the channel info schema
     *
     * @return
     * @throws MappingException
     * @since 1.0.0
     */
    public static JsonNode getChannelTypeSchema() throws MappingException {
        ObjectNode channelNode = MappingUtils.newObjectNode();

        {
            ObjectNode sourceChannel = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_ENABLED, true)});
            channelNode.set(SchemaKeys.KEY_SOURCE, sourceChannel);

            ObjectNode propertiesNode = MappingUtils.newObjectNode();
            {
                ObjectNode channelScopeId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(CHANNEL_SCOPE_ID, channelScopeId);

                ObjectNode channelClientId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(CHANNEL_CLIENT_ID, channelClientId);

                ObjectNode channelName = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(CHANNEL_NAME, channelName);

                ObjectNode channelTimestamp = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                propertiesNode.set(CHANNEL_TIMESTAMP, channelTimestamp);

                ObjectNode channelMessageId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(CHANNEL_MESSAGE_ID, channelMessageId);
            }
            channelNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, propertiesNode);
        }
        return channelNode;
    }

}
