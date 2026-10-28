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
package org.eclipse.kapua.service.device.call.message.kura;

import com.google.common.collect.Maps;
import com.google.common.primitives.Booleans;
import com.google.protobuf.ByteString;
import org.eclipse.kapua.message.internal.MessageErrorCodes;
import org.eclipse.kapua.message.internal.MessageException;
import org.eclipse.kapua.service.device.call.message.DevicePayload;
import org.eclipse.kapua.service.device.call.message.DevicePosition;
import org.eclipse.kapua.service.device.call.message.kura.proto.KuraPayloadProto;
import org.eclipse.kapua.service.device.call.message.kura.utils.GZIPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * {@link DevicePayload} {@link org.eclipse.kapua.service.device.call.kura.Kura} implementation.
 *
 * @since 1.0.0
 */
public class KuraPayload implements DevicePayload {

    private static final Logger LOG = LoggerFactory.getLogger(KuraPayload.class);

    private static final int GZIP_BUFFER_SIZE = 8192;

    /**
     * The timestamp.
     *
     * @since 1.0.0
     */
    protected Date timestamp;

    /**
     * The {@link DevicePosition}.
     *
     * @since 1.0.0
     */
    protected DevicePosition position;

    /**
     * The metrics.
     *
     * @since 1.0.0
     */
    protected Map<String, Object> metrics;

    /**
     * The raw body.
     *
     * @since 1.0.0
     */
    protected byte[] body;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public KuraPayload() {
        super();
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public DevicePosition getPosition() {
        return position;
    }

    @Override
    public void setPosition(DevicePosition position) {
        this.position = position;
    }

    @Override
    public Map<String, Object> getMetrics() {
        if (metrics == null) {
            metrics = new HashMap<>();
        }

        return metrics;
    }

    @Override
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    @Override
    public void addMetric(String name, Object value) {
        getMetrics().put(name, value);
    }

    @Override
    public void removeMetric(String name) {
        getMetrics().remove(name);
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] toByteArray() {
        KuraPayloadProto.KuraPayload.Builder protoMsg = KuraPayloadProto.KuraPayload.newBuilder();
        // Set the timestamp
        if (getTimestamp() != null) {
            protoMsg.setTimestamp(getTimestamp().getTime());
        }
        // Set the position
        if (getPosition() != null) {
            protoMsg.setPosition(buildPositionProtoBuf(getPosition()));
        }
        // Set the metrics
        getMetrics().forEach((name, value) -> {
            if (value != null) {
                try {
                    KuraPayloadProto.KuraPayload.KuraMetric.Builder metricBuilder = KuraPayloadProto.KuraPayload.KuraMetric.newBuilder();
                    metricBuilder.setName(name);

                    setProtoKuraMetricValue(metricBuilder, value);
                    metricBuilder.build();

                    protoMsg.addMetric(metricBuilder);
                } catch (MessageException me) {
                    LOG.warn("During serialization, ignoring metric named: {}. Unrecognized value type: {}.", name, value.getClass().getName(), me);
                }
            }
        });
        // Set the body
        if (hasBody()) {
            protoMsg.setBody(ByteString.copyFrom(getBody()));
        }

        return protoMsg.build().toByteArray();
    }

    @Override
    public void readFromByteArray(byte[] bytes) throws MessageException {
        // Decompress and convert protobuf
        KuraPayloadProto.KuraPayload protoMsg;
        try {
            if (GZIPUtils.isCompressed(bytes)) {
                // Decompress while parsing, to avoid allocating the whole decompressed byte[]
                try (InputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(bytes), GZIP_BUFFER_SIZE)) {
                    protoMsg = KuraPayloadProto.KuraPayload.parseFrom(inputStream);
                }
            } else {
                protoMsg = KuraPayloadProto.KuraPayload.parseFrom(bytes);
            }
        } catch (IOException ioe) {
            throw new MessageException(MessageErrorCodes.INVALID_MESSAGE, ioe, (Object[]) null);
        }
        // Pre-size metrics to avoid rehashing while adding them
        if (metrics == null && protoMsg.getMetricCount() > 0) {
            metrics = Maps.newHashMapWithExpectedSize(protoMsg.getMetricCount());
        }
        // Add timestamp
        if (protoMsg.hasTimestamp()) {
            timestamp = (new Date(protoMsg.getTimestamp()));
        }
        // Add position
        if (protoMsg.hasPosition()) {
            position = (buildFromProtoBuf(protoMsg.getPosition()));
        }
        // Add metrics
        protoMsg.getMetricList().forEach(kuraMetric -> {
            try {
                Object value = getProtoKuraMetricValue(kuraMetric, kuraMetric.getType());
                addMetric(kuraMetric.getName(), value);
            } catch (MessageException me) {
                LOG.warn("During deserialization, ignoring metric named: {}. Unrecognized value type: {}", kuraMetric.getName(), kuraMetric.getType(), me);
            }
        });
        // Set the body
        if (protoMsg.hasBody()) {
            setBody(protoMsg.getBody().toByteArray());
        }
    }

