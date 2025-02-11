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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.message;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * {@link KapuaChannel} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "semanticParts" })
public class KapuaChannel implements Channel {

    private List<String> semanticParts;

    public String toString() {
        return semanticParts != null ? String.join("/", semanticParts) : "";
    }

    /**
     * Get the channel destination semantic part
     *
     * @return
     * @since 1.0.0
     */
    public List<String> getSemanticParts() {
        return semanticParts;
    }

    /**
     * Set the channel destination semantic part
     *
     * @param semanticParts
     * @since 1.0.0
     */
    public void setSemanticParts(final List<String> semanticParts) {
        this.semanticParts = semanticParts;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public String toPathString() {
        return String.join("/", getSemanticParts());
    }
}
