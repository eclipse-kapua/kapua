/*******************************************************************************
 * Copyright (c) 2024, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.inventory.job;

import com.google.inject.multibindings.ProvidesIntoSet;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.service.device.management.inventory.job.definition.DeviceContainerStartJobStepDefinition;
import org.eclipse.kapua.service.device.management.inventory.job.definition.DeviceContainerStopJobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;

public class DeviceContainerJobModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
    }

    @ProvidesIntoSet
    public JobStepDefinition deviceContainerStartJobStepDefinition() {
        return new DeviceContainerStartJobStepDefinition();
    }

    @ProvidesIntoSet
    public JobStepDefinition deviceContainerStopJobStepDefinition() {
        return new DeviceContainerStopJobStepDefinition();
    }

}