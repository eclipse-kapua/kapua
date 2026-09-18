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
package org.eclipse.kapua.plugin.devicestorage.provider.elasticsearch;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.elasticsearch.client.rest.lowlevel.DeviceStoreClientBuilder;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@Category(JUnitTests.class)
public class ElasticsearchDeviceStoreClientBuilderModuleTest {

    @Test
    public void idMatchesTheConstantAndIsContributedToTheSharedSet() {
        Assertions.assertThat(new ElasticsearchDeviceStoreClientBuilder().getId()).isEqualTo(ElasticsearchDeviceStoreClientBuilder.ID);

        Injector injector = Guice.createInjector(new ElasticsearchDeviceStoreClientBuilderModule());
        Set<DeviceStoreClientBuilder> available = injector.getInstance(Key.get(new TypeLiteral<Set<DeviceStoreClientBuilder>>() {
        }));

        Assertions.assertThat(available).hasSize(1);
        Assertions.assertThat(available.iterator().next()).isInstanceOf(ElasticsearchDeviceStoreClientBuilder.class);
    }
}
