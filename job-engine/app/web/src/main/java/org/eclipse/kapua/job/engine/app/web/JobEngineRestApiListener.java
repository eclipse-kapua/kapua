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
package org.eclipse.kapua.job.engine.app.web;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.core.ServiceModuleBundle;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.scheduler.quartz.SchedulerServiceInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @since 1.0.0
 */
public class JobEngineRestApiListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(JobEngineRestApiListener.class);


    private ServiceModuleBundle moduleBundle;

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        // Start Quartz scheduler
        try {
            LOG.info("Starting Quartz scheduler...");
            SchedulerServiceInit.initialize();
            LOG.info("Starting Quartz scheduler... DONE!");
        } catch (Exception e) {
            LOG.error("Starting Quartz scheduler... ERROR! Error: {}", e.getMessage(), e);
            throw new ExceptionInInitializerError(e);
        }
        // Start service modules
        try {
            LOG.info("Starting service modules...");
            if (moduleBundle == null) {
                moduleBundle = KapuaLocator.getInstance().getComponent(ServiceModuleBundle.class);
            }
            moduleBundle.startup();
            LOG.info("Starting service modules... DONE!");
        } catch (KapuaException e) {
            LOG.error("Starting service modules... ERROR! Error: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        // Stop event modules
        try {
            LOG.info("Stopping service modules...");
            if (moduleBundle != null) {
                moduleBundle.shutdown();
                moduleBundle = null;
            }
            LOG.info("Stopping service modules... DONE!");
        } catch (KapuaException e) {
            LOG.error("Stopping service modules... ERROR! Error: {}", e.getMessage(), e);
        }
    }

}
