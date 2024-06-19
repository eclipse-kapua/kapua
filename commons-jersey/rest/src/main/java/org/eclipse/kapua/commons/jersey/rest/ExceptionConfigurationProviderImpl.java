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
package org.eclipse.kapua.commons.jersey.rest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExceptionConfigurationProviderImpl implements ExceptionConfigurationProvider {

    private final boolean showStackTrace;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public ExceptionConfigurationProviderImpl(@Named("showStackTrace") Boolean showStackTrace) {
        this.showStackTrace = showStackTrace;
        logger.debug("Initialized with showStackTrace={}", showStackTrace);
    }

    @Override
    public boolean showStackTrace() {
        return showStackTrace;
    }
}
