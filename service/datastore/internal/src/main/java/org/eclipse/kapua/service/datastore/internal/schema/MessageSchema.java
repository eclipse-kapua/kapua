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
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.storeengine.client.SchemaKeys;
import org.eclipse.kapua.service.storable.exception.MappingException;
import org.eclipse.kapua.service.storable.model.utils.KeyValueEntry;
import org.eclipse.kapua.service.storable.model.utils.MappingUtils;

/**
 * {@link Message} schema definition.
 *
 * @since 1.0.0
 */
public class MessageSchema {

    /**
     * @since 1.0.0
     */
    private MessageSchema() {
    }

    /**
     * Message schema name
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_TYPE_NAME = "message";

    /**
     * Message id
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_ID = "message_id";

    /**
     * Message timestamp
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_TIMESTAMP = "timestamp";

    /**
     * Message received on timestamp
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_RECEIVED_ON = "received_on";

    /**
     * Message received by address
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_IP_ADDRESS = "ip_address";

    /**
     * Message scope id
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_SCOPE_ID = "scope_id";

    /**
     * Message device identifier
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_DEVICE_ID = "device_id";

    /**
     * Message client identifier
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_CLIENT_ID = "client_id";

    /**
     * Message channel
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_CHANNEL = "channel";

    /**
     * Message channel parts
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_CHANNEL_PARTS = "channel_parts";

    /**
     * Message captured on timestamp
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_CAPTURED_ON = "captured_on";

    /**
     * Message sent on timestamp
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_SENT_ON = "sent_on";

    /**
     * Message position - (composed object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POSITION = "position";

    /**
     * Message position - location (field name relative to the position object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_LOCATION = "location";

    /**
     * Message position - location (full field name)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_LOCATION_FULL = "position.location";

    /**
     * Message position - altitude (field name relative to the position object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_ALT = "alt";

    /**
     * Message position - altitude (full field name)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_ALT_FULL = "position.alt";

    /**
     * Message position - precision (field name relative to the position object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_PRECISION = "precision";

    /**
     * Message position - precision (full field name)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_PRECISION_FULL = "position.precision";

    /**
     * Message position - heading (field name relative to the position object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_HEADING = "heading";

    /**
     * Message position - heading (full field name)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_HEADING_FULL = "position.heading";

    /**
     * Message position - speed (field name relative to the position object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_SPEED = "speed";

    /**
     * Message position - speed (full field name)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_SPEED_FULL = "position.speed";

    /**
     * Message position - timestamp (field name relative to the position object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_TIMESTAMP = "timestamp";

    /**
     * Message position - timestamp (full field name)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_TIMESTAMP_FULL = "position.timestamp";

    /**
     * Message position - satellites (field name relative to the position object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_SATELLITES = "satellites";

    /**
     * Message position - satellites (full field name)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_SATELLITES_FULL = "position.satellites";

    /**
     * Message position - status (field name relative to the position object)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_STATUS = "status";

    /**
     * Message position - status (full field name)
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POS_STATUS_FULL = "position.status";

    /**
     * Message metrics
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_METRICS = "metrics";

    /**
     * Message body
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_BODY = "body";

    // position internal fields
    /**
     * Position latitude inner field
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POSITION_LATITUDE = "lat";

    /**
     * Position longitude inner field
     *
     * @since 1.0.0
     */
    public static final String MESSAGE_POSITION_LONGITUDE = "lon";

