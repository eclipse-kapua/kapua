/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.systeminfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link SystemInfo} {@link org.eclipse.kapua.model.KapuaEntity} definition
 * <p>
 * {@link SystemInfo}s represent the system info.
 *
 * @since 2.0.0
 */
@XmlRootElement(name = "systemInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = SystemInfoXmlRegistry.class, factoryMethod = "newSystemInfo")
public interface SystemInfo {
    @XmlElement(name = "version")
    @Schema(example = "2.0.0")
    String getVersion();

    void setVersion(String version);

    @XmlElement(name = "revision")
    @Schema(example = "e53c7b7d4208204a0791ec296fa3d4dbe2344585")
    String getRevision();

    void setRevision(String revision);

    @XmlElement(name = "buildDate")
    String getBuildTimestamp();

    void setBuildTimestamp(String buildDate);

    @XmlElement(name = "buildBranch")
    @Schema(example = "release/2.0.0")
    String getBuildBranch();

    void setBuildBranch(String buildBranch);

    @XmlElement(name = "buildNumber")
    @Schema(example = "142")
    String getBuildNumber();

    void setBuildNumber(String buildNumber);
}
