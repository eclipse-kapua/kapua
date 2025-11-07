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
package org.eclipse.kapua.service.device.registry.group.internal;

import org.eclipse.kapua.commons.model.query.AbstractKapuaNamedQuery;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.group.DeviceGroupQuery;

/**
 * {@link DeviceGroupQuery} implementation.
 *
 * @since 2.1.0
 */
public class DeviceGroupQueryImpl extends AbstractKapuaNamedQuery implements DeviceGroupQuery {

    /**
     * Constructor.
     *
     * @param scopeId The {@link #getScopeId()}.
     * @since 2.1.0
     */
    public DeviceGroupQueryImpl(KapuaId scopeId) {
        super(scopeId);
    }
}
