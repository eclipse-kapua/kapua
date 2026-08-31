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
package org.eclipse.kapua.app.console.module.api.shared.model;

import java.io.Serializable;

public class GwtConfigComponentCreator extends KapuaBaseModel implements Serializable {

    private static final long serialVersionUID = -6388356998309026758L;

    private static final String COMPONENT_FACTORY_ID = "componentFactoryId";
    private static final String COMPONENT_ID = "componentId";

    public String getComponentFactoryId() {
        return get(COMPONENT_FACTORY_ID);
    }

    public String getUnescapedComponentFactoryId() {
        return getUnescaped(COMPONENT_FACTORY_ID);
    }

    public void setComponentFactoryId(String componentFactoryId) {
        set(COMPONENT_FACTORY_ID, componentFactoryId);
    }

    public String getComponentId() {
        return get(COMPONENT_ID);
    }

    public String getUnescapedComponentId() {
        return getUnescaped(COMPONENT_ID);
    }

    public void setComponentId(String componentId) {
        set(COMPONENT_ID, componentId);
    }

}
