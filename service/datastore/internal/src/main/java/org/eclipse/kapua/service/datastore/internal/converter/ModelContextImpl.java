/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.datastore.internal.converter;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.internal.KapuaPositionImpl;
import org.eclipse.kapua.message.internal.device.data.KapuaDataChannelImpl;
import org.eclipse.kapua.message.internal.device.data.KapuaDataPayloadImpl;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.model.ChannelInfoImpl;
import org.eclipse.kapua.service.datastore.internal.model.ClientInfoImpl;
import org.eclipse.kapua.service.datastore.internal.model.DatastoreMessageImpl;
import org.eclipse.kapua.service.datastore.internal.model.MetricInfoImpl;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoSchema;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoSchema;
import org.eclipse.kapua.service.datastore.model.query.MessageSchema;
import org.eclipse.kapua.service.datastore.model.query.MetricInfoSchema;
import org.eclipse.kapua.service.elasticsearch.client.ModelContext;
import org.eclipse.kapua.service.elasticsearch.client.QueryConverter;
import org.eclipse.kapua.service.elasticsearch.client.exception.DatamodelMappingException;
import org.eclipse.kapua.service.storable.model.id.StorableIdFactory;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.Base64Variants;

/**
 * Datastore model context implementation
 *
 * @since 1.0
 */
public class ModelContextImpl implements ModelContext {

    private static final Logger logger = LoggerFactory.getLogger(ModelContextImpl.class);

    private final StorableIdFactory storableIdFactory;
    private final DatastoreUtils datastoreUtils;
    private static final String UNSUPPORTED_OBJECT_TYPE_ERROR_MSG = "The conversion of object [%s] is not supported!";
    private static final String MARSHAL_INVALID_PARAMETERS_ERROR_MSG = "Object and/or object type cannot be null!";

    @Inject
    public ModelContextImpl(StorableIdFactory storableIdFactory, DatastoreUtils datastoreUtils) {
        this.storableIdFactory = storableIdFactory;
        this.datastoreUtils = datastoreUtils;
    }

    @Override
    public String getIdKeyName() {
        return "datastore_id";
    }

    @Override
    public <T> T unmarshal(Class<T> clazz, Map<String, Object> serializedObject) throws DatamodelMappingException {
        if (clazz == null || serializedObject == null) {
            throw new DatamodelMappingException("Object and/or object type cannot be null");
        }

        try {
            if (DatastoreMessage.class.isAssignableFrom(clazz)) {
                return (T) unmarshalDatastoreMessage(serializedObject);
            } else if (ClientInfo.class.isAssignableFrom(clazz)) {
                return (T) unmarshalClientInfo(serializedObject);
            } else if (MetricInfo.class.isAssignableFrom(clazz)) {
                return (T) unmarshalMetricInfo(serializedObject);
            } else if (ChannelInfo.class.isAssignableFrom(clazz)) {
                return (T) unmarshalChannelInfo(serializedObject);
            }
        } catch (ParseException e) {
            throw new DatamodelMappingException(e, "Data conversion error");
        }

        throw new DatamodelMappingException(String.format(UNSUPPORTED_OBJECT_TYPE_ERROR_MSG, clazz.getName())); // FIXME: create specific exception and remove String.format
    }

    @Override
    public Map<String, Object> marshal(Object object) throws DatamodelMappingException {
        if (object == null) {
            throw new DatamodelMappingException(MARSHAL_INVALID_PARAMETERS_ERROR_MSG);
        }

        try {
            if (object instanceof DatastoreMessage) {
                return marshalDatastoreMessage((DatastoreMessage) object);
            }
            if (object instanceof ClientInfo) {
                return marshalClientInfo((ClientInfo) object);
            }
            if (object instanceof ChannelInfo) {
                return marshalChannelInfo((ChannelInfo) object);
            }
            if (object instanceof MetricInfo) {
                return marshalMetricInfo((MetricInfo) object);
            }
        } catch (ParseException e) {
            throw new DatamodelMappingException(e, "Date conversion error");
        }

        throw new DatamodelMappingException(String.format(UNSUPPORTED_OBJECT_TYPE_ERROR_MSG, object.getClass().getName())); // FIXME: create specific exception and remove String.format
    }

