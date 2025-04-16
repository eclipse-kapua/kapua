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
package org.eclipse.kapua.commons;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.core.JaxbClassProvider;
import org.eclipse.kapua.commons.core.SimpleJaxbClassProvider;
import org.eclipse.kapua.commons.crypto.CryptoUtil;
import org.eclipse.kapua.commons.crypto.CryptoUtilImpl;
import org.eclipse.kapua.commons.crypto.setting.CryptoSettings;
import org.eclipse.kapua.commons.event.JsonServiceEventMarshaler;
import org.eclipse.kapua.commons.event.ServiceEventMarshaler;
import org.eclipse.kapua.commons.event.XmlServiceEventMarshaler;
import org.eclipse.kapua.commons.jpa.EventStorer;
import org.eclipse.kapua.commons.jpa.EventStorerImpl;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.mappers.KapuaBaseMapper;
import org.eclipse.kapua.commons.model.mappers.KapuaBaseMapperImpl;
import org.eclipse.kapua.commons.model.query.QueryFactoryImpl;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordCreator;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreRecordRepository;
import org.eclipse.kapua.commons.service.event.store.api.EventStoreService;
import org.eclipse.kapua.commons.service.event.store.internal.EventStoreRecordImplJpaRepository;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.qr.QRCodeBuilder;
import org.eclipse.kapua.commons.util.qr.QRCodeBuilderImpl;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.event.ServiceEventBusException;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.model.query.QueryFactory;

import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;

/**
 * {@code kapua-commons} {@link AbstractKapuaModule}.
 *
 * @since 2.0.0
 */
public class CommonsModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(QueryFactory.class).to(QueryFactoryImpl.class).in(Singleton.class);
        bind(CryptoSettings.class).toInstance(new CryptoSettings());
        bind(CryptoUtil.class).to(CryptoUtilImpl.class).in(Singleton.class);
        bind(QRCodeBuilder.class).to(QRCodeBuilderImpl.class).in(Singleton.class);
        bind(KapuaBaseMapper.class).to(KapuaBaseMapperImpl.class).in(Singleton.class);
        bind(XmlUtil.class).in(Singleton.class);

        final Multibinder<JaxbClassProvider> jaxbClassProviderMultibinder = Multibinder.newSetBinder(binder(), JaxbClassProvider.class);
        jaxbClassProviderMultibinder.addBinding()
                .toInstance(new SimpleJaxbClassProvider(
                        EventStoreRecordCreator.class,
                        ServiceEvent.class
                ));
    }

    @ProvidesIntoSet
    public Domain eventStoreDomain() {
        return new DomainEntry(Domains.EVENT_STORE, EventStoreService.class.getName(), false, Actions.read, Actions.delete, Actions.write);
    }

    @Provides
    @Singleton
    EventStoreRecordRepository eventStoreRecordRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        return new EventStoreRecordImplJpaRepository(jpaRepoConfig);
    }

    @Provides
    @Singleton
    EventStorer eventPersister(EventStoreRecordRepository repository) {
        return new EventStorerImpl(repository);
    }

    /**
     * Very basic mechanism to switch between the XML and JSON implementations provided - if you need a third option, just use an OverridingModule and redefine this wiring completely.
     */
    @Provides
    @Singleton
    ServiceEventMarshaler serviceEventMarshaler(XmlUtil xmlUtil) throws ServiceEventBusException {
        final String messageSerializer = SystemSetting.getInstance().getString(SystemSettingKey.EVENT_BUS_MESSAGE_SERIALIZER);
        if (XmlServiceEventMarshaler.class.getName().equals(messageSerializer)) {
            return new XmlServiceEventMarshaler(xmlUtil);
        }
        if (JsonServiceEventMarshaler.class.getName().equals(messageSerializer)) {
            return new JsonServiceEventMarshaler(xmlUtil);
        }
        throw new ServiceEventBusException(String.format("Wrong message serializer Object type ('%s')!", messageSerializer));
    }

}