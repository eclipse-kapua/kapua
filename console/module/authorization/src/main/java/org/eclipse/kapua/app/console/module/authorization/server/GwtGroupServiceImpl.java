/*******************************************************************************
 * Copyright (c) 2017, 2025 Eurotech and/or its affiliates and others
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

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.server.util.UserCreatedByModifiedByUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroup;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupCreator;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtGroupQuery;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtGroupService;
import org.eclipse.kapua.app.console.module.authorization.shared.util.GwtKapuaAuthorizationModelConverter;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.GroupCreator;
import org.eclipse.kapua.service.authorization.group.GroupFactory;
import org.eclipse.kapua.service.authorization.group.GroupListResult;
import org.eclipse.kapua.service.authorization.group.GroupQuery;
import org.eclipse.kapua.service.authorization.group.GroupService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GwtGroupServiceImpl extends KapuaRemoteServiceServlet implements GwtGroupService {

    private static final long serialVersionUID = 929002466564699535L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final GroupService GROUP_SERVICE = LOCATOR.getService(GroupService.class);
    private static final GroupFactory GROUP_FACTORY = LOCATOR.getFactory(GroupFactory.class);

    private static final String ENTITY_INFO = "entityInfo";

    @Override
    public GwtGroup create(GwtGroupCreator gwtGroupCreator) throws GwtKapuaException {
        GwtGroup gwtGroup = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtGroupCreator.getScopeId());

            GroupCreator groupCreator = GROUP_FACTORY.newCreator(scopeId, gwtGroupCreator.getName());
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
    public PagingLoadResult<GwtGroup> query(PagingLoadConfig loadConfig, GwtGroupQuery gwtGroupQuery) throws GwtKapuaException {
        try {
            GroupQuery groupQuery = GwtKapuaAuthorizationModelConverter.convertGroupQuery(loadConfig, gwtGroupQuery);

            GroupListResult groups = GROUP_SERVICE.query(groupQuery);

            List<GwtGroup> gwtGroupList = new ArrayList<GwtGroup>();
            if (!groups.isEmpty()) {

                Map<KapuaId, String> idUsernameMap = UserCreatedByModifiedByUtils.resolveFromListResult(groups);

                for (Group group : groups.getItems()) {
                    GwtGroup gwtGroup = KapuaGwtAuthorizationModelConverter.convertGroup(group);

                    gwtGroup.setCreatedByName(idUsernameMap.get(group.getCreatedBy()));
                    gwtGroup.setModifiedByName(idUsernameMap.get(group.getModifiedBy()));

                    gwtGroupList.add(gwtGroup);
                }
            }

            return new BasePagingLoadResult<GwtGroup>(gwtGroupList, loadConfig.getOffset(), groups.getTotalCount().intValue());
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
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
    public ListLoadResult<GwtGroupedNVPair> getGroupDescription(String scopeShortId, String groupShortId) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
            KapuaId groupId = KapuaEid.parseCompactId(groupShortId);

            Group group = GROUP_SERVICE.find(scopeId, groupId);

            List<GwtGroupedNVPair> gwtGroupDescription = new ArrayList<GwtGroupedNVPair>();
            if (group != null) {
                // gwtGroupDescription.add(new GwtGroupedNVPair("Entity", "Scope
                // Id", KapuaGwtAuthenticationModelConverter.convertKapuaId(group.getScopeId())));
                gwtGroupDescription.add(new GwtGroupedNVPair("accessGroupInfo", "accessGroupName", group.getName()));
                gwtGroupDescription.add(new GwtGroupedNVPair("accessGroupInfo", "accessGroupDescription", group.getDescription()));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupCreatedOn", group.getCreatedOn()));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupCreatedBy", UserCreatedByModifiedByUtils.resolveFromId(group.getCreatedBy())));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupModifiedOn", group.getModifiedOn()));
                gwtGroupDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "accessGroupModifiedBy", UserCreatedByModifiedByUtils.resolveFromId(group.getModifiedBy())));

            }

            return new BaseListLoadResult<GwtGroupedNVPair>(gwtGroupDescription);
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public List<GwtGroup> findAll(String scopeId) throws GwtKapuaException {
        List<GwtGroup> groupList = new ArrayList<GwtGroup>();
        GroupQuery query = GROUP_FACTORY.newQuery(GwtKapuaCommonsModelConverter.convertKapuaId(scopeId));
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
