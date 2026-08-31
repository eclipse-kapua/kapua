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
package org.eclipse.kapua.app.console.module.api.shared.model;

import java.io.Serializable;

public class GwtConfigComponentFactory extends KapuaBaseModel implements Serializable {

    private static final long serialVersionUID = -6388356998309026758L;

    private static final String ID = "id";

    public GwtConfigComponentFactory() {}

    public GwtConfigComponentFactory(String id) {
        setId(id);
    }

    public String getId() {
        return get(ID);
    }

    public void setId(String id) {
        set(ID, id);
    }
}
