/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.job.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTriggerProperty;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.definition.GwtTriggerDefinition;
import org.eclipse.kapua.app.console.module.job.shared.service.GwtTriggerDefinitionService;
import org.eclipse.kapua.app.console.module.job.shared.util.KapuaGwtJobModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionListResult;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionService;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;

public class GwtTriggerDefinitionServiceImpl extends KapuaRemoteServiceServlet implements GwtTriggerDefinitionService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final TriggerDefinitionService TRIGGER_DEFINITION_SERVICE = LOCATOR.getService(TriggerDefinitionService.class);
    private static final TriggerDefinitionFactory TRIGGER_DEFINITION_FACTORY = LOCATOR.getFactory(TriggerDefinitionFactory.class);

    @Override
    public ListLoadResult<GwtTriggerDefinition> findAll() throws GwtKapuaException {
        List<GwtTriggerDefinition> gwtTriggerDefinitionList = new ArrayList<GwtTriggerDefinition>();
        try {
            TriggerDefinitionListResult result = TRIGGER_DEFINITION_SERVICE.query(new KapuaQuery((KapuaId) null));
            for (TriggerDefinition jsd : result.getItems()) {

                GwtTriggerDefinition gwtTriggerDefinition = KapuaGwtJobModelConverter.convertTriggerDefinition(jsd);

                setEnumOnTriggerProperty(gwtTriggerDefinition.getTriggerProperties());

                gwtTriggerDefinitionList.add(gwtTriggerDefinition);
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return new BaseListLoadResult<GwtTriggerDefinition>(gwtTriggerDefinitionList);
    }

    @Override
    public GwtTriggerDefinition find(String gwtTriggerDefinitionId) throws GwtKapuaException {
        KapuaId triggerDefinitionId = KapuaEid.parseCompactId(gwtTriggerDefinitionId);

        GwtTriggerDefinition gwtTriggerDefinition = null;
        try {
            TriggerDefinition triggerDefinition = TRIGGER_DEFINITION_SERVICE.find(null, triggerDefinitionId);
            if (triggerDefinition != null) {
                gwtTriggerDefinition = KapuaGwtJobModelConverter.convertTriggerDefinition(triggerDefinition);

                setEnumOnTriggerProperty(gwtTriggerDefinition.getTriggerProperties());
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtTriggerDefinition;
    }

    @Override
    public GwtTriggerProperty trickGwt() {
        return null;
    }

    /**
     * Set the {@link GwtTriggerProperty#isEnum()} property. This cannot be performed in *.shared.* packages (entity converters are in that package), since `Class.forName` is not present in the JRE
     * Emulation library.
     *
     * @param triggerProperties
     * @throws ClassNotFoundException
     */
    private void setEnumOnTriggerProperty(List<GwtTriggerProperty> triggerProperties) throws ClassNotFoundException {
        for (GwtTriggerProperty gjsp : triggerProperties) {
            gjsp.setEnum(Class.forName(gjsp.getPropertyType()).isEnum());
        }
    }

}
