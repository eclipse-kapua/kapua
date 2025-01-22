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
package org.eclipse.kapua.service.datastore.model;

import java.util.Date;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.storable.model.StorableCreator;
import org.eclipse.kapua.service.storable.model.id.StorableId;

/**
 * Metric information schema creator definition
 *
 * @since 1.0.0
 */
public class MetricInfoCreator<T> extends StorableCreator<MetricInfo> {

    private String clientId;
    private String channel;

    private String name;
    private Class<T> metricType;

    private StorableId messageId;
    private Date messageTimestamp;

    public MetricInfoCreator() {
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The scope {@link KapuaId}
     * @since 1.0.0
     */
    public MetricInfoCreator(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * Get the client identifier
     *
     * @return
     * @since 1.0.0
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * Sets the client identifier
     *
     * @param clientId
     *         The client identifier
     * @since 1.0.0
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Get the channel
     *
     * @return
     * @since 1.0.0
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Set the channel
     *
     * @param channel
     * @since 1.0.0
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * Get the metric name
     *
     * @return
     * @since 1.0.0
     */
    public String getName() {
        return name;
    }

    /**
     * Set the metric name
     *
     * @param name
     * @since 1.0.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the metric type
     *
     * @return
     * @since 1.0.0
     */
    public Class<T> getMetricType() {
        return metricType;
    }

    /**
     * Sets the metric type
     *
     * @param metricType
     *         The metric type
     * @since 1.0.0
     */
    public void setMetricType(Class<T> metricType) {
        this.metricType = metricType;
    }

    /**
     * Get the message identifier (of the first message published that containing this metric)
     *
     * @return
     * @since 1.0.0
     */
    public StorableId getMessageId() {
        return messageId;
    }

    /**
     * Set the message identifier (of the first message published that containing this metric)
     *
     * @param messageId
     * @since 1.0.0
     */
    public void setMessageId(StorableId messageId) {
        this.messageId = messageId;
    }

    /**
     * Get the message timestamp (of the first message published that containing this metric)
     *
     * @return
     * @since 1.0.0
     */
    public Date getMessageTimestamp() {
        return messageTimestamp;
    }

    /**
     * Set the message timestamp (of the first message published that containing this metric)
     *
     * @param messageTimestamp
     * @since 1.0.0
     */
    public void setMessageTimestamp(Date messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

}
