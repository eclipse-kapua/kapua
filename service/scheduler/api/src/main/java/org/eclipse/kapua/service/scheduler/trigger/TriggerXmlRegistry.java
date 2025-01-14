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

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerProperty;

/**
 * {@link Trigger} xml factory class
 *
 * @since 1.0.0
 */
@XmlRegistry
public class TriggerXmlRegistry {

    private final TriggerFactory triggerFactory = KapuaLocator.getInstance().getFactory(TriggerFactory.class);

    public Trigger newEntity() {
        return triggerFactory.newEntity(null);
    }

    public TriggerCreator newCreator() {
        return triggerFactory.newCreator(null);
    }

    public TriggerProperty newTriggerProperty() {
        return triggerFactory.newTriggerProperty(null, null, null);
    }

}
