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
package org.eclipse.kapua.service.device.management.inventory.job;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.job.engine.commons.operation.AbstractDeviceTargetProcessor;
import org.eclipse.kapua.job.engine.commons.wrappers.JobTargetWrapper;
import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementService;
import org.eclipse.kapua.service.device.management.inventory.job.definition.DeviceContainerPropertyKeys;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainerAction;
import org.eclipse.kapua.service.job.operation.TargetProcessor;
import org.eclipse.kapua.service.job.targets.JobTarget;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

public class DeviceContainerStopTargetProcessor extends AbstractDeviceTargetProcessor implements TargetProcessor {

    @Inject
    DeviceInventoryManagementService deviceInventoryManagementService;
    @Inject
    JobContext jobContext;
    @Inject
    StepContext stepContext;

    @Override
    protected void initProcessing(JobTargetWrapper wrappedJobTarget) {
        setContext(jobContext, stepContext);
    }

    @Override
    public void processTarget(JobTarget jobTarget) throws KapuaException {

        DeviceInventoryContainer containerInput = stepContextWrapper.getStepProperty(DeviceContainerPropertyKeys.CONTAINER_INPUT, DeviceInventoryContainer.class);
        Long timeout = stepContextWrapper.getStepProperty(DeviceContainerPropertyKeys.TIMEOUT, Long.class);

        KapuaSecurityUtils.doPrivileged(() -> deviceInventoryManagementService.execContainer(jobTarget.getScopeId(), jobTarget.getJobTargetId(), containerInput, DeviceInventoryContainerAction.STOP, timeout));
    }
}


