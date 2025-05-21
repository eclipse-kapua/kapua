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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Device command input definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "commandInput")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "command",
        "password",
        "arguments",
        "timeout",
        "workingDir",
        "body",
        "environment",
        "environments",
        "runAsynch",
        "stdin"
})
public class DeviceCommandInput implements DeviceCommand {

    private static final long serialVersionUID = -2141178091281947848L;

    private String command;
    private String password;
    private String[] arguments;
    private Integer timeout;
    private String workingDir;
    private byte[] body;
    private String[] envVars;
    private boolean runAsync;
    private String stdIn;

    /**
     * Get the device command
     *
     * @return
     */
    @XmlElement(name = "command")
    public String getCommand() {
        return command;
    }

    /**
     * Set the device command
     *
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Get the device password
     *
     * @return
     */
    @XmlElement(name = "password")
    public String getPassword() {
        return password;
    }

    /**
     * Set the device password
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get command arguments
     *
     * @return
     */
    @XmlElementWrapper(name = "arguments")
    @XmlElement(name = "argument")
    public String[] getArguments() {
        return arguments;
    }

    /**
     * Set command arguments
     *
     * @param arguments
     */
    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    /**
     * Get the command timeout
     *
     * @return
     */
    @XmlElement(name = "timeout")
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Set the command timeout
     *
     * @param timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * Get the working directory
     *
     * @return
     */
    @XmlElement(name = "workingDir")
    public String getWorkingDir() {
        return workingDir;
    }

    /**
     * Set the working directory
     *
     * @param workingDir
     */
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    /**
     * Get the command input body
     *
     * @return
     */
    @XmlElement(name = "body")
    public byte[] getBody() {
        return body;
    }

    /**
     * Set the command input body
     *
     * @param bytes
     */
    public void setBody(byte[] bytes) {
        this.body = bytes;
    }

    /**
     * Get the environment attributes
     *
     * @return
     * @deprecated Since 2.1.0
     */
    @Deprecated
    @XmlElement(name = "environment")
    public String[] getEnvironment() {
        return envVars;
    }

    /**
     * Set the environment attributes
     *
     * @param environment
     * @deprecated Since 2.1.0
     */
    @Deprecated
    public void setEnvironment(String[] environment) {
        this.envVars = environment;
    }

    /**
     * Get the asynchronous run flag
     *
     * @return
     */
    @XmlElement(name = "runAsynch")
    public boolean isRunAsynch() {
        return runAsync;
    }

    /**
     * Set the asynchronous run flag
     *
     * @param runAsync
     */
    public void setRunAsynch(boolean runAsync) {
        this.runAsync = runAsync;
    }

    /**
     * Get the device standard input
     *
     * @return
     */
    @XmlElement(name = "stdin")
    public String getStdin() {
        return stdIn;
    }

    /**
     * Set the device standard input
     *
     * @param stdin
     */
    public void setStdin(String stdin) {
        this.stdIn = stdin;
    }

}
