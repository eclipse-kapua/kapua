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
package org.eclipse.kapua.app.console.module.authorization.server;

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
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.util.GwtKapuaAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtGroupServiceImpl extends KapuaRemoteServiceServlet implements GwtGroupService {

    private static final long serialVersionUID = 929002466564699535L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final GroupService GROUP_SERVICE = LOCATOR.getService(GroupService.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);

    private static final String ENTITY_INFO = "entityInfo";

    @Override
    public GwtGroup create(GwtGroupCreator gwtGroupCreator) throws GwtKapuaException {
        GwtGroup gwtGroup = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtGroupCreator.getScopeId());

            GroupCreator groupCreator = new GroupCreator(scopeId, gwtGroupCreator.getName());
            groupCreator.setDescription(gwtGroupCreator.getDescription());
            Group group = GROUP_SERVICE.create(groupCreator);

            gwtGroup = KapuaGwtAuthorizationModelConverter.convertGroup(group);
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return gwtGroup;
    }

    @Override
    public GwtGroup update(GwtGroup gwtGroup) throws GwtKapuaException {
        GwtGroup gwtGroupUpdated = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtGroup.getScopeId());
            KapuaId groupId = KapuaEid.parseCompactId(gwtGroup.getId());

            Group group = GROUP_SERVICE.find(scopeId, groupId);

            if (group != null) {
                group.setName(gwtGroup.getGroupName());
                group.setDescription(gwtGroup.getUnescapedDescription());
                GROUP_SERVICE.update(group);
                gwtGroupUpdated = KapuaGwtAuthorizationModelConverter.convertGroup(GROUP_SERVICE.find(group.getScopeId(), group.getId()));
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return gwtGroupUpdated;
    }

    @Override
    public GwtGroup find(String scopeShortId, String groupShortId) throws GwtKapuaException {
        GwtGroup gwtGroup = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
            KapuaId groupId = KapuaEid.parseCompactId(groupShortId);

            Group group = GROUP_SERVICE.find(scopeId, groupId);

            if (group != null) {
                gwtGroup = KapuaGwtAuthorizationModelConverter.convertGroup(group);
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }

        return gwtGroup;
    }

    @Override
    public PagingLoadResult<GwtGroup> query(PagingLoadConfig loadConfig,
            final GwtGroupQuery gwtGroupQuery) throws GwtKapuaException {
        int totalLength = 0;
        List<GwtGroup> gwtGroupList = new ArrayList<GwtGroup>();
        try {
            GroupQuery groupQuery = GwtKapuaAuthorizationModelConverter.convertGroupQuery(loadConfig, gwtGroupQuery);
            GroupListResult groups = GROUP_SERVICE.query(groupQuery);
            totalLength = groups.getTotalCount().intValue();

            if (!groups.isEmpty()) {
                UserListResult usernames = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User user : usernames.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                for (Group g : groups.getItems()) {
                    GwtGroup gwtGroup = KapuaGwtAuthorizationModelConverter.convertGroup(g);
                    gwtGroup.setCreatedByName(usernameMap.get(g.getCreatedBy().toCompactId()));
                    gwtGroupList.add(gwtGroup);
                }
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }

        return new BasePagingLoadResult<GwtGroup>(gwtGroupList, loadConfig.getOffset(), totalLength);
    }

    @Override
    public void delete(String scopeIdString, String groupIdString) throws GwtKapuaException {

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId groupId = GwtKapuaCommonsModelConverter.convertKapuaId(groupIdString);

            GROUP_SERVICE.delete(scopeId, groupId);
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }

    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> getGroupDescription(String scopeShortId,
            String groupShortId) throws GwtKapuaException {
        List<GwtGroupedNVPair> gwtGroupDescription = new ArrayList<GwtGroupedNVPair>();
        try {
            final KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
            KapuaId groupId = KapuaEid.parseCompactId(groupShortId);

            final Group group = GROUP_SERVICE.find(scopeId, groupId);

            //TODO: #LAYER_VIOLATION - user lookup should not be done here (horribly inefficient)
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

            if (group != null) {
                // gwtGroupDescription.add(new GwtGroupedNVPair("Entity", "Scope
                // Id", KapuaGwtAuthenticationModelConverter.convertKapuaId(group.getScopeId())));
                gwtGroupDescription.add(new GwtGroupedNVPair("accessGroupInfo", "accessGroupName", group.getName()));
                gwtGroupDescription.add(new GwtGroupedNVPair("accessGroupInfo", "accessGroupDescription", group.getDescription()));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupModifiedOn", group.getModifiedOn()));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupModifiedBy", group.getModifiedBy() != null ? usernameMap.get(group.getModifiedBy().toCompactId()) : null));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupCreatedOn", group.getCreatedOn()));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupCreatedBy", group.getCreatedBy() != null ? usernameMap.get(group.getCreatedBy().toCompactId()) : null));

            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return new BaseListLoadResult<GwtGroupedNVPair>(gwtGroupDescription);
    }

    @Override
    public List<GwtGroup> findAll(String scopeId) throws GwtKapuaException {
        List<GwtGroup> groupList = new ArrayList<GwtGroup>();
        GroupQuery query = new GroupQuery(GwtKapuaCommonsModelConverter.convertKapuaId(scopeId));
        try {
            GroupListResult result = GROUP_SERVICE.query(query);
            for (Group group : result.getItems()) {
                groupList.add(KapuaGwtAuthorizationModelConverter.convertGroup(group));
            }
        } catch (KapuaException e) {
            KapuaExceptionHandler.handle(e);
        }
        return groupList;
    }

}
