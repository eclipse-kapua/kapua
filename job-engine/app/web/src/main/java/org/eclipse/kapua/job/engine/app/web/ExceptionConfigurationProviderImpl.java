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
package org.eclipse.kapua.job.engine.app.web;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.rest.errors.ExceptionConfigurationProvider;
import org.eclipse.kapua.commons.rest.jersey.KapuaCommonApiCoreSetting;
import org.eclipse.kapua.commons.rest.jersey.KapuaCommonApiCoreSettingKeys;
import org.eclipse.kapua.locator.KapuaLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExceptionConfigurationProviderImpl implements ExceptionConfigurationProvider {

    private final boolean showStackTrace = KapuaLocator.getInstance().getComponent(KapuaCommonApiCoreSetting.class).getBoolean(KapuaCommonApiCoreSettingKeys.API_EXCEPTION_STACKTRACE_SHOW, false);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ExceptionConfigurationProviderImpl() {
        logger.debug("Initialized with showStackTrace={}", showStackTrace);
    }

    @Override
    public boolean showStackTrace() {
        return showStackTrace;
    }
}
