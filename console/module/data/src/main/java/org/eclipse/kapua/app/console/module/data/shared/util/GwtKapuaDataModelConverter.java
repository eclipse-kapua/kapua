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
package org.eclipse.kapua.app.console.module.data.shared.util;

import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.data.shared.model.GwtDataChannelInfoQuery;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.datastore.ChannelInfoFactory;
import org.eclipse.kapua.service.datastore.model.query.ChannelInfoQuery;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class GwtKapuaDataModelConverter {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final ChannelInfoFactory CHANNEL_INFO_FACTORY = LOCATOR.getFactory(ChannelInfoFactory.class);

    private GwtKapuaDataModelConverter() {
    }

    public static ChannelInfoQuery convertChannelInfoQuery(GwtDataChannelInfoQuery query, PagingLoadConfig pagingLoadConfig) {
        ChannelInfoQuery channelInfoQuery = new ChannelInfoQuery(GwtKapuaCommonsModelConverter.convertKapuaId(query.getScopeId()));
        channelInfoQuery.setOffset(pagingLoadConfig.getOffset());
        channelInfoQuery.setLimit(pagingLoadConfig.getLimit());
        return channelInfoQuery;
    }
}
