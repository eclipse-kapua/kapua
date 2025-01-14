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
package org.eclipse.kapua.service.job.step.definition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.AbstractKapuaNamedQuery;
import org.eclipse.kapua.model.query.KapuaQuery;

/**
 * {@link JobStepDefinition} {@link KapuaQuery} definition.
 *
 * @see KapuaQuery
 * @since 1.0.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobStepDefinitionQuery extends AbstractKapuaNamedQuery {

    public JobStepDefinitionQuery() {
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The {@link #getScopeId()}.
     * @since 1.0.0
     */
    public JobStepDefinitionQuery(KapuaId scopeId) {
        super(scopeId);
    }

    /**
     * This enables {@link JobStepDefinitionQuery} to retrieve also not scoped {@link JobStepDefinition}s since they are public.
     *
     * @return {@code true}
     * @since 2.0.0
     */
    @Override
    public boolean getNotScopedEntities() {
        return true;
    }
}
