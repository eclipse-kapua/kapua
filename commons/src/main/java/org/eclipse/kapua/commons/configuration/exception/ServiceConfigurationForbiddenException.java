/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.configuration.exception;

import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.eclipse.kapua.service.user.User;

/**
 * {@link KapuaConfigurationException} to {@code throw} when an update of a {@link ServiceComponentConfiguration#getProperties()} contains updates to values that cannot be updated by the curren {@link User}.
 * <p>
 * {@link ServiceComponentConfiguration#getProperties()} can be updated under the following conditions:
 * <ul>
 *     <li>If the current {@link User} is root. Root {@link User} can do whatever he wants</li>
 *     <li>If the {@link KapuaTad} is marked to allow self-edit. For those properties a {@link User} can update the values for its {@link Account}</li>
 *     <li>If the {@link ServiceComponentConfiguration} belongs to a child {@link Account} of the current {@link User}</li>
 * </ul>
 *
 * @since 2.1.0
 */
public class ServiceConfigurationForbiddenException extends KapuaConfigurationException {

    private final KapuaId scopeId;
    private final String servicePid;

    private final String propertyId;

    private final String propertyValue;

    /**
     * Constructor.
     *
     * @param servicePid    The {@link KapuaConfigurableService} pid.
     * @param scopeId       The scope {@link KapuaId} for which limit has been exceeded.
     * @param propertyId    The {@link KapuaTad#getId()} for which the update is forbidden.
     * @param propertyValue The {@link KapuaTad} value for which the update is forbidden.
     * @since 2.0.0
     */
    public ServiceConfigurationForbiddenException(KapuaId scopeId, String servicePid, String propertyId, String propertyValue) {
        super(KapuaConfigurationErrorCodes.UPDATE_PROPERTY_FORBIDDEN, scopeId, servicePid, propertyId, propertyValue);

        this.scopeId = scopeId;
        this.servicePid = servicePid;
        this.propertyId = propertyId;
        this.propertyValue = propertyValue;
    }

    /**
     * Gets the scope {@link KapuaId} for which the update is forbidden.
     *
     * @return The scope {@link KapuaId} for which the update is forbidden.
     * @since 2.1.0
     */
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Gets the {@link KapuaConfigurableService} pid for which the update is forbidden.
     *
     * @return he {@link KapuaConfigurableService} pid for which the update is forbidden.
     * @since 2.1.0
     */
    public String getServicePid() {
        return servicePid;
    }

    /**
     * Gets the {@link KapuaTad#getId()} for which the update is forbidden.
     *
     * @return The {@link KapuaTad#getId()} for which the update is forbidden.
     * @since 2.1.0
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * Gets the {@link KapuaTad} value for which the update is forbidden.
     *
     * @return The {@link KapuaTad} value for which the update is forbidden.
     * @since 2.1.0
     */
    public String getPropertyValue() {
        return propertyValue;
    }
}
