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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.storable.model.Storable;
import org.eclipse.kapua.service.storable.model.id.StorableId;
import org.eclipse.kapua.service.storable.model.id.StorableIdXmlAdapter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Information about device metric value. Metric is an arbitrary named value. We usually
 * keep only the most recent value of the metric.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "metricInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "id",
        "clientId",
        "channel",
        "name",
        "metricType",
        "firstMessageId",
        "firstMessageOn",
        "lastMessageId",
        "lastMessageOn"})
public interface MetricInfo extends Storable {


    /**
     * Gets the {@link StorableId}
     *
     * @return The {@link StorableId}
     * @since 1.0.0
     */
    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(StorableIdXmlAdapter.class)
    @Schema(example = "oLVtE7wK67wO5Lxx4nZGsTEwbVjeo0xcFcnTvmhCxLU=")
    StorableId getId();

    /**
     * Sets the {@link StorableId}
     *
     * @param id The {@link StorableId}
     * @since 1.0.0
     */
    @XmlElement(name = "id")
    @XmlJavaTypeAdapter(StorableIdXmlAdapter.class)
    void setId(StorableId id);

    /**
     * Get the client identifier
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "clientId")
    @Schema(example = "Client-Id-1")
    String getClientId();

    /**
     * Sets the client identifier
     *
     * @param clientId The client identifier
     * @since 1.0.0
     */
    void setClientId(String clientId);

    /**
     * Get the channel
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "channel")
    @Schema(example = "ALERT")
    String getChannel();

    /**
     * Set the channel
     *
     * @param channel
     * @since 1.0.0
     */
    void setChannel(String channel);

    /**
     * Gets the metric name
     *
     * @return The metric name
     * @since 1.0.0
     */
    @XmlElement(name = "name")
    @Schema(example = "alert_category")
    String getName();

    /**
     * Sets the metric name
     *
     * @param name The metric name
     * @since 1.0.0
     */
    void setName(String name);

    /**
     * Get the metric type
     *
     * @return The metric type
     * @since 1.0.0
     */
    @XmlElement(name = "metricType")
    @XmlJavaTypeAdapter(MetricInfoTypeAdapter.class)
    @Schema(example = "string")
    Class<?> getMetricType();

    /**
     * Sets the metric type
     *
     * @param metricType The metric type
     */
    void setMetricType(Class<?> metricType);

    /**
     * Get the message identifier (of the first message published that containing this metric)
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "firstMessageId")
    @XmlJavaTypeAdapter(StorableIdXmlAdapter.class)
    @Schema(example = "c623d131-6f47-49af-9ba0-1474a90dd9ed", type = "string")
    StorableId getFirstMessageId();

    /**
     * Set the message identifier (of the first message published that containing this metric)
     *
     * @param firstMessageId
     * @since 1.0.0
     */
    void setFirstMessageId(StorableId firstMessageId);

    /**
     * Get the message timestamp (of the first message published that containing this metric)
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "firstMessageOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getFirstMessageOn();

    /**
     * Set the message timestamp (of the first message published that containing this metric)
     *
     * @param firstMessageOn
     * @since 1.0.0
     */
    void setFirstMessageOn(Date firstMessageOn);

    /**
     * Get the message identifier of the last published message for this metric.<br>
     * <b>Transient data field (the last publish message identifier should get from the message table by the find service)</b>
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "lastMessageId")
    @XmlJavaTypeAdapter(StorableIdXmlAdapter.class)
    @Schema(example = "653b4aee-e0d5-4af6-b120-ffc7c26057b9", type = "string")
    StorableId getLastMessageId();

    /**
     * Set the message identifier of the last published message for this metric.<br>
     * <b>Transient data field (the last publish message identifier should get from the message table by the find service)</b>
     *
     * @param lastMessageId
     * @since 1.0.0
     */
    void setLastMessageId(StorableId lastMessageId);

    /**
     * Get the timestamp of the last published message for this metric.<br>
     * <b>Transient data field (the last publish timestamp should get from the message table by the find service)</b>
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "lastMessageOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    Date getLastMessageOn();

    /**
     * Set the timestamp of the last published message for this metric.<br>
     * <b>Transient data field (the last publish timestamp should get from the message table by the find service)</b>
     *
     * @param lastMessageOn
     * @since 1.0.0
     */
    void setLastMessageOn(Date lastMessageOn);
}
