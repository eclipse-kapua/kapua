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

import java.util.HashMap;
import java.util.Map;

public class H2TcpSpecifics extends H2Specifics {

    // Singleton instance
    private static final H2TcpSpecifics INSTANCE = new H2TcpSpecifics();

    private static final String IMAGE_NAME = "kapua/kapua-sql-h2";

    public static H2TcpSpecifics getInstance() {
        return INSTANCE;
    }

    @Override
    public String getImageName() {
        return IMAGE_NAME;
    }

    @Override
    public String[] getContainerEnvVars() {
        return new String[] {""};
    }

    @Override
    public String getDbContainerPort() {
        return "3306";
    }

    @Override
    public String[] getDbContainerEnvVars() {
        return new String[] {
                "DATABASE=kapuadb",
                "DB_USER=kapua",
                "DB_PASSWORD=kapua",
                "H2_WEB_OPTS=-web -webAllowOthers -webPort 8181", //to enable the H2 web console (WARNING enable it only for test and then disable it again!)
                "DB_PORT_3306_TCP_PORT=3306"
        };
    }

    @Override
    public Map<String, String> getClientJdbcSettings() {
        String dbPort = this.getDbContainerPort();
        return new HashMap<String,String>() {{
            put(SystemSettingKey.DB_JDBC_CONNECTION_URL_RESOLVER.key(),"DEFAULT");
            put(SystemSettingKey.DB_JDBC_DRIVER.key(),"org.h2.Driver");
            put(SystemSettingKey.DB_CONNECTION_SCHEME.key(), "jdbc:h2:tcp");
            put(SystemSettingKey.DB_CONNECTION_PORT.key(), dbPort);
            put(SystemSettingKey.DB_CONNECTION_HOST.key(), "localhost");
        }};
    }

}
