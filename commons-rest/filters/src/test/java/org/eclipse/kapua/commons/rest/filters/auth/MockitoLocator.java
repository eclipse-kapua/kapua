/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.rest.filters.auth;

import java.util.Collections;
import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.metric.CommonsMetric;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;
import org.mockito.Mockito;

import com.codahale.metrics.Counter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;

public class MockitoLocator extends KapuaLocator {

    @Override
    public <S extends KapuaService> S getService(Class<S> serviceClass) {
        return Mockito.mock(serviceClass);
    }

    @Override
    public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass) {
        return Mockito.mock(factoryClass);
    }

    @Override
    public List<KapuaService> getServices() {
        return Collections.emptyList();
    }

    @Override
    public <T> T getComponent(Class<T> componentClass) {
        final MetricsService metricsService = new MetricsService() {

            @Override
            public Counter getCounter(String module, String component, String... names) {
                return new Counter();
            }

            @Override
            public Histogram getHistogram(String module, String component, String... names) {
                return new Histogram(new ExponentiallyDecayingReservoir());
            }

            @Override
            public Timer getTimer(String module, String component, String... names) {
                return new Timer();
            }

            @Override
            public void registerGauge(Gauge<?> gauge, String module, String component, String... names) throws KapuaException {

            }

        };
        if (MetricsService.class.equals(componentClass)) {
            return (T) metricsService;
        }
        //        if (KapuaCommonApiCoreSetting.class.equals(componentClass)) {
        //            return (T) new KapuaCommonApiCoreSetting();
        //        }
        if (CommonsMetric.class.equals(componentClass)) {
            try {
                return (T) new CommonsMetric(metricsService, "tests");
            } catch (KapuaException e) {
                throw new RuntimeException(e);
            }
        }
        return Mockito.mock(componentClass);
    }

    @Override
    public <T> T getComponent(Class<T> componentClass, String named) {
        return null;
    }
}
