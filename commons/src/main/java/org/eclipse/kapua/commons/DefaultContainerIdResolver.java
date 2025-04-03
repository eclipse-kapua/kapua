/*******************************************************************************
 * Copyright (c) 2017, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons;

import com.google.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lookup from the configuration file
 *
 * @since 1.0
 */
public class DefaultContainerIdResolver implements ContainerIdResolver {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultContainerIdResolver.class);

    private final String containerId;

    @Inject
    public DefaultContainerIdResolver() {
        logger.info("Resolving container id...");
        this.containerId = RandomStringUtils.randomAlphanumeric(8);
        logger.info("Resolving container id. Generating random name {}", this.containerId);
    }

    @Override
    public String getContainerId() {
        return containerId;
    }
}
