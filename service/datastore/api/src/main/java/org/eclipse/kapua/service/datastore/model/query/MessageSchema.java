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

import org.eclipse.kapua.message.Message;

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

}
