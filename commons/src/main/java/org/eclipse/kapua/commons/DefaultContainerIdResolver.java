/*******************************************************************************
 * Copyright (c) 2017, 2024 Eurotech and/or its affiliates and others
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
import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lookup from the configuration file
 *
 * @since 1.0
 */
public class DefaultContainerIdResolver implements ContainerIdResolver {

    protected static final Logger logger = LoggerFactory.getLogger(DefaultContainerIdResolver.class);

    private static final String CANNOT_FIND_IP_ERROR_MSG = "Cannot resolve the container id. Please check the configuration!";
    private final String containerId;

    @Inject
    public DefaultContainerIdResolver(String containerId) {
        logger.info("Resolving container id...");
        if (StringUtils.isEmpty(containerId)) {
            this.containerId = RandomStringUtils.randomAlphanumeric(8);
            logger.info("Resolving container id. No external value received. Generating random name {}", this.containerId);
        }
    }

    @Override
    public String getContainerId() {
        return containerId;
    }
}
