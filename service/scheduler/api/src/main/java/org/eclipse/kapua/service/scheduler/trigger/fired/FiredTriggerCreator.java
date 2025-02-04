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
package org.eclipse.kapua.service.scheduler.trigger.fired;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;

/**
 * {@link FiredTrigger} {@link org.eclipse.kapua.model.KapuaEntityCreator} definition.
 *
 * @since 1.5.0
 */
@XmlRootElement(name = "firedTriggerCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class FiredTriggerCreator extends KapuaEntityCreator {

    private KapuaId triggerId;
    private Date firedOn;
    private FiredTriggerStatus status;
    private String message;

    public FiredTriggerCreator() {
    }

    public FiredTriggerCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public FiredTriggerCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }

    /**
     * Gets the {@link Trigger#getId()}
     *
     * @return The {@link Trigger#getId()}
     * @since 1.5.0
     */
    @XmlElement(name = "triggerId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public KapuaId getTriggerId() {
        return triggerId;
    }

    /**
     * Sets the {@link Trigger#getId()}.
     *
     * @param triggerId
     *         The {@link Trigger#getId()}.
     * @since 1.5.0
     */
    public void setTriggerId(KapuaId triggerId) {
        this.triggerId = triggerId;
    }

    /**
     * Gets the actual fire {@link Date}.
     *
     * @return The actual fire {@link Date}.
     * @since 1.5.0
     */
    @XmlElement(name = "firedOn")
    public Date getFiredOn() {
        return firedOn;
    }

    /**
     * Sets the actual fire {@link Date}.
     *
     * @param firedOn
     *         The actual fire {@link Date}.
     * @since 1.5.0
     */
    public void setFiredOn(Date firedOn) {
        this.firedOn = firedOn;
    }

    /**
     * Gets the {@link FiredTriggerStatus} of the processing.
     *
     * @return The {@link FiredTriggerStatus} of the processing.
     * @since 1.5.0
     */
    @XmlElement(name = "status")
    public FiredTriggerStatus getStatus() {
        return status;
    }

    /**
     * Sets the {@link FiredTriggerStatus} of the processing.
     *
     * @param status
     *         The {@link FiredTriggerStatus} of the processing.
     * @since 1.5.0
     */
    public void setStatus(FiredTriggerStatus status) {
        this.status = status;
    }

    /**
     * Gets the {@link Exception#getMessage()} if there are errors in the processing.
     *
     * @return The {@link Exception#getMessage()} if there are errors in the processing.
     * @since 1.5.0
     */
    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    /**
     * Sets the {@link Exception#getMessage()} if there are errors in the processing.
     *
     * @param message
     *         The {@link Exception#getMessage()} if there are errors in the processing.
     * @since 1.5.0
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
