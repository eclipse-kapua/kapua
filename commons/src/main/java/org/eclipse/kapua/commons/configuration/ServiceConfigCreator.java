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
package org.eclipse.kapua.commons.configuration;

import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * Service configuration creator definition.
 *
 * @since 1.0
 */
public class ServiceConfigCreator extends KapuaUpdatableEntityCreator {

    private static final long serialVersionUID = 7508550960304732465L;

    @XmlElement(name = "pid")
    private String pid;

    @XmlElement(name = "configurations")
    private Properties configurations;

    public ServiceConfigCreator() {
    }

    public ServiceConfigCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public ServiceConfigCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    /**
     * Return service pid
     *
     * @return
     */
    public String getPid() {
        return pid;
    }

    /**
     * Set service pid
     *
     * @param pid
     */
    public void setPid(String pid) {
        this.pid = pid;
    }

    /**
     * Return service configurations
     *
     * @return
     */
    public Properties getConfigurations() {
        return this.configurations;
    }

    /**
     * Set service configurations
     *
     * @param configurations
     */
    public void setConfigurations(Properties configurations) {
        this.configurations = configurations;
    }
}
