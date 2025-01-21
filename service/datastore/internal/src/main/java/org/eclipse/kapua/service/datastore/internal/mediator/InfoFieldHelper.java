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
package org.eclipse.kapua.service.datastore.internal.mediator;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoCreator;
import org.eclipse.kapua.service.datastore.model.ClientInfo;
import org.eclipse.kapua.service.datastore.model.MetricInfo;
import org.eclipse.kapua.service.datastore.model.MetricInfoCreator;
import org.eclipse.kapua.service.storable.model.id.StorableId;

public class InfoFieldHelper {

    private InfoFieldHelper() {
    }

    /**
     * Get the client identifier (combining accountName and clientId).<br>
     * <b>If the id is null then it is generated</b>
     *
     * @param id
     * @param scopeId
     * @param clientId
     * @return
     */
    public static String getOrDeriveId(StorableId id, KapuaId scopeId, String clientId) {
        if (id == null) {
            //TODO: FIXME: REMOVE: A collaborator in a data class? Behaviour should not be part of a data class!
            return KapuaLocator.getInstance().getComponent(DatastoreUtils.class).getHashCode(scopeId.toCompactId(), clientId);
        } else {
            return id.toString();
        }
    }

    /**
     * Get the client identifier (combining accountName and clientId).<br>
     * <b>If the id is null then it is generated</b>
     *
     * @param id
     * @param clientInfo
     * @return
     */
    public static String getOrDeriveId(StorableId id, ClientInfo clientInfo) {
        return getOrDeriveId(id, clientInfo.getScopeId(), clientInfo.getClientId());
    }

    /**
     * Get the channel identifier (combining accountName clientId and c).<br>
     * <b>If the id is null then it is generated</b>
     *
     * @param id
     * @param scopeId
     * @param clientId
     * @param channel
     * @return
     */
    private static String getOrDeriveId(StorableId id, KapuaId scopeId, String clientId, String channel) {
        if (id == null) {
            //TODO: FIXME: REMOVE: A collaborator in a data class? Behaviour should not be part of a data class!
            return KapuaLocator.getInstance().getComponent(DatastoreUtils.class).getHashCode(scopeId.toCompactId(), clientId, channel);
        } else {
            return id.toString();
        }
    }

    /**
     * Get the channel identifier getting parameters from the metricInfoCreator. Then it calls {@link InfoFieldHelper#getOrDeriveId(StorableId id, KapuaId scopeId, String clientId, String channel)}
     *
     * @param id
     * @param channelInfoCreator
     * @return
     */
    public static String getOrDeriveId(StorableId id, ChannelInfoCreator channelInfoCreator) {
        return getOrDeriveId(id,
                channelInfoCreator.getScopeId(),
                channelInfoCreator.getClientId(),
                channelInfoCreator.getName());
    }

    /**
     * Get the channel identifier getting parameters from the channelInfo. Then it calls {@link InfoFieldHelper#getOrDeriveId(StorableId id, KapuaId scopeId, String clientId, String channel)}
     *
     * @param id
     * @param channelInfo
     * @return
     */
    public static String getOrDeriveId(StorableId id, ChannelInfo channelInfo) {
        return getOrDeriveId(id,
                channelInfo.getScopeId(),
                channelInfo.getClientId(),
                channelInfo.getName());
    }

    /**
     * Get the metric identifier (return the hash code of the string obtained by combining accountName, clientId, channel and the converted metricName and metricType).<br>
     * <b>If the id is null then it is generated.</b>
     *
     * @param id
     * @param scopeId
     * @param clientId
     * @param channel
     * @param metricName
     * @param metricType
     * @return
     */
    private static String getOrDeriveId(StorableId id, KapuaId scopeId, String clientId, String channel, String metricName, Class<?> metricType) {
        if (id == null) {
            //TODO: FIXME: REMOVE: A collaborator in a data class? Behaviour should not be part of a data class!
            final DatastoreUtils datastoreUtils = KapuaLocator.getInstance().getComponent(DatastoreUtils.class);
            String metricMappedName = datastoreUtils.getMetricValueQualifier(metricName, datastoreUtils.convertToClientMetricType(metricType));

            return datastoreUtils.getHashCode(scopeId.toCompactId(), clientId, channel, metricMappedName);
        } else {
            return id.toString();
        }
    }

    /**
     * Get the metric identifier getting parameters from the metricInfoCreator. Then it calls
     * {@link InfoFieldHelper#getOrDeriveId(StorableId id, KapuaId scopeId, String clientId, String channel, String metricName, Class metricType)}
     *
     * @param id
     * @param metricInfoCreator
     * @return
     */
    public static String getOrDeriveId(StorableId id, MetricInfoCreator metricInfoCreator) {
        return getOrDeriveId(id,
                metricInfoCreator.getScopeId(),
                metricInfoCreator.getClientId(),
                metricInfoCreator.getChannel(),
                metricInfoCreator.getName(),
                metricInfoCreator.getMetricType());// use short me
    }

    /**
     * Get the metric identifier getting parameters from the metricInfo. Then it calls
     * {@link InfoFieldHelper#getOrDeriveId(StorableId id, KapuaId scopeId, String clientId, String channel, String metricName, Class metricType)}
     *
     * @param id
     * @param metricInfo
     * @return
     */
    public static String getOrDeriveId(StorableId id, MetricInfo metricInfo) {
        return getOrDeriveId(id,
                metricInfo.getScopeId(),
                metricInfo.getClientId(),
                metricInfo.getChannel(),
                metricInfo.getName(),
                metricInfo.getMetricType());
    }
}
