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
package org.eclipse.kapua.service.datastore.model.query;

import java.util.Collections;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;
import org.eclipse.kapua.service.storable.model.query.StorableQuery;

/**
 * Channel information schema query definition
 *
 * @since 1.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class ChannelInfoQuery extends StorableQuery {

    public ChannelInfoQuery() {
        setSortFields(Collections.singletonList(SortField.ascending(ChannelInfoSchema.CHANNEL_NAME)));
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @since 1.0.0
     */
    public ChannelInfoQuery(KapuaId scopeId) {
        super(scopeId);
        setSortFields(Collections.singletonList(SortField.ascending(ChannelInfoSchema.CHANNEL_NAME)));
    }

    @Override
    public String[] getIncludes(StorableFetchStyle fetchStyle) {
        return new String[] { "*" };
    }

    @Override
    public String[] getExcludes(StorableFetchStyle fetchStyle) {
        return new String[] { "" };
    }

    @Override
    public String[] getFields() {
        return new String[] { ChannelInfoField.SCOPE_ID.field(),
                ChannelInfoField.CHANNEL.field(),
                ChannelInfoField.TIMESTAMP.field(),
                ChannelInfoField.MESSAGE_ID.field(),
                ChannelInfoField.CLIENT_ID.field() };
    }

}
