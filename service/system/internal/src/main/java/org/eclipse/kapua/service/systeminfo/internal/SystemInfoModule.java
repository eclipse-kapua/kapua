/*******************************************************************************
 * Copyright (c) 2023, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.systeminfo.internal;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.service.systeminfo.SystemInfoService;

public class SystemInfoModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        bind(SystemInfoService.class).to(SystemInfoServiceImpl.class);
        bind(SystemSetting.class).toInstance(SystemSetting.getInstance());
    }
}
