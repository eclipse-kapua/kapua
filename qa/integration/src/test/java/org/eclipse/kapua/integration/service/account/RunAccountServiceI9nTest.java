/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.integration.service.account;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "classpath:features/account/AccountServiceCreation.feature",
                "classpath:features/account/AccountExpirationI9n.feature",
                "classpath:features/account/FindSelfAccount.feature",
                "classpath:features/account/AccountGroupService.feature",
                "classpath:features/account/AccountDeviceRegistryService.feature",
                "classpath:features/account/AccountJobService.feature",
                "classpath:features/account/AccountRoleService.feature",
                "classpath:features/account/AccountTagService.feature",
                "classpath:features/account/AccountUserService.feature",
                "classpath:features/account/AccountCredentialService.feature"
        },
        glue = { "org.eclipse.kapua.qa.common",
                "org.eclipse.kapua.service.account.steps",
                "org.eclipse.kapua.service.user.steps",
                "org.eclipse.kapua.service.authorization.steps",
                "org.eclipse.kapua.service.device.registry.steps",
                "org.eclipse.kapua.service.job.steps",
                "org.eclipse.kapua.service.tag.steps"
        },
        plugin = { "pretty",
                "html:target/cucumber/AccountServiceI9n",
                "json:target/AccountServiceI9n_cucumber.json"
        },
        monochrome = true)
public class RunAccountServiceI9nTest {

}
