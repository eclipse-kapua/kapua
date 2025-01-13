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
package org.eclipse.kapua.service.endpoint.internal;

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaUpdatableEntityJpaRepository;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfoRepository;

public class EndpointInfoImplJpaRepository
        extends KapuaUpdatableEntityJpaRepository<EndpointInfo, EndpointInfoImpl, EndpointInfoListResult>
        implements EndpointInfoRepository {

    public EndpointInfoImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(EndpointInfoImpl.class, EndpointInfo.TYPE, () -> new EndpointInfoListResult(), jpaRepoConfig);
    }
}
