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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.device.management.snapshot.message.internal;

import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshot;

/**
 * {@link DeviceSnapshot} {@link KapuaResponseMessage} implementation.
 *
 * @since 1.0.0
 */
public class SnapshotResponseMessage extends KapuaResponseMessage<SnapshotResponseChannel, SnapshotResponsePayload> {

    private static final long serialVersionUID = 4622573069731164789L;
}
