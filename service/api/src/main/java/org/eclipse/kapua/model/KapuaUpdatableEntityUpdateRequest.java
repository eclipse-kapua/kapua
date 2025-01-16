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
package org.eclipse.kapua.model;

import java.util.Properties;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link KapuaUpdatableEntityUpdateRequest} definition.
 *
 * @since 2.0.0
 */
public class KapuaUpdatableEntityUpdateRequest {

    @XmlElement(name = "optlock")
    @Schema(description = "The optlock field is used to detect that this entity has not been modified by someone else. When updating an entity, first do a find to get the latest version of the entity and note the value of the optlock. Then in the update operation, set the optlock value to match the value that you found. If someone else has updated the entity between your find and update operations, the update will fail and the db/server will return an error.\n" +
                              "See this [StackOverflow question](http://stackoverflow.com/questions/129329/optimistic-vs-pessimistic-locking) for more information on optimistic locking")
    public int optlock;

    /**
     * Introduces only for retrocompatibility to an outdated model
     */
    @XmlTransient
    @JsonIgnore
    public Properties entityAttributes;

    /**
     * Introduces only for retrocompatibility to an outdated model
     */
    @XmlTransient
    @JsonIgnore
    public Properties entityProperties;
}
