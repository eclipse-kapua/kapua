/*******************************************************************************
 * Copyright (c) 2019, 2025 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.qa.common.dbms;

import org.eclipse.kapua.commons.setting.system.SystemSettingKey;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class MySQLSpecifics implements DbmsSpecifics {

    private static final MySQLSpecifics INSTANCE = new MySQLSpecifics();

    private static final String IMAGE_NAME = "kapua/kapua-sql-mysql";

    private MySQLSpecifics() {}

    public static MySQLSpecifics getInstance() {
        return INSTANCE;
    }

    @Override
    public String getImageName() {
        return IMAGE_NAME;
    }

    @Override
    public String[] getContainerEnvVars() {
        String[] mariaDBContEnvVars = MariadbSpecifics.getInstance().getContainerEnvVars();

        return Arrays.stream(mariaDBContEnvVars)
                .map(envVar -> {
                    if (envVar.startsWith("DB_CONNECTION_SCHEME=")) {
                        return "DB_CONNECTION_SCHEME=jdbc:mysql";
                    } else if (envVar.startsWith("DB_CONNECTION_ADDITIONAL_OPTIONS=")) {
                        return "DB_CONNECTION_ADDITIONAL_OPTIONS=allowPublicKeyRetrieval=True";
                    }
                    return envVar;}
                ).toArray(String[]::new);
    }

    @Override
    public String getDbContainerPort() {
        return MariadbSpecifics.getInstance().getDbContainerPort();
    }

    @Override
    public String[] getDbContainerEnvVars() {
        return MariadbSpecifics.getInstance().getDbContainerEnvVars();
    }

    @Override
    public Map<String, String> getClientJdbcSettings() {
        Map<String, String> jdbcSettingsMariadb = MariadbSpecifics.getInstance().getClientJdbcSettings();
        jdbcSettingsMariadb.put(SystemSettingKey.DB_CONNECTION_SCHEME.key(), "jdbc:mysql");
        jdbcSettingsMariadb.put(SystemSettingKey.DB_CONNECTION_ADDITIONAL_OPTIONS.key(),"allowPublicKeyRetrieval=True");
        return jdbcSettingsMariadb;
    }

    @Override
    public void dropAllTables(Connection connection) throws SQLException {
        MariadbSpecifics.getInstance().dropAllTables(connection);
    }
}
