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
package org.eclipse.kapua.integration.service.datastore;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "classpath:features/datastore/Datastore.feature",
                "classpath:features/datastore/DatastoreMetricStore.feature",
        },
        glue = {"org.eclipse.kapua.qa.common",
               "org.eclipse.kapua.qa.integration.steps",
                "org.eclipse.kapua.service.account.steps",
                "org.eclipse.kapua.service.datastore.steps",
                "org.eclipse.kapua.service.user.steps",
                "org.eclipse.kapua.service.device.registry.steps"},
        plugin = {"pretty",
                  "html:target/cucumber/DockerBroker",
                  "json:target/DockerBroker_cucumber.json",
                  "html:target/cucumber/DatastoreTransportI9n",
                  "json:target/DatastoreTransportI9n_cucumber.json"},
        monochrome = true)
public class RunDatastoreTransportI9nTest {
}
