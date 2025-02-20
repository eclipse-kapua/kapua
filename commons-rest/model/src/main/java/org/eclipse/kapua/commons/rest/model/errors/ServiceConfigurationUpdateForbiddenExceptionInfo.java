/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.rest.model.errors;

import org.eclipse.kapua.commons.configuration.exception.ServiceConfigurationForbiddenException;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "serviceConfigurationUpdateForbiddenExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceConfigurationUpdateForbiddenExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private KapuaId scopeId;

    @XmlElement(name = "servicePid")
    private String servicePid;

    @XmlElement(name = "propertyId")
    private String propertyId;

    @XmlElement(name = "propertyValue")
    private String propertyValue;

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    protected ServiceConfigurationUpdateForbiddenExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param serviceConfigurationForbiddenException The original exception.
     * @since 2.1.0
     */
    public ServiceConfigurationUpdateForbiddenExceptionInfo(ServiceConfigurationForbiddenException serviceConfigurationForbiddenException, boolean showStackTrace) {
        super(403/*Response.Status.FORBIDDEN*/, serviceConfigurationForbiddenException, showStackTrace);

        this.servicePid = serviceConfigurationForbiddenException.getServicePid();
        this.scopeId = serviceConfigurationForbiddenException.getScopeId();
        this.propertyId = serviceConfigurationForbiddenException.getPropertyId();
        this.propertyValue = serviceConfigurationForbiddenException.getPropertyValue();
    }

    /**
     * Gets the scope {@link KapuaId} for which the update is forbidden.
     *
     * @return The scope {@link KapuaId} for which the update is forbidden.
     * @since 2.1.0
     */
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Gets the {@link KapuaConfigurableService} pid.
     *
     * @return he {@link KapuaConfigurableService} pid.
     * @since 2.1.0
     */
    public String getServicePid() {
        return servicePid;
    }

    /**
     * Gets the {@link KapuaTad#getId()} for which the update is forbidden.
     *
     * @return The {@link KapuaTad#getId()} for which the update is forbidden.
     * @since 2.1.0
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * Gets the {@link KapuaTad} value for which the update is forbidden.
     *
     * @return The {@link KapuaTad} value for which the update is forbidden.
     * @since 2.1.0
     */
    public String getPropertyValue() {
        return propertyValue;
    }
}
