/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.registry.operation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "deviceManagementOperationProperty")
public interface DeviceManagementOperationProperty {

    @XmlElement(name = "name")
    @Schema(example = "kapua.package.download.file.type")
    String getName();

    void setName(String name);

    @XmlElement(name = "propertyType")
    @Schema(example = "string")
    String getPropertyType();

    void setPropertyType(String propertyType);

    @XmlElement(name = "propertyValue")
    @Schema(example = "DEPLOYMENT_PACKAGE")
    String getPropertyValue();

    void setPropertyValue(String propertyValue);
}