    /**
     * Unmarshals the {@link DatastoreMessage}.
     *
     * @param messageMap
     *         The {@link DatastoreMessage}.
     * @throws ParseException
     * @since 1.0.0
     */
    private DatastoreMessage unmarshalDatastoreMessage(Map<String, Object> messageMap) throws ParseException {

        StorableFetchStyle fetchStyle = getStorableFetchStyle(messageMap);
        DatastoreMessageImpl message = new DatastoreMessageImpl();
        String id = (String) messageMap.get(getIdKeyName());
        message.setDatastoreId(storableIdFactory.newStorableId(id));
        String messageId = (String) messageMap.get(MessageSchema.MESSAGE_ID);
        if (messageId != null) {
            message.setId(UUID.fromString(messageId));
        }

        KapuaId scopeId = new KapuaEid(new BigInteger((String) messageMap.get(MessageSchema.MESSAGE_SCOPE_ID)));
        message.setScopeId(scopeId);
        KapuaId deviceId = null;
        String deviceIdStr = (String) messageMap.get(MessageSchema.MESSAGE_DEVICE_ID);
        if (deviceIdStr != null) {
            deviceId = new KapuaEid(new BigInteger(deviceIdStr));
        }

        message.setDeviceId(deviceId);
        String clientId = (String) messageMap.get(MessageSchema.MESSAGE_CLIENT_ID);
        message.setClientId(clientId);
        message.setDatastoreId(storableIdFactory.newStorableId(id));

        KapuaDataChannelImpl dataChannel = new KapuaDataChannelImpl();
        message.setChannel(dataChannel);

        String timestamp = (String) messageMap.get(MessageSchema.MESSAGE_TIMESTAMP);
        message.setTimestamp(timestamp == null ? null : KapuaDateUtils.parseDate(timestamp));

        // stop the mapping if only fields are requested
        if (fetchStyle.equals(StorableFetchStyle.FIELDS)) {
            return message;
        }

        List<String> channelParts = (List<String>) messageMap.get(MessageSchema.MESSAGE_CHANNEL_PARTS);
        dataChannel.setSemanticParts(channelParts);

        KapuaDataPayloadImpl payload = new KapuaDataPayloadImpl();
        Map<String, Object> positionMap = (Map<String, Object>) messageMap.get(MessageSchema.MESSAGE_POSITION);

        KapuaPositionImpl position;
        if (positionMap != null) {
            Map<String, Object> locationMap = (Map<String, Object>) positionMap.get(MessageSchema.MESSAGE_POS_LOCATION);

            position = new KapuaPositionImpl();
            if (locationMap != null && locationMap.get(MessageSchema.MESSAGE_POSITION_LATITUDE) != null) {
                position.setLatitude((double) locationMap.get(MessageSchema.MESSAGE_POSITION_LATITUDE));
            }
            if (locationMap != null && locationMap.get(MessageSchema.MESSAGE_POSITION_LONGITUDE) != null) {
                position.setLongitude((double) locationMap.get(MessageSchema.MESSAGE_POSITION_LONGITUDE));
            }
            Object obj = positionMap.get(MessageSchema.MESSAGE_POS_ALT);
            if (obj != null) {
                position.setAltitude((double) obj);
            }
            obj = positionMap.get(MessageSchema.MESSAGE_POS_HEADING);
            if (obj != null) {
                position.setHeading((double) obj);
            }
            obj = positionMap.get(MessageSchema.MESSAGE_POS_PRECISION);
            if (obj != null) {
                position.setPrecision((double) obj);
            }
            obj = positionMap.get(MessageSchema.MESSAGE_POS_SATELLITES);
            if (obj != null) {
                position.setSatellites((int) obj);
            }
            obj = positionMap.get(MessageSchema.MESSAGE_POS_SPEED);
            if (obj != null) {
                position.setSpeed((double) obj);
            }
            obj = positionMap.get(MessageSchema.MESSAGE_POS_STATUS);
            if (obj != null) {
                position.setStatus((int) obj);
            }
            obj = positionMap.get(MessageSchema.MESSAGE_POS_TIMESTAMP);
            position.setTimestamp(KapuaDateUtils.parseDate((String) obj));
            message.setPosition(position);
        }

        Object capturedOnFld = messageMap.get(MessageSchema.MESSAGE_CAPTURED_ON);
        message.setCapturedOn(KapuaDateUtils.parseDate((String) capturedOnFld));
        Object sentOnFld = messageMap.get(MessageSchema.MESSAGE_SENT_ON);
        message.setSentOn(KapuaDateUtils.parseDate((String) sentOnFld));
        Object receivedOnFld = messageMap.get(MessageSchema.MESSAGE_RECEIVED_ON);
        message.setReceivedOn(KapuaDateUtils.parseDate((String) receivedOnFld));
        if (messageMap.get(MessageSchema.MESSAGE_METRICS) != null) {
            Map<String, Object> metrics = (Map<String, Object>) messageMap.get(MessageSchema.MESSAGE_METRICS);
            Map<String, Object> payloadMetrics = new HashMap<>();
            String[] metricNames = metrics.keySet().toArray(new String[] {});
            for (String metricsName : metricNames) {
                Map<String, Object> metricValue = (Map<String, Object>) metrics.get(metricsName);
                if (metricValue.size() > 0) {
                    String[] valueTypes = metricValue.keySet().toArray(new String[] {});
                    Object value = metricValue.get(valueTypes[0]);
                    // since elasticsearch doesn't return always the same type of the saved field
                    // (usually due to some promotion of the field type)
                    // we need to check the metric type returned by elasticsearch and, if needed, convert to the proper type
                    payloadMetrics.put(datastoreUtils.restoreMetricName(metricsName), datastoreUtils.convertToCorrectType(valueTypes[0], value));
                }
            }
            payload.setMetrics(payloadMetrics);
        }
        if (fetchStyle.equals(StorableFetchStyle.SOURCE_SELECT)) {
            return message;
        }
        if (messageMap.get(MessageSchema.MESSAGE_BODY) != null) {
            byte[] body = Base64Variants.getDefaultVariant().decode((String) messageMap.get(MessageSchema.MESSAGE_BODY));
            payload.setBody(body);
        }

        message.setPayload(payload);

        return message;
    }

