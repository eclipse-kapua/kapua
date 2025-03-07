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
package org.eclipse.kapua.service.device.management.asset.job.definition;

import com.beust.jcommander.internal.Lists;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.asset.DeviceAssetManagementService;
import org.eclipse.kapua.service.device.management.asset.DeviceAssets;
import org.eclipse.kapua.service.device.management.asset.job.DeviceAssetWriteTargetProcessor;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepPropertyRecord;
import org.eclipse.kapua.service.job.step.definition.JobStepType;
import org.eclipse.kapua.service.job.step.definition.device.management.TimeoutJobStepPropertyRecord;

/**
 * {@link JobStepDefinition} to perform {@link DeviceAssetManagementService#write(KapuaId, KapuaId, DeviceAssets, Long)}
 *
 * @since 2.0.0
 */
public class DeviceAssetWriteJobStepDefinition extends JobStepDefinitionRecord {

    private static final long serialVersionUID = -7814283244134241606L;

    public DeviceAssetWriteJobStepDefinition() {
        super(null,
                "Asset Write",
                "Execute request to write values on a specified set of channels and assets to the target devices of the Job",
                JobStepType.TARGET,
                null,
                DeviceAssetWriteTargetProcessor.class.getName(),
                null,
                Lists.newArrayList(
                        new JobStepPropertyRecord(
                                DeviceAssetWritePropertyKeys.ASSETS,
                                "XML or JSON string that defines the asset, channels and values to be written",
                                DeviceAssets.class.getName(),
                                null,
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <deviceAssets>\n     <deviceAsset>\n         <name>assetName</name>\n         <channels>\n             <channel>\n                 <valueType>binary</valueType>\n                 <value>EGVzdCBzdHJpbmcgdmFsdWU=</value>\n                 <name>binaryTest</name>\n             </channel>\n         </channels>\n     </deviceAsset>\n</deviceAssets>",
                                Boolean.TRUE,
                                Boolean.FALSE,
                                null,
                                null,
                                null,
                                null,
                                null),
                        new TimeoutJobStepPropertyRecord()
                )
        );
    }
}
