/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.core.model.message;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.xml.XmlAdaptedMetric;
import org.eclipse.kapua.model.type.ObjectValueConverter;
import org.eclipse.kapua.model.xml.BinaryXmlAdapter;

@XmlRootElement(name = "payload")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class JsonKapuaPayload {

    private List<XmlAdaptedMetric> metrics;
    private byte[] body;

    /**
     * Constructor
     */
    public JsonKapuaPayload() {
    }

    public JsonKapuaPayload(KapuaPayload payload) {

        setBody(payload.getBody());

        payload.getMetrics().entrySet().stream().filter(metric -> metric.getValue() != null).forEach(metric -> {

            XmlAdaptedMetric jsonMetric = new XmlAdaptedMetric();
            jsonMetric.setName(metric.getKey());
            jsonMetric.setValueType(metric.getValue().getClass());
            jsonMetric.setValue(ObjectValueConverter.toString(metric.getValue()));

            getMetrics().add(jsonMetric);
        });
    }

    @XmlElementWrapper(name = "metrics")
    @XmlElement(name = "metric")
    public List<XmlAdaptedMetric> getMetrics() {
        if (metrics == null) {
            metrics = new ArrayList<>();
        }

        return metrics;
    }

    public void setMetrics(List<XmlAdaptedMetric> metrics) {
        this.metrics = metrics;
    }

    @XmlElement(name = "body")
    @XmlJavaTypeAdapter(BinaryXmlAdapter.class)
    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}
