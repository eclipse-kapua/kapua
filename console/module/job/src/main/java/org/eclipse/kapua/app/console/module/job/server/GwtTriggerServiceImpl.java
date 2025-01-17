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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTrigger;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTriggerCreator;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTriggerProperty;
import org.eclipse.kapua.app.console.module.job.shared.model.scheduler.GwtTriggerQuery;
import org.eclipse.kapua.app.console.module.job.shared.service.GwtTriggerService;
import org.eclipse.kapua.app.console.module.job.shared.util.GwtKapuaJobModelConverter;
import org.eclipse.kapua.app.console.module.job.shared.util.KapuaGwtJobModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.scheduler.trigger.Trigger;
import org.eclipse.kapua.service.scheduler.trigger.TriggerCreator;
import org.eclipse.kapua.service.scheduler.trigger.TriggerFactory;
import org.eclipse.kapua.service.scheduler.trigger.TriggerListResult;
import org.eclipse.kapua.service.scheduler.trigger.TriggerService;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinition;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionFactory;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionListResult;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionQuery;
import org.eclipse.kapua.service.scheduler.trigger.definition.TriggerDefinitionService;
import org.quartz.CronExpression;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.common.base.Strings;

public class GwtTriggerServiceImpl extends KapuaRemoteServiceServlet implements GwtTriggerService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final TriggerService TRIGGER_SERVICE = LOCATOR.getService(TriggerService.class);
    private static final TriggerFactory TRIGGER_FACTORY = LOCATOR.getFactory(TriggerFactory.class);

    private static final TriggerDefinitionService TRIGGER_DEFINITION_SERVICE = LOCATOR.getService(TriggerDefinitionService.class);
    private static final TriggerDefinitionFactory TRIGGER_DEFINITION_FACTORY = LOCATOR.getFactory(TriggerDefinitionFactory.class);

    private static final String TRIGGER_DEFINITION_INTERVAL_NAME = "Interval Job";
    private static final TriggerDefinition TRIGGER_DEFINITION_INTERVAL;

    private static final String TRIGGER_DEFINITION_CRON_NAME = "Cron Job";
    private static final TriggerDefinition TRIGGER_DEFINITION_CRON;

    private static final String TRIGGER_DEFINITION_DEVICE_CONNECT_NAME = "Device Connect";
    private static final TriggerDefinition TRIGGER_DEFINITION_DEVICE_CONNECT;

    static {
        try {
            TriggerDefinition triggerDefinitionInterval = null;
            TriggerDefinition triggerDefinitionCron = null;
            TriggerDefinition triggerDefinitionDeviceConnect = null;

            TriggerDefinitionListResult triggerDefinitions = KapuaSecurityUtils.doPrivileged(new Callable<TriggerDefinitionListResult>() {

                @Override
                public TriggerDefinitionListResult call() throws Exception {
                    TriggerDefinitionQuery query = new TriggerDefinitionQuery((KapuaId) null);
                    return TRIGGER_DEFINITION_SERVICE.query(query);
                }
            });

            for (TriggerDefinition td : triggerDefinitions.getItems()) {
                if (TRIGGER_DEFINITION_INTERVAL_NAME.equals(td.getName())) {
                    triggerDefinitionInterval = td;
                } else if (TRIGGER_DEFINITION_CRON_NAME.equals(td.getName())) {
                    triggerDefinitionCron = td;
                } else if (TRIGGER_DEFINITION_DEVICE_CONNECT_NAME.equals(td.getName())) {
                    triggerDefinitionDeviceConnect = td;
                }
            }

            TRIGGER_DEFINITION_INTERVAL = triggerDefinitionInterval;
            TRIGGER_DEFINITION_CRON = triggerDefinitionCron;
            TRIGGER_DEFINITION_DEVICE_CONNECT = triggerDefinitionDeviceConnect;

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public PagingLoadResult<GwtTrigger> findByJobId(PagingLoadConfig loadConfig, String gwtScopeId, String gwtJobId) throws GwtKapuaException {
        // Do query
        int totalLength = 0;
        List<GwtTrigger> gwtTriggerList = new ArrayList<GwtTrigger>();
        try {
            // Convert from GWT entity
            GwtTriggerQuery gwtTriggerQuery = new GwtTriggerQuery();
            gwtTriggerQuery.setScopeId(gwtScopeId);
            gwtTriggerQuery.setJobId(gwtJobId);

            KapuaQuery triggerQuery = GwtKapuaJobModelConverter.convertTriggerQuery(gwtTriggerQuery, loadConfig);

            // query
            TriggerListResult triggerListResult = TRIGGER_SERVICE.query(triggerQuery);
            totalLength = triggerListResult.getTotalCount().intValue();

            // Converto to GWT entity
            for (Trigger t : triggerListResult.getItems()) {

                String triggerDefinitionName = "";
                if (TRIGGER_DEFINITION_INTERVAL.getId().equals(t.getTriggerDefinitionId())) {
                    triggerDefinitionName = TRIGGER_DEFINITION_INTERVAL_NAME;
                } else if (TRIGGER_DEFINITION_CRON.getId().equals(t.getTriggerDefinitionId())) {
                    triggerDefinitionName = TRIGGER_DEFINITION_CRON_NAME;
                } else if (TRIGGER_DEFINITION_DEVICE_CONNECT.getId().equals(t.getTriggerDefinitionId())) {
                    triggerDefinitionName = TRIGGER_DEFINITION_DEVICE_CONNECT_NAME;
                }

                gwtTriggerList.add(KapuaGwtJobModelConverter.convertTrigger(t, triggerDefinitionName));
            }

        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BasePagingLoadResult<GwtTrigger>(gwtTriggerList, loadConfig.getOffset(), totalLength);
    }

    @Override
    public GwtTrigger create(GwtXSRFToken xsrfToken, GwtTriggerCreator gwtTriggerCreator) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        GwtTrigger gwtTrigger = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtTriggerCreator.getScopeId());

            TriggerCreator triggerCreator = new TriggerCreator(scopeId);
            triggerCreator.setName(gwtTriggerCreator.getTriggerName());
            triggerCreator.setStartsOn(gwtTriggerCreator.getStartsOn());
            triggerCreator.setEndsOn(gwtTriggerCreator.getEndsOn());
            triggerCreator.setTriggerProperties(GwtKapuaJobModelConverter.convertTriggerProperties(gwtTriggerCreator.getTriggerProperties()));

            String triggerDefinitionName = null;
            if (TRIGGER_DEFINITION_INTERVAL.getName().equals(gwtTriggerCreator.getTriggerType())) {
                triggerDefinitionName = TRIGGER_DEFINITION_INTERVAL_NAME;
                triggerCreator.setTriggerDefinitionId(TRIGGER_DEFINITION_INTERVAL.getId());

                triggerCreator.getTriggerProperties().add(TRIGGER_FACTORY.newTriggerProperty("interval", Integer.class.getName(), gwtTriggerCreator.getRetryInterval().toString()));
            } else if (TRIGGER_DEFINITION_CRON.getName().equals(gwtTriggerCreator.getTriggerType())) {
                triggerDefinitionName = TRIGGER_DEFINITION_CRON_NAME;
                triggerCreator.setTriggerDefinitionId(TRIGGER_DEFINITION_CRON.getId());

                triggerCreator.getTriggerProperties().add(TRIGGER_FACTORY.newTriggerProperty("cronExpression", String.class.getName(), gwtTriggerCreator.getCronScheduling()));
            } else if (TRIGGER_DEFINITION_DEVICE_CONNECT.getName().equals(gwtTriggerCreator.getTriggerType())) {
                triggerDefinitionName = TRIGGER_DEFINITION_DEVICE_CONNECT_NAME;
                triggerCreator.setTriggerDefinitionId(TRIGGER_DEFINITION_DEVICE_CONNECT.getId());
            }
            // Create the User
            Trigger trigger = TRIGGER_SERVICE.create(triggerCreator);

            // convert to GwtAccount and return
            // reload the user as we want to load all its permissions
            gwtTrigger = KapuaGwtJobModelConverter.convertTrigger(trigger, triggerDefinitionName);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtTrigger;
    }

    @Override
    public void delete(GwtXSRFToken xsrfToken, String gwtScopeId, String gwtTriggerId) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
            KapuaId triggerId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtTriggerId);

            TRIGGER_SERVICE.delete(scopeId, triggerId);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public boolean validateCronExpression(String cronExpression) throws GwtKapuaException {
        if (Strings.isNullOrEmpty(cronExpression)) {
            throw new GwtKapuaException(GwtKapuaErrorCode.UNABLE_TO_PARSE_CRON_EXPRESSION, null);
        }

        try {
            new CronExpression(cronExpression);
        } catch (ParseException paex) {
            throw new GwtKapuaException(GwtKapuaErrorCode.UNABLE_TO_PARSE_CRON_EXPRESSION, paex, paex.getMessage());
        }

        return true;
    }

    @Override
    public GwtTriggerProperty trickGwt() {
        return null;
    }
}
