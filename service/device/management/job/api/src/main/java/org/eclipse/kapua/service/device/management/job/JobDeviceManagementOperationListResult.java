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
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.query.KapuaListResult;

/**
 * {@link JobDeviceManagementOperationListResult} definition.
 *
 * @since 1.1.0
 */
@XmlRootElement(name = "jobDeviceManagementOperationListResult")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobDeviceManagementOperationListResult extends KapuaListResult<JobDeviceManagementOperation> {

}
