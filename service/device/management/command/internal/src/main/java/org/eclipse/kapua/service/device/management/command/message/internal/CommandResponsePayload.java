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
package org.eclipse.kapua.service.device.management.command.message.internal;

import org.eclipse.kapua.service.device.management.command.DeviceCommand;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponsePayload;

/**
 * {@link DeviceCommand} {@link KapuaResponsePayload} implementation.
 *
 * @since 1.0.0
 */
public class CommandResponsePayload extends KapuaResponsePayload {

    private static final long serialVersionUID = -7611654604657980851L;

    /**
     * Standard error application property
     *
     * @since 1.0.0
     */
    public static final String APP_PROPERTY_STDERR = "kapua.cmd.stderr";

    /**
     * Standard output application property
     *
     * @since 1.0.0
     */
    public static final String APP_PROPERTY_STDOUT = "kapua.cmd.stdout";

    /**
     * Command exit code application property
     *
     * @since 1.0.0
     */
    public static final String APP_PROPERTY_EXIT_CODE = "kapua.cmd.exit.code";

    /**
     * Command timed out application property
     *
     * @since 1.0.0
     */
    public static final String APP_PROPERTY_TIMEDOUT = "kapua.cmd.timedout";

    /**
     * Gets the command standard error.
     *
     * @return The command standard error.
     * @since 1.0.0
     */
    public String getStderr() {
        return (String) getMetrics().get(APP_PROPERTY_STDERR);
    }

    /**
     * Sets the command standard error.
     *
     * @param stderr
     *         The command standard error.
     * @since 1.0.0
     */
    public void setStderr(String stderr) {
        getMetrics().put(APP_PROPERTY_STDERR, stderr);
    }

    /**
     * Gets the command standard output.
     *
     * @return The command standard output.
     * @since 1.0.0
     */
    public String getStdout() {
        return (String) getMetrics().get(APP_PROPERTY_STDOUT);
    }

    /**
     * Sets the command standard output.
     *
     * @param stdout
     *         The command standard output.
     */
    public void setStdout(String stdout) {
        getMetrics().put(APP_PROPERTY_STDOUT, stdout);
    }

    /**
     * Get the command exit code
     *
     * @return
     * @since 1.0.0
     */
    public Integer getExitCode() {
        return (Integer) getMetrics().get(APP_PROPERTY_EXIT_CODE);
    }

    /**
     * Get the command exit code
     *
     * @param exitCode
     * @since 1.0.0
     */
    public void setExitCode(Integer exitCode) {
        getMetrics().put(APP_PROPERTY_EXIT_CODE, exitCode);
    }

    /**
     * Get the command execution timed out flag
     *
     * @return
     * @since 1.0.0
     */
    public Boolean hasTimedout() {
        return (Boolean) getMetrics().get(APP_PROPERTY_TIMEDOUT);
    }

    /**
     * Set the command execution timed out flag
     *
     * @param timedout
     * @since 1.0.0
     */
    public void setTimedout(boolean timedout) {
        getMetrics().put(APP_PROPERTY_TIMEDOUT, timedout);
    }

}
