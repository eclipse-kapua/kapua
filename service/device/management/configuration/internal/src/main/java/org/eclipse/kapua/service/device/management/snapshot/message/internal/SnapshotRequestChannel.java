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
package org.eclipse.kapua.service.device.management.snapshot.message.internal;

import org.eclipse.kapua.service.device.management.message.request.KapuaRequestChannel;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshot;

/**
 * {@link DeviceSnapshot} {@link KapuaRequestChannel} implementation.
 *
 * @since 1.0.0
 */
public class SnapshotRequestChannel extends KapuaRequestChannel {

    private static final long serialVersionUID = 7526183410364623630L;

    private String snapshotId;

    /**
     * Gets the {@link DeviceSnapshot#getId()}.
     *
     * @return The {@link DeviceSnapshot#getId()}.
     * @since 1.0.0
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    /**
     * Sets the {@link DeviceSnapshot#getId()}.
     *
     * @param snapshotId
     *         The {@link DeviceSnapshot#getId()}.
     * @since 1.0.0
     */
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }
}
