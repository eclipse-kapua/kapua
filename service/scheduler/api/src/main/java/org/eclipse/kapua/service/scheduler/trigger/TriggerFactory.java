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
package org.eclipse.kapua.service.scheduler.trigger;

import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;

/**
 * {@link Trigger} {@link KapuaEntityFactory} definition.
 *
 * @see KapuaEntityFactory
 * @since 1.0.0
 */
public interface TriggerFactory extends KapuaEntityFactory<Trigger, TriggerCreator, TriggerQuery> {

    /**
     * Instantiates a new {@link TriggerProperty}
     *
     * @param name
     *         The name to set into the {@link TriggerProperty}.
     * @param type
     *         The type to set into the {@link TriggerProperty}.
     * @param value
     *         The value to set into the {@link TriggerProperty}.
     * @return The newly instatiated {@link TriggerProperty}.
     * @since 1.0.0
     */
    TriggerProperty newTriggerProperty(String name, String type, String value);
}
