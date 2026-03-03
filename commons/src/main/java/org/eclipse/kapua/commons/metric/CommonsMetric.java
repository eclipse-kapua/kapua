/*******************************************************************************
 * Copyright (c) 2022, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.metric;

import com.codahale.metrics.Counter;
import org.eclipse.kapua.KapuaException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Helper class to handle commons metrics.
 */
@Singleton
public class CommonsMetric {

    private final String module;
    //cache
    private static final String CACHE_MANAGER = "cache_manager";
    private Integer cacheStatus = new Integer(0);
    private Counter registeredCache;

    //raise service events
    private static final String EVENT_DATA_FILLER = "event_data_filler";
    private Counter raiseEventWrongId;
    private Counter raiseEventWrongEntity;

    //event bus
    private static final String EVENT_BUS_CONNECTION = "event_bus_connection";
    private Counter eventBusConnectionRetry;
    private Counter eventBusConnectionError;

    //cache
    private static final String CACHE_ENTITY = "cache_entity";
    private Counter cacheMiss;
    private Counter cacheHit;
    private Counter cacheRemoval;
    private Counter cacheError;

    @Inject
    public CommonsMetric(MetricsService metricsService,
                         @Named("metricModuleName") String metricModuleName) throws KapuaException {
        this.module = metricModuleName;
        metricsService.registerGauge(() -> cacheStatus, module, CACHE_MANAGER, "cache_status");
        registeredCache = metricsService.getCounter(module, CACHE_MANAGER, "available_cache");

        raiseEventWrongId = metricsService.getCounter(module, EVENT_DATA_FILLER, "wrong_id");
        raiseEventWrongEntity = metricsService.getCounter(module, EVENT_DATA_FILLER, "wrong_entity");

        eventBusConnectionRetry = metricsService.getCounter(module, EVENT_BUS_CONNECTION, MetricsLabel.RETRY);
        eventBusConnectionError = metricsService.getCounter(module, EVENT_BUS_CONNECTION, MetricsLabel.ERROR);

        cacheMiss = metricsService.getCounter(module, CACHE_ENTITY, "miss");
        cacheHit = metricsService.getCounter(module, CACHE_ENTITY, "hit");
        cacheRemoval = metricsService.getCounter(module, CACHE_ENTITY, "removed");
        cacheError = metricsService.getCounter(module, CACHE_ENTITY, "error");
    }

    public String getModule() {
        return module;
    }

    //TODO should be synchronized?
    public void setCacheStatus(int value) {
        cacheStatus = value;
    }

    public Counter getRegisteredCache() {
        return registeredCache;
    }

    public Counter getRaiseEventWrongId() {
        return raiseEventWrongId;
    }

    public Counter getRaiseEventWrongEntity() {
        return raiseEventWrongEntity;
    }

    public Counter getEventBusConnectionRetry() {
        return eventBusConnectionRetry;
    }

    public Counter getEventBusConnectionError() {
        return eventBusConnectionError;
    }

    public Counter getCacheMiss() {
        return cacheMiss;
    }

    public Counter getCacheHit() {
        return cacheHit;
    }

    public Counter getCacheRemoval() {
        return cacheRemoval;
    }

    public Counter getCacheError() {
        return cacheError;
    }

}
