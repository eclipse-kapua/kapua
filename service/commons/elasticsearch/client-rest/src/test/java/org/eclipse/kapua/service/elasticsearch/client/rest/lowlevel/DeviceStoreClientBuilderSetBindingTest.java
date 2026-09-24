/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.assertj.core.api.Assertions;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.multibindings.ProvidesIntoSet;

/**
 * Verifies the DI wiring that lets {@code DatastoreModule} (and, symmetrically, a downstream project's own module - e.g. edc-next's {@code DeviceLogstoreModule})
 * pick a {@link DeviceStoreClientBuilder} out of a Guice-multibound {@link Set}, by {@link DeviceStoreClientBuilder#getId()} - instead of hardcoding a concrete
 * vendor class.
 * <p>
 * This lives in {@code client-rest} - the module owning {@link DeviceStoreClientBuilderLocator} - and deliberately does NOT depend on any real vendor module
 * (e.g. {@code client-rest-elasticsearch}), since that dependency would run backwards (those modules depend on this one). Two local fakes stand in for
 * "whatever vendor modules happen to be on the classpath".
 * <p>
 * The one non-obvious property under test: two independent "flows" (e.g. Message Store vs. other store implementations), each resolving the <em>same</em> engine id from the
 * <em>same</em> shared {@link Set}, must each get their <em>own</em> {@link DeviceStoreClientBuilder} instance - never the same mutable instance - because a
 * real {@link DeviceStoreClientBuilder} implementation is stateful ({@link DeviceStoreClientBuilder#initializeAndSetHosts}). That only holds because a
 * contributing module's {@code @ProvidesIntoSet} method must NOT be scoped as {@code @Singleton}.
 */
@Category(JUnitTests.class)
public class DeviceStoreClientBuilderSetBindingTest {

    private static final Key<Set<DeviceStoreClientBuilder>> DEVICE_STORE_CLIENT_BUILDER_SET_KEY = Key.get(new TypeLiteral<Set<DeviceStoreClientBuilder>>() {
    });

    /**
     * Stands in for a real vendor implementation (e.g. {@code ElasticsearchDeviceStoreClientBuilder}): only {@link #getId()} matters for these tests.
     */
    private static class FakeDeviceStoreClientBuilder implements DeviceStoreClientBuilder {

        private final String id;

        FakeDeviceStoreClientBuilder(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getVendorName() {
            return id;
        }

        @Override
        public DeviceStoreClientBuilder initializeAndSetHosts(HttpHost[] hosts) {
            throw new UnsupportedOperationException("not needed by this test");
        }

        @Override
        public DeviceStoreClientBuilder setHttpClientConfigCallback(UnaryOperator<HttpAsyncClientBuilder> callback) {
            throw new UnsupportedOperationException("not needed by this test");
        }

        @Override
        public DeviceStoreClientBuilder setRequestConfigCallback(UnaryOperator<RequestConfig.Builder> callback) {
            throw new UnsupportedOperationException("not needed by this test");
        }

        @Override
        public DeviceStoreClient build() {
            throw new UnsupportedOperationException("not needed by this test");
        }
    }

    /**
     * Stands in for a real vendor's {@code *BuilderModule} (e.g. {@code ElasticsearchDeviceStoreClientBuilderModule}): contributes a fresh
     * {@link FakeDeviceStoreClientBuilder} into the {@link Set} on every resolution - deliberately not {@code @Singleton}.
     */
    private static class FirstFakeBuilderModule extends AbstractKapuaModule {

        static final String ID = "fake-first";

        @Override
        protected void configureModule() {
        }

        @ProvidesIntoSet
        DeviceStoreClientBuilder fakeBuilder() {
            return new FakeDeviceStoreClientBuilder(ID);
        }
    }

    private static class SecondFakeBuilderModule extends AbstractKapuaModule {

        static final String ID = "fake-second";

        @Override
        protected void configureModule() {
        }

