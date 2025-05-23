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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public abstract class H2Specifics implements DbmsSpecifics {

    public abstract String getImageName();

    public abstract String[] getContainerEnvVars();

    public abstract String getDbContainerPort();

    public abstract String[] getDbContainerEnvVars();

    public abstract Map<String, String> getClientJdbcSettings();

    public void dropAllTables(Connection connection) throws SQLException {
        String[] types = {"TABLE"};
        ResultSet sqlResults = connection.getMetaData().getTables(null, null, "%", types);

        while (sqlResults.next()) {
            String sqlStatement = String.format("DROP TABLE %s CASCADE", sqlResults.getString("TABLE_NAME").toUpperCase());
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
                preparedStatement.execute();
            }
        }
    }
}

