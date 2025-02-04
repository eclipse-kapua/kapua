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
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.tag;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link TagCreator} definition
 * <p>
 * It is used to create a new {@link Tag}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "tagCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class TagCreator extends KapuaNamedEntityCreator {

    private static final long serialVersionUID = -4676187845961673421L;

    public TagCreator() {
    }

    public TagCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public TagCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }
}
