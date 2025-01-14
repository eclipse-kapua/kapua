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
package org.eclipse.kapua.app.console.module.device.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaIdFactory;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventAttributes;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventQuery;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceEventExporterServlet extends HttpServlet {

    private static final long serialVersionUID = -2533869595709953567L;
    private static final Logger logger = LoggerFactory.getLogger(DeviceEventExporterServlet.class);

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
            final String deviceId = request.getParameter("deviceId");
            Date startDate = new Date(Long.parseLong(request.getParameter("startDate")));
            Date endDate = new Date(Long.parseLong(request.getParameter("endDate")));

            // data exporter
            DeviceEventExporter deviceEventExporter;
            if ("csv".equals(format)) {
                deviceEventExporter = new DeviceEventExporterCsv(response);
            } else {
                throw new IllegalArgumentException("format");
            }

            if (scopeId == null || scopeId.isEmpty()) {
                throw new IllegalArgumentException("scopeId");
            }

            if (deviceId == null || deviceId.isEmpty()) {
                throw new IllegalArgumentException("deviceId");
            }
            // get the devices and append them to the exporter
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceEventService des = locator.getService(DeviceEventService.class);
            DeviceEventFactory def = locator.getFactory(DeviceEventFactory.class);

            final AccountService as = locator.getService(AccountService.class);
            final DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
            final KapuaIdFactory kif = locator.getFactory(KapuaIdFactory.class);

            Device device = KapuaSecurityUtils.doPrivileged(new Callable<Device>() {

                @Override
                public Device call() throws Exception {
                    return deviceRegistryService.find(KapuaEid.parseCompactId(scopeId), KapuaEid.parseCompactId(deviceId));
                }
            });

            Account account = KapuaSecurityUtils.doPrivileged(new Callable<Account>() {

                @Override
                public Account call() throws Exception {
                    return as.find(kif.newKapuaId(scopeId));
                }
            });

            deviceEventExporter.init(scopeId, device.getClientId());

            int offset = 0;

            // paginate through the matching message
            DeviceEventQuery query = new DeviceEventQuery(KapuaEid.parseCompactId(scopeId));
            query.setLimit(250);

            // Inserting filter parameter if specified
            AndPredicate andPred = query.andPredicate(
                    query.attributePredicate(DeviceEventAttributes.DEVICE_ID, KapuaEid.parseCompactId(deviceId), Operator.EQUAL),
                    query.attributePredicate(DeviceEventAttributes.RECEIVED_ON, startDate, Operator.GREATER_THAN),
                    query.attributePredicate(DeviceEventAttributes.RECEIVED_ON, endDate, Operator.LESS_THAN)
            );

            query.setPredicate(andPred);

            KapuaListResult<DeviceEvent> results;
            do {
                query.setOffset(offset);
                results = des.query(query);

                deviceEventExporter.append(results);

                offset += results.getSize();
                results.getSize();
            } while (results.getSize() > 0);

            // Close things up
            deviceEventExporter.close();
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
