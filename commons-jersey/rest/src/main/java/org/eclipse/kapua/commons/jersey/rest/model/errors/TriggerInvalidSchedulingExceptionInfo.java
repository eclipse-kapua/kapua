/*******************************************************************************
 * Copyright (c) 2024, 2024 Eurotech and/or its affiliates and others
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

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.ExceptionInfo;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.scheduler.exception.TriggerInvalidSchedulingException;

@XmlRootElement(name = "triggerInvalidSchedulingExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggerInvalidSchedulingExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "startsOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date startsOn;

    @XmlElement(name = "endsOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date endsOn;

    @XmlElement(name = "triggerDefinitionId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    private KapuaId triggerDefinitionId;

    @XmlElement(name = "scheduling")
    private String scheduling;

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    public TriggerInvalidSchedulingExceptionInfo() {
    }

    /**
     * Constructor.
     *
     * @param triggerInvalidSchedulingException
     *         The root exception.
     * @since 2.1.0
     */
    public TriggerInvalidSchedulingExceptionInfo(TriggerInvalidSchedulingException triggerInvalidSchedulingException, boolean showStackTrace) {
        super(400,
                triggerInvalidSchedulingException,
                showStackTrace);

        startsOn = triggerInvalidSchedulingException.getStartsOn();
        endsOn = triggerInvalidSchedulingException.getEndsOn();
        triggerDefinitionId = triggerInvalidSchedulingException.getTriggerDefinitionId();
        scheduling = triggerInvalidSchedulingException.getScheduling();
    }

    /**
     * Gets the {@link TriggerInvalidSchedulingException#getStartsOn()}.
     *
     * @return The {@link TriggerInvalidSchedulingException#getStartsOn()}.
     * @since 2.1.0
     */
    public Date getStartsOn() {
        return startsOn;
    }

    /**
     * Gets the {@link TriggerInvalidSchedulingException#getEndsOn()}.
     *
     * @return The {@link TriggerInvalidSchedulingException#getEndsOn()}.
     * @since 2.1.0
     */
    public Date getEndsOn() {
        return endsOn;
    }

    /**
     * Gets the {@link TriggerInvalidSchedulingException#getTriggerDefinitionId()}.
     *
     * @return The {@link TriggerInvalidSchedulingException#getTriggerDefinitionId()}.
     * @since 2.1.0
     */
    public KapuaId getTriggerDefinitionId() {
        return triggerDefinitionId;
    }

    /**
     * Gets the {@link TriggerInvalidSchedulingException#getScheduling()}.
     *
     * @return The {@link TriggerInvalidSchedulingException#getScheduling()}.
     * @since 2.1.0
     */
    public String getScheduling() {
        return scheduling;
    }
}
