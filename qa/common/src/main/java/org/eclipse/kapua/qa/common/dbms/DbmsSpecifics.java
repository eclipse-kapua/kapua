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
import java.sql.SQLException;
import java.util.Map;

//Abstraction that isolates details of specific DBMS
public interface DbmsSpecifics {

    //retrieve container image name
    public String getImageName();

    //retrieve db container env. variables values
    public String[] getContainerEnvVars();

    //retrieve container port
    public String getDbContainerPort();

    //retrieve non-db container env. variables values (for container that depends on the db)
    public String[] getDbContainerEnvVars();

    //retrieve settings for the jdbc connection
    public Map<String, String> getClientJdbcSettings();

    //execute a drop of all tables inside the db
    public void dropAllTables(Connection connection) throws SQLException;

}
