/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.endpoint.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.endpoint.client.EndpointModel;
import org.eclipse.kapua.app.console.module.endpoint.shared.model.GwtEndpoint;
import org.eclipse.kapua.app.console.module.endpoint.shared.model.GwtEndpointCreator;
import org.eclipse.kapua.app.console.module.endpoint.shared.model.GwtEndpointQuery;
import org.eclipse.kapua.app.console.module.endpoint.shared.service.GwtEndpointService;
import org.eclipse.kapua.app.console.module.endpoint.shared.util.GwtKapuaEndpointModelConverter;
import org.eclipse.kapua.app.console.module.endpoint.shared.util.KapuaGwtEndpointModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoCreator;
import org.eclipse.kapua.service.endpoint.EndpointInfoListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;
import org.eclipse.kapua.service.endpoint.EndpointUsage;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtEndpointServiceImpl extends KapuaRemoteServiceServlet implements GwtEndpointService {

    private static final long serialVersionUID = 929002466564699535L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final EndpointInfoService ENDPOINT_INFO_SERVICE = LOCATOR.getService(EndpointInfoService.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);

    private static final String ENDPOINT_INFO = "endpointInfo";
    private static final String ENTITY_INFO = "entityInfo";

    @Override
    public GwtEndpoint create(GwtXSRFToken xsrfToken, GwtEndpointCreator gwtEndpointCreator) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);
        GwtEndpoint gwtEndpoint = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtEndpointCreator.getScopeId());

            EndpointInfoCreator endpointCreator = new EndpointInfoCreator(scopeId);
            endpointCreator.setSchema(gwtEndpointCreator.getSchema());
            endpointCreator.setDns(gwtEndpointCreator.getDns());
            endpointCreator.setPort(gwtEndpointCreator.getPort().intValue());
            endpointCreator.setSecure(gwtEndpointCreator.getSecure());
            endpointCreator.setEndpointType(gwtEndpointCreator.getEndpointType());

            EndpointInfo endpointInfo = ENDPOINT_INFO_SERVICE.create(endpointCreator);

            gwtEndpoint = KapuaGwtEndpointModelConverter.convertEndpoint(endpointInfo);

        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return gwtEndpoint;
    }

    @Override
    public GwtEndpoint update(GwtXSRFToken xsrfToken, GwtEndpoint gwtEndpoint) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);
        GwtEndpoint gwtEndpointUpdated = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtEndpoint.getScopeId());
            KapuaId endpointId = KapuaEid.parseCompactId(gwtEndpoint.getId());

            EndpointInfo endpointInfo = ENDPOINT_INFO_SERVICE.find(scopeId, endpointId);

            if (endpointInfo != null) {
                endpointInfo.setSchema(gwtEndpoint.getSchema());
                endpointInfo.setDns(gwtEndpoint.getDns());
                endpointInfo.setPort(gwtEndpoint.getPort().intValue());
                endpointInfo.setSecure(gwtEndpoint.getSecure());

                ENDPOINT_INFO_SERVICE.update(endpointInfo);

                gwtEndpointUpdated = KapuaGwtEndpointModelConverter.convertEndpoint(ENDPOINT_INFO_SERVICE.find(endpointInfo.getScopeId(), endpointInfo.getId()));
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return gwtEndpointUpdated;
    }

    @Override
    public GwtEndpoint find(String scopeShortId, String endpointShortId) throws GwtKapuaException {
        KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
        KapuaId endpointId = KapuaEid.parseCompactId(endpointShortId);
        GwtEndpoint gwtEndpoint = null;
        try {
            EndpointInfo endpoint = ENDPOINT_INFO_SERVICE.find(scopeId, endpointId);

            if (endpoint != null) {
                gwtEndpoint = KapuaGwtEndpointModelConverter.convertEndpoint(endpoint);
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return gwtEndpoint;
    }

    @Override
    public PagingLoadResult<GwtEndpoint> query(PagingLoadConfig loadConfig, final GwtEndpointQuery gwtEndpointQuery, String section) throws GwtKapuaException {
        int totalLength = 0;
        List<GwtEndpoint> gwtEndpointList = new ArrayList<GwtEndpoint>();
        try {
            EndpointInfoQuery endpointQuery = GwtKapuaEndpointModelConverter.convertEndpointQuery(loadConfig, gwtEndpointQuery);

            EndpointInfoListResult endpoints = ENDPOINT_INFO_SERVICE.query(endpointQuery, section);
            totalLength = endpoints.getTotalCount().intValue();

            if (!endpoints.isEmpty()) {
                //TODO: #LAYER_VIOLATION - user lookup should not be done here
                UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User user : userListResult.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                for (EndpointInfo ei : endpoints.getItems()) {
                    GwtEndpoint gwtEndpoint = KapuaGwtEndpointModelConverter.convertEndpoint(ei);
                    gwtEndpoint.setCreatedByName(ei.getCreatedBy() != null ? usernameMap.get(ei.getCreatedBy().toCompactId()) : null);
                    gwtEndpoint.setModifiedByName(ei.getModifiedBy() != null ? usernameMap.get(ei.getModifiedBy().toCompactId()) : null);
                    gwtEndpointList.add(gwtEndpoint);
                }
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return new BasePagingLoadResult<GwtEndpoint>(gwtEndpointList, loadConfig.getOffset(),
                totalLength);
    }

    @Override
    public void delete(GwtXSRFToken xsrfToken, String scopeIdString, String endpointIdString) throws GwtKapuaException {
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId endpointId = GwtKapuaCommonsModelConverter.convertKapuaId(endpointIdString);

            ENDPOINT_INFO_SERVICE.delete(scopeId, endpointId);
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }

    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> getEndpointDescription(String scopeShortId, String endpointShortId) throws GwtKapuaException {
        List<GwtGroupedNVPair> gwtEndpointDescription = new ArrayList<GwtGroupedNVPair>();
        try {
            final KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
            KapuaId endpointId = KapuaEid.parseCompactId(endpointShortId);

            final EndpointInfo endpointInfo = ENDPOINT_INFO_SERVICE.find(scopeId, endpointId);

            if (endpointInfo != null) {
                UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User user : userListResult.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                gwtEndpointDescription.add(new GwtGroupedNVPair(ENDPOINT_INFO, "endpointSchema", endpointInfo.getSchema()));
                gwtEndpointDescription.add(new GwtGroupedNVPair(ENDPOINT_INFO, "endpointDns", endpointInfo.getDns()));
                gwtEndpointDescription.add(new GwtGroupedNVPair(ENDPOINT_INFO, "endpointPort", endpointInfo.getPort()));
                gwtEndpointDescription.add(new GwtGroupedNVPair(ENDPOINT_INFO, "endpointSecure", endpointInfo.getSecure()));

                List<String> usages = new ArrayList<String>();
                for (EndpointUsage eu : endpointInfo.getUsages()) {
                    usages.add(eu.getName());
                }
                gwtEndpointDescription.add(new GwtGroupedNVPair(ENDPOINT_INFO, "endpointUsages", usages));

                gwtEndpointDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "endpointModifiedOn", endpointInfo.getModifiedOn()));
                gwtEndpointDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "endpointModifiedBy",
                        endpointInfo.getModifiedBy() != null ? usernameMap.get(endpointInfo.getModifiedBy().toCompactId()) : null));
                gwtEndpointDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "endpointCreatedOn", endpointInfo.getCreatedOn()));
                gwtEndpointDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "endpointCreatedBy",
                        endpointInfo.getCreatedBy() != null ? usernameMap.get(endpointInfo.getCreatedBy().toCompactId()) : null));

            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return new BaseListLoadResult<GwtGroupedNVPair>(gwtEndpointDescription);
    }

    @Override
    public List<GwtEndpoint> findAll(String scopeId) throws GwtKapuaException {
        List<GwtEndpoint> endpointList = new ArrayList<GwtEndpoint>();

        try {
            EndpointInfoQuery query = new EndpointInfoQuery(GwtKapuaCommonsModelConverter.convertKapuaId(scopeId));
            EndpointInfoListResult result = ENDPOINT_INFO_SERVICE.query(query);

            for (EndpointInfo endpoint : result.getItems()) {
                endpointList.add(KapuaGwtEndpointModelConverter.convertEndpoint(endpoint));
            }
        } catch (KapuaException e) {
            KapuaExceptionHandler.handle(e);
        }
        return endpointList;
    }

    @Override
    public EndpointModel parseEndpointModel(EndpointModel endpointModel, String origin) throws GwtKapuaException {
        try {
            URL url;
            url = new URL(origin);
            String protocol = url.getProtocol();
            endpointModel.setSchema(protocol);
            int port = url.getPort();
            if (port == -1) {
                if (protocol.equalsIgnoreCase("http")) {
                    port = 80;
                } else if (protocol.equalsIgnoreCase("https")) {
                    port = 443;
                }
            }
            endpointModel.setPort(port == -1 ? 80 : port);
            endpointModel.setDns(url.getHost());
            endpointModel.setSecure(protocol.equals("https"));
            endpointModel.setEndpointType(EndpointInfo.ENDPOINT_TYPE_CORS);
            return endpointModel;
        } catch (MalformedURLException malformedURLException) {
            throw KapuaExceptionHandler.buildExceptionFromError(malformedURLException);
        }
    }

}
