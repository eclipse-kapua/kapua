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
package org.eclipse.kapua.job.engine.client;

import javax.inject.Singleton;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.job.engine.JobEngineService;
import org.eclipse.kapua.job.engine.client.settings.JobEngineClientSetting;

public class JobEngineClientModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(JobEngineService.class).to(JobEngineServiceClient.class).in(Singleton.class);
        bind(JobEngineClientSetting.class).in(Singleton.class);
    }
}
