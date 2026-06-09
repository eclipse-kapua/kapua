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
package org.eclipse.kapua.app.console.module.api.server;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.MainViewDescriptor;
import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.TabDescriptor;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtConfigComponent;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtConsoleService;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationsFacade;
import org.eclipse.kapua.commons.util.ResourceUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class GwtConsoleServiceImpl extends KapuaRemoteServiceServlet implements GwtConsoleService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final ServiceLoader<MainViewDescriptor> MAIN_VIEW_DESCRIPTOR_CLASSES = ServiceLoader.load(MainViewDescriptor.class);
    private static final JsonArray MAIN_VIEW_DESCRIPTORS;
    private static JsonReader jsonReader;
    private static final ServiceConfigurationsFacade FACADE_CONFIGURATION_SERVICE = LOCATOR.getComponent(ServiceConfigurationsFacade.class);

    static {
        jsonReader = null;
        try {
            jsonReader = Json.createReader(ResourceUtils.openAsReader(ResourceUtils.getResource("org/eclipse/kapua/app/console/view-descriptors.json"), Charset.forName("UTF-8")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MAIN_VIEW_DESCRIPTORS = jsonReader.readArray();
    }

    @Override
    public List<MainViewDescriptor> getCustomEntityViews() throws GwtKapuaException {
        List<MainViewDescriptor> views = new ArrayList<MainViewDescriptor>();
        for (MainViewDescriptor descriptorClass : MAIN_VIEW_DESCRIPTOR_CLASSES) {
            for (int i = 0; i < MAIN_VIEW_DESCRIPTORS.size(); i++) {
                JsonObject descriptor = MAIN_VIEW_DESCRIPTORS.getJsonObject(i);
                if (descriptor.containsKey("descriptor") && descriptorClass.getClass().getName().equals(descriptor.getString("descriptor"))) {
                    views.add(descriptorClass);
                    break;
                }
            }
        }
        Collections.sort(views);
        return views;
    }

    @Override
    public List<TabDescriptor> getCustomTabsForView(String viewClass) throws GwtKapuaException {
        List<TabDescriptor> tabs = new ArrayList<TabDescriptor>();
        try {
            for (int i = 0; i < MAIN_VIEW_DESCRIPTORS.size(); i++) {
                JsonObject descriptor = MAIN_VIEW_DESCRIPTORS.getJsonObject(i);
                if (descriptor.getString("view").equals(viewClass)) {
                    for (JsonValue jsonValue : descriptor.getJsonArray("tabs")) {
                        if (jsonValue != null && jsonValue instanceof JsonString) {
                            tabs.add((TabDescriptor) Class.forName(((JsonString) jsonValue).getString()).newInstance());
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new GwtKapuaException(GwtKapuaErrorCode.INTERNAL_ERROR, e);
        } catch (InstantiationException e) {
            throw new GwtKapuaException(GwtKapuaErrorCode.INTERNAL_ERROR, e);
        } catch (ClassNotFoundException e) {
            throw new GwtKapuaException(GwtKapuaErrorCode.INTERNAL_ERROR, e);
        }
        Collections.sort(tabs);
        return tabs;
    }

    @Override
    public void updateComponentConfiguration(GwtXSRFToken xsrfToken, String scopeId, String parentScopeId, GwtConfigComponent configComponent) throws GwtKapuaException {
        String serviceClassName = configComponent.getComponentId();
        try {
            // Checking validity of the given XSRF Token
            checkXSRFToken(xsrfToken);
            Map<String, Object> configParameters = GwtKapuaCommonsModelConverter.convertConfigComponent(configComponent);
            ServiceComponentConfiguration toUpdate = new ServiceComponentConfiguration();
            toUpdate.setId(serviceClassName);
            toUpdate.setProperties(configParameters);
            KapuaId kapuaScopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeId);
            FACADE_CONFIGURATION_SERVICE.update(kapuaScopeId, serviceClassName, toUpdate);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }
}
