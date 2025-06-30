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

import io.cucumber.java.Scenario;

import java.util.Collection;

public class DbmsSpecificsFactory {

    private DbmsSpecificsFactory(){}

    public static DbmsSpecifics createSpecifics(String type) {
        switch (type.toLowerCase()) {
            case "mariadb": return MariadbSpecifics.getInstance();
            case "mysql": return MySQLSpecifics.getInstance();
            case "h2": return H2TcpSpecifics.getInstance();
            case "in-memory": return H2InMemorySpecifics.getInstance();
            default: throw new IllegalArgumentException("Unknown dbms deployment type: " + type);
        }
    }

    //factory that receive context about current test scenario
    public static DbmsSpecifics createSpecifics(Scenario scenario) {
        Collection<String> sourceTagNames = scenario.getSourceTagNames();
        if (sourceTagNames.contains("@env_none")) {
            return createSpecifics("in-memory"); //return the implementation for the in-memory deployment
        } else if (sourceTagNames.contains("@env_docker") || sourceTagNames.contains("@env_docker_base") ) {
            return createSpecifics();
        } else {
            throw new IllegalArgumentException("Unknown setup scenario: " + scenario);
        }
    }

    //create specifics from the system variable set
    public static DbmsSpecifics createSpecifics() {
        return createSpecifics(System.getProperty("org.eclipse.kapua.qa.dbms", "h2"));
    }


}
