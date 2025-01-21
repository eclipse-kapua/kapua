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
package org.eclipse.kapua.app.console.module.data.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSetting;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSettingKeys;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.data.shared.util.KapuaGwtDataModelConverter;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.datastore.MessageStoreFactory;
import org.eclipse.kapua.service.datastore.MessageStoreService;
import org.eclipse.kapua.service.datastore.model.DatastoreMessage;
import org.eclipse.kapua.service.datastore.model.MessageListResult;
import org.eclipse.kapua.service.datastore.model.query.MessageField;
import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
import org.eclipse.kapua.service.datastore.model.query.predicate.DatastorePredicateFactory;
import org.eclipse.kapua.service.storable.model.query.SortDirection;
import org.eclipse.kapua.service.storable.model.query.SortField;
import org.eclipse.kapua.service.storable.model.query.predicate.AndPredicate;
import org.eclipse.kapua.service.storable.model.query.predicate.RangePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataExporterServlet extends HttpServlet {

    private static final long serialVersionUID = 226461063207179649L;

    private static final Logger LOG = LoggerFactory.getLogger(DataExporterServlet.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final MessageStoreFactory MESSAGE_STORE_FACTORY = LOCATOR.getFactory(MessageStoreFactory.class);

    private static final DatastorePredicateFactory DATASTORE_PREDICATE_FACTORY = LOCATOR.getFactory(DatastorePredicateFactory.class);

    private static final MessageStoreService MESSAGE_STORE_SERVICE = LOCATOR.getService(MessageStoreService.class);

    private static final int QUERY_PAGE = 250;

    private static final int MAX_PAGES = ConsoleSetting.getInstance().getInt(ConsoleSettingKeys.EXPORT_MAX_PAGES, 10);

    private static final int MAX_PAGE_SIZE = ConsoleSetting.getInstance().getInt(ConsoleSettingKeys.EXPORT_MAX_PAGE_SIZE, 10000);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reqPathInfo = request.getPathInfo();
        if (reqPathInfo != null) {
            response.sendError(404);
            return;
        }

        internalDoGet(request, response);
    }

    private void internalDoGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String format = request.getParameter("format");
            String scopeIdString = request.getParameter("scopeIdString");
            String topic = request.getParameter("topic");
            String device = request.getParameter("device");
            String asset = request.getParameter("asset");
            String[] headers = request.getParameterValues("headers");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String sortField = "timestamp";
            String sortDir = "ASC";
            String topicOrDevice;

            AndPredicate predicate = DATASTORE_PREDICATE_FACTORY.newAndPredicate();
            if (topic != null) {
                topicOrDevice = topic;
                predicate.getPredicates().add(DATASTORE_PREDICATE_FACTORY.newChannelMatchPredicate(topic.replaceFirst("/#$", "")));
            } else if (device != null) {
                topicOrDevice = device;
                predicate.getPredicates().add(DATASTORE_PREDICATE_FACTORY.newTermPredicate(MessageField.CLIENT_ID, device));
            } else if (asset != null) {
                topicOrDevice = asset;
                predicate.getPredicates().add(DATASTORE_PREDICATE_FACTORY.newTermPredicate(MessageField.CHANNEL, asset));
            } else {
                throw new IllegalArgumentException("topic, device");
            }

            if (startDate == null) {
                throw new IllegalArgumentException("startDate");
            }

            if (endDate == null) {
                throw new IllegalArgumentException("endDate");
            }

            if (headers == null) {
                throw new IllegalArgumentException("headers");
            }

            DataExporter dataExporter;
            if ("csv".equals(format)) {
                dataExporter = new DataExporterCsv(response, topicOrDevice);
            } else {
                throw new IllegalArgumentException("format");
            }
            dataExporter.init(headers);
            MessageQuery query = new MessageQuery(GwtKapuaCommonsModelConverter.convertKapuaId(scopeIdString));
            Date start = new Date(Long.valueOf(startDate));
            Date end = new Date(Long.valueOf(endDate));
            RangePredicate datePredicate = DATASTORE_PREDICATE_FACTORY.newRangePredicate(MessageField.TIMESTAMP, start, end);
            predicate.getPredicates().add(datePredicate);

            if (sortField != null && sortDir != null) {
                query.setSortFields(Collections.singletonList(SortField.of(sortField, SortDirection.valueOf(sortDir))));
            }

            query.setPredicate(predicate);
            MessageListResult result;
            long totalCount = 0;
            totalCount = KapuaGwtDataModelConverter.countEsDataCap10k(MESSAGE_STORE_SERVICE, query);

            long totalOffset = 0;
            query.setLimit(250);
            int maxRows = MAX_PAGE_SIZE * MAX_PAGES;

            do {
                int internalOffset = 0;
                do {
                    query.setOffset(internalOffset);
                    result = MESSAGE_STORE_SERVICE.query(query);
                    dataExporter.append(result.getItems());
                    internalOffset += result.getSize();
                } while (internalOffset < MAX_PAGE_SIZE && !result.isEmpty());
                totalOffset += internalOffset;
                if (!result.isEmpty()) {
                    DatastoreMessage lastMessage = result.getItems().get(result.getSize() - 1);
                    Date lastMessageDate = lastMessage.getTimestamp();
                    predicate.getPredicates().remove(datePredicate);
                    datePredicate = DATASTORE_PREDICATE_FACTORY.newRangePredicate(MessageField.TIMESTAMP, lastMessageDate, end);
                    predicate.getPredicates().add(datePredicate);
                }
            } while (totalOffset < Long.min(totalCount, maxRows));
            if (totalOffset >= maxRows) {
                dataExporter.append(MessageFormat.format("Warning! The query returned more than {0} results. Please refine the time range.", maxRows));
            }
            dataExporter.close();
        } catch (IllegalArgumentException iae) {
            response.sendError(400, "Illegal value for query parameter(s): " + iae.getMessage());
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
            LOG.error("Error creating data export", e);
            throw new ServletException(e);
        }
    }

}
