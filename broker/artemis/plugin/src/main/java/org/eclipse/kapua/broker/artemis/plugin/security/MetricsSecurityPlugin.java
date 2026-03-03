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
package org.eclipse.kapua.broker.artemis.plugin.security;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.LoginMetric;
import org.eclipse.kapua.commons.metric.MetricsLabel;
import org.eclipse.kapua.commons.metric.MetricsService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MetricsSecurityPlugin {

    private static final String ACL = "acl";
    private static final String SESSION_CONTEXT = "session_context";
    private static final String SESSION_CONTEXT_BY_CLIENT = "session_context_by_client";
    private static final String ACTIVE_CONNECTION = "active_connection";
    private static final String TOTAL_MESSAGE = "total_message";
    private static final String TOTAL_MESSAGE_ACKNOWLEDGED = "total_message_acknowledged";
    private static final String TOTAL_MESSAGE_ADDED = "total_message_added";
    private static final String SECURITY_PLUGIN_EVENT = "security_plugin_event";

    private final MetricsService metricsService;
    private final String metricModuleName;

    private Counter processedEvent;
    private Counter dequeuedEvent;
    private Counter enqueuedEvent;

    @Inject
    public MetricsSecurityPlugin(
            MetricsService metricsService,
            @Named("metricModuleName")
            String metricModuleName) {
        this.metricsService = metricsService;
        this.metricModuleName = metricModuleName;
    }

    public void init(ActiveMQServer server, Gauge<Integer> mapSize, Gauge<Integer> mapByClientSize, Gauge<Integer> aclSize, Gauge<Integer> activeConnection) throws KapuaException {
        metricsService.registerGauge(mapSize, metricModuleName, LoginMetric.COMPONENT_LOGIN, SESSION_CONTEXT);
        metricsService.registerGauge(mapByClientSize, metricModuleName, LoginMetric.COMPONENT_LOGIN, SESSION_CONTEXT_BY_CLIENT);
        metricsService.registerGauge(aclSize, metricModuleName, LoginMetric.COMPONENT_LOGIN, ACL);
        metricsService.registerGauge(activeConnection, metricModuleName, LoginMetric.COMPONENT_LOGIN, ACTIVE_CONNECTION);

        metricsService.registerGauge(() -> server.getTotalMessageCount(), metricModuleName, LoginMetric.COMPONENT_LOGIN, TOTAL_MESSAGE, MetricsLabel.SIZE);
        metricsService.registerGauge(() -> server.getTotalMessagesAcknowledged(), metricModuleName, LoginMetric.COMPONENT_LOGIN, TOTAL_MESSAGE_ACKNOWLEDGED, MetricsLabel.SIZE);
        metricsService.registerGauge(() -> server.getTotalMessagesAdded(), metricModuleName, LoginMetric.COMPONENT_LOGIN, TOTAL_MESSAGE_ADDED, MetricsLabel.SIZE);

        processedEvent = metricsService.getCounter(metricModuleName, SECURITY_PLUGIN_EVENT, "processed");
        dequeuedEvent = metricsService.getCounter(metricModuleName, SECURITY_PLUGIN_EVENT, "dequeued");
        enqueuedEvent = metricsService.getCounter(metricModuleName, SECURITY_PLUGIN_EVENT, "enqueued");
    }

    public Counter getProcessedEvent() {
        return processedEvent;
    }

    public Counter getDequeuedEvent() {
        return dequeuedEvent;
    }

    public Counter getEnqueuedEvent() {
        return enqueuedEvent;
    }
}