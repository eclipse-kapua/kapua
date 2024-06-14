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
package org.eclipse.kapua.commons.rest.model.errors;

import org.eclipse.kapua.model.xml.DateXmlAdapter;
import org.eclipse.kapua.service.scheduler.exception.TriggerInvalidDatesException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

@XmlRootElement(name = "triggerInvalidDatesExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggerInvalidDatesExceptionInfo extends ExceptionInfo {

    @XmlElement(name = "startsOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date startsOn;

    @XmlElement(name = "endsOn")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date endsOn;

    @XmlElement(name = "currentDate")
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date currentDate;

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    public TriggerInvalidDatesExceptionInfo() {
    }

    /**
     * Constructor.
     *
     * @param triggerInvalidDatesException The root exception.
     * @since 2.1.0
     */
    public TriggerInvalidDatesExceptionInfo(TriggerInvalidDatesException triggerInvalidDatesException, boolean showStackTrace) {
        super(400,
                triggerInvalidDatesException,
                showStackTrace);

        startsOn = triggerInvalidDatesException.getStartsOn();
        endsOn = triggerInvalidDatesException.getEndsOn();
        currentDate = triggerInvalidDatesException.getCurrentDate();
    }

    /**
     * Gets the {@link TriggerInvalidDatesException#getStartsOn()}.
     *
     * @return The {@link TriggerInvalidDatesException#getStartsOn()}.
     * @since 2.1.0
     */
    public Date getStartsOn() {
        return startsOn;
    }

    /**
     * Gets the {@link TriggerInvalidDatesException#getEndsOn()}.
     *
     * @return The {@link TriggerInvalidDatesException#getEndsOn()}.
     * @since 2.1.0
     */
    public Date getEndsOn() {
        return endsOn;
    }

    /**
     * Gets the {@link TriggerInvalidDatesException#getCurrentDate()}.
     *
     * @return The {@link TriggerInvalidDatesException#getCurrentDate()}.
     * @since 2.1.0
     */
    public Date getCurrentDate() {
        return currentDate;
    }
}
