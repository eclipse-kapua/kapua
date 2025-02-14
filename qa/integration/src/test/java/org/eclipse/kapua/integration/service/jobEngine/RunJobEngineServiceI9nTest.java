/*******************************************************************************
 * Copyright (c) 2024, 2024 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.integration.service.jobEngine;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
//                "classpath:features/jobEngine/JobEngineServiceOperations.feature",
//                "classpath:features/jobEngine/JobEngineServiceProcessorAssetI9n.feature",
//                "classpath:features/jobEngine/JobEngineServiceProcessorBundleI9n.feature",
//                "classpath:features/jobEngine/JobEngineServiceProcessorCommandI9n.feature",
//                "classpath:features/jobEngine/JobEngineServiceProcessorConfigurationI9n.feature",
//                "classpath:features/jobEngine/JobEngineServiceProcessorKeystoreI9n.feature",
//                "classpath:features/jobEngine/JobEngineServiceProcessorPackagesI9n.feature",
                "classpath:features/jobEngine/JobEngineServiceProcessorInventoryI9n.feature"
        },
        glue = {
                "org.eclipse.kapua.service.job.steps",
                "org.eclipse.kapua.service.user.steps",
                "org.eclipse.kapua.qa.common",
                "org.eclipse.kapua.qa.integration.steps",
                "org.eclipse.kapua.service.account.steps",
                "org.eclipse.kapua.service.device.registry.steps",
        },
        plugin = {
                "pretty",
                "html:target/cucumber/RunJobEngineServiceI9n",
                "json:target/RunJobEngineServiceI9n_cucumber.json"
        },
        monochrome = true)
public class RunJobEngineServiceI9nTest {
}
