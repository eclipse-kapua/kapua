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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.message.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;

/**
 * Test Utility for repeating parts of code in Message module. Mostly for populating structures with random / semi random test data.
 */
public class KapuaMessageUtil {

    private KapuaMessageUtil() {
    }

    /**
     * Prepare payload data that contains two metrics and simple byte payload.
     *
     * @param kapuaPayload
     *         payload reference to fill with test data
     */
    public static void populatePayload(KapuaPayload kapuaPayload) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("key1", "value1");
        metrics.put("key2", "value2");
        kapuaPayload.setMetrics(metrics);
        byte[] body = { 'b', 'o', 'd', 'y' };
        kapuaPayload.setBody(body);
    }

    /**
     * Prepare payload data that contains metrics with all available types.
     *
     * @param kapuaPayload
     *         payload reference to fill with test data
     */
    public static void populatePayloadWithAllTypesOfMetrics(KapuaPayload kapuaPayload) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("Float", Float.valueOf(42.42f));
        metrics.put("Double", Double.valueOf(42.42d));
        metrics.put("Integer", Integer.valueOf(42));
        metrics.put("Long", Long.valueOf(43l));
        metrics.put("Boolean", Boolean.TRUE);
        metrics.put("String", "Big brown fox");
        metrics.put("byte", new byte[] { 'b', 'o', 'd', 'y', 0 });
        metrics.put("unknown", new BigDecimal("42.42"));
        kapuaPayload.setMetrics(metrics);
    }

    /**
     * Prepare channel semantic parts with three part metric.
     *
     * @param kapuaChannel
     *         channel reference to fill with test data
     */
    public static void populateChannel(KapuaChannel kapuaChannel) {
        List<String> semanticParts = new ArrayList<>();
        semanticParts.add("part1");
        semanticParts.add("part2");
        semanticParts.add("part3");
        kapuaChannel.setSemanticParts(semanticParts);
    }

    /**
     * Prepare full KapuaMessage with fixed data. Data is not semantically correct, just tokens that fill all necessary fields.
     *
     * @param kapuaMessage
     *         KapuaMessage reference to fill with test data
     * @param referenceDate
     *         reference date on which all date fields are based
     */
    public static void populateKapuaMessage(KapuaMessage<?, ?> kapuaMessage, ZonedDateTime referenceDate) {
        kapuaMessage.setId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        kapuaMessage.setScopeId(new KapuaEid(BigInteger.ONE));
        kapuaMessage.setDeviceId(new KapuaEid(BigInteger.ONE));
        kapuaMessage.setReceivedOn(Date.from(referenceDate.plusMinutes(1).toInstant()));
        kapuaMessage.setSentOn(Date.from(referenceDate.plusSeconds(10).toInstant()));
        kapuaMessage.setCapturedOn(Date.from(referenceDate.toInstant()));
        KapuaPosition position = new KapuaPosition();
        populatePosition(position, referenceDate);
        kapuaMessage.setPosition(position);
    }

    /**
     * Prepare position data with fixed values. Values are semantically correct.
     *
     * @param position
     *         reference to position object that is filled with test data
     * @param referenceDate
     *         reference date on which all date fields are based
     */
    public static void populatePosition(KapuaPosition position, ZonedDateTime referenceDate) {
        position.setLongitude(Double.valueOf("45.1111"));
        position.setLatitude(Double.valueOf("15.3333"));
        position.setAltitude(Double.valueOf("430.30"));
        position.setPrecision(Double.valueOf("12"));
        position.setHeading(Double.valueOf("280"));
        position.setSpeed(Double.valueOf("60.20"));
        position.setTimestamp(Date.from(referenceDate.toInstant()));
        position.setSatellites(5);
        position.setStatus(4);
    }
}
