/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.jersey.rest.model.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementResponseCodeException;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseCode;

@XmlRootElement(name = "deviceManagementResponseCodeExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceManagementResponseCodeExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "responseCode")
    private KapuaResponseCode responseCode;

    @XmlElement(name = "errorMessage")
    private String errorMessage;

    @XmlElement(name = "stacktrace")
    private String stacktrace;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected DeviceManagementResponseCodeExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param deviceManagementResponseCodeException
     *         The root exception.
     * @since 1.0.0
     */
    public DeviceManagementResponseCodeExceptionInfo(DeviceManagementResponseCodeException deviceManagementResponseCodeException, boolean showStackTrace) {
        this(500, deviceManagementResponseCodeException, showStackTrace);
    }

    /**
     * Constructor with a custom http status code
     *
     * @param httpStatusCode the http status code we want to set in the response
     * @param deviceManagementResponseCodeException The root exception.
     * @since 2.0.0
     */
    public DeviceManagementResponseCodeExceptionInfo(int httpStatusCode, DeviceManagementResponseCodeException deviceManagementResponseCodeException, boolean showStackTrace) {
        super(httpStatusCode, deviceManagementResponseCodeException, showStackTrace);

        this.responseCode = deviceManagementResponseCodeException.getResponseCode();
        this.errorMessage = deviceManagementResponseCodeException.getErrorMessage();
        this.stacktrace = deviceManagementResponseCodeException.getStacktrace();

    }

    /**
     * Gets the {@link DeviceManagementResponseCodeException#getResponseCode()}.
     *
     * @return The {@link DeviceManagementResponseCodeException#getResponseCode()}.
     * @since 1.0.0
     */
    public KapuaResponseCode getResponseCode() {
        return responseCode;
    }

    /**
     * Gets the {@link DeviceManagementResponseCodeException#getErrorMessage()}.
     *
     * @return The {@link DeviceManagementResponseCodeException#getErrorMessage()}.
     * @since 1.0.0
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the {@link DeviceManagementResponseCodeException#getStacktrace()}.
     *
     * @return The {@link DeviceManagementResponseCodeException#getStacktrace()}.
     * @since 1.0.0
     */
    public String getStacktrace() {
        return stacktrace;
    }
}
