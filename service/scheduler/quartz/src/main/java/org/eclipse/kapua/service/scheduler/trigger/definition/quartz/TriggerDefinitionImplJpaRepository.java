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
package org.eclipse.kapua.service.scheduler.trigger.definition.quartz;

import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.commons.jpa.KapuaNamedEntityJpaRepository;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionListResult;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionRepository;

public class TriggerDefinitionImplJpaRepository
        extends KapuaNamedEntityJpaRepository<TriggerDefinition, TriggerDefinitionImpl, TriggerDefinitionListResult>
        implements TriggerDefinitionRepository {

    public TriggerDefinitionImplJpaRepository(KapuaJpaRepositoryConfiguration jpaRepoConfig) {
        super(TriggerDefinitionImpl.class, TriggerDefinition.TYPE, () -> new TriggerDefinitionListResult(), jpaRepoConfig);
    }
}
