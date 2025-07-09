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
package org.eclipse.kapua.extras.liquibaseTool;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import org.eclipse.kapua.commons.jpa.JdbcConnectionUrlResolvers;
import org.eclipse.kapua.commons.liquibase.KapuaLiquibaseClient;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.log.ConfigurationPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

    private static final SystemSetting SYSTEM_SETTING = SystemSetting.getInstance();

    private Application() {
    }

    public static void main(String[] args) {
        LOG.info("Liquibase tool... STARTING");
        try {
            {
                printConfiguration();

                String dbUsername = SYSTEM_SETTING.getString(SystemSettingKey.DB_USERNAME);
                String dbPassword = SYSTEM_SETTING.getString(SystemSettingKey.DB_PASSWORD);
                String schema = MoreObjects.firstNonNull(
                        SYSTEM_SETTING.getString(SystemSettingKey.DB_SCHEMA_ENV),
                        SYSTEM_SETTING.getString(SystemSettingKey.DB_SCHEMA)
                );
                KapuaLiquibaseClient client = new KapuaLiquibaseClient(JdbcConnectionUrlResolvers.resolveJdbcUrl(), dbUsername, dbPassword, schema);

                if (args[0].equals("locks")) {
                    client.forceReleaseChangelogLock();
                } else if (args[0].equals("checksums")) {
                    client.clearChecksum();
                }
            }
        } catch (Exception e) {
            LOG.info("Liquibase tool... ERROR!", e);
            return;
        }

        LOG.info("Liquibase tool... DONE!");
    }

    /**
     * Prints the JDBC configuration on startup
     *
     * @since 2.0.0
     */
    protected static void printConfiguration() {
        ConfigurationPrinter configurationPrinter =
                ConfigurationPrinter.create()
                        .withLogger(LOG)
                        .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                        .withTitle("Liquibase Tool Configuration");

        configurationPrinter
                .openSection("JDBC Configuration")
                .addParameter("Resolver", System.getProperty(SystemSettingKey.DB_JDBC_CONNECTION_URL_RESOLVER.key()))
                .addParameter("Driver", System.getProperty(SystemSettingKey.DB_JDBC_DRIVER.key()))
                .addParameter("Scheme", System.getProperty(SystemSettingKey.DB_CONNECTION_SCHEME.key()))
                .addParameter("Host", System.getProperty(SystemSettingKey.DB_CONNECTION_HOST.key()))
                .addParameter("Port", System.getProperty(SystemSettingKey.DB_CONNECTION_PORT.key()))
                .addParameter("Database Name", System.getProperty(SystemSettingKey.DB_NAME.key()))
                .addParameter("Database Schema", System.getProperty(SystemSettingKey.DB_SCHEMA.key()))
                .addParameter("Username", Strings.isNullOrEmpty(System.getProperty(SystemSettingKey.DB_USERNAME.key())) ? "No" : "Yes")
                .addParameter("Password", Strings.isNullOrEmpty(System.getProperty(SystemSettingKey.DB_PASSWORD.key())) ? "No" : "Yes")
                .addParameter("SSL Enabled", System.getProperty(SystemSettingKey.DB_CONNECTION_USE_SSL.key()))
                .addParameter("SSL Enabled Protocols", System.getProperty(SystemSettingKey.DB_CONNECTION_ENABLED_SSL_PROTOCOL_SUITES.key()))
                .closeSection();

        configurationPrinter.printLog();
    }
}