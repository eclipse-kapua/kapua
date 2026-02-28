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
package org.eclipse.kapua.app.api.web;

import javax.servlet.ServletContextEvent;

import org.eclipse.kapua.commons.jersey.web.ServiceBundleContextListener;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class ServiceBundleContextListenerTest {

    ServiceBundleContextListener serviceBundleContextListener;
    ServletContextEvent servletContextEvent;

    @Before
    public void initialize() {
        serviceBundleContextListener = new ServiceBundleContextListener();
        servletContextEvent = Mockito.mock(ServletContextEvent.class);
    }

    @Test
    public void contextInitializedTest() {
        try {
            serviceBundleContextListener.contextInitialized(servletContextEvent);
        } catch (Exception e) {
            Assert.fail("Exception not expected.");
        }
    }

    @Test
    public void contextInitializedNullTest() {
        try {
            serviceBundleContextListener.contextInitialized(null);
        } catch (Exception e) {
            Assert.fail("Exception not expected.");
        }
    }

    @Test
    public void contextDestroyedTest() {
        try {
            serviceBundleContextListener.contextDestroyed(servletContextEvent);
        } catch (Exception e) {
            Assert.fail("Exception not expected.");
        }
    }

    @Test
    public void contextDestroyedNullTest() {
        try {
            serviceBundleContextListener.contextDestroyed(null);
        } catch (Exception e) {
            Assert.fail("Exception not expected.");
        }
    }
}