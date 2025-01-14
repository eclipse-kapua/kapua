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
package org.eclipse.kapua.app.console.module.job.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaIdFactory;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.job.targets.JobTarget;
import org.eclipse.kapua.service.job.targets.JobTargetAttributes;
import org.eclipse.kapua.service.job.targets.JobTargetFactory;
import org.eclipse.kapua.service.job.targets.JobTargetListResult;
import org.eclipse.kapua.service.job.targets.JobTargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobTargetExporterServlet extends HttpServlet {

    private static final long serialVersionUID = -2533869595709953567L;
    private static final Logger logger = LoggerFactory.getLogger(JobTargetExporterServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String reqPathInfo = request.getPathInfo();
        if (reqPathInfo != null) {
            response.sendError(404);
            return;
        }

        internalDoGet(request, response);
    }

    private void internalDoGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // parameter extraction
            String format = request.getParameter("format");
            final String scopeId = request.getParameter("scopeId");
            final String jobId = request.getParameter("jobId");

            // data exporter
            JobTargetExporter jobTargetExporter;
            if ("csv".equals(format)) {
                jobTargetExporter = new JobTargetExporterCsv(response);
            } else {
                throw new IllegalArgumentException("format");
            }

            if (scopeId == null || scopeId.isEmpty()) {
                throw new IllegalArgumentException("scopeId");
            }

            if (jobId == null || jobId.isEmpty()) {
                throw new IllegalArgumentException("jobId");
            }
            // get the job and the targets and append them to the exporter
            KapuaLocator locator = KapuaLocator.getInstance();
            KapuaIdFactory kapuaIdFactory = locator.getFactory(KapuaIdFactory.class);
            JobTargetService jobTargetService = locator.getService(JobTargetService.class);
            JobTargetFactory jobTargetFactory = locator.getFactory(JobTargetFactory.class);

            jobTargetExporter.init(scopeId, jobId);

            int offset = 0;

            KapuaQuery jobTargetQuery = new KapuaQuery(kapuaIdFactory.newKapuaId(scopeId));
            jobTargetQuery.setPredicate(jobTargetQuery.attributePredicate(JobTargetAttributes.JOB_ID, kapuaIdFactory.newKapuaId(jobId)));
            // paginate through the matching message
            jobTargetQuery.setLimit(250);

            KapuaListResult<JobTarget> totalJobTargets = new JobTargetListResult();
            KapuaListResult<JobTarget> results;
            do {
                jobTargetQuery.setOffset(offset);
                results = jobTargetService.query(jobTargetQuery);
                if (results.getSize() > 0) {
                    totalJobTargets.addItems(results.getItems());
                }
                offset += totalJobTargets.getSize();
            } while (results.getSize() > 0);

            jobTargetExporter.append(totalJobTargets);

            // Close things up
            jobTargetExporter.close();
        } catch (IllegalArgumentException iae) {
            logger.info("Failed to export", iae);
            response.sendError(400, "Illegal value for query parameter: " + iae.getMessage());
            return;
        } catch (KapuaEntityNotFoundException eenfe) {
            response.sendError(400, eenfe.getMessage());
            return;
        } catch (KapuaUnauthenticatedException eiae) {
            response.sendError(401, eiae.getMessage());
            return;
        } catch (KapuaIllegalAccessException eiae) {
            response.sendError(403, eiae.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Error creating device export", e);
            throw new ServletException(e);
        }
    }
}
