/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.extras.migrator.encryption.api;

import java.util.List;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.extras.migrator.encryption.settings.EncryptionMigrationSettingKeys;
import org.eclipse.kapua.extras.migrator.encryption.settings.EncryptionMigrationSettings;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEntityAttributeMigrator<E extends KapuaUpdatableEntity> implements EntitySecretAttributeMigrator<E> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEntityAttributeMigrator.class);

    private static final Boolean DRY_RUN = new EncryptionMigrationSettings().getBoolean(EncryptionMigrationSettingKeys.DRY_RUN);

    protected final KapuaEntityService<E, ?> entityService;
    protected final KapuaUpdatableEntityService<E> entityUpdatableService;

    public AbstractEntityAttributeMigrator(KapuaUpdatableEntityService<E> entityService) {
        this.entityService = (KapuaEntityService) entityService;
        this.entityUpdatableService = entityService;
    }

    @Override
    public void migrate(List<E> entitiesToMigrate) throws KapuaException {
        for (E entityToMigrate : entitiesToMigrate) {
            if (DRY_RUN) {
                LOG.info("            [DRY RUN] Found {} to migrate with scopeId {} and Id {}", getEntityName(), entityToMigrate.getScopeId(), entityToMigrate.getId());
            } else {
                LOG.info("            Migrating {} with scopeId {} and Id {}", getEntityName(), entityToMigrate.getScopeId(), entityToMigrate.getId());
                entityUpdatableService.update(entityToMigrate);
            }
        }
    }

    @Override
    public List<E> getChunk(int offset, int limit) throws KapuaException {
        KapuaQuery query = new KapuaQuery();

        // This is the most stable sorting even if it is not always indexed
        query.setSortCriteria(query.fieldSortCriteria(KapuaEntityAttributes.CREATED_ON, SortOrder.ASCENDING));

        query.setOffset(offset);
        query.setLimit(limit);

        return entityService.query(query).getItems();
    }

    @Override
    public long getTotalCount() throws KapuaException {
        KapuaQuery query = new KapuaQuery();
        return entityService.count(query);
    }

}
