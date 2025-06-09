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
package org.eclipse.kapua.service.device.management.message.response;

import org.eclipse.kapua.message.KapuaPayload;

/**
 * Response {@link KapuaPayload} definition.
 * <p>
 * This object defines the {@link KapuaPayload} for a {@link KapuaResponseMessage}.<br> The response message is used to perform interactive operations with the device (e.g. to send command to the
 * device, to ask configurations...)
 *
 * @since 1.0.0
 */
public class KapuaResponsePayload extends KapuaPayload {

    /**
     * Gets the exception message (if present).
     *
     * @return The exception message.
     * @since 1.0.0
     */
    public String getExceptionMessage() {
        return (String) getMetrics().get(ResponseProperties.RESP_PROPERTY_EXCEPTION_MESSAGE.getValue());
    }

    /**
     * Sets the exception message.
     *
     * @param exceptionMessage
     *         The exception message.
     * @since 1.0.0
     */
    public void setExceptionMessage(String exceptionMessage) {
        getMetrics().put(ResponseProperties.RESP_PROPERTY_EXCEPTION_MESSAGE.getValue(), exceptionMessage);
    }

    /**
     * Gets the exception stack trace (if present).
     *
     * @return The exception stack trace (if present).
     * @since 1.0.0
     */
    public String getExceptionStack() {
        return (String) getMetrics().get(ResponseProperties.RESP_PROPERTY_EXCEPTION_STACK.getValue());
    }

    /**
     * Sets the exception stack trace.
     *
     * @param setExecptionStack
     *         The exception stack trace.
     * @since 1.0.0
     */
    public void setExceptionStack(String setExecptionStack) {
        getMetrics().put(ResponseProperties.RESP_PROPERTY_EXCEPTION_STACK.getValue(), setExecptionStack);
    }

}
