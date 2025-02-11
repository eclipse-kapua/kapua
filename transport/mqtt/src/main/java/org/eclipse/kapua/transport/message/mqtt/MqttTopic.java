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
package org.eclipse.kapua.transport.message.mqtt;

import java.util.regex.Pattern;

import org.eclipse.kapua.transport.message.TransportChannel;
import org.eclipse.kapua.transport.mqtt.setting.MqttClientSetting;
import org.eclipse.kapua.transport.mqtt.setting.MqttClientSettingKeys;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Implementation of {@link TransportChannel} API for MQTT transport facade
 *
 * @since 1.0.0
 */
public class MqttTopic implements TransportChannel {

    /**
     * The topic separator value for MQTT topics returned from {@link MqttClientSetting}.{@link MqttClientSettingKeys#TRANSPORT_TOPIC_SEPARATOR}
     *
     * @since 1.0.0
     */
    //TODO: FIXME: REMOVE: A collaborator in a data class? Behaviour should not be part of a data class!
    private static final String TOPIC_SEPARATOR = MqttClientSetting.getInstance().getString(MqttClientSettingKeys.TRANSPORT_TOPIC_SEPARATOR, "/");

    /**
     * {@link Pattern} used to optimize {@link String#split(String)} of the {@link #topic}
     *
     * @since 1.2.0
     */
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\" + TOPIC_SEPARATOR);

    /**
     * The full topic.
     *
     * @since 1.0.0
     */
    private String topic;

    /**
     * Construct a {@link MqttTopic} with the given parameter
     *
     * @param topic
     *         The topic to set for this {@link MqttTopic}
     * @since 1.0.0
     */
    public MqttTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Construct a {@link MqttTopic} with the given parameters.
     * <p>
     * Topic is built by concatenating all {@link String}[] token following the array order, separating each token with the topic separator configured in
     * {@link MqttClientSetting}.{@link MqttClientSettingKeys#TRANSPORT_TOPIC_SEPARATOR}
     * </p>
     *
     * @param topicParts
     *         The {@link String}[] from which build the full topic.
     * @since 1.0.0
     */
    public MqttTopic(String[] topicParts) {
        // Concatenate topic parts
        if (topicParts != null) {
            setTopic(String.join(TOPIC_SEPARATOR, Lists.newArrayList(topicParts)));
        }
    }

    /**
     * Gets the full topic set for this {@link MqttTopic}
     *
     * @return the full topic of this {@link MqttTopic}
     * @since 1.0.0
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the full topic for this {@link MqttTopic}
     *
     * @param topic
     *         The full topic to set for this {@link MqttTopic}
     * @since 1.0.0
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Gets the topic split-ed by the topic separator configured in {@link MqttClientSetting}.{@link MqttClientSettingKeys#TRANSPORT_TOPIC_SEPARATOR}
     *
     * @return The topic tokens. Empty {@code String[]} is return in case of {@code topic == null}.
     * @since 1.0.0
     */
    public String[] getSplittedTopic() {
        if (Strings.isNullOrEmpty(getTopic())) {
            return new String[0];
        }

        return SPLIT_PATTERN.split(getTopic());
    }

    /**
     * Gets {@link #getTopic()} for a more user-friendly output.
     *
     * @return The {@link #getTopic()}
     * @since 1.2.0
     */
    @Override
    public String toString() {
        return getTopic();
    }
}
