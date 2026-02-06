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
package org.eclipse.kapua.service.device.registry;

import org.eclipse.kapua.model.KapuaUpdatableEntityAttributes;

/**
 * {@link DeviceQuery} predicates.
 *
 * @since 1.0.0
 */
public class DeviceAttributes extends KapuaUpdatableEntityAttributes {

    /**
     * Group id.
     *
     * @since 1.0.0
     */
    public static final String GROUP_ID = "groupId";

    /**
     * Group ids.
     *
     * @since 2.1.0
     */
    public static final String GROUP_IDS = "groupIds";

    /**
     * Tag id.
     *
     * @since 1.0.0
     */
    public static final String TAG_IDS = "tagIds";

    /**
     * Client identifier.
     *
     * @since 1.0.0
     */
    public static final String CLIENT_ID = "clientId";
    /**
     * Display name.
     *
     * @since 1.0.0
     */
    public static final String DISPLAY_NAME = "displayName";

    /**
     * Connection id.
     *
     * @since 1.0.0
     */
    public static final String CONNECTION_ID = "connectionId";

    /**
     * Connection.
     *
     * @since 1.0.0
     */
    public static final String CONNECTION = "connection";

    /**
     * Connection status.
     *
     * @since 1.0.0
     */
    public static final String CONNECTION_STATUS = CONNECTION + ".status";

    /**
     * Connection protocol.
     *
     * @since 1.0.0
     */
    public static final String CONNECTION_PROTOCOL = CONNECTION + ".protocol";

    /**
     * Connection clientId.
     *
     * @since 1.0.0
     */
    public static final String CONNECTION_CLIENT_ID = CONNECTION + ".clientId";

    /**
     * Connection Client IP.
     *
     * @since 1.0.0
     */
    public static final String CLIENT_IP = CONNECTION + ".clientIp";

    /**
     * Last event.
     *
     * @since 1.0.0
     */
    public static final String LAST_EVENT = "lastEvent";

    /**
     * Last event on.
     *
     * @since 1.0.0
     */
    public static final String LAST_EVENT_ON = LAST_EVENT + ".receivedOn";

    /**
     * Last event resource.
     *
     * @since 1.0.0
     */
    public static final String LAST_EVENT_RESOURCE = LAST_EVENT + ".resource";

    /**
     * Serial number.
     *
     * @since 1.0.0
     */
    public static final String SERIAL_NUMBER = "serialNumber";
    /**
     * Imei.
     *
     * @since 1.0.0
     */
    public static final String IMEI = "imei";
    /**
     * Imsi.
     *
     * @since 1.0.0
     */
    public static final String IMSI = "imsi";
    /**
     * Iccd.
     *
     * @since 1.0.0
     */
    public static final String ICCID = "iccid";
    /**
     * Model identifier.
     *
     * @since 1.0.0
     */
    public static final String MODEL_ID = "modelId";
    /**
     * Model name.
     *
     * @since 1.0.0
     */
    public static final String MODEL_NAME = "modelName";
    /**
     * Bios version.
     *
     * @since 1.0.0
     */
    public static final String BIOS_VERSION = "biosVersion";
    /**
     * Firmware version.
     *
     * @since 1.0.0
     */
    public static final String FIRMWARE_VERSION = "firmwareVersion";
    /**
     * Operating system version.
     *
     * @since 1.0.0
     */
    public static final String OS_VERSION = "osVersion";
    /**
     * Jvm version.
     *
     * @since 1.0.0
     */
    public static final String JVM_VERSION = "jvmVersion";
    /**
     * Osgi framework version.
     *
     * @since 1.0.0
     */
    public static final String OSGI_FRAMEWORK_VERSION = "osgiFrameworkVersion";
    /**
     * Application framework version.
     *
     * @since 1.0.0
     */
    public static final String APPLICATION_FRAMEWORK_VERSION = "applicationFrameworkVersion";
    /**
     * Application identifier.
     *
     * @since 1.0.0
     */
    public static final String APPLICATION_IDENTIFIERS = "applicationIdentifiers";
    /**
     * Custom attribute 1.
     *
     * @since 1.0.0
     */
    public static final String CUSTOM_ATTRIBUTE_1 = "customAttribute1";
    /**
     * Custom attribute 2.
     *
     * @since 1.0.0
     */
    public static final String CUSTOM_ATTRIBUTE_2 = "customAttribute2";
    /**
     * Custom attribute 3.
     *
     * @since 1.0.0
     */
    public static final String CUSTOM_ATTRIBUTE_3 = "customAttribute3";
    /**
     * Custom attribute 4.
     *
     * @since 1.0.0
     */
    public static final String CUSTOM_ATTRIBUTE_4 = "customAttribute4";
    /**
     * Custom attribute 5.
     *
     * @since 1.0.0
     */
    public static final String CUSTOM_ATTRIBUTE_5 = "customAttribute5";
    /**
     * Accept encoding.
     *
     * @since 1.0.0
     */
    public static final String ACCEPT_ENCODING = "acceptEncoding";
    /**
     * Gps longitude.
     *
     * @since 1.0.0
     */
    public static final String GPS_LONGITUDE = "gpsLongitude";
    /**
     * Gps latitude.
     *
     * @since 1.0.0
     */
    public static final String GPS_LATITUDE = "gpsLatitude";
    /**
     * Device status.
     *
     * @since 1.0.0
     */
    public static final String STATUS = "status";
    /**
     * Credentials mode.
     *
     * @since 1.0.0
     */
    public static final String CREDENTIALS_MODE = "credentialsMode";
    /**
     * Preferred user identifier.
     *
     * @since 1.0.0
     */
    public static final String PREFERRED_USER_ID = "preferredUserId";
    /**
     * Connection Interface.
     *
     * @since 1.0.0
     */
    public static final String CONNECTION_INTERFACE = "connectionInterface";
    /**
     * Connection IP.
     *
     * @since 1.0.0
     */
    public static final String CONNECTION_IP = "connectionIp";
    // Extended properties
    /**
     * {@link DeviceExtendedProperty}es
     *
     * @since 1.5.0
     */
    public static final String EXTENDED_PROPERTIES = "extendedProperties";

    /**
     * {@link DeviceExtendedProperty}es group name
     *
     * @since 1.5.0
     */
    public static final String EXTENDED_PROPERTIES_GROUP_NAME = EXTENDED_PROPERTIES + ".groupName";

    /**
     * {@link DeviceExtendedProperty}es name
     *
     * @since 1.5.0
     */
    public static final String EXTENDED_PROPERTIES_NAME = EXTENDED_PROPERTIES + "name";

    /**
     * {@link DeviceExtendedProperty}es value
     *
     * @since 1.5.0
     */
    public static final String EXTENDED_PROPERTIES_VALUE = EXTENDED_PROPERTIES + "value";
}
