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
package org.eclipse.kapua.commons.metric;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import org.eclipse.kapua.service.KapuaService;

/**
 * Metric service definition
 *
 * @since 1.0
 */
public interface MetricsService extends KapuaService {

    /**
     * Get a Counter for the specified name. If the counter doesn't exist the method should create a new one counter with the specified name.
     *
     * @param module
     * @param component
     * @param names
     * @return
     */
    public Counter getCounter(String module, String component, String... names);

    /**
     * Get a Histogram for the specified name. If the histogram doesn't exist the method should create a new one histogram with the specified name.
     *
     * @param module
     * @param component
     * @param names
     * @return
     */
    public Histogram getHistogram(String module, String component, String... names);

    /**
     * Get a Timer for the specified name. If the timer doesn't exist the method should create a new one timer with the specified name.
     *
     * @param module
     * @param component
     * @param names
     * @return
     */
    public Timer getTimer(String module, String component, String... names);

    /**
     * Register a Gauge for the specified name. If the Gauge exists the method must catch possible exception from underlying service.
     *
     * @param module
     * @param component
     * @param names
     */
    public void registerGauge(Gauge<?> gauge, String module, String component, String... names);

}
