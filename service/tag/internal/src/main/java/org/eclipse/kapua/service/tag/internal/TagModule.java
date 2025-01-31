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
package org.eclipse.kapua.service.tag.internal;

import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagRepository;
import org.eclipse.kapua.service.tag.TagService;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;

public class TagModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(TagFactory.class).to(TagFactoryImpl.class);
    }

    @Provides
    @Singleton
    TagService tagService(
            AuthorizationService authorizationService,
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass,
            TagRepository tagRepository,
            TagFactory tagFactory,
            KapuaJpaTxManagerFactory jpaTxManagerFactory) {
        return new TagServiceImpl(authorizationService, serviceConfigurationManagersByServiceClass.get(TagService.class),
                jpaTxManagerFactory.create("kapua-tag"),
                tagRepository,
                tagFactory);
    }

    @ProvidesIntoSet
    public Domain tagDomain() {
        return new DomainEntry(Domains.TAG, TagService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @Provides
    @Singleton
    TagRepository tagRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new TagImplJpaRepository(jpaRepoConfig);
    }
}
