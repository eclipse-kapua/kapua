/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.endpoint.EndpointInfoFactory;
import org.eclipse.kapua.service.endpoint.EndpointInfoRepository;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;

public class EndpointModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(EndpointInfoFactory.class).to(EndpointInfoFactoryImpl.class);
    }

    @Provides
    @Singleton
    EndpointInfoService endpointInfoService(
            AuthorizationService authorizationService,
            AccountService accountService,
            EndpointInfoRepository endpointInfoRepository,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new EndpointInfoServiceImpl(
                accountService,
                authorizationService,
                endpointInfoRepository,
                jpaTxManagerFactory.create("kapua-endpoint"));
    }

    @ProvidesIntoSet
    public Domain endpointDomain() {
        return new DomainEntry(Domains.ENDPOINT_INFO, EndpointInfoService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @Provides
    @Singleton
    EndpointInfoRepository endpointInfoRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new EndpointInfoImplJpaRepository(jpaRepoConfig);
    }
}
