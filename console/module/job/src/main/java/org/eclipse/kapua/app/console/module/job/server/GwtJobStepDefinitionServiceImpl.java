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
package org.eclipse.kapua.app.console.module.job.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSetting;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSettingKeys;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepDefinition;
import org.eclipse.kapua.app.console.module.job.shared.model.GwtJobStepProperty;
import org.eclipse.kapua.app.console.module.job.shared.service.GwtJobStepDefinitionService;
import org.eclipse.kapua.app.console.module.job.shared.util.KapuaGwtJobModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinition;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionListResult;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionQuery;
import org.eclipse.kapua.service.job.step.definition.JobStepDefinitionService;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.google.common.base.Strings;

public class GwtJobStepDefinitionServiceImpl extends KapuaRemoteServiceServlet implements GwtJobStepDefinitionService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final JobStepDefinitionService JOB_STEP_DEFINITION_SERVICE = LOCATOR.getService(JobStepDefinitionService.class);

    private static final ConsoleSetting CONSOLE_SETTING = ConsoleSetting.getInstance();

    private static final String JOB_STEP_DEFINITION_EXCLUDE_REGEX = CONSOLE_SETTING.getString(ConsoleSettingKeys.JOB_STEP_DEFINITION_EXCLUDE_REGEX);
    private static final long serialVersionUID = 8660088632558036600L;

    @Override
    public ListLoadResult<GwtJobStepDefinition> findAll(String scopeIdString) throws GwtKapuaException {
        List<GwtJobStepDefinition> gwtJobStepDefinitionList = new ArrayList<GwtJobStepDefinition>();
        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeIdString);

            JobStepDefinitionListResult result = JOB_STEP_DEFINITION_SERVICE.query(new JobStepDefinitionQuery(scopeId));
            for (JobStepDefinition jsd : result.getItems()) {

                if (!Strings.isNullOrEmpty(JOB_STEP_DEFINITION_EXCLUDE_REGEX) && jsd.getName().matches(JOB_STEP_DEFINITION_EXCLUDE_REGEX)) {
                    continue;
                }

                GwtJobStepDefinition gwtJobStepDefinition = KapuaGwtJobModelConverter.convertJobStepDefinition(jsd);

                setEnumOnJobStepProperty(gwtJobStepDefinition.getStepProperties());

                gwtJobStepDefinitionList.add(gwtJobStepDefinition);
            }

            return new BaseListLoadResult<GwtJobStepDefinition>(gwtJobStepDefinitionList);
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public GwtJobStepDefinition find(String scopeIdString, String gwtJobStepDefinitionId) throws GwtKapuaException {

        GwtJobStepDefinition gwtJobStepDefinition = null;
        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeIdString);
            KapuaId jobStepDefinitionId = KapuaEid.parseCompactId(gwtJobStepDefinitionId);

            JobStepDefinition jobStepDefinition = JOB_STEP_DEFINITION_SERVICE.find(scopeId, jobStepDefinitionId);
            if (jobStepDefinition != null) {
                gwtJobStepDefinition = KapuaGwtJobModelConverter.convertJobStepDefinition(jobStepDefinition);

                setEnumOnJobStepProperty(gwtJobStepDefinition.getStepProperties());
            }
            return gwtJobStepDefinition;
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public GwtJobStepProperty trickGwt() {
        return null;
    }

    /**
     * Set the {@link GwtJobStepProperty#isEnum()} property. This cannot be performed in *.shared.* packages (entity converters are in that package), since `Class.forName` is not present in the JRE
     * Emulation library.
     *
     * @param jobStepProperties
     * @throws ClassNotFoundException
     */
    private void setEnumOnJobStepProperty(List<GwtJobStepProperty> jobStepProperties) throws ClassNotFoundException {
        for (GwtJobStepProperty gjsp : jobStepProperties) {
            gjsp.setEnum(Class.forName(gjsp.getPropertyType()).isEnum());
        }
    }

}
