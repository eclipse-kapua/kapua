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
package org.eclipse.kapua.service.datastore.internal.model;

import org.eclipse.kapua.service.datastore.model.ChannelInfo;
import org.eclipse.kapua.service.datastore.model.ChannelInfoListResult;
import org.eclipse.kapua.service.storeengine.client.model.ResultList;
import org.eclipse.kapua.service.storable.model.AbstractStorableListResult;

/**
 * {@link ChannelInfoListResult} implementation.
 *
 * @since 1.0.0
 */
public class ChannelInfoListResultImpl extends AbstractStorableListResult<ChannelInfo> implements ChannelInfoListResult {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public ChannelInfoListResultImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param resultList The {@link ResultList} to add.
     * @since 1.0.0
     */
    public ChannelInfoListResultImpl(ResultList<ChannelInfo> resultList) {
        super(resultList.getResult(), resultList.getTotalCount());
    }

}
