/*******************************************************************************
 * Copyright (c) 2024, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.domain;

import java.util.EnumSet;

/**
 * {@link Domain} implementation.
 *
 * @since 2.0.0
 */
public class DomainEntry extends AbstractDomain implements Domain {
    private final String name;
    private final boolean groupable;
    private final EnumSet<Actions> actions;
    private final String serviceName;

    public DomainEntry(String domainName, String serviceName, boolean groupable, Actions action, Actions... actions) {
        this.name = domainName;
        this.serviceName = serviceName;
        this.groupable = groupable;
        this.actions = EnumSet.of(action, actions);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getGroupable() {
        return groupable;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public EnumSet<Actions> getActions() {
        return actions;
    }

    @Override
    public String toString() {
        return "DomainEntry{" +
                "name='" + name + '\'' +
                ", actions=" + actions +
                ", groupable=" + groupable +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
