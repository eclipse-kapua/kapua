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
package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.eclipse.kapua.service.config.ServiceConfiguration;

public interface ServiceConfigurationsFacade {

    ServiceConfiguration fetchAllConfigurations(KapuaId scopeId) throws KapuaException;

    void update(KapuaId scopeId, ServiceConfiguration serviceConfiguration) throws KapuaException;

    ServiceComponentConfiguration fetchConfiguration(KapuaId scopeId, String serviceId) throws KapuaException;

    void update(KapuaId scopeId, String serviceId, ServiceComponentConfiguration newServiceComponentConfiguration) throws KapuaException;

}