    // Private methods
    private Object getProtoKuraMetricValue(KuraPayloadProto.KuraPayload.KuraMetric metric, KuraPayloadProto.KuraPayload.KuraMetric.ValueType type) throws MessageException {
        switch (type) {
            case DOUBLE:
                return metric.getDoubleValue();
            case FLOAT:
                return metric.getFloatValue();
            case INT64:
                return metric.getLongValue();
            case INT32:
                return metric.getIntValue();
            case BOOL:
                return metric.getBoolValue();
            case STRING:
                return metric.getStringValue();
            case BYTES:
                ByteString bs = metric.getBytesValue();
                return bs.toByteArray();
            default:
                throw new MessageException(MessageErrorCodes.INVALID_METRIC_TYPE, null, type);
        }
    }

    private static void setProtoKuraMetricValue(KuraPayloadProto.KuraPayload.KuraMetric.Builder metric, Object o) throws MessageException {

        if (o instanceof String) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.STRING);
            metric.setStringValue((String) o);
        } else if (o instanceof Double) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.DOUBLE);
            metric.setDoubleValue((Double) o);
        } else if (o instanceof Integer) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.INT32);
            metric.setIntValue((Integer) o);
        } else if (o instanceof Float) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.FLOAT);
            metric.setFloatValue((Float) o);
        } else if (o instanceof Long) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.INT64);
            metric.setLongValue((Long) o);
        } else if (o instanceof Boolean) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.BOOL);
            metric.setBoolValue((Boolean) o);
        } else if (o instanceof byte[]) {
            metric.setType(KuraPayloadProto.KuraPayload.KuraMetric.ValueType.BYTES);
            metric.setBytesValue(ByteString.copyFrom((byte[]) o));
        } else if (o == null) {
            throw new MessageException(MessageErrorCodes.INVALID_METRIC_VALUE, null, "null");
        } else {
            throw new MessageException(MessageErrorCodes.INVALID_METRIC_TYPE, null, o.getClass().getName());
        }
    }

    private KuraPayloadProto.KuraPayload.KuraPosition buildPositionProtoBuf(DevicePosition position) {
        KuraPayloadProto.KuraPayload.KuraPosition.Builder protoPos = null;
        protoPos = KuraPayloadProto.KuraPayload.KuraPosition.newBuilder();

        if (position.getLatitude() != null) {
            protoPos.setLatitude(position.getLatitude());
        }
        if (position.getLongitude() != null) {
            protoPos.setLongitude(position.getLongitude());
        }
        if (position.getAltitude() != null) {
            protoPos.setAltitude(position.getAltitude());
        }
        if (position.getPrecision() != null) {
            protoPos.setPrecision(position.getPrecision());
        }
        if (position.getHeading() != null) {
            protoPos.setHeading(position.getHeading());
        }
        if (position.getSpeed() != null) {
            protoPos.setSpeed(position.getSpeed());
        }
        if (position.getTimestamp() != null) {
            protoPos.setTimestamp(position.getTimestamp().getTime());
        }
        if (position.getSatellites() != null) {
            protoPos.setSatellites(position.getSatellites());
        }
        if (position.getStatus() != null) {
            protoPos.setStatus(position.getStatus());
        }

        return protoPos.build();
    }

    private DevicePosition buildFromProtoBuf(KuraPayloadProto.KuraPayload.KuraPosition protoPosition) {
        DevicePosition devicePosition = getPosition();

        // For performance reason check the position before
        if (devicePosition == null &&
                Booleans.countTrue(
                        protoPosition.hasLatitude(),
                        protoPosition.hasLatitude(),
                        protoPosition.hasLongitude(),
                        protoPosition.hasAltitude(),
                        protoPosition.hasPrecision(),
                        protoPosition.hasHeading(),
                        protoPosition.hasHeading(),
                        protoPosition.hasSpeed(),
                        protoPosition.hasSatellites(),
                        protoPosition.hasStatus(),
                        protoPosition.hasTimestamp()) > 0) {
            devicePosition = new KuraPosition();
        }

        if (devicePosition != null) {
            if (protoPosition.hasLatitude()) {
                devicePosition.setLatitude(protoPosition.getLatitude());
            }
            if (protoPosition.hasLongitude()) {
                devicePosition.setLongitude(protoPosition.getLongitude());
            }
            if (protoPosition.hasAltitude()) {
                devicePosition.setAltitude(protoPosition.getAltitude());
            }
            if (protoPosition.hasPrecision()) {
                devicePosition.setPrecision(protoPosition.getPrecision());
            }
            if (protoPosition.hasHeading()) {
                devicePosition.setHeading(protoPosition.getHeading());
            }
            if (protoPosition.hasSpeed()) {
                devicePosition.setSpeed(protoPosition.getSpeed());
            }
            if (protoPosition.hasSatellites()) {
                devicePosition.setSatellites(protoPosition.getSatellites());
            }
            if (protoPosition.hasStatus()) {
                devicePosition.setStatus(protoPosition.getStatus());
            }
            if (protoPosition.hasTimestamp()) {
                devicePosition.setTimestamp(new Date(protoPosition.getTimestamp()));
            }
        }
        return devicePosition;
    }
}
