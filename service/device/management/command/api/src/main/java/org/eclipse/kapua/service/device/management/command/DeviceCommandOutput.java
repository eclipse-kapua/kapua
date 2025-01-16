/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Device command output entity definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "commandOutput")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "stderr",
        "stdout",
        "exceptionMessage",
        "exceptionStack",
        "exitCode",
        "hasTimedout"
}, factoryClass = DeviceCommandXmlRegistry.class, factoryMethod = "newCommandOutput")
@Schema(description = "The output of a Command executed on a Device")
public interface DeviceCommandOutput extends DeviceCommand {

    /**
     * Get the standard error
     *
     * @return
     */
    @XmlElement(name = "stderr")
    @Schema(example = "")
    String getStderr();

    /**
     * Set the standard error
     *
     * @param stderr
     */
    void setStderr(String stderr);

    /**
     * Get the standard output
     *
     * @return
     */
    @XmlElement(name = "stdout")
    @Schema(example = "total 40\n12503707 4 drwxrwxrwt 1 root root 4096 Sep 13 07:44 .\n12507178 4 drwxr-xr-x 1 root root 4096 Sep 12 08:03 ..\n61409843 4 drwxrwxrwt 2 root root 4096 Feb 28  2018 .ICE-unix\n61409845 4 drwxrwxrwt 2 root root 4096 Feb 28  2018 .Test-unix\n61409838 4 drwxrwxrwt 2 root root 4096 Feb 28  2018 .X11-unix\n61409844 4 drwxrwxrwt 2 root root 4096 Feb 28  2018 .XIM-unix\n61409846 4 drwxrwxrwt 2 root root 4096 Feb 28  2018 .font-unix\n12503708 4 drwxr-xr-x 3 root root 4096 Sep 12 08:03 .kura\n12507353 4 drwxr-xr-x 2 root root 4096 Sep 12 08:03 hsperfdata_root\n12507385 4 drwxr-xr-x 3 root root 4096 Sep 12 08:03 kura\n")
    String getStdout();

    /**
     * Set the standard output
     *
     * @param stdout
     */
    void setStdout(String stdout);

    /**
     * Get the command execution exception message
     *
     * @return
     */
    @XmlElement(name = "exceptionMessage")
    String getExceptionMessage();

    /**
     * Set the command execution exception message
     *
     * @param exceptionMessage
     */
    void setExceptionMessage(String exceptionMessage);

    /**
     * Get the command execution exception stack
     *
     * @return
     */
    @XmlElement(name = "exceptionStack")
    String getExceptionStack();

    /**
     * Set the command execution exception stack
     *
     * @param exceptionStack
     */
    void setExceptionStack(String exceptionStack);

    /**
     * Get the command execution exit code
     *
     * @return
     */
    @XmlElement(name = "exitCode")
    Integer getExitCode();

    /**
     * Set the command execution exit code
     *
     * @param exitCode
     */
    void setExitCode(Integer exitCode);

    /**
     * Get the command execution timed out flag
     *
     * @return
     */
    @XmlElement(name = "hasTimedout")
    Boolean getHasTimedout();

    /**
     * Set the command execution timed out flag
     *
     * @param hasTimedout
     */
    void setHasTimedout(Boolean hasTimedout);
}
