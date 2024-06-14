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
package org.eclipse.kapua.app.api.core.model;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.web.rest.KapuaCommonApiCoreSetting;
import org.eclipse.kapua.commons.web.rest.KapuaCommonApiCoreSettingKeys;

import com.google.inject.Provides;

public class TestModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(KapuaCommonApiCoreSetting.class).in(Singleton.class);
    }

    @Provides
    @Named("showStackTrace")
    Boolean showStackTrace(KapuaCommonApiCoreSetting kapuaCommonApiCoreSetting) {
        return kapuaCommonApiCoreSetting.getBoolean(KapuaCommonApiCoreSettingKeys.API_EXCEPTION_STACKTRACE_SHOW, false);
    }

    @Provides
    @Named("metricModuleName")
    String metricModuleName() {
        return "unit-tests";
    }

    @Provides
    @Named("eventsModuleName")
    String eventModuleName() {
        return "unit_tests";
    }

}
