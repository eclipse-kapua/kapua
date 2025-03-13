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
package org.eclipse.kapua.service.device.management.command.internal;

import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;

/**
 * {@link DeviceCommandInput} implementation.
 *
 * @since 1.0.0
 */
public class DeviceCommandInputImpl implements DeviceCommandInput {

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

    @Override
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String[] getArguments() {
        return arguments;
    }

    @Override
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public Integer getTimeout() {
        return timeout;
    }

    @Override
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    @Override
    public String getWorkingDir() {
        return workingDir;
    }

    @Override
    public void setBody(byte[] bytes) {
        this.body = bytes;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public void setEnvironment(String[] environment) {
        this.envVars = environment;
    }

    @Override
    public void setEnvironments(String[] environment) {
        this.envVars = environment;
    }

    @Override
    public String[] getEnvironment() {
        return envVars;
    }

    @Override
    public String[] getEnvironments() {
        return envVars;
    }

    @Override
    public void setRunAsynch(boolean runAsync) {
        this.runAsync = runAsync;
    }

    @Override
    public boolean isRunAsynch() {
        return runAsync;
    }

    @Override
    public void setStdin(String stdIn) {
        this.stdIn = stdIn;
    }

    @Override
    public String getStdin() {
        return stdIn;
    }
}
