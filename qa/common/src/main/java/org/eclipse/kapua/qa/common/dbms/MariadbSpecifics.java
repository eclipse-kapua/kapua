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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class MariadbSpecifics implements DbmsSpecifics {

    // Singleton instance
    private static final MariadbSpecifics INSTANCE = new MariadbSpecifics();

    private static final String IMAGE_NAME = "kapua/kapua-sql-mariadb";

    private MariadbSpecifics() {}

    public static MariadbSpecifics getInstance() {
        return INSTANCE;
    }

    @Override
    public String getImageName() {
        return IMAGE_NAME;
    }

    public String[] getContainerEnvVars() {
        return new String[] {
                "DB_CONNECTION_ADDITIONAL_OPTIONS=allowPublicKeyRetrieval=True",
                "DB_CONNECTION_SCHEME=jdbc:mariadb",
                "DB_RESOLVER=MariaDB",
                "DB_DRIVER=org.mariadb.jdbc.Driver",
                "DB_TARGET=MySQL", //change this according to: https://eclipse.dev/eclipselink/documentation/2.5/jpa/extensions/p_target_database.htm
                //following env. vars are not strictly necessary, but set as default value for completeness
                "DB_HOST=db",
                "DB_PORT=3306",
                "DB_NAME=kapuadb",
                "DB_SCHEMA_NAME=kapuadb",
                "DB_USERNAME=kapua",
                "DB_PASSWORD=kapua"
        };
    }

    public String getDbContainerPort() {
        return "3306";
    }

    public String[] getDbContainerEnvVars() {
        return new String[] {
                "MYSQL_ROOT_PASSWORD=keepCalm123",
                "MYSQL_DATABASE=kapuadb",
                "MYSQL_USER=kapua",
                "MYSQL_PASSWORD=kapua"
        };
    }

    public Map<String, String> getClientJdbcSettings() {
        String dbPort = this.getDbContainerPort();
        return new HashMap<String,String>() {{
            put(SystemSettingKey.DB_JDBC_CONNECTION_URL_RESOLVER.key(),"MariaDB");
            put(SystemSettingKey.DB_JDBC_DRIVER.key(),"org.mariadb.jdbc.Driver");
            put(SystemSettingKey.DB_CONNECTION_SCHEME.key(), "jdbc:mariadb");
            put(SystemSettingKey.DB_CONNECTION_PORT.key(), dbPort);
            put(SystemSettingKey.DB_CONNECTION_HOST.key(), "localhost");
            put(SystemSettingKey.DB_CONNECTION_ADDITIONAL_OPTIONS.key(),"allowPublicKeyRetrieval=True");
            put(SystemSettingKey.DB_JDBC_DATABASE_TARGET.key(), "MySQL");
        }};
    }

    public void dropAllTables(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            // Fetch tables
            String[] types = {"TABLE"};
            ResultSet sqlResults = connection.getMetaData().getTables(null, null, "%", types);

            while (sqlResults.next()) {
                String tableName = sqlResults.getString("TABLE_NAME");
                String sqlStatement = null;
                sqlStatement = String.format("DROP TABLE IF EXISTS `%s`", tableName);
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
                    preparedStatement.execute();
                }
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

}
