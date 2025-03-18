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
 * Device command input entity definition.
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
}, factoryClass = DeviceCommandXmlRegistry.class, factoryMethod = "newCommandInput")
public interface DeviceCommandInput extends DeviceCommand {

    /**
     * Get the device command
     *
     * @return
     */
    @XmlElement(name = "command")
    String getCommand();

    /**
     * Set the device command
     *
     * @param command
     */
    void setCommand(String command);

    /**
     * Get the device password
     *
     * @return
     */
    @XmlElement(name = "password")
    String getPassword();

    /**
     * Set the device password
     *
     * @param password
     */
    void setPassword(String password);

    /**
     * Get command arguments
     *
     * @return
     */
    @XmlElementWrapper(name = "arguments")
    @XmlElement(name = "argument")
    String[] getArguments();

    /**
     * Set command arguments
     *
     * @param arguments
     */
    void setArguments(String[] arguments);

    /**
     * Get the command timeout
     *
     * @return
     */
    @XmlElement(name = "timeout")
    Integer getTimeout();

    /**
     * Set the command timeout
     *
     * @param timeout
     */
    void setTimeout(Integer timeout);

    /**
     * Get the working directory
     *
     * @return
     */
    @XmlElement(name = "workingDir")
    String getWorkingDir();

    /**
     * Set the working directory
     *
     * @param workingDir
     */
    void setWorkingDir(String workingDir);

    /**
     * Get the command input body
     *
     * @return
     */
    @XmlElement(name = "body")
    byte[] getBody();

    /**
     * Set the command input body
     *
     * @param bytes
     */
    void setBody(byte[] bytes);

    /**
     * Get the environment attributes
     *
     * @return
     * @deprecated Since 2.1.0
     */
    @Deprecated
    @XmlElement(name = "environment")
    String[] getEnvironment();

    /**
     * Get the environment attributes
     *
     * @return
     * @since 2.1.0
     */
    @XmlElementWrapper(name = "environments")
    @XmlElement(name = "environment")
    String[] getEnvironments();

    /**
     * Set the environment attributes
     *
     * @param environment
     * @deprecated Since 2.1.0
     */
    @Deprecated
    void setEnvironment(String[] environment);

    /**
     * Set the environment attributes
     *
     * @param environment
     * @since 2.1.0
     */
    void setEnvironments(String[] environment);

    /**
     * Get the asynchronous run flag
     *
     * @return
     */
    @XmlElement(name = "runAsynch")
    boolean isRunAsynch();

    /**
     * Set the asynchronous run flag
     *
     * @param runAsync
     */
    void setRunAsynch(boolean runAsync);

    /**
     * Get the device standard input
     *
     * @return
     */
    @XmlElement(name = "stdin")
    String getStdin();

    /**
     * Set the device standard input
     *
     * @param stdin
     */
    void setStdin(String stdin);
}
