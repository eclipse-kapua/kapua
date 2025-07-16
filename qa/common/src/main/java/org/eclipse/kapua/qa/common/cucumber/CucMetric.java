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
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.qa.common.cucumber;

import org.eclipse.kapua.model.type.ObjectTypeConverter;
import org.eclipse.kapua.model.type.ObjectValueConverter;

public class CucMetric {

    private String metric;
    private String type;
    private String value;
    private int message;

    private Class<?> typeCasted;

    private Object valueCasted;

    public CucMetric(String metric, String type, String value, int message) {
        this.metric = metric;
        this.type = type;
        this.value = value;
        this.message = message;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public Class<?> getTypeCasted() throws ClassNotFoundException {
        if (typeCasted == null) {
            typeCasted = ObjectTypeConverter.fromString(type);
        }
        return typeCasted;
    }

    public Object getValueCasted() throws ClassNotFoundException {
        return ObjectValueConverter.fromString(value, getTypeCasted());
    }
}
