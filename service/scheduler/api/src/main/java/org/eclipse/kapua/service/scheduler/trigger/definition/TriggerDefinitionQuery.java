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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.AbstractKapuaNamedQuery;
import org.eclipse.kapua.model.query.KapuaQuery;

/**
 * {@link TriggerDefinition} {@link KapuaQuery} definition.
 *
 * @see KapuaQuery
 * @since 1.1.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class TriggerDefinitionQuery extends AbstractKapuaNamedQuery {

    public TriggerDefinitionQuery() {
    }

    public TriggerDefinitionQuery(KapuaId scopeId) {
        super(scopeId);
    }

    public TriggerDefinitionQuery(KapuaQuery query) {
        super(query);
    }
}
