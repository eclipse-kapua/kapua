/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.domain;

import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaService;

import java.util.Set;

/**
 * The {@link KapuaEntityService} {@link Domain}.
 * <p>
 * A {@link KapuaService} can be associated with a domain which it uses to validate access
 *
 * @since 1.0.0
 */
public interface Domain {

    /**
     * Gets the {@link Domain} name.
     *
     * @return The {@link Domain} name.
     * @since 1.0.0
     */
    String getName();

    /**
     * Gets the set of {@link Actions} available in this {@link Domain}.<br>
     * The implementation must return the reference of the set and not make a clone.
     *
     * @return The set of {@link Actions}.
     * @since 1.0.0
     */
    Set<Actions> getActions();

    /**
     * Gets whether or not this {@link Domain} is group-able or not.
     *
     * @return {@code true} if the KapuaEntity is group-able, {@code false} otherwise.
     * @since 1.0.0
     */
    boolean getGroupable();

    /**
     * @deprecated since 2.0.0 - This will be removed in future versions as it is not used
     */
    @Deprecated
    String getServiceName();
}
