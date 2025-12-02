/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import org.eclipse.kapua.commons.metric.MetricsLabel;
import org.eclipse.kapua.commons.metric.MetricsService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MetricsDatastore {

    private static final String CONSUMER_TELEMETRY = "consumer_telemetry";
    private static final String STORE = "store";
    private static final String PROCESSED = "processed";

    private final Counter message;
    private final Counter communicationError;
    private final Counter configurationError;
    private final Counter genericError;
    private final Counter validationError;
    // store timers
    private final Timer dataSaveTime;
    // queues counters
    private final Counter processedCommunicationError;
    private final Counter processedConfigurationError;
    private final Counter processedGenericError;

    @Inject
    public MetricsDatastore(MetricsService metricsService) {
        // data message
        message = metricsService.getCounter(CONSUMER_TELEMETRY, STORE, MetricsLabel.ATTEMPT);
        communicationError = metricsService.getCounter(CONSUMER_TELEMETRY, STORE, MetricsLabel.COMMUNICATION, MetricsLabel.ERROR);
        configurationError = metricsService.getCounter(CONSUMER_TELEMETRY, STORE, MetricsLabel.CONFIGURATION, MetricsLabel.ERROR);
        genericError = metricsService.getCounter(CONSUMER_TELEMETRY, STORE, MetricsLabel.GENERIC, MetricsLabel.ERROR);
        validationError = metricsService.getCounter(CONSUMER_TELEMETRY, STORE, MetricsLabel.VALIDATION, MetricsLabel.ERROR);

        // error messages queues size
        processedCommunicationError = metricsService.getCounter(CONSUMER_TELEMETRY, PROCESSED, MetricsLabel.COMMUNICATION, MetricsLabel.ERROR);
        processedConfigurationError = metricsService.getCounter(CONSUMER_TELEMETRY, PROCESSED, MetricsLabel.CONFIGURATION, MetricsLabel.ERROR);
        processedGenericError = metricsService.getCounter(CONSUMER_TELEMETRY, PROCESSED, MetricsLabel.GENERIC, MetricsLabel.ERROR);

        // store timers
        dataSaveTime = metricsService.getTimer(CONSUMER_TELEMETRY, STORE, MetricsLabel.TIME, MetricsLabel.SECONDS);
    }

    public Counter getMessage() {
        return message;
    }

    public Counter getCommunicationError() {
        return communicationError;
    }

    public Counter getConfigurationError() {
        return configurationError;
    }

    public Counter getGenericError() {
        return genericError;
    }

    public Counter getValidationError() {
        return validationError;
    }

    public Timer getDataSaveTime() {
        return dataSaveTime;
    }

    public Counter getProcessedCommunicationError() {
        return processedCommunicationError;
    }

    public Counter getProcessedConfigurationError() {
        return processedConfigurationError;
    }

    public Counter getProcessedGenericError() {
        return processedGenericError;
    }

}
