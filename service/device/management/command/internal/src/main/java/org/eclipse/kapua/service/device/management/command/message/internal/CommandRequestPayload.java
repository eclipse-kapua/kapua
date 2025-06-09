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
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.command.message.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.service.device.management.command.DeviceCommand;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.internal.CommandAppProperties;
import org.eclipse.kapua.service.device.management.message.request.KapuaRequestPayload;

/**
 * {@link DeviceCommand} {@link KapuaRequestPayload} implementation.
 *
 * @since 1.0.0
 */
public class CommandRequestPayload extends KapuaRequestPayload {

    private static final long serialVersionUID = 2272153023534866045L;

    /**
     * Gets the {@link DeviceCommandInput#getArguments()}.
     *
     * @return The {@link DeviceCommandInput#getArguments()}.
     * @since 1.0.0
     */
    public String[] getArguments() {
        List<String> argumentsList = new ArrayList<>();

        for (int i = 0; ; i++) {
            String value = (String) getMetrics().get(CommandAppProperties.APP_PROPERTY_ARG.getValue() + i);
            if (value != null) {
                argumentsList.add(value);
            } else {
                break;
            }
        }

        return argumentsList.toArray(new String[0]);
    }

    /**
     * Sets the {@link DeviceCommandInput#getArguments()}.
     *
     * @param arguments
     *         The {@link DeviceCommandInput#getArguments()}.
     * @since 1.0.0
     */
    public void setArguments(String[] arguments) {
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                getMetrics().put(CommandAppProperties.APP_PROPERTY_ARG.getValue() + i, arguments[i]);
            }
        }
    }

    /**
     * Gets the {@link DeviceCommandInput#getEnvironment()}.
     *
     * @return The {@link DeviceCommandInput#getEnvironment()}.
     * @since 1.0.0
     */
    public String[] getEnvironmentPairs() {
        List<String> v = new ArrayList<>();

        for (int i = 0; ; i++) {
            String value = (String) getMetrics().get(CommandAppProperties.APP_PROPERTY_ENVP.getValue() + i);
            if (value != null) {
                v.add(value);
            } else {
                break;
            }
        }

        return v.toArray(new String[0]);
    }

    /**
     * Sets the {@link DeviceCommandInput#getEnvironment()}.
     *
     * @param environmentPairs
     *         The {@link DeviceCommandInput#getEnvironment()}.
     * @since 1.0.0
     */
    public void setEnvironmentPairs(String[] environmentPairs) {
        if (environmentPairs != null) {
            for (int i = 0; i < environmentPairs.length; i++) {
                getMetrics().put(CommandAppProperties.APP_PROPERTY_ENVP.getValue() + i, environmentPairs[i]);
            }
        }
    }

    /**
     * Gets the {@link DeviceCommandInput#getWorkingDir()}.
     *
     * @return The {@link DeviceCommandInput#getWorkingDir()}.
     * @since 1.0.0
     */
    public String getWorkingDir() {
        return (String) getMetrics().get(CommandAppProperties.APP_PROPERTY_DIR.getValue());
    }

    /**
     * Sets the {@link DeviceCommandInput#getWorkingDir()}.
     *
     * @param workingDir
     *         The {@link DeviceCommandInput#getWorkingDir()}.
     * @since 1.0.0
     */
    public void setWorkingDir(String workingDir) {
        if (workingDir != null) {
            getMetrics().put(CommandAppProperties.APP_PROPERTY_DIR.getValue(), workingDir);
        }
    }

    /**
     * Gets the {@link DeviceCommandInput#getStdin()}.
     *
     * @return The {@link DeviceCommandInput#getStdin()}.
     * @since 1.0.0
     */
    public String getStdin() {
        return (String) getMetrics().get(CommandAppProperties.APP_PROPERTY_STDIN.getValue());
    }

    /**
     * Sets the {@link DeviceCommandInput#getStdin()}.
     *
     * @param stdin
     *         The {@link DeviceCommandInput#getStdin()}.
     * @since 1.0.0
     */
    public void setStdin(String stdin) {
        if (stdin != null) {
            getMetrics().put(CommandAppProperties.APP_PROPERTY_STDIN.getValue(), stdin);
        }
    }

    /**
     * Gets the {@link DeviceCommandInput#getTimeout()}.
     *
     * @return The {@link DeviceCommandInput#getTimeout()}.
     * @since 1.0.0
     */
    public Integer getTimeout() {
        return (Integer) getMetrics().get(CommandAppProperties.APP_PROPERTY_TOUT.getValue());
    }

    /**
     * Sets the {@link DeviceCommandInput#getTimeout()}.
     *
     * @param timeout
     *         The {@link DeviceCommandInput#getTimeout()}.
     * @since 1.0.0
     */
    public void setTimeout(int timeout) {
        getMetrics().put(CommandAppProperties.APP_PROPERTY_TOUT.getValue(), timeout);
    }

    /**
     * Gets the {@link DeviceCommandInput#isRunAsynch()}.
     *
     * @return The {@link DeviceCommandInput#isRunAsynch()}.
     */
    public Boolean isRunAsync() {
        return (Boolean) getMetrics().get(CommandAppProperties.APP_PROPERTY_ASYNC.getValue());
    }

    /**
     * Sets the {@link DeviceCommandInput#isRunAsynch()}.
     *
     * @param runAsync
     *         The {@link DeviceCommandInput#isRunAsynch()}.
     * @since 1.0.0
     */
    public void setRunAsync(boolean runAsync) {
        getMetrics().put(CommandAppProperties.APP_PROPERTY_ASYNC.getValue(), runAsync);
    }

    /**
     * Gets the {@link DeviceCommandInput#getCommand()}.
     *
     * @return The {@link DeviceCommandInput#getCommand()}.
     * @since 1.0.0
     */
    public String getCommand() {
        return (String) getMetrics().get(CommandAppProperties.APP_PROPERTY_CMD.getValue());
    }

    /**
     * Sets the {@link DeviceCommandInput#getCommand()}.
     *
     * @param command
     *         The {@link DeviceCommandInput#getCommand()}.
     * @since 1.0.0
     */
    public void setCommand(String command) {
        getMetrics().put(CommandAppProperties.APP_PROPERTY_CMD.getValue(), command);
    }

    /**
     * Gets the {@link DeviceCommandInput#getPassword()}.
     *
     * @return The {@link DeviceCommandInput#getPassword()}.
     * @since 1.0.0
     */
    public String getPassword() {
        return (String) getMetrics().get(CommandAppProperties.APP_PROPERTY_PASSWORD.getValue());
    }

    /**
     * Sets the {@link DeviceCommandInput#getPassword()}.
     *
     * @param password
     *         The {@link DeviceCommandInput#getPassword()}.
     * @since 1.0.0
     */
    public void setPassword(String password) {
        getMetrics().put(CommandAppProperties.APP_PROPERTY_PASSWORD.getValue(), password);
    }
}
