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
package org.eclipse.kapua.app.console.module.authorization.client.group;

import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.AbstractEntityTabDescriptor;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;

public class GroupTabDescriptionDescriptor extends AbstractEntityTabDescriptor<GwtGroup, GroupTabDescription, GroupView> {

    @Override
    public GroupTabDescription getTabViewInstance(GroupView view, GwtSession currentSession) {
        return new GroupTabDescription(currentSession, new AccessGroupDataProvider());
    }

    @Override
    public String getViewId() {
        return "group.description";
    }

    @Override
    public Integer getOrder() {
        return 100;
    }

    @Override
    public Boolean isEnabled(GwtSession currentSession) {
        return true;
    }
}
