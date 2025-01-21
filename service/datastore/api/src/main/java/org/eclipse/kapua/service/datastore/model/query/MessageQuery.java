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
 * Datastore message schema query definition
 *
 * @since 1.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class MessageQuery extends StorableQuery {

    public MessageQuery() {
        setSortFields(Collections.singletonList(SortField.descending(MessageSchema.MESSAGE_TIMESTAMP)));
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @since 1.0.0
     */
    public MessageQuery(KapuaId scopeId) {
        super(scopeId);

        setSortFields(Collections.singletonList(SortField.descending(MessageSchema.MESSAGE_TIMESTAMP)));
    }

    @Override
    public String[] getIncludes(StorableFetchStyle fetchStyle) {
        // Fetch mode
        String[] includeSource = null;
        switch (fetchStyle) {
        case FIELDS:
            includeSource = getFields();
            break;
        case SOURCE_SELECT:
            includeSource = new String[] { MessageSchema.MESSAGE_CAPTURED_ON, MessageSchema.MESSAGE_POSITION + ".*", MessageSchema.MESSAGE_METRICS + ".*" };
            break;
        case SOURCE_FULL:
            includeSource = new String[] { "*" };
        }
        return includeSource;
    }

    @Override
    public String[] getExcludes(StorableFetchStyle fetchStyle) {
        // Fetch mode
        String[] excludeSource = null;
        switch (fetchStyle) {
        case FIELDS:
            excludeSource = new String[] { "" };
            break;
        case SOURCE_SELECT:
            excludeSource = new String[] { MessageSchema.MESSAGE_BODY };
            break;
        case SOURCE_FULL:
            excludeSource = new String[] { "" };
        }
        return excludeSource;
    }

    @Override
    public String[] getFields() {
        return new String[] {
                MessageField.MESSAGE_ID.field(),
                MessageField.SCOPE_ID.field(),
                MessageField.DEVICE_ID.field(),
                MessageField.CLIENT_ID.field(),
                MessageField.CHANNEL.field(),
                MessageField.TIMESTAMP.field() };
    }

}
