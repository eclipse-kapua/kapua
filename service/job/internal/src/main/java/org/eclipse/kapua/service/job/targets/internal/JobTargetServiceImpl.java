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
package org.eclipse.kapua.service.job.targets.internal;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaEntityUniquenessException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.AbstractKapuaService;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.job.Job;
import org.eclipse.kapua.service.job.JobDomains;
import org.eclipse.kapua.service.job.JobService;
import org.eclipse.kapua.service.job.internal.JobEntityManagerFactory;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.job.targets.JobTargetCreator;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetQuery;
import org.eclipse.kapua.service.job.targets.JobTargetService;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link JobTargetService} implementation.
 *
 * @since 1.0.0
 */
@KapuaProvider
public class JobTargetServiceImpl extends AbstractKapuaService implements JobTargetService {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);
    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);

    private static final JobService JOB_SERVICE = LOCATOR.getService(JobService.class);

    public JobTargetServiceImpl() {
        super(JobEntityManagerFactory.getInstance(), null);
    }

    @Override
    public JobTarget create(JobTargetCreator jobTargetCreator) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(jobTargetCreator, "jobTargetCreator");
        ArgumentValidator.notNull(jobTargetCreator.getScopeId(), "jobTargetCreator.scopeId");
        ArgumentValidator.notNull(jobTargetCreator.getJobId(), "jobTargetCreator.jobId");
        ArgumentValidator.notNull(jobTargetCreator.getJobTargetId(), "jobTargetCreator.jobTargetId");

        //
        // Check access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.write, jobTargetCreator.getScopeId()));

        //
        // Check Job Existing
        Job job = JOB_SERVICE.find(jobTargetCreator.getScopeId(), jobTargetCreator.getJobId());
        if (job == null) {
            throw new KapuaEntityNotFoundException(Job.TYPE, jobTargetCreator.getJobId());
        }

        //
        // Check duplicate
        JobTargetQuery jobTargetQuery = new JobTargetQueryImpl(jobTargetCreator.getScopeId());
        jobTargetQuery.setPredicate(
                jobTargetQuery.andPredicate(
                        jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_ID, jobTargetCreator.getJobId()),
                        jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_TARGET_ID, jobTargetCreator.getJobTargetId())
                )
        );

        if (count(jobTargetQuery) > 0) {
            List<Map.Entry<String, Object>> uniquesFieldValues = new ArrayList<>();
            uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobTargetAttributes.SCOPE_ID, jobTargetCreator.getScopeId()));
            uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobTargetAttributes.JOB_ID, jobTargetCreator.getJobId()));
            uniquesFieldValues.add(new AbstractMap.SimpleEntry<>(JobTargetAttributes.ENTITY_ID, jobTargetCreator.getJobTargetId()));

            throw new KapuaEntityUniquenessException(JobTarget.TYPE, uniquesFieldValues);
        }

        //
        // Do create
        return entityManagerSession.doTransactedAction(em -> JobTargetDAO.create(em, jobTargetCreator));
    }

    @Override
    public JobTarget find(KapuaId scopeId, KapuaId jobTargetId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobTargetId, "jobTargetId");

        //
        // Check Access
        if (!AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.read, scopeId))) {
            AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.write, scopeId)); //backward compatibility check
        }

        //
        // Do find
        return entityManagerSession.doAction(em -> JobTargetDAO.find(em, scopeId, jobTargetId));
    }

    @Override
    public JobTargetListResult query(KapuaQuery query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.read, query.getScopeId()));

        //
        // Do query
        return entityManagerSession.doAction(em -> JobTargetDAO.query(em, query));
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.read, query.getScopeId()));

        //
        // Do query
        return entityManagerSession.doAction(em -> JobTargetDAO.count(em, query));
    }

    @Override
    public JobTarget update(JobTarget jobTarget) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(jobTarget, "jobTarget");
        ArgumentValidator.notNull(jobTarget.getScopeId(), "jobTarget.scopeId");
        ArgumentValidator.notNull(jobTarget.getId(), "jobTarget.id");
        ArgumentValidator.notNull(jobTarget.getStepIndex(), "jobTarget.stepIndex");
        ArgumentValidator.notNull(jobTarget.getStatus(), "jobTarget.status");

        //
        // Check access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.write, jobTarget.getScopeId()));

        //
        // Check existence
        if (find(jobTarget.getScopeId(), jobTarget.getId()) == null) {
            throw new KapuaEntityNotFoundException(jobTarget.getType(), jobTarget.getId());
        }

        //
        // Do update
        return entityManagerSession.doTransactedAction(em -> JobTargetDAO.update(em, jobTarget));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId jobTargetId) throws KapuaException {
        //
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(jobTargetId, "jobTargetId");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(JobDomains.JOB_DOMAIN, Actions.delete, scopeId));

        //
        // Check existence
        if (find(scopeId, jobTargetId) == null) {
            throw new KapuaEntityNotFoundException(JobTarget.TYPE, jobTargetId);
        }

        //
        // Do delete
        entityManagerSession.doTransactedAction(em -> JobTargetDAO.delete(em, scopeId, jobTargetId));
    }
}
