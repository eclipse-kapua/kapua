/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.jpa;

import com.google.common.base.Strings;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class for JPA operations.
 *
 * @since 1.0.0
 */
public abstract class AbstractEntityManagerFactory implements org.eclipse.kapua.commons.jpa.EntityManagerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEntityManagerFactory.class);

    private static final SystemSetting SYSTEM_SETTING = SystemSetting.getInstance();

    private static final Map<String, String> UNIQUE_CONTRAINTS = new HashMap<>();
    private EntityManagerFactory entityManagerFactory;

    /**
     * Protected constructor
     *
     * @param persistenceUnitName
     * @param datasourceName
     * @param uniqueConstraints
     */
    protected AbstractEntityManagerFactory(String persistenceUnitName, String datasourceName, Map<String, String> uniqueConstraints) {
        SystemSetting config = SystemSetting.getInstance();

        //
        // Initialize the EntityManagerFactory
        try {

            // JPA configuration overrides
            Map<String, Object> configOverrides = new HashMap<>();
            configOverrides.put("javax.persistence.jdbc.driver", config.getString(SystemSettingKey.DB_JDBC_DRIVER));

            configOverrides.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false"); // This has to be set to false in order to disable the local object cache of EclipseLink.

            String targetDatabase = SYSTEM_SETTING.getString(SystemSettingKey.DB_JDBC_DATABASE_TARGET);
            if (!Strings.isNullOrEmpty(targetDatabase)) {
                configOverrides.put(PersistenceUnitProperties.TARGET_DATABASE, targetDatabase);
            }

            // Connection pooling
            configOverrides.put("eclipselink.connection-pool.default.url", JdbcConnectionUrlResolvers.resolveJdbcUrl());
            configOverrides.put("eclipselink.connection-pool.default.user", config.getString(SystemSettingKey.DB_USERNAME));
            configOverrides.put("eclipselink.connection-pool.default.password", config.getString(SystemSettingKey.DB_PASSWORD));

            configOverrides.put("eclipselink.connection-pool.default.dataSourceName", datasourceName);
            configOverrides.put("eclipselink.connection-pool.default.initial", config.getString(SystemSettingKey.DB_POOL_SIZE_INITIAL));
            configOverrides.put("eclipselink.connection-pool.default.min", config.getString(SystemSettingKey.DB_POOL_SIZE_MIN));
            configOverrides.put("eclipselink.connection-pool.default.max", config.getString(SystemSettingKey.DB_POOL_SIZE_MAX));
            configOverrides.put("eclipselink.connection-pool.default.wait", config.getString(SystemSettingKey.DB_POOL_BORROW_TIMEOUT));

            // Logging
            configOverrides.put("eclipselink.logging.level", "FINE");
            configOverrides.put("eclipselink.logging.parameters", "true");

            // Others
            configOverrides.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, "org.eclipse.kapua.commons.jpa.KapuaSessionCustomizer");

            // Standalone JPA
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, configOverrides);
        } catch (Throwable ex) {
            LOG.error("Error creating EntityManagerFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }

        //
        // Set unique constrains for this persistence unit
        // FIXME: this is needed? With EclipseLink we lost the ConstraintViolationException.
        for (Entry<String, String> uc : uniqueConstraints.entrySet()) {
            UNIQUE_CONTRAINTS.put(uc.getKey(), uc.getValue());
        }
    }

    // Entity manager factory methods

    /**
     * Returns an EntityManager instance.
     *
     * @return An entity manager for the persistence unit.
     * @throws KapuaException If {@link EntityManagerFactory#createEntityManager()} cannot create the {@link EntityManager}
     * @since 1.0.0
     */
    @Override
    public EntityManager createEntityManager() throws KapuaException {
        return new EntityManager(entityManagerFactory.createEntityManager());
    }

}
