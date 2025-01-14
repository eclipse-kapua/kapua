/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.scheduler.trigger.definition;

import org.eclipse.kapua.model.KapuaEntityFactory;

/**
 * {@link TriggerDefinition} {@link KapuaEntityFactory }definition.
 *
 * @see KapuaEntityFactory
 * @since 1.1.0
 */
public interface TriggerDefinitionFactory extends KapuaEntityFactory<TriggerDefinition, TriggerDefinitionCreator> {

    /**
     * Instantiates a new {@link TriggerProperty}.
     *
     * @param name
     *         The name to set into the {@link TriggerProperty}.
     * @param type
     *         The type to set into the {@link TriggerProperty}.
     * @param value
     *         The value to set into the {@link TriggerProperty}.
     * @return The newly instantiated {@link TriggerProperty}.
     * @since 1.1.0
     */
    TriggerProperty newTriggerProperty(String name, String type, String value);

}
