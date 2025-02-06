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
})
public class DeviceCommandOutput implements DeviceCommand {

    private static final long serialVersionUID = -3987291078035621467L;

    private String stdErr;
    private String stdOut;
    private String exceptionMessage;
    private String exceptionStack;
    private Integer exitCode;
    private Boolean timedOut;

    /**
     * Get the standard error
     *
     * @return
     */
    @XmlElement(name = "stderr")
    public String getStderr() {
        return stdErr;
    }

    /**
     * Set the standard error
     *
     * @param stderr
     */
    public void setStderr(String stderr) {
        this.stdErr = stderr;
    }

    /**
     * Get the standard output
     *
     * @return
     */
    @XmlElement(name = "stdout")
    public String getStdout() {
        return stdOut;
    }

    /**
     * Set the standard output
     *
     * @param stdout
     */
    public void setStdout(String stdout) {
        this.stdOut = stdout;
    }

    /**
     * Get the command execution exception message
     *
     * @return
     */
    @XmlElement(name = "exceptionMessage")
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    /**
     * Set the command execution exception message
     *
     * @param exceptionMessage
     */
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * Get the command execution exception stack
     *
     * @return
     */
    @XmlElement(name = "exceptionStack")
    public String getExceptionStack() {
        return exceptionStack;
    }

    /**
     * Set the command execution exception stack
     *
     * @param exceptionStack
     */
    public void setExceptionStack(String exceptionStack) {
        this.exceptionStack = exceptionStack;
    }

    /**
     * Get the command execution exit code
     *
     * @return
     */
    @XmlElement(name = "exitCode")
    public Integer getExitCode() {
        return exitCode;
    }

    /**
     * Set the command execution exit code
     *
     * @param exitCode
     */
    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Get the command execution timed out flag
     *
     * @return
     */
    @XmlElement(name = "hasTimedout")
    public Boolean getHasTimedout() {
        return timedOut;
    }

    /**
     * Set the command execution timed out flag
     *
     * @param hasTimedout
     */
    public void setHasTimedout(Boolean hasTimedout) {
        this.timedOut = hasTimedout;
    }

}
