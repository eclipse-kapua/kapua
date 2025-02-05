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
package org.eclipse.kapua.service.device.management.packages.job;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.job.engine.commons.wrappers.JobTargetWrapper;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.packages.DevicePackageManagementService;
import org.eclipse.kapua.service.device.management.packages.job.definition.DevicePackageDownloadPropertyKeys;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadOptions;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.job.operation.TargetProcessor;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetStatus;

/**
 * {@link TargetProcessor} for {@link DevicePackageManagementService#downloadExec(KapuaId, KapuaId, DevicePackageDownloadRequest, Long)}.
 *
 * @since 1.0.0
 */
public class DevicePackageDownloadTargetProcessor extends AbstractDevicePackageTargetProcessor implements TargetProcessor {

    @Inject
    DevicePackageManagementService devicePackageManagementService;
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

        if (JobTargetStatus.AWAITING_COMPLETION.equals(jobTarget.getStatus()) || JobTargetStatus.NOTIFIED_COMPLETION.equals(jobTarget.getStatus())) {
            return;
        }

        KapuaId scopeId = jobTarget.getScopeId();
        KapuaId jobId = jobTarget.getJobId();
        // Extract parameters from context
        DevicePackageDownloadRequest packageDownloadRequest = stepContextWrapper.getStepProperty(DevicePackageDownloadPropertyKeys.PACKAGE_DOWNLOAD_REQUEST, DevicePackageDownloadRequest.class);
        Long timeout = stepContextWrapper.getStepProperty(DevicePackageDownloadPropertyKeys.TIMEOUT, Long.class);
        // Send the request
        DevicePackageDownloadOptions packageDownloadOptions = new DevicePackageDownloadOptions();
        packageDownloadOptions.setTimeout(timeout);

        KapuaId operationId = KapuaSecurityUtils.doPrivileged(() -> devicePackageManagementService.downloadExec(scopeId, jobTarget.getJobTargetId(), packageDownloadRequest, packageDownloadOptions));
        // Save the jobId-deviceManagementOperationId pair to track resuming
        createJobDeviceManagementOperation(scopeId, jobId, jobTarget, operationId);
    }

    @Override
    protected JobTargetStatus getCompletedStatus(JobTarget jobTarget) {
        if (JobTargetStatus.NOTIFIED_COMPLETION.equals(jobTarget.getStatus())) {
            return super.getCompletedStatus(jobTarget);
        }

        return JobTargetStatus.AWAITING_COMPLETION;
    }
}
