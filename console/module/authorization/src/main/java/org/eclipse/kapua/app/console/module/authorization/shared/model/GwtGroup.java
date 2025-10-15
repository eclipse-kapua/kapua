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
package org.eclipse.kapua.app.console.module.authorization.shared.model;

import org.eclipse.kapua.app.console.module.api.shared.model.GwtUpdatableEntityModel;

public class GwtGroup extends GwtUpdatableEntityModel {

    private static final long serialVersionUID = 1L;

    private static final String DESCRIPTION = "description";

    public String getGroupName() {
        return get("groupName");
    }

    public void setGroupName(String name) {
        set("groupName", name);
        set("value", name);
    }

    public String getGroupDescription() {
        return get(DESCRIPTION);
    }

    public String getUnescapedDescription() {
        return (String) getUnescaped(DESCRIPTION);
    }

    public void setGroupDescription(String description) {
        set(DESCRIPTION, description);
        set("value", description);
    }

    public String getDomain() {
        return get("domain");
    }

    public void setDomain(String domain) {
        set("domain", domain);
    }

    @Override
    public String toString() {
        return getGroupName();
    }
}
