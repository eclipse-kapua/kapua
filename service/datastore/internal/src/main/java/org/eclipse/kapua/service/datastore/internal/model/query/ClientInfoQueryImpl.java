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
package org.eclipse.kapua.service.datastore.internal.model.query;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.mediator.ClientInfoField;
import org.eclipse.kapua.service.datastore.internal.schema.ClientInfoSchema;
import org.eclipse.kapua.service.datastore.model.query.ClientInfoQuery;
import org.eclipse.kapua.service.storable.model.query.AbstractStorableQuery;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.StorableFetchStyle;

import java.util.Collections;

/**
 * {@link ClientInfoQuery} implementation.
 *
 * @since 1.0.0
 */
public class ClientInfoQueryImpl extends AbstractStorableQuery implements ClientInfoQuery {

    /**
     * Constructor.
     *
     * @param scopeId The scope {@link KapuaId}.
     * @since 1.0.0
     */
    public ClientInfoQueryImpl(KapuaId scopeId) {
        super(scopeId);

        setSortFields(Collections.singletonList(SortField.ascending(ClientInfoSchema.CLIENT_ID)));
    }

    public ClientInfoQueryImpl(ClientInfoQuery other) {
        super((AbstractStorableQuery) other);
    }

    @Override
    public String[] getIncludes(StorableFetchStyle fetchStyle) {
        return new String[]{"*"};
    }

    @Override
    public String[] getExcludes(StorableFetchStyle fetchStyle) {
        return new String[]{""};
    }

    @Override
    public String[] getFields() {
        return new String[]{ClientInfoField.SCOPE_ID.field(),
                ClientInfoField.CLIENT_ID.field(),
                ClientInfoField.TIMESTAMP.field(),
                ClientInfoField.MESSAGE_ID.field()};
    }

}
