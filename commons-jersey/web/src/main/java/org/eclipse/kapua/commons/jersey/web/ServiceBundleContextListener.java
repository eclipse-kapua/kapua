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
package org.eclipse.kapua.commons.jersey.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.core.ServiceModuleBundle;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.locator.KapuaLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.0
 */
public class ServiceBundleContextListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceBundleContextListener.class);

    //Injection not supported here, unfortunately
    private SystemSetting systemSetting = KapuaLocator.getInstance().getComponent(SystemSetting.class);
    private ServiceModuleBundle moduleBundle = KapuaLocator.getInstance().getComponent(ServiceModuleBundle.class);

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        initServiceModuleBundles();
    }

    public void initServiceModuleBundles() {
        LOG.warn("Initialized, systemSettings:{}, moduleBundle: {}", systemSetting, moduleBundle);

        // Start service modules
        try {
            LOG.info("Starting service modules...");
            moduleBundle.startup();
            LOG.info("Starting service modules... DONE!");
        } catch (KapuaException e) {
            LOG.error("Starting service modules... ERROR! Error: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        stopServiceModuleBundles();
    }

    public void stopServiceModuleBundles() {
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
