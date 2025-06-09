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
package org.eclipse.kapua.service.device.management.message.notification;

import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * Notify {@link KapuaPayload} definition.
 *
 * @since 1.0.0
 */
public class KapuaNotifyPayload extends KapuaPayload {

    private KapuaId operationId;
    private String resource;
    private NotifyStatus status;
    private Integer progress;
    private String message;

    /**
     * Gets the operation {@link KapuaId}.
     *
     * @return The operation {@link KapuaId}.
     * @since 1.0.0
     */
    public KapuaId getOperationId() {
        return operationId;
    }

    /**
     * Sets the operation {@link KapuaId}.
     *
     * @param operationId
     *         The operation {@link KapuaId}.
     * @since 1.0.0
     */
    public void setOperationId(KapuaId operationId) {
        this.operationId = operationId;
    }

    /**
     * Gets the resource.
     *
     * @return The resource.
     * @since 1.0.0
     * @deprecated Since 1.2.0. Please make use of {@link KapuaNotifyChannel#getResources()}
     */
    @Deprecated
    public String getResource() {
        return resource;
    }

    /**
     * Sets the resource.
     *
     * @param resource
     *         The resource.
     * @since 1.0.0
     * @deprecated Since 1.2.0. Please make use of {@link KapuaNotifyChannel#setResources(String[])}
     */
    @Deprecated
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Gets the {@link NotifyStatus}.
     *
     * @return The {@link NotifyStatus}.
     * @since 1.0.0
     */
    public NotifyStatus getStatus() {
        return status;
    }

    /**
     * Sets the {@link NotifyStatus}.
     *
     * @param status
     *         The {@link NotifyStatus}.
     * @since 1.0.0
     */
    public void setStatus(NotifyStatus status) {
        this.status = status;
    }

    /**
     * Gets the operation progress.
     *
     * @return The operation progress.
     * @since 1.0.0
     */
    public Integer getProgress() {
        return progress;
    }

    /**
     * Sets the operation progress.
     *
     * @param progress
     *         The operation progress.
     * @since 1.0.0
     */
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    /**
     * Gets the message.
     *
     * @return The message.
     * @since 1.2.0
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the message.
     *
     * @param message
     *         The message.
     * @since 1.2.0
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
