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
package org.eclipse.kapua.message;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.commons.util.Payloads;
import org.eclipse.kapua.message.xml.MetricsXmlAdapter;
import org.eclipse.kapua.model.xml.BinaryXmlAdapter;

/**
 * {@link KapuaPayload} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "payload")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "metrics", "body" })
public class KapuaPayload implements Payload {

    private Map<String, Object> metrics;
    private byte[] body;

    /**
     * Get the metrics map
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "metrics")
    @XmlJavaTypeAdapter(MetricsXmlAdapter.class)
    public Map<String, Object> getMetrics() {
        if (metrics == null) {
            metrics = new HashMap<>();
        }

        return metrics;
    }

    /**
     * Set the metrics map
     *
     * @param metrics
     * @since 1.0.0
     */
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    /**
     * Get the message body
     *
     * @return
     * @since 1.0.0
     */
    @XmlElement(name = "body")
    @XmlJavaTypeAdapter(BinaryXmlAdapter.class)
    public byte[] getBody() {
        return body;
    }

    /**
     * Set the message body
     *
     * @param body
     * @since 1.0.0
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Says whether or not the {@link #getBody()} has value.
     * <p>
     * Checks for {@code null} and size equals to 0
     *
     * @return {@code true} if {@link #getBody()} is not {@code null} and {@link #getBody()}{@code length > 0}, {@code false} otherwise.
     * @since 1.2.0
     */
    public boolean hasBody() {
        return getBody() != null && getBody().length > 0;
    }

    /**
     * Returns a string for displaying the {@link KapuaPayload} content.
     *
     * @return A {@link String} used for displaying the content of the {@link KapuaPayload}, never returns {@code null}
     * @since 1.0.0
     */
    @XmlTransient
    public String toDisplayString() {
        return Payloads.toDisplayString(getMetrics());
    }
}
