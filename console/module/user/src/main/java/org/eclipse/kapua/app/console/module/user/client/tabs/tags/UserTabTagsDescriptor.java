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
package org.eclipse.kapua.app.console.module.user.client.tabs.tags;

import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.AbstractEntityTabDescriptor;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.user.client.UserView;
import org.eclipse.kapua.app.console.module.user.shared.model.GwtUser;
import org.eclipse.kapua.app.console.module.tag.shared.model.permission.TagSessionPermission;

public class UserTabTagsDescriptor extends AbstractEntityTabDescriptor<GwtUser, UserTabTags, UserView> {

    @Override
    public UserTabTags getTabViewInstance(UserView view, GwtSession currentSession) {
        return new UserTabTags(currentSession);
    }

    @Override
    public String getViewId() {
        return "user.tags";
    }

    @Override
    public Integer getOrder() {
        return 150;
    }

    @Override
    public Boolean isEnabled(GwtSession currentSession) {
        return currentSession.hasPermission(TagSessionPermission.read());
    }
}
