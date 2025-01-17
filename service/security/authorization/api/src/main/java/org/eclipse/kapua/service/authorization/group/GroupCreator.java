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
package org.eclipse.kapua.service.authorization.group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * {@link GroupCreator} definition.
 * <p>
 * It is used to create a new {@link Group}.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "groupCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class GroupCreator extends KapuaNamedEntityCreator<Group> {

    private static final long serialVersionUID = -4676187845961673421L;

    public GroupCreator() {
    }

    public GroupCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public GroupCreator(KapuaId scopeId, String name) {
        super(scopeId, name);
    }
}
