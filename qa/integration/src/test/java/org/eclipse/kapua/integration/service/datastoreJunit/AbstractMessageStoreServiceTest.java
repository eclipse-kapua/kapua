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
package org.eclipse.kapua.integration.service.datastoreJunit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.JdbcConnectionUrlResolvers;
import org.eclipse.kapua.commons.liquibase.KapuaLiquibaseClient;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.RandomUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.authentication.AuthenticationService;
import org.eclipse.kapua.service.authentication.UsernamePasswordCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

@Category(JUnitTests.class)
public abstract class AbstractMessageStoreServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageStoreServiceTest.class);

    protected static Random random = RandomUtils.getInstance();
    protected static KapuaLocator locator = KapuaLocator.getInstance();

    protected static KapuaId adminUserId;
    protected static KapuaId adminScopeId;

    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        logger.debug("Setting up test...");
        try {
            System.setProperty(SystemSettingKey.DB_JDBC_CONNECTION_URL_RESOLVER.key(), "H2");
            SystemSetting config = SystemSetting.getInstance();
            String dbUsername = config.getString(SystemSettingKey.DB_USERNAME);
            String dbPassword = config.getString(SystemSettingKey.DB_PASSWORD);
            String jdbcUrl = JdbcConnectionUrlResolvers.resolveJdbcUrl();
            String schema = MoreObjects.firstNonNull(config.getString(SystemSettingKey.DB_SCHEMA_ENV), config.getString(SystemSettingKey.DB_SCHEMA));

            connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            new KapuaLiquibaseClient(jdbcUrl, dbUsername, dbPassword, schema).update();
            // Login
            String username = "kapua-sys";
            String password = "kapua-password";

            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);
            authenticationService.login(new UsernamePasswordCredentials(username, password));
            // Get current user Id
            adminUserId = KapuaSecurityUtils.getSession().getUserId();
            adminScopeId = KapuaSecurityUtils.getSession().getScopeId();
        } catch (KapuaException exc) {
            logger.warn("Error", exc);
        }
    }

    @After
    public void tearDown() {
        logger.debug("Stopping Kapua test context.");

        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            logger.warn("Failed to close database", e);
        }

        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            AuthenticationService authenticationService = locator.getService(AuthenticationService.class);

            authenticationService.logout();
        } catch (KapuaException exc) {
            logger.warn("Error", exc);
        }
    }
    // Test utility methods

    /**
     * Generates a random {@link String} from the given parameters
     *
     * @param chars
     *         length of the generated {@link String}
     * @param letters
     *         whether or not use chars
     * @param numbers
     *         whether or not use numbers
     * @return the generated {@link String}
     */
    private static String generateRandomString(int chars, boolean letters, boolean numbers) {
        return RandomStringUtils.random(chars, 0, 0, letters, numbers, null, random);
    }

}
