/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.model.misc;

import org.eclipse.kapua.commons.jpa.KapuaEntityJpaRepository;
import org.eclipse.kapua.commons.jpa.KapuaJpaRepositoryConfiguration;
import org.eclipse.kapua.model.query.KapuaListResult;

public class CollisionEntityJpaRepository extends KapuaEntityJpaRepository<CollisionEntity, CollisionEntity, KapuaListResult<CollisionEntity>> {

    public CollisionEntityJpaRepository(KapuaJpaRepositoryConfiguration configuration) {
        super(CollisionEntity.class, CollisionEntity.TYPE, () -> new KapuaListResult<>(), configuration);
    }

}
