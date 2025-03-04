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
package org.eclipse.kapua.model.config.metatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Tscalar complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;simpleType name="Tscalar"&gt;
 *  &lt;restriction base="string"&gt;
 *      &lt;enumeration value="String"/&gt;
 *      &lt;enumeration value="Long"/&gt;
 *      &lt;enumeration value="Double"/&gt;
 *      &lt;enumeration value="Float"/&gt;
 *      &lt;enumeration value="Integer"/&gt;
 *      &lt;enumeration value="Byte"/&gt;
 *      &lt;enumeration value="Char"/&gt;
 *      &lt;enumeration value="Boolean"/&gt;
 *      &lt;enumeration value="Short"/&gt;
 *      &lt;enumeration value="Password"/&gt;
 *  &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 * @since 1.0
 */
@XmlEnum
@XmlType(name = "Tscalar")
@XmlRootElement(name = "OCD", namespace = "http://www.osgi.org/xmlns/metatype/v1.2.0")
@XmlAccessorType(XmlAccessType.PROPERTY)
public enum KapuaTscalar {
    @XmlEnumValue("String")
    STRING("String"),
    @XmlEnumValue("Long")
    LONG("Long"),
    @XmlEnumValue("Double")
    DOUBLE("Double"),
    @XmlEnumValue("Float")
    FLOAT("Float"),
    @XmlEnumValue("Integer")
    INTEGER("Integer"),
    @XmlEnumValue("Byte")
    BYTE("Byte"),
    @XmlEnumValue("Char")
    CHAR("Char"),
    @XmlEnumValue("Boolean")
    BOOLEAN("Boolean"),
    @XmlEnumValue("Short")
    SHORT("Short"),
    @XmlEnumValue("Password")
    PASSWORD("Password");
    private final String value;

    /**
     * Constructor
     *
     * @param v
     */
    KapuaTscalar(String v) {
        value = v;
    }

    /**
     * Gets the value property.
     *
     * @return possible object is {@link String } with restricted values
     */
    public String value() {
        return value;
    }

    /**
     * Convert a String value to a {@link KapuaTscalar}
     *
     * @param v
     * @return
     */
    public static KapuaTscalar fromValue(String v) {
        for (KapuaTscalar c : KapuaTscalar.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
