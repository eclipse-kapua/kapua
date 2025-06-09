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
package org.eclipse.kapua.service.device.management.keystore.internal.message.response;

import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.message.response.KapuaResponseMessage;

/**
 * {@link DeviceKeystore} {@link KapuaResponseMessage} implementation.
 *
 * @since 1.5.0
 */
public class KeystoreResponseMessage extends KapuaResponseMessage<KeystoreResponseChannel, KeystoreResponsePayload> {

    private static final long serialVersionUID = -1398713049819248123L;
}
