/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.rest.model.errors;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "throwableInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThrowableInfo {
    @XmlElement(name = "httpErrorCode")
    private int httpErrorCode;

    @XmlElement(name = "message")
    private String message;

    @XmlElement(name = "stackTrace")
    private String stackTrace;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public ThrowableInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param httpStatusCode The http status code of the response containing this info
     * @param throwable      The cause of the error.
     * @since 1.0.0
     */
    public ThrowableInfo(int httpStatusCode, Throwable throwable, boolean showStackTrace) {
        this.httpErrorCode = httpStatusCode;

        if (throwable != null) {
            this.message = throwable.getMessage();

            // Print stack trace
            if (showStackTrace) {
                StringWriter stringWriter = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stringWriter));
                this.stackTrace = stringWriter.toString();
            }
        }
    }

    /**
     * Gets the http status code of the response containing this info.
     *
     * @return The http status code of the response containing this info.
     * @since 1.0.0
     */
    @Schema(
        description = "The http error code for this response",
        example = "400"
    )
    public int getHttpErrorCode() {
        return httpErrorCode;
    }

    /**
     * Sets the http status code of the response containing this info.
     *
     * @param httpStatusCode the http status code of the response containing this info.
     * @since 1.0.0
     */
    public void setHttpErrorCode(int httpStatusCode) {
        this.httpErrorCode = httpStatusCode;
    }

    /**
     * Gets the {@link Throwable#getMessage()}.
     *
     * @return The {@link Throwable#getMessage()}.
     * @since 1.0.0
     */
    @Schema(
        description = "An extended description of the error that occurred when performing the operation",
        example = "An illegal value was provided for the argument user.email: thisIsNotAnEmailAtAll"
    )
    public String getMessage() {
        return message;
    }

    /**
     * Sets the {@link Throwable#getMessage()}.
     *
     * @param message The {@link Throwable#getMessage()}.
     * @since 1.0.0
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the {@link Throwable#getStackTrace()} if {@code api.exception.stacktrace.show} is {@code true}.
     *
     * @return The {@link Throwable#getStackTrace()} if {@code api.exception.stacktrace.show} is {@code true}.
     * @since 1.0.0
     */
    public String getStackTrace() {
        return stackTrace;
    }
}
