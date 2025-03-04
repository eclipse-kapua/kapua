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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * <p>
 * Java class for Tocd complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Tocd"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AD" type="{http://www.osgi.org/xmlns/metatype/v1.2.0}Tad" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Icon" type="{http://www.osgi.org/xmlns/metatype/v1.2.0}Ticon" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;anyAttribute/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 * @since 1.0
 */
@XmlRootElement(name = "OCD", namespace = "http://www.osgi.org/xmlns/metatype/v1.2.0")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class KapuaTocd {

    protected List<KapuaTad> ad;
    protected List<KapuaTicon> icon;
    protected List<Object> any;
    protected String name;
    protected String description;
    protected String id;
    private Map<QName, String> otherAttributes = new HashMap<>();

    /**
     * Gets the value of the ad property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This is why there is
     * not a <CODE>set</CODE> method for the ad property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <p>
     * <pre>
     * getAD().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link KapuaTad }
     */
    @XmlElement(name = "AD", namespace = "http://www.osgi.org/xmlns/metatype/v1.2.0", required = true)

    public List<KapuaTad> getAD() {
        if (ad == null) {
            ad = new ArrayList<>();
        }
        return this.ad;
    }

    public void setAD(List<KapuaTad> ad) {
        this.ad = ad;
    }

    /**
     * Gets the value of the icon property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This is why there is
     * not a <CODE>set</CODE> method for the icon property.
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getIcon().add(newItem);
     * </pre>
     */
    @XmlElement(name = "Icon", namespace = "http://www.osgi.org/xmlns/metatype/v1.2.0")

    /**
     * Gets the value of the icon property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This is why there is
     * not a <CODE>set</CODE> method for the icon property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <p>
     * <pre>
     * getIcon().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link TiconImpl }
     */
    public List<KapuaTicon> getIcon() {
        if (icon == null) {
            icon = new ArrayList<>();
        }
        return this.icon;
    }

    public void setIcon(List<KapuaTicon> icon) {
        this.icon = icon;
    }

    /**
     * Add icon to the internal list
     *
     * @param icon
     */
    public void addIcon(KapuaTicon icon) {
        if (this.icon == null) {
            this.icon = new ArrayList<>();
        }
        this.icon.add(icon);
    }

    /**
     * Gets the value of the any property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object. This is why there is
     * not a <CODE>set</CODE> method for the any property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <p>
     * <pre>
     * getAny().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Element } {@link Object }
     */
    @XmlAnyElement(lax = true)
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    public void setAny(List<Object> any) {
        this.any = any;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @XmlAttribute(name = "name", required = true)
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
     * @return possible object is {@link String }
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

    public void setOtherAttributes(Map<QName, String> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }

    /**
     * Insert a generic value for the provided QName
     *
     * @param key
     * @param value
     */
    public void putOtherAttribute(QName key, String value) {
        getOtherAttributes().put(key,
                value);
    }

    /**
     * Add Tad to the internal list
     *
     * @param ad
     */
    public void addAD(KapuaTad ad) {
        if (this.ad == null) {
            this.ad = new ArrayList<>();
        }

        this.ad.add(ad);
    }

    /**
     * Add a generic object to the internal list
     *
     * @param object
     */
    public void addAny(Object object) {
        getAny().add(object);
    }

}
