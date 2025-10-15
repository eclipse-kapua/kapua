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

public class H2InMemorySpecifics extends H2Specifics {

    // Singleton instance
    private static final H2InMemorySpecifics INSTANCE = new H2InMemorySpecifics();

    public static H2InMemorySpecifics getInstance() {
        return INSTANCE;
    }

    @Override
    public String getImageName() {
        throw new UnsupportedOperationException("this DBMS is not supported in a docker environment");
    }

    @Override
    public String[] getContainerEnvVars() {
        throw new UnsupportedOperationException("this DBMS is not supported in a docker environment");
    }

    @Override
    public String getDbContainerPort() {
        return "";
    }

    @Override
    public String[] getDbContainerEnvVars() {
        throw new UnsupportedOperationException("this DBMS is not supported in a docker environment");
    }

    @Override
    public Map<String, String> getClientJdbcSettings() {
        String dbPort = this.getDbContainerPort();
        return new HashMap<String,String>() {{
            put(SystemSettingKey.DB_JDBC_CONNECTION_URL_RESOLVER.key(),"H2");
            put(SystemSettingKey.DB_JDBC_DRIVER.key(),"org.h2.Driver");
            put(SystemSettingKey.DB_CONNECTION_SCHEME.key(), "jdbc:h2:mem:");
            put(SystemSettingKey.DB_CONNECTION_PORT.key(), "");
            put(SystemSettingKey.DB_CONNECTION_HOST.key(), "");
        }};
    }

}
