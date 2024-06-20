/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.server.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.kapua.commons.jersey.web.ServiceBundleContextListener;
import org.eclipse.kapua.service.scheduler.quartz.SchedulerServiceInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.0
 */
public class ConsoleListener extends ServiceBundleContextListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleListener.class);

    @Override
    public void contextInitialized(final ServletContextEvent event) {

        // Start service modules
        initServiceModuleBundles();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        // Stop event modules
        stopServiceModuleBundles();

        // Stop Quartz scheduler
        try {
            LOG.info("Stopping Quartz scheduler...");
            SchedulerServiceInit.close();
            LOG.info("Stopping Quartz scheduler... DONE");
        } catch (Exception e) {
            LOG.info("Stopping Quartz scheduler... ERROR! Error: {}", e.getMessage(), e);
        }
    }

}
