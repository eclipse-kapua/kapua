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

/**
 * {@link SystemInfo} {@link org.eclipse.kapua.model.KapuaEntity} definition
 * <p>
 * {@link SystemInfo}s represent the system info.
 *
 * @since 2.0.0
 */
@XmlRootElement(name = "systemInfo")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class SystemInfo {

    private String version;
    private String buildNumber;
    private String buildDate;
    private String buildBranch;
    private String buildRevision;

    @XmlElement(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlElement(name = "revision")
    public String getRevision() {
        return buildRevision;
    }

    public void setRevision(String revision) {
        this.buildRevision = revision;
    }

    @XmlElement(name = "buildDate")
    public String getBuildTimestamp() {
        return buildDate;
    }

    public void setBuildTimestamp(String buildDate) {
        this.buildDate = buildDate;
    }

    @XmlElement(name = "buildBranch")
    public String getBuildBranch() {
        return buildBranch;
    }

    public void setBuildBranch(String buildBranch) {
        this.buildBranch = buildBranch;
    }

    @XmlElement(name = "buildNumber")
    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

}
