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

import java.io.IOException;
import java.util.Properties;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.PropertiesUtils;
import org.eclipse.kapua.commons.model.AbstractKapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "pid" })
@Entity(name = "ServiceConfig")
@Table(name = "sys_configuration")
/**
 * {@link ServiceConfig} implementation.
 *
 * @since 1.0.0
 */
public class ServiceConfigImpl extends AbstractKapuaUpdatableEntity implements ServiceConfig {

    private static final long serialVersionUID = 8699765898092343484L;

    @XmlElement(name = "pid")
    @Basic
    @Column(name = "pid")
    private String pid;

    @XmlTransient
    @Basic
    @Column(name = "configurations")
    protected String configurations;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public ServiceConfigImpl() {
        super();
    }

    /**
     * Constructor.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to set into the {@link ServiceConfig}
     * @since 1.0.0
     */
    public ServiceConfigImpl(KapuaId scopeId) {
        super(scopeId);
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public Properties getConfigurations() throws KapuaException {
        try {
            return PropertiesUtils.readPropertiesFromString(configurations);
        } catch (IOException e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public void setConfigurations(Properties properties) throws KapuaException {
        try {
            configurations = PropertiesUtils.writePropertiesToString(properties);
        } catch (IOException e) {
            throw KapuaException.internalError(e);
        }
    }
}
