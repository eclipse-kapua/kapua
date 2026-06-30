/*******************************************************************************
 * Copyright (c) 2017, 2024 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.integration.service.device.management;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "classpath:features/deviceManagement/DeviceManagementWireI9n.feature",
        },
        glue = {
                "org.eclipse.kapua.qa.common",
                "org.eclipse.kapua.qa.integration.steps",
                "org.eclipse.kapua.service.account.steps",
                "org.eclipse.kapua.service.device.registry.steps",
                "org.eclipse.kapua.service.user.steps",
        },
        plugin = {"pretty",
                "html:target/cucumber/DeviceManagementInventoryI9n",
                "json:target/DeviceManagementInventoryI9n_cucumber.json"},
        monochrome = true)
public class RunDeviceManagementWiresI9nTest {
}
