/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdAdapter;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;
import org.eclipse.kapua.service.job.targets.JobTarget;

/**
 * {@link JobStartOptions} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobStartOptions")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class JobStartOptions implements KapuaSerializable {

    private static final long serialVersionUID = 5339966879340542119L;

    private Set<KapuaId> targetIdSublist;
    private List<JobStepProperty> stepPropertiesOverrides;
    private boolean resetStepIndex;
    private Integer fromStepIndex;
    private boolean enqueue;

    /**
     * Gets the sub{@link java.util.List} of {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId}s.
     *
     * @return The sub{@link java.util.List} of {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId}s.
     * @since 1.0.0
     */
    @XmlElementWrapper(name = "targetIdSublist")
    @XmlElement(name = "targetId")
    @XmlJavaTypeAdapter(KapuaIdAdapter.class)
    public Set<KapuaId> getTargetIdSublist() {
        if (targetIdSublist == null) {
            targetIdSublist = new HashSet<>();
        }

        return targetIdSublist;
    }

    /**
     * Sets the sub{@link java.util.List} of {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId}s.
     *
     * @param targetIdSublist
     *         The sub{@link java.util.List} of {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId}s.
     * @since 1.0.0
     */
    public void setTargetIdSublist(Set<KapuaId> targetIdSublist) {
        this.targetIdSublist = targetIdSublist;
    }

    /**
     * Removes a {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId} from sub{@link java.util.List} of {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId}s.
     *
     * @param targetId
     *         The {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId} to remove.
     * @since 1.0.0
     */
    @XmlTransient
    public void removeTargetIdToSublist(KapuaId targetId) {
        getTargetIdSublist().remove(targetId);
    }

    /**
     * Adds a {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId} from sub{@link java.util.List} of {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId}s.
     *
     * @param targetId
     *         The {@link org.eclipse.kapua.service.job.targets.JobTarget} {@link KapuaId} to add.
     * @since 1.0.0
     */
    @XmlTransient
    public void addTargetIdToSublist(KapuaId targetId) {
        getTargetIdSublist().add(targetId);
    }

    public List<JobStepProperty> getStepPropertiesOverrides() {
        if (stepPropertiesOverrides == null) {
            stepPropertiesOverrides = new ArrayList<>();
        }

        return stepPropertiesOverrides;
    }

    public void addStepPropertyOverride(JobStepProperty stepPropertyOverride) {
        getStepPropertiesOverrides().add(stepPropertyOverride);
    }

    public void setStepPropertiesOverrides(List<JobStepProperty> stepPropertiesOverrides) {
        this.stepPropertiesOverrides = new ArrayList<>(stepPropertiesOverrides);
    }

    /**
     * Gets whether or not the {@link JobTarget#getStepIndex()} needs to be reset to the given {@link #getFromStepIndex()}.
     *
     * @return {@code true} if the {@link JobTarget#getStepIndex()} needs to be reset to the given {@link #getFromStepIndex()}, {@code false} otherwise.
     * @since 1.1.0
     */
    public boolean getResetStepIndex() {
        return resetStepIndex;
    }

    /**
     * Sets whether or not the {@link JobTarget#getStepIndex()} needs to be reset to the given {@link #getFromStepIndex()}.
     *
     * @param resetStepIndex
     *         {@code true} if the {@link JobTarget#getStepIndex()} needs to be reset to the given {@link #getFromStepIndex()}, {@code false} otherwise.
     * @since 1.1.0
     */
    public void setResetStepIndex(boolean resetStepIndex) {
        this.resetStepIndex = resetStepIndex;
    }

    /**
     * Gets the starting {@link org.eclipse.kapua.service.job.step.JobStep} index.
     *
     * @return The starting {@link org.eclipse.kapua.service.job.step.JobStep} index.
     * @since 1.0.0
     */
    public Integer getFromStepIndex() {
        return fromStepIndex;
    }

    /**
     * Sets the starting {@link org.eclipse.kapua.service.job.step.JobStep} index.
     *
     * @param fromStepIndex
     *         The starting {@link org.eclipse.kapua.service.job.step.JobStep} index.
     * @since 1.0.0
     */
    public void setFromStepIndex(Integer fromStepIndex) {
        this.fromStepIndex = fromStepIndex;
    }

    /**
     * Gets whether or not enqueue the {@link org.eclipse.kapua.service.job.execution.JobExecution}.
     *
     * @return {@code true} if the {@link org.eclipse.kapua.service.job.execution.JobExecution} needs to be enqueued, {@code false} otherwise.
     * @since 1.1.0
     */
    public boolean getEnqueue() {
        return enqueue;
    }

    /**
     * Sets whether or not enqueue the {@link org.eclipse.kapua.service.job.execution.JobExecution}.
     *
     * @param enqueue
     *         {@code true} if the {@link org.eclipse.kapua.service.job.execution.JobExecution} needs to be enqueued, {@code false} otherwise.
     * @since 1.1.0
     */
    public void setEnqueue(boolean enqueue) {
        this.enqueue = enqueue;
    }

}