        @ProvidesIntoSet
        DeviceStoreClientBuilder fakeBuilder() {
            return new FakeDeviceStoreClientBuilder(ID);
        }
    }

    @Test
    public void bothContributedBuildersAppearInTheSharedSetWithTheExpectedIds() {
        Injector injector = Guice.createInjector(new FirstFakeBuilderModule(), new SecondFakeBuilderModule());

        Set<DeviceStoreClientBuilder> available = injector.getInstance(DEVICE_STORE_CLIENT_BUILDER_SET_KEY);

        Assertions.assertThat(available).hasSize(2);
        Assertions.assertThat(available)
                .extracting(DeviceStoreClientBuilder::getId)
                .containsExactlyInAnyOrder(FirstFakeBuilderModule.ID, SecondFakeBuilderModule.ID);
    }

    @Test
    public void locateResolvesTheMatchingBuilderCaseInsensitively() {
        DeviceStoreClientBuilder first = new FakeDeviceStoreClientBuilder(FirstFakeBuilderModule.ID);
        DeviceStoreClientBuilder second = new FakeDeviceStoreClientBuilder(SecondFakeBuilderModule.ID);
        Set<DeviceStoreClientBuilder> candidates = new HashSet<>(Arrays.asList(first, second));

        DeviceStoreClientBuilder picked = new DeviceStoreClientBuilderLocator().locate("Fake-Second", candidates);

        Assertions.assertThat(picked).isSameAs(second);
    }

    @Test
    public void locateThrowsWhenNoContributedBuilderMatches() {
        Assertions.assertThatThrownBy(() -> new DeviceStoreClientBuilderLocator().locate("solr", Collections.emptySet()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void twoIndependentFlowsPickingTheSameEngineGetTheirOwnInstance() {
        // Simulates DatastoreModule and DeviceLogstoreModule: two independent modules, each with their own "resolvedBuilder" @Provides @Singleton method,
        // both reading the SAME shared multibound Set, both configured for the same engine id.
        Injector injector = Guice.createInjector(
                new FirstFakeBuilderModule(),
                new SecondFakeBuilderModule(),
                new AbstractModule() {
                    @Provides
                    @Singleton
                    @Named("messageStore")
                    DeviceStoreClientBuilder messageStoreBuilder(Set<DeviceStoreClientBuilder> availableDeviceStoreClientBuilders, DeviceStoreClientBuilderLocator locator) {
                        return locator.locate(FirstFakeBuilderModule.ID, availableDeviceStoreClientBuilders);
                    }

                    @Provides
                    @Singleton
                    @Named("logStore")
                    DeviceStoreClientBuilder logStoreBuilder(Set<DeviceStoreClientBuilder> availableDeviceStoreClientBuilders, DeviceStoreClientBuilderLocator locator) {
                        return locator.locate(FirstFakeBuilderModule.ID, availableDeviceStoreClientBuilders);
                    }
                });

        DeviceStoreClientBuilder messageStoreBuilder = injector.getInstance(Key.get(DeviceStoreClientBuilder.class, Names.named("messageStore")));
        DeviceStoreClientBuilder logStoreBuilder = injector.getInstance(Key.get(DeviceStoreClientBuilder.class, Names.named("logStore")));

        // Both flows chose the same engine id ...
        Assertions.assertThat(messageStoreBuilder.getId()).isEqualTo(FirstFakeBuilderModule.ID);
        Assertions.assertThat(logStoreBuilder.getId()).isEqualTo(FirstFakeBuilderModule.ID);
        // ... but each flow must hold its OWN mutable instance, not share one.
        Assertions.assertThat(messageStoreBuilder).isNotSameAs(logStoreBuilder);

        // Within a single flow, the @Singleton on the resolving method still means repeated lookups return the same cached instance.
        DeviceStoreClientBuilder messageStoreBuilderAgain = injector.getInstance(Key.get(DeviceStoreClientBuilder.class, Names.named("messageStore")));
        Assertions.assertThat(messageStoreBuilderAgain).isSameAs(messageStoreBuilder);
    }
}
