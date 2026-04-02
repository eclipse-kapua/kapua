/*******************************************************************************
 * Copyright (c) 2026, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.extras.migrator.device.group;

import org.eclipse.kapua.commons.model.query.KapuaListResultImpl;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceListResult;

/**
 * {@link DeviceListResult} implementation.
 *
 * @since 2.1.0
 */
public class DeviceListResultMigratorImpl extends KapuaListResultImpl<Device> implements DeviceListResult {
}