    private MetricInfo unmarshalMetricInfo(Map<String, Object> metricInfoMap) throws ParseException {
        KapuaId scopeId = new KapuaEid(new BigInteger((String) metricInfoMap.get(MetricInfoSchema.METRIC_SCOPE_ID)));
        String id = (String) metricInfoMap.get(getIdKeyName());

        Map<String, Object> metricMap = (Map<String, Object>) metricInfoMap.get(MetricInfoSchema.METRIC_MTR);
        String name = (String) metricMap.get(MetricInfoSchema.METRIC_MTR_NAME);
        String type = (String) metricMap.get(MetricInfoSchema.METRIC_MTR_TYPE);
        String lastMsgTimestamp = (String) metricMap.get(MetricInfoSchema.METRIC_MTR_TIMESTAMP);
        String lastMsgId = (String) metricMap.get(MetricInfoSchema.METRIC_MTR_MSG_ID);
        String clientId = (String) metricInfoMap.get(MetricInfoSchema.METRIC_CLIENT_ID);
        String channel = (String) metricInfoMap.get(MetricInfoSchema.METRIC_CHANNEL);
        String metricName = datastoreUtils.restoreMetricName(name);
        Date timestamp = KapuaDateUtils.parseDate(lastMsgTimestamp);

        MetricInfo metricInfo = new MetricInfoImpl(scopeId);
        metricInfo.setId(storableIdFactory.newStorableId(id));
        metricInfo.setClientId(clientId);
        metricInfo.setChannel(channel);
        metricInfo.setFirstMessageId(storableIdFactory.newStorableId(lastMsgId));
        metricInfo.setName(metricName);
        metricInfo.setFirstMessageOn(timestamp);
        metricInfo.setMetricType(datastoreUtils.convertToKapuaType(type));

        return metricInfo;
    }

