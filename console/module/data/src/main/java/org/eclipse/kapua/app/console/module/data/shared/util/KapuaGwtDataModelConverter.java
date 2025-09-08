/*******************************************************************************
 * Copyright (c) 2017, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.data.shared.util;

import org.eclipse.kapua.app.console.module.data.client.GwtTopic;
import org.eclipse.kapua.app.console.module.data.client.util.GwtMessage;
import org.eclipse.kapua.app.console.module.data.shared.model.GwtDatastoreAsset;
import org.eclipse.kapua.app.console.module.data.shared.model.GwtDatastoreDevice;
import org.eclipse.kapua.app.console.module.data.shared.model.GwtHeader;
import org.eclipse.kapua.model.type.ByteArrayConverter;
import org.eclipse.kapua.model.type.ObjectTypeConverter;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MetricInfo;

import java.util.List;

public class KapuaGwtDataModelConverter {

    private KapuaGwtDataModelConverter() { }

    public static GwtTopic convertToTopic(ChannelInfo channelInfo) {
        return new GwtTopic(channelInfo.getName(), channelInfo.getName(), channelInfo.getName(), channelInfo.getLastMessageOn());
    }

    public static GwtDatastoreAsset convertToAssets(ChannelInfo channelInfo) {
        return new GwtDatastoreAsset(channelInfo.getName().substring(6), channelInfo.getName(), channelInfo.getName(), channelInfo.getLastMessageOn());
    }

    public static GwtHeader convertToHeader(MetricInfo metric) {
        GwtHeader header = new GwtHeader();
        header.setName(metric.getName());
        header.setType(ObjectTypeConverter.toString(metric.getMetricType()));
        return header;
    }

    /**
     * @param client
     * @return
     */
    public static GwtDatastoreDevice convertToDatastoreDevice(ClientInfo client) {
        GwtDatastoreDevice device = new GwtDatastoreDevice(client.getClientId(), client.getLastMessageOn());
        device.setClientId(client.getClientId());
        return device;
    }

    /**
     * @param message
     * @param headers
     * @return
     */
    public static GwtMessage convertToMessage(DatastoreMessage message, List<GwtHeader> headers) {
        GwtMessage gwtMessage = new GwtMessage();
        List<String> semanticParts = message.getChannel().getSemanticParts();
        StringBuilder semanticTopic = new StringBuilder();
        for (int i = 0; i < semanticParts.size() - 1; i++) {
            semanticTopic.append(semanticParts.get(i));
            semanticTopic.append("/");
        }
        semanticTopic.append(semanticParts.get(semanticParts.size() - 1));
        gwtMessage.setChannel(semanticTopic.toString());
        gwtMessage.setClientId(message.getClientId());
        gwtMessage.setTimestamp(message.getTimestamp());
        for (GwtHeader header : headers) {
            Object metricValue = message.getPayload().getMetrics().get(header.getName());

            if (metricValue instanceof byte[]) {
                metricValue = ByteArrayConverter.toString((byte[]) metricValue);
            }

            gwtMessage.set(header.getName(), metricValue);
        }
        return gwtMessage;
    }
}
