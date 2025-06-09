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
package org.eclipse.kapua.service.device.management.keystore.job;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.job.engine.commons.operation.AbstractDeviceTargetProcessor;
import org.eclipse.kapua.job.engine.commons.wrappers.JobTargetWrapper;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.keystore.DeviceKeystoreManagementService;
import org.eclipse.kapua.service.device.management.keystore.job.definition.DeviceKeystoreKeypairCreatePropertyKeys;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCertificate;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreKeypair;
import org.eclipse.kapua.service.job.operation.TargetProcessor;
import org.eclipse.kapua.service.job.targets.JobTarget;

/**
 * {@link TargetProcessor} for {@link DeviceKeystoreManagementService#createKeystoreCertificate(KapuaId, KapuaId, DeviceKeystoreCertificate, Long)}.
 *
 * @since 1.0.0
 */
public class DeviceKeystoreKeypairCreateTargetProcessor extends AbstractDeviceTargetProcessor implements TargetProcessor {

    @Inject
    DeviceKeystoreManagementService deviceKeystoreManagementService;
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

        String keystoreId = stepContextWrapper.getStepProperty(DeviceKeystoreKeypairCreatePropertyKeys.KEYSTORE_ID, String.class);
        String alias = stepContextWrapper.getStepProperty(DeviceKeystoreKeypairCreatePropertyKeys.ALIAS, String.class);
        Integer size = stepContextWrapper.getStepProperty(DeviceKeystoreKeypairCreatePropertyKeys.SIZE, Integer.class);
        String algorithm = stepContextWrapper.getStepProperty(DeviceKeystoreKeypairCreatePropertyKeys.ALGORITHM, String.class);
        String signatureAlgorithm = stepContextWrapper.getStepProperty(DeviceKeystoreKeypairCreatePropertyKeys.SIGNATURE_ALGORITHM, String.class);
        String attributes = stepContextWrapper.getStepProperty(DeviceKeystoreKeypairCreatePropertyKeys.ATTRIBUTES, String.class);
        Long timeout = stepContextWrapper.getStepProperty(DeviceKeystoreKeypairCreatePropertyKeys.TIMEOUT, Long.class);

        DeviceKeystoreKeypair deviceKeystoreKeypair = new DeviceKeystoreKeypair();

        deviceKeystoreKeypair.setKeystoreId(keystoreId);
        deviceKeystoreKeypair.setAlias(alias);
        deviceKeystoreKeypair.setSize(size);
        deviceKeystoreKeypair.setAlgorithm(algorithm);
        deviceKeystoreKeypair.setSignatureAlgorithm(signatureAlgorithm);
        deviceKeystoreKeypair.setAttributes(attributes);

        KapuaSecurityUtils.doPrivileged(() -> deviceKeystoreManagementService.createKeystoreKeypair(jobTarget.getScopeId(), jobTarget.getJobTargetId(), deviceKeystoreKeypair, timeout));
    }
}