    private ChannelInfo unmarshalChannelInfo(Map<String, Object> channelInfoMap) throws ParseException {
        KapuaId scopeId = new KapuaEid(new BigInteger((String) channelInfoMap.get(ChannelInfoSchema.CHANNEL_SCOPE_ID)));
        String id = (String) channelInfoMap.get(getIdKeyName());

        ChannelInfo channelInfo = new ChannelInfoImpl(scopeId);
        channelInfo.setId(storableIdFactory.newStorableId(id));
        channelInfo.setClientId((String) channelInfoMap.get(ChannelInfoSchema.CHANNEL_CLIENT_ID));
        channelInfo.setName((String) channelInfoMap.get(ChannelInfoSchema.CHANNEL_NAME));
        channelInfo.setFirstMessageId(storableIdFactory.newStorableId((String) channelInfoMap.get(ChannelInfoSchema.CHANNEL_MESSAGE_ID)));
        channelInfo.setFirstMessageOn(KapuaDateUtils.parseDate((String) channelInfoMap.get(ChannelInfoSchema.CHANNEL_TIMESTAMP)));

        return channelInfo;
    }

    private ClientInfo unmarshalClientInfo(Map<String, Object> clientInfoMap) throws ParseException {
        KapuaId scopeId = new KapuaEid(new BigInteger((String) clientInfoMap.get(ClientInfoSchema.CLIENT_SCOPE_ID)));
        String id = (String) clientInfoMap.get(getIdKeyName());

        ClientInfo clientInfo = new ClientInfoImpl(scopeId);
        clientInfo.setId(storableIdFactory.newStorableId(id));
        clientInfo.setClientId((String) clientInfoMap.get(ClientInfoSchema.CLIENT_ID));
        clientInfo.setFirstMessageId(storableIdFactory.newStorableId((String) clientInfoMap.get(ClientInfoSchema.CLIENT_MESSAGE_ID)));
        clientInfo.setFirstMessageOn(KapuaDateUtils.parseDate((String) clientInfoMap.get(ClientInfoSchema.CLIENT_TIMESTAMP)));

        return clientInfo;
    }

