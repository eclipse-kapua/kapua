/*******************************************************************************
 * Copyright (c) 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.device.shared.model.management.inventory;

import org.eclipse.kapua.app.console.module.api.shared.model.KapuaBaseModel;

public class GwtInventoryImage extends KapuaBaseModel {

    private static final long serialVersionUID = -4437969268565872654L;

    public void setName(String name) {
        set("name", name);
    }

    public String getName() {
        return get("name");
    }

    public void setVersion(String version) {
        set("version", version);
    }

    public String getVersion() {
        return get("version");
    }

    public void setType(String type) {
        set("type", type);
    }

    public String getType() {
        return get("type");
    }
}
