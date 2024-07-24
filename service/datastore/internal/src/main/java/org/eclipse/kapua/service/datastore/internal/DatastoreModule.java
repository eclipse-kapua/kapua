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
package org.eclipse.kapua.service.datastore.internal;

import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.jpa.KapuaJpaTxManagerFactory;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.datastore.ChannelInfoFactory;
import org.eclipse.kapua.service.datastore.ChannelInfoRegistryService;
import org.eclipse.kapua.service.datastore.ClientInfoFactory;
import org.eclipse.kapua.service.datastore.ClientInfoRegistryService;
import org.eclipse.kapua.service.datastore.MessageStoreFactory;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.MetricInfoFactory;
import org.eclipse.kapua.service.datastore.MetricInfoRegistryService;
import org.eclipse.kapua.service.datastore.internal.client.DatastoreElasticsearchClientConfiguration;
import org.eclipse.kapua.service.datastore.internal.converter.ModelContextImpl;
import org.eclipse.kapua.service.datastore.internal.converter.QueryConverterImpl;
import org.eclipse.kapua.service.datastore.internal.mediator.DatastoreUtils;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.elasticsearch.client.ElasticsearchClientProvider;
import org.eclipse.kapua.service.elasticsearch.client.configuration.ElasticsearchClientConfiguration;
import org.eclipse.kapua.service.elasticsearch.client.rest.MetricsEsClient;
import org.eclipse.kapua.service.elasticsearch.client.rest.RestElasticsearchClientProvider;
import org.eclipse.kapua.service.storable.model.id.StorableIdFactory;

import com.google.inject.Provides;
import com.google.inject.multibindings.ProvidesIntoSet;

public class DatastoreModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(DatastoreSettings.class).in(Singleton.class);
        bind(ClientInfoFactory.class).to(ClientInfoFactoryImpl.class).in(Singleton.class);
        bind(ClientInfoRepository.class).to(ClientInfoElasticsearchRepository.class).in(Singleton.class);
        bind(ClientInfoRegistryFacade.class).to(ClientInfoRegistryFacadeImpl.class).in(Singleton.class);
        bind(ClientInfoRegistryService.class).to(ClientInfoRegistryServiceImpl.class).in(Singleton.class);

        bind(MetricInfoFactory.class).to(MetricInfoFactoryImpl.class).in(Singleton.class);
        bind(MetricInfoRepository.class).to(MetricInfoRepositoryImpl.class).in(Singleton.class);
        bind(MetricInfoRegistryFacade.class).to(MetricInfoRegistryFacadeImpl.class).in(Singleton.class);
        bind(MetricInfoRegistryService.class).to(MetricInfoRegistryServiceImpl.class).in(Singleton.class);

        bind(ChannelInfoFactory.class).to(ChannelInfoFactoryImpl.class).in(Singleton.class);
        bind(ChannelInfoRepository.class).to(ChannelInfoElasticsearchRepository.class).in(Singleton.class);
        bind(ChannelInfoRegistryFacade.class).to(ChannelInfoRegistryFacadeImpl.class).in(Singleton.class);
        bind(ChannelInfoRegistryService.class).to(ChannelInfoRegistryServiceImpl.class).in(Singleton.class);

        bind(MessageStoreFactory.class).to(MessageStoreFactoryImpl.class).in(Singleton.class);
        bind(MessageRepository.class).to(MessageElasticsearchRepository.class).in(Singleton.class);
        bind(MessageStoreFacade.class).to(MessageStoreFacadeImpl.class).in(Singleton.class);
        bind(MetricsDatastore.class).in(Singleton.class);
        bind(DatastoreUtils.class).in(Singleton.class);
        bind(DatastoreCacheManager.class).in(Singleton.class);
    }

    @ProvidesIntoSet
    public Domain dataStoreDomain() {
        return new DomainEntry(Domains.DATASTORE, "org.eclipse.kapua.service.datastore.DatastoreService", false, Actions.read, Actions.delete, Actions.write);
    }

    @Provides
    @Singleton
    ElasticsearchClientProvider elasticsearchClientProvider(MetricsEsClient metricsEsClient, StorableIdFactory storableIdFactory, DatastoreUtils datastoreUtils) {
        ElasticsearchClientConfiguration esClientConfiguration = DatastoreElasticsearchClientConfiguration.getInstance();
        return new RestElasticsearchClientProvider(metricsEsClient)
                .withClientConfiguration(esClientConfiguration)
                .withModelContext(new ModelContextImpl(storableIdFactory, datastoreUtils))
                .withModelConverter(new QueryConverterImpl());
    }

    @Provides
    @Singleton
    ConfigurationProvider configurationProvider(
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            AccountService accountService
    ) {
        final ConfigurationProviderImpl configurationProvider = new ConfigurationProviderImpl(
                serviceConfigurationManagersByServiceClass.get(MessageStoreService.class),
                accountService);
        return configurationProvider;
    }

    @Provides
    @Singleton
    MessageStoreService messageStoreService(
            PermissionFactory permissionFactory,
            AuthorizationService authorizationService,
            Map<Class<?>, ServiceConfigurationManager> serviceConfigurationManagersByServiceClass,
            KapuaJpaTxManagerFactory jpaTxManagerFactory,
            MessageStoreFacade messageStoreFacade,
            MetricsDatastore metricsDatastore,
            DatastoreSettings datastoreSettings) {
        return new MessageStoreServiceImpl(
                jpaTxManagerFactory.create("kapua-datastore"),
                permissionFactory,
                authorizationService,
                serviceConfigurationManagersByServiceClass.get(MessageStoreService.class),
                messageStoreFacade,
                metricsDatastore,
                datastoreSettings);
    }
}