    /**
     * Marshals the {@link DatastoreMessage}.
     *
     * @param message
     *         The {@link DatastoreMessage}.
     * @throws ParseException
     * @since 1.0.0
     */
    private Map<String, Object> marshalDatastoreMessage(DatastoreMessage message) throws ParseException {
        // Message
        Map<String, Object> unmarshalledMessage = new HashMap<>();
        String scopeId = message.getScopeId().toStringId();
        String deviceIdStr = message.getDeviceId() == null ? null : message.getDeviceId().toStringId();
        if (message.getId() != null) {
            unmarshalledMessage.put(MessageSchema.MESSAGE_ID, message.getId().toString());
        }
        unmarshalledMessage.put(MessageSchema.MESSAGE_TIMESTAMP, KapuaDateUtils.formatDate(message.getTimestamp()));
        unmarshalledMessage.put(MessageSchema.MESSAGE_RECEIVED_ON, KapuaDateUtils.formatDate(message.getReceivedOn()));
        unmarshalledMessage.put(MessageSchema.MESSAGE_IP_ADDRESS, "127.0.0.1"); // FIXME: support this or remove?
        unmarshalledMessage.put(MessageSchema.MESSAGE_SCOPE_ID, scopeId);
        unmarshalledMessage.put(MessageSchema.MESSAGE_DEVICE_ID, deviceIdStr);
        unmarshalledMessage.put(MessageSchema.MESSAGE_CLIENT_ID, message.getClientId());
        // Channel
        unmarshalledMessage.put(MessageSchema.MESSAGE_CHANNEL, message.getChannel().toString());
        unmarshalledMessage.put(MessageSchema.MESSAGE_CHANNEL_PARTS, message.getChannel().getSemanticParts());
        unmarshalledMessage.put(MessageSchema.MESSAGE_CAPTURED_ON, KapuaDateUtils.formatDate(message.getCapturedOn()));
        unmarshalledMessage.put(MessageSchema.MESSAGE_SENT_ON, KapuaDateUtils.formatDate(message.getSentOn()));
        // Position
        KapuaPosition kapuaPosition = message.getPosition();
        if (kapuaPosition != null) {
            Map<String, Object> location = null;
            if (kapuaPosition.getLongitude() != null && kapuaPosition.getLatitude() != null) {
                location = new HashMap<>();
                location.put(MessageSchema.MESSAGE_POSITION_LONGITUDE, kapuaPosition.getLongitude());
                location.put(MessageSchema.MESSAGE_POSITION_LATITUDE, kapuaPosition.getLatitude());
            }
            Map<String, Object> position = new HashMap<>();
            position.put(MessageSchema.MESSAGE_POS_LOCATION, location);
            position.put(MessageSchema.MESSAGE_POS_ALT, kapuaPosition.getAltitude());
            position.put(MessageSchema.MESSAGE_POS_PRECISION, kapuaPosition.getPrecision());
            position.put(MessageSchema.MESSAGE_POS_HEADING, kapuaPosition.getHeading());
            position.put(MessageSchema.MESSAGE_POS_SPEED, kapuaPosition.getSpeed());
            position.put(MessageSchema.MESSAGE_POS_TIMESTAMP, KapuaDateUtils.formatDate(kapuaPosition.getTimestamp()));
            position.put(MessageSchema.MESSAGE_POS_SATELLITES, kapuaPosition.getSatellites());
            position.put(MessageSchema.MESSAGE_POS_STATUS, kapuaPosition.getStatus());
            unmarshalledMessage.put(MessageSchema.MESSAGE_POSITION, position);
        }
        // Payload
        KapuaPayload payload = message.getPayload();
        if (payload == null) {
            return unmarshalledMessage;
        }
        unmarshalledMessage.put(MessageSchema.MESSAGE_BODY, payload.getBody());
        Map<String, Object> kapuaMetrics = payload.getMetrics();
        if (kapuaMetrics != null) {
            Map<String, Object> metrics = new HashMap<>();
            String[] metricNames = kapuaMetrics.keySet().toArray(new String[] {});
            for (String kapuaMetricName : metricNames) {
                Object metricValue = kapuaMetrics.get(kapuaMetricName);
                // Sanitize field names: '.' is not allowed
                String metricName = datastoreUtils.normalizeMetricName(kapuaMetricName);
                String clientMetricType = datastoreUtils.getClientMetricFromType(metricValue.getClass());
                String clientMetricTypeAcronym = datastoreUtils.getClientMetricFromAcronym(clientMetricType);
                Map<String, Object> field = new HashMap<>();
                if (datastoreUtils.isDateMetric(clientMetricTypeAcronym) && metricValue instanceof Date) {
                    field.put(clientMetricTypeAcronym, KapuaDateUtils.formatDate((Date) metricValue));
                } else {
                    field.put(clientMetricTypeAcronym, metricValue);
                }
                metrics.put(metricName, field);
            }
            unmarshalledMessage.put(MessageSchema.MESSAGE_METRICS, metrics);
        }

        return unmarshalledMessage;
    }

