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
import java.util.concurrent.Callable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceAttributes;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceStatus;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceExporterServlet extends HttpServlet {

    private static final long serialVersionUID = -2533869595709953567L;
    private static final Logger logger = LoggerFactory.getLogger(DeviceExporterServlet.class);

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
            final String scopeIdString = request.getParameter("scopeId");

            // data exporter
            DeviceExporter deviceExporter;
            if ("csv".equals(format)) {
                deviceExporter = new DeviceExporterCsv(response);
            } else {
                throw new IllegalArgumentException("format");
            }

            if (scopeIdString == null || scopeIdString.isEmpty()) {
                throw new IllegalArgumentException("scopeIdString");
            }
            // get the devices and append them to the exporter
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceRegistryService drs = locator.getService(DeviceRegistryService.class);
            DeviceFactory drf = locator.getFactory(DeviceFactory.class);
            final AccountService accountService = locator.getService(AccountService.class);
            Account account = KapuaSecurityUtils.doPrivileged(new Callable<Account>() {

                @Override
                public Account call() throws Exception {
                    return accountService.find(KapuaEid.parseCompactId(scopeIdString));
                }
            });

            deviceExporter.init(scopeIdString, account.getName());

            int offset = 0;

            // paginate through the matching message
            DeviceQuery query = new DeviceQuery(KapuaEid.parseCompactId(scopeIdString));
            query.setLimit(250);

            // Inserting filter parameter if specified
            AndPredicate andPred = query.andPredicate();

            String clientId = request.getParameter("clientId");
            if (clientId != null && !clientId.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.CLIENT_ID, clientId, Operator.STARTS_WITH));
            }

            String displayName = request.getParameter("displayName");
            if (displayName != null && !displayName.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.DISPLAY_NAME, displayName, Operator.STARTS_WITH));
            }

            String serialNumber = request.getParameter("serialNumber");
            if (serialNumber != null && !serialNumber.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.SERIAL_NUMBER, serialNumber));
            }

            String deviceStatus = request.getParameter("deviceStatus");
            if (deviceStatus != null && !deviceStatus.equals(GwtDeviceQueryPredicates.GwtDeviceStatus.ANY.name())) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.STATUS, DeviceStatus.valueOf(deviceStatus)));
            }

            String deviceConnectionStatus = request.getParameter("deviceConnectionStatus");
            if (deviceConnectionStatus != null && !deviceConnectionStatus.equals(GwtDeviceQueryPredicates.GwtDeviceConnectionStatus.ANY.name())) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.CONNECTION_STATUS, DeviceConnectionStatus.valueOf(deviceConnectionStatus)));
            }

            String iotFrameworkVersion = request.getParameter("iotFrameworkVersion");
            if (iotFrameworkVersion != null && !iotFrameworkVersion.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.APPLICATION_FRAMEWORK_VERSION, iotFrameworkVersion));
            }

            String applicationIdentifiers = request.getParameter("applicationIdentifiers");
            if (applicationIdentifiers != null && !applicationIdentifiers.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.APPLICATION_IDENTIFIERS, applicationIdentifiers, Operator.LIKE));
            }

            String customAttribute1 = request.getParameter("customAttribute1");
            if (customAttribute1 != null && !customAttribute1.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.CUSTOM_ATTRIBUTE_1, customAttribute1));
            }

            String customAttribute2 = request.getParameter("customAttribute2");
            if (customAttribute2 != null && !customAttribute2.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.CUSTOM_ATTRIBUTE_2, customAttribute2));
            }

            String groupId = request.getParameter("accessGroup");
            if (groupId != null && !groupId.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.GROUP_ID, KapuaEid.parseCompactId(groupId)));
            }

            String tagId = request.getParameter("tag");
            if (tagId != null && !tagId.isEmpty()) {
                andPred = andPred.and(query.attributePredicate(DeviceAttributes.TAG_IDS, new KapuaId[] { KapuaEid.parseCompactId(tagId) }));
            }

            String sortAttribute = request.getParameter("sortAttribute");
            if (sortAttribute != null && !sortAttribute.isEmpty()) {

                String sortOrderString = request.getParameter("sortOrder");
                SortOrder sortOrder;
                if (sortOrderString != null && !sortOrderString.isEmpty()) {
                    sortOrder = SortOrder.valueOf(sortOrderString);
                } else {
                    sortOrder = SortOrder.ASCENDING;
                }

                if (sortAttribute.compareTo("CLIENT_ID") == 0) {
                    query.setSortCriteria(query.fieldSortCriteria(DeviceAttributes.CLIENT_ID, sortOrder));
                } else if (sortAttribute.compareTo("DISPLAY_NAME") == 0) {
                    query.setSortCriteria(query.fieldSortCriteria(DeviceAttributes.DISPLAY_NAME, sortOrder));
                } else if (sortAttribute.compareTo("LAST_EVENT_ON") == 0) {
                    query.setSortCriteria(query.fieldSortCriteria(DeviceAttributes.LAST_EVENT_ON, sortOrder));
                }
            }

            query.setPredicate(andPred);
            query.addFetchAttributes(DeviceAttributes.CONNECTION);
            query.addFetchAttributes(DeviceAttributes.LAST_EVENT);

            KapuaListResult<Device> results;
            do {
                query.setOffset(offset);
                results = drs.query(query);

                deviceExporter.append(results);

                offset += results.getSize();
                results.getSize();
            } while (results.getSize() > 0);

            // Close things up
            deviceExporter.close();
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
