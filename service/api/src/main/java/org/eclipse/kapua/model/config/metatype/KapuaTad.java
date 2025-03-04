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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

/**
 * <p>
 * Java class for Tad complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Tad"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Option" type="{http://www.osgi.org/xmlns/metatype/v1.2.0}Toption" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" use="required" type="{http://www.osgi.org/xmlns/metatype/v1.2.0}Tscalar" /&gt;
 *       &lt;attribute name="cardinality" type="{http://www.w3.org/2001/XMLSchema}int" default="0" /&gt;
 *       &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;anyAttribute/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 * @since 1.0
 */
@XmlRootElement(name = "AD", namespace = "http://www.osgi.org/xmlns/metatype/v1.2.0")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "Tad", propOrder = {
        "option",
        "any"
})
public class KapuaTad {

    protected List<KapuaToption> option;
    protected List<Object> any;
    protected String name;
    protected String description;
    protected String id;
    protected KapuaTscalar type;
    protected Integer cardinality;
    protected String min;
    protected String max;
    protected String defaultValue;
    protected Boolean required;
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the option property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This is why there is
     * not a <CODE>set</CODE> method for the option property.
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getOption().add(newItem);
     * </pre>
     * <p>
     */
    @XmlElement(name = "Option", namespace = "http://www.osgi.org/xmlns/metatype/v1.2.0")
    public List<KapuaToption> getOption() {
        if (option == null) {
            option = new ArrayList<>();
        }
        return new ArrayList<>(this.option);
    }

    /**
     * Add an option to the internal list
     *
     * @param option
     */
    public void addOption(KapuaToption option) {
        if (this.option == null) {
            this.option = new ArrayList<>();
        }

        this.option.add(option);
    }

    public void setOption(List<KapuaToption> option) {
        this.option = new ArrayList<>();
        for (KapuaToption singleOption : option) {
            this.option.add(singleOption);
        }
    }

    /**
     * Gets the value of the any property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This is why there is
     * not a <CODE>set</CODE> method for the any property.
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getAny().add(newItem);
     * </pre>
     * <p>
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    public void setAny(List<Object> any) {
        this.any = any;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String}
     */
    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *         allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     */
    @XmlAttribute(name = "description")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *         allowed object is {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String}
     */
    @XmlAttribute(name = "id", required = true)
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *         allowed object is {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return possible object is {@link KapuaTscalar }
     */
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(KapuaTscalarAdapter.class)
    public KapuaTscalar getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *         allowed object is {@link KapuaTscalar }
     */
    public void setType(KapuaTscalar value) {
        this.type = value;
    }

    /**
     * Gets the value of the cardinality property.
     *
     * @return possible object is {@link Integer }
     */
    @XmlAttribute(name = "cardinality")
    public Integer getCardinality() {
        if (cardinality == null) {
            return 0;
        } else {
            return cardinality;
        }
    }

    /**
     * Sets the value of the cardinality property.
     *
     * @param value
     *         allowed object is {@link Integer }
     */
    public void setCardinality(Integer value) {
        this.cardinality = value;
    }

    /**
     * Gets the value of the min property.
     *
     * @return possible object is  {@link String }
     */
    @XmlAttribute(name = "min")
    public String getMin() {
        return min;
    }

    /**
     * Sets the value of the min property.
     *
     * @param value
     *         allowed object is {@link String }
     */
    public void setMin(String value) {
        this.min = value;
    }

    /**
     * Gets the value of the max property.
     *
     * @return possible object is {@link String }
     */
    @XmlAttribute(name = "max")
    public String getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     *
     * @param value
     *         allowed object is  {@link String }
     */
    public void setMax(String value) {
        this.max = value;
    }

    /**
     * Gets the value of the default property.
     *
     * @return possible object is {@link String }
     */
    @XmlAttribute(name = "default")
    public String getDefault() {
        return defaultValue;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value
     *         allowed object is {@link String }
     */
    public void setDefault(String value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the required property.
     *
     * @return possible object is {@link Boolean }
     */
    @XmlAttribute(name = "required")
    public Boolean isRequired() {
        if (required == null) {
            return true;
        } else {
            return required;
        }
    }

    /**
     * Sets the value of the required property.
     *
     * @param value
     *         allowed object is  {@link Boolean }
     */
    public void setRequired(Boolean value) {
        this.required = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * <p>
     * the map is keyed by the name of the attribute and the value is the string value of the attribute.
     * <p>
     * the map returned by this method is live, and you can add new attribute by updating the map directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    @XmlAnyAttribute
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    public void putOtherAttribute(QName key, String value) {
        getOtherAttributes().put(key,
                value);
    }

}
