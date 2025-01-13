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
package org.eclipse.kapua.service.scheduler.trigger.fired;

import org.eclipse.kapua.model.KapuaEntityFactory;

/**
 * {@link FiredTrigger} {@link KapuaEntityFactory} definition.
 *
 * @see KapuaEntityFactory
 * @since 1.5.0
 */
public interface FiredTriggerFactory extends KapuaEntityFactory<FiredTrigger, FiredTriggerCreator, FiredTriggerQuery> {

}
