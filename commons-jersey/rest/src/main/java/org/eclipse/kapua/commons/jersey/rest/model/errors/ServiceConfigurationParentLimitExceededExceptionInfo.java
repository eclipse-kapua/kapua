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
package org.eclipse.kapua.commons.jersey.rest.model.errors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.commons.configuration.exception.ServiceConfigurationParentLimitExceededException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

@XmlRootElement(name = "serviceConfigurationParentLimitExceededExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceConfigurationParentLimitExceededExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "servicePid")
    private String servicePid;

    @XmlElement(name = "parentScopeId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private KapuaId parentScopeId;

    @XmlElement(name = "limitExceededBy")
    private long limitExceededBy;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected ServiceConfigurationParentLimitExceededExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param serviceConfigurationParentLimitExceededException
     *         The root exception.
     * @since 1.0.0
     */
    public ServiceConfigurationParentLimitExceededExceptionInfo(ServiceConfigurationParentLimitExceededException serviceConfigurationParentLimitExceededException, boolean showStackTrace) {
        super(400/*Response.Status.BAD_REQUEST*/, serviceConfigurationParentLimitExceededException, showStackTrace);

        this.servicePid = serviceConfigurationParentLimitExceededException.getServicePid();
        this.parentScopeId = serviceConfigurationParentLimitExceededException.getScopeId();
        this.limitExceededBy = serviceConfigurationParentLimitExceededException.getLimitExceededBy();
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
     * @return The parent scope {@link KapuaId} for which limit has been exceeded.
     * @since 2.0.0
     */
    public KapuaId getParentScopeId() {
        return parentScopeId;
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
