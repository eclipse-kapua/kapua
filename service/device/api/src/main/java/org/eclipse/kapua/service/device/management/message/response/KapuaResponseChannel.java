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

import org.eclipse.kapua.service.device.management.message.KapuaAppChannel;

/**
 * Response {@link KapuaAppChannel}.
 * <p>
 * This object defines the {@link KapuaAppChannel} for a {@link KapuaResponseMessage}.<br> The response message is used to perform interactive operations with the device (e.g. to send command to the
 * device, to ask configurations...)
 *
 * @since 1.0.0
 */
public class KapuaResponseChannel extends KapuaAppChannel {

    private static final long serialVersionUID = -3127210628323426502L;

}