    /**
     * Create and return the Json representation of the message schema
     *
     * @return
     * @throws MappingException
     * @since 1.0.0
     */
    public static JsonNode getMessageTypeSchema() throws MappingException {
        ObjectNode messageNode = MappingUtils.newObjectNode();
        {
            ObjectNode sourceMessage = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_ENABLED, true)});
            messageNode.set(SchemaKeys.KEY_SOURCE, sourceMessage);

            ObjectNode propertiesNode = MappingUtils.newObjectNode();
            {
                ObjectNode messageId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(MESSAGE_ID, messageId);

                ObjectNode messageTimestamp = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                propertiesNode.set(MESSAGE_TIMESTAMP, messageTimestamp);

                ObjectNode messageReceivedOn = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                propertiesNode.set(MESSAGE_RECEIVED_ON, messageReceivedOn);

                ObjectNode messageIp = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_IP)});
                propertiesNode.set(MESSAGE_IP_ADDRESS, messageIp);

                ObjectNode messageScopeId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(MESSAGE_SCOPE_ID, messageScopeId);

                ObjectNode messageDeviceId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(MESSAGE_DEVICE_ID, messageDeviceId);

                ObjectNode messageClientId = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(MESSAGE_CLIENT_ID, messageClientId);

                ObjectNode messageChannel = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_KEYWORD), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_TRUE)});
                propertiesNode.set(MESSAGE_CHANNEL, messageChannel);

                ObjectNode messageCapturedOn = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                propertiesNode.set(MESSAGE_CAPTURED_ON, messageCapturedOn);

                ObjectNode messageSentOn = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                propertiesNode.set(MESSAGE_SENT_ON, messageSentOn);

                ObjectNode positionNode = MappingUtils.newObjectNode(
                        new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_OBJECT), new KeyValueEntry(SchemaKeys.KEY_ENABLED, true),
                                new KeyValueEntry(SchemaKeys.KEY_DYNAMIC, false)});

                ObjectNode positionPropertiesNode = MappingUtils.newObjectNode();
                {
                    ObjectNode messagePositionPropLocation = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_GEO_POINT)});
                    positionPropertiesNode.set(MESSAGE_POS_LOCATION, messagePositionPropLocation);

                    ObjectNode messagePositionPropAlt = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DOUBLE)});
                    positionPropertiesNode.set(MESSAGE_POS_ALT, messagePositionPropAlt);

                    ObjectNode messagePositionPropPrec = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DOUBLE)});
                    positionPropertiesNode.set(MESSAGE_POS_PRECISION, messagePositionPropPrec);

                    ObjectNode messagePositionPropHead = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DOUBLE)});
                    positionPropertiesNode.set(MESSAGE_POS_HEADING, messagePositionPropHead);

                    ObjectNode messagePositionPropSpeed = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DOUBLE)});
                    positionPropertiesNode.set(MESSAGE_POS_SPEED, messagePositionPropSpeed);

                    ObjectNode messagePositionPropTime = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_DATE), new KeyValueEntry(SchemaKeys.KEY_FORMAT, DatastoreUtils.DATASTORE_DATE_FORMAT)});
                    positionPropertiesNode.set(MESSAGE_POS_TIMESTAMP, messagePositionPropTime);

                    ObjectNode messagePositionPropSat = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_INTEGER)});
                    positionPropertiesNode.set(MESSAGE_POS_SATELLITES, messagePositionPropSat);

                    ObjectNode messagePositionPropStat = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_INTEGER)});
                    positionPropertiesNode.set(MESSAGE_POS_STATUS, messagePositionPropStat);
                }
                positionNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, positionPropertiesNode);

                propertiesNode.set(SchemaKeys.FIELD_NAME_POSITION, positionNode);
            }
            messageNode.set(SchemaKeys.FIELD_NAME_PROPERTIES, propertiesNode);

            ObjectNode messageMetrics = MappingUtils.newObjectNode(
                    new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_OBJECT), new KeyValueEntry(SchemaKeys.KEY_ENABLED, true),
                            new KeyValueEntry(SchemaKeys.KEY_DYNAMIC, true)});
            propertiesNode.set(MESSAGE_METRICS, messageMetrics);

            ObjectNode messageBody = MappingUtils.newObjectNode(new KeyValueEntry[]{new KeyValueEntry(SchemaKeys.KEY_TYPE, SchemaKeys.TYPE_BINARY), new KeyValueEntry(SchemaKeys.KEY_INDEX, SchemaKeys.VALUE_FALSE)});
            propertiesNode.set(MESSAGE_BODY, messageBody);
        }
        return messageNode;
    }

}
