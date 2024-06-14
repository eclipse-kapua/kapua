/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.web.rest.model.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.commons.configuration.exception.ServiceConfigurationLimitExceededException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

@XmlRootElement(name = "serviceConfigurationLimitExceededExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceConfigurationLimitExceededExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "servicePid")
    private String servicePid;

    @XmlElement(name = "scopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private KapuaId scopeId;

    @XmlElement(name = "limitExceededBy")
    private long limitExceededBy;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected ServiceConfigurationLimitExceededExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param serviceConfigurationLimitExceededException
     *         The root exception.
     * @since 1.0.0
     */
    public ServiceConfigurationLimitExceededExceptionInfo(ServiceConfigurationLimitExceededException serviceConfigurationLimitExceededException, boolean showStackTrace) {
        super(400/*Response.Status.BAD_REQUEST*/, serviceConfigurationLimitExceededException, showStackTrace);

        this.servicePid = serviceConfigurationLimitExceededException.getServicePid();
        this.scopeId = serviceConfigurationLimitExceededException.getScopeId();
        this.limitExceededBy = serviceConfigurationLimitExceededException.getLimitExceededBy();
    }

    /**
     * Gets the {@link KapuaConfigurableService} pid.
     *
     * @return he {@link KapuaConfigurableService} pid.
     * @since 2.0.0
     */
    public String getServicePid() {
        return servicePid;
    }

    /**
     * Gets the scope {@link KapuaId} for which limit has been exceeded.
     *
     * @return The scope {@link KapuaId} for which limit has been exceeded.
     * @since 2.0.0
     */
    public KapuaId getScopeId() {
        return scopeId;
    }

    /**
     * Gets the amount of exceed.
     *
     * @return the amount of exceed.
     * @since 2.0.0
     */
    public long getLimitExceededBy() {
        return limitExceededBy;
    }
}