    private Map<String, Object> marshalClientInfo(ClientInfo clientInfo) throws ParseException {
        Map<String, Object> unmarshalledClientInfo = new HashMap<>();
        unmarshalledClientInfo.put(ClientInfoSchema.CLIENT_ID, clientInfo.getClientId());
        unmarshalledClientInfo.put(ClientInfoSchema.CLIENT_MESSAGE_ID, clientInfo.getFirstMessageId().toString());
        unmarshalledClientInfo.put(ClientInfoSchema.CLIENT_TIMESTAMP, KapuaDateUtils.formatDate(clientInfo.getFirstMessageOn()));
        unmarshalledClientInfo.put(ClientInfoSchema.CLIENT_SCOPE_ID, clientInfo.getScopeId().toStringId());

        return unmarshalledClientInfo;
    }

    private Map<String, Object> marshalChannelInfo(ChannelInfo channelInfo) throws ParseException {
        Map<String, Object> unmarshalledChannelInfo = new HashMap<>();
        unmarshalledChannelInfo.put(ChannelInfoSchema.CHANNEL_NAME, channelInfo.getName());
        unmarshalledChannelInfo.put(ChannelInfoSchema.CHANNEL_TIMESTAMP, KapuaDateUtils.formatDate(channelInfo.getFirstMessageOn()));
        unmarshalledChannelInfo.put(ChannelInfoSchema.CHANNEL_CLIENT_ID, channelInfo.getClientId());
        unmarshalledChannelInfo.put(ChannelInfoSchema.CHANNEL_SCOPE_ID, channelInfo.getScopeId().toStringId());
        unmarshalledChannelInfo.put(ChannelInfoSchema.CHANNEL_MESSAGE_ID, channelInfo.getFirstMessageId().toString());

        return unmarshalledChannelInfo;
    }

    private Map<String, Object> marshalMetricInfo(MetricInfo metricInfo) throws ParseException {
        Map<String, Object> unmarshalledMetricInfo = new HashMap<>();
        unmarshalledMetricInfo.put(MetricInfoSchema.METRIC_SCOPE_ID, metricInfo.getScopeId().toStringId());
        unmarshalledMetricInfo.put(MetricInfoSchema.METRIC_CLIENT_ID, metricInfo.getClientId());
        unmarshalledMetricInfo.put(MetricInfoSchema.METRIC_CHANNEL, metricInfo.getChannel());

        Map<String, Object> unmarshalledMetricValue = new HashMap<>();
        unmarshalledMetricValue.put(MetricInfoSchema.METRIC_MTR_NAME, metricInfo.getName());
        unmarshalledMetricValue.put(MetricInfoSchema.METRIC_MTR_TYPE, datastoreUtils.convertToClientMetricType(metricInfo.getMetricType()));
        unmarshalledMetricValue.put(MetricInfoSchema.METRIC_MTR_TIMESTAMP, KapuaDateUtils.formatDate(metricInfo.getFirstMessageOn()));
        unmarshalledMetricValue.put(MetricInfoSchema.METRIC_MTR_MSG_ID, metricInfo.getFirstMessageId().toString());
        unmarshalledMetricInfo.put(MetricInfoSchema.METRIC_MTR, unmarshalledMetricValue);

        return unmarshalledMetricInfo;
    }

    private StorableFetchStyle getStorableFetchStyle(Map<String, Object> objectMap) {
        Object storableFetchStyle = objectMap.get(QueryConverter.QUERY_FETCH_STYLE_KEY);
        if (storableFetchStyle instanceof StorableFetchStyle) {
            return (StorableFetchStyle) storableFetchStyle;
        } else {
            logger.warn("Wrong storable fetch style object instance! This cannot be parsed: {}", (storableFetchStyle != null ? storableFetchStyle.getClass() : "null"));
            return StorableFetchStyle.FIELDS;
        }
    }
}
