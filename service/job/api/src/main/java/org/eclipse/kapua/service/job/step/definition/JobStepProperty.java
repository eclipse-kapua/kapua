/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.job.step.definition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.service.job.step.JobStepXmlRegistry;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link JobStepProperty} definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "jobStepProperty")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(factoryClass = JobStepXmlRegistry.class, factoryMethod = "newJobStepProperty")
public interface JobStepProperty {

    /**
     * @return
     * @since 1.0.0
     */
    @Schema(example = "assets")
    String getName();

    /**
     * @param name
     * @since 1.0.0
     */
    void setName(String name);

    /**
     * Gets the description
     *
     * @return The description
     * @since 2.1.0
     */
    @Schema(description = "Execute request to write values on a specified set of channels and assets to the target devices of the Job")
    String getDescription();

    /**
     * Sets the description
     *
     * @param description The description
     * @since 2.1.0
     */
    void setDescription(String description);

    /**
     * @return
     * @since 1.0.0
     */
    @Schema(example = "org.eclipse.kapua.service.device.management.asset.DeviceAssets")
    String getPropertyType();

    /**
     * @param propertyType
     * @since 1.0.0
     */
    void setPropertyType(String propertyType);

    /**
     * @return
     * @since 1.0.0
     */
    String getPropertyValue();

    /**
     * @param propertyValue
     * @since 1.0.0
     */
    void setPropertyValue(String propertyValue);

    /**
     * @return
     * @since 1.1.0
     */
    @Schema(example = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n<deviceAssets>\\n    <deviceAsset>\\n        <name>assetName</name>\\n        <channels>\\n            <channel>\\n                <valueType>binary</valueType>\\n                <value>EGVzdCBzdHJpbmcgdmFsdWU=</value>\\n                <name>binaryTest</name>\\n            </channel>\\n        </channels>\\n    </deviceAsset>\\n</deviceAssets>")
    String getExampleValue();

    /**
     * @param exampleValue
     * @since 1.1.0
     */
    void setExampleValue(String exampleValue);

    /**
     * Gets whether this must have a {@link #getPropertyValue()}
     *
     * @return {@code true} if must have a {@link #getPropertyValue()}, {@code false} otherwise.
     * @since 1.5.0
     */
    Boolean getRequired();

    /**
     * Whether this must have a {@link #getPropertyValue()}
     *
     * @param required {@code true} if must have a {@link #getPropertyValue()}, {@code false} otherwise.
     * @since 1.5.0
     */
    void setRequired(Boolean required);

    /**
     * Gets whether this {@link #getPropertyValue()} is a secret that shouldn't be shown (i.e. passwords).
     *
     * @return {@code true} if {@link #getPropertyValue()} is a secret that shouldn't be shown, {@code false} otherwise.
     * @since 2.0.0
     */
    Boolean getSecret();

    /**
     * Whether t{@link #getPropertyValue()} is a secret that shouldn't be shown (i.e. passwords).
     *
     * @param se {@code true} if {@link #getPropertyValue()} is a secret that shouldn't be shown, {@code false} otherwise.
     * @since 2.0.0
     */
    void setSecret(Boolean se);

    /**
     * @return
     * @since 1.5.0
     */
    Integer getMinLength();

    /**
     * @param minLength
     * @since 1.5.0
     */
    void setMinLength(Integer minLength);

    /**
     * @return
     * @since 1.5.0
     */
    Integer getMaxLength();

    /**
     * @param maxLength
     * @since 1.5.0
     */
    void setMaxLength(Integer maxLength);

    /**
     * @return
     * @since 1.5.0
     */
    String getMinValue();

    /**
     * @param minValue
     * @since 1.5.0
     */
    void setMinValue(String minValue);

    /**
     * @return
     * @since 1.5.0
     */
    String getMaxValue();

    /**
     * @param maxValue
     * @since 1.5.0
     */
    void setMaxValue(String maxValue);

    /**
     * @return
     * @since 1.5.0
     */
    String getValidationRegex();

    /**
     * @param validationRegex
     * @since 1.5.0
     */
    void setValidationRegex(String validationRegex);
}
