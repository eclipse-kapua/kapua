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
package org.eclipse.kapua.job.engine.app.core.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.kapua.commons.rest.model.IsJobRunningResponse;
import org.eclipse.kapua.job.engine.JobStartOptions;
import org.eclipse.kapua.job.engine.app.core.jackson.mixin.IsJobRunningResponseMixin;
import org.eclipse.kapua.job.engine.app.core.jackson.mixin.JobStartOptionsMixin;
import org.eclipse.kapua.job.engine.app.core.jackson.mixin.JobStepPropertyMixin;
import org.eclipse.kapua.job.engine.app.core.jackson.mixin.KapuaIdMixin;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.job.step.definition.JobStepProperty;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public ObjectMapperProvider() {
        objectMapper = new ObjectMapper();
        objectMapper.addMixIn(KapuaId.class, KapuaIdMixin.class);
        objectMapper.addMixIn(JobStartOptions.class, JobStartOptionsMixin.class);
        objectMapper.addMixIn(JobStepProperty.class, JobStepPropertyMixin.class);
        objectMapper.addMixIn(IsJobRunningResponse.class, IsJobRunningResponseMixin.class);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }

}
