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
package org.eclipse.kapua.app.console.module.tag.server;

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
import org.eclipse.kapua.app.console.module.api.shared.util.KapuaGwtCommonsModelConverter;
import org.eclipse.kapua.app.console.module.tag.shared.model.GwtTag;
import org.eclipse.kapua.app.console.module.tag.shared.model.GwtTagCreator;
import org.eclipse.kapua.app.console.module.tag.shared.model.GwtTagQuery;
import org.eclipse.kapua.app.console.module.tag.shared.service.GwtTagService;
import org.eclipse.kapua.app.console.module.tag.shared.util.GwtKapuaTagModelConverter;
import org.eclipse.kapua.app.console.module.tag.shared.util.KapuaGwtTagModelConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagCreator;
import org.eclipse.kapua.service.tag.TagListResult;
import org.eclipse.kapua.service.tag.TagQuery;
import org.eclipse.kapua.service.tag.TagService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GwtTagServiceImpl extends KapuaRemoteServiceServlet implements GwtTagService {

    private static final long serialVersionUID = 929002466564699535L;

    KapuaLocator locator = KapuaLocator.getInstance();

    DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);

    TagService tagService = locator.getService(TagService.class);

    final UserService userService = locator.getService(UserService.class);

    private static final String ENTITY_INFO = "entityInfo";

    @Override
    public GwtTag create(GwtTagCreator gwtTagCreator) throws GwtKapuaException {
        GwtTag gwtTag = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtTagCreator.getScopeId());
            TagCreator tagCreator = new TagCreator(scopeId, gwtTagCreator.getName());
            tagCreator.setDescription(gwtTagCreator.getDescription());

            Tag tag = tagService.create(tagCreator);

            gwtTag = KapuaGwtTagModelConverter.convertTag(tag);

        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return gwtTag;
    }

    @Override
    public GwtTag update(GwtTag gwtTag) throws GwtKapuaException {
        GwtTag gwtTagUpdated = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtTag.getScopeId());
            KapuaId tagId = KapuaEid.parseCompactId(gwtTag.getId());

            Tag tag = tagService.find(scopeId, tagId);

            if (tag != null) {
                tag.setName(gwtTag.getTagName());
                tag.setDescription(gwtTag.getUnescapedDescription());
                tagService.update(tag);
                gwtTagUpdated = KapuaGwtTagModelConverter.convertTag(tagService.find(tag.getScopeId(), tag.getId()));
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return gwtTagUpdated;
    }

    @Override
    public GwtTag find(String scopeShortId, String tagShortId) throws GwtKapuaException {
        KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
        KapuaId tagId = KapuaEid.parseCompactId(tagShortId);
        GwtTag gwtTag = null;
        try {
            Tag tag = tagService.find(scopeId, tagId);

            if (tag != null) {
                gwtTag = KapuaGwtTagModelConverter.convertTag(tag);
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return gwtTag;
    }

    @Override
    public PagingLoadResult<GwtTag> query(PagingLoadConfig loadConfig, final GwtTagQuery gwtTagQuery) throws GwtKapuaException {
        int totalLength = 0;
        List<GwtTag> gwtTagList = new ArrayList<GwtTag>();
        try {
            TagQuery tagQuery = GwtKapuaTagModelConverter.convertTagQuery(loadConfig, gwtTagQuery);

            TagListResult tags = tagService.query(tagQuery);
            totalLength = tags.getTotalCount().intValue();

            if (!tags.isEmpty()) {
                //TODO: #LAYER_VIOLATION - user lookup should not be done here (horribly inefficient)
                UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return userService.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User user : userListResult.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                for (Tag g : tags.getItems()) {
                    GwtTag gwtTag = KapuaGwtTagModelConverter.convertTag(g);
                    gwtTag.setCreatedByName(usernameMap.get(g.getCreatedBy().toCompactId()));
                    gwtTagList.add(gwtTag);
                }
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return new BasePagingLoadResult<GwtTag>(gwtTagList, loadConfig.getOffset(),
                totalLength);
    }

    @Override
    public void delete(String scopeIdString, String tagIdString) throws GwtKapuaException {

        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId tagId = GwtKapuaCommonsModelConverter.convertKapuaId(tagIdString);

            tagService.delete(scopeId, tagId);
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }

    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> getTagDescription(String scopeShortId, String tagShortId) throws GwtKapuaException {
        List<GwtGroupedNVPair> gwtTagDescription = new ArrayList<GwtGroupedNVPair>();
        try {
            final KapuaId scopeId = KapuaEid.parseCompactId(scopeShortId);
            KapuaId tagId = KapuaEid.parseCompactId(tagShortId);

            final Tag tag = tagService.find(scopeId, tagId);
            if (tag != null) {
                //TODO: #LAYER_VIOLATION - user lookup logic should not be done here (fetching all users is incredibly inefficient anyway)
                UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return userService.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User user : userListResult.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                // gwtTagDescription.add(new GwtGroupedNVPair("Entity", "Scope
                // Id", KapuaGwtCommonsModelConverter.convertKapuaId(tag.getScopeId())));
                gwtTagDescription.add(new GwtGroupedNVPair("tagInfo", "tagName", tag.getName()));
                gwtTagDescription.add(new GwtGroupedNVPair("tagInfo", "tagDescription", tag.getDescription()));
                gwtTagDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "tagModifiedOn", tag.getModifiedOn()));
                gwtTagDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "tagModifiedBy", tag.getModifiedBy() != null ? usernameMap.get(tag.getModifiedBy().toCompactId()) : null));
                gwtTagDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "tagCreatedOn", tag.getCreatedOn()));
                gwtTagDescription.add(new GwtGroupedNVPair(ENTITY_INFO, "tagCreatedBy", tag.getCreatedBy() != null ? usernameMap.get(tag.getCreatedBy().toCompactId()) : null));

            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        return new BaseListLoadResult<GwtGroupedNVPair>(gwtTagDescription);
    }

    @Override
    public List<GwtTag> findAll(String scopeId) throws GwtKapuaException {
        List<GwtTag> tagList = new ArrayList<GwtTag>();
        try {
            TagQuery query = new TagQuery(GwtKapuaCommonsModelConverter.convertKapuaId(scopeId));

            TagListResult result = tagService.query(query);

            for (Tag tag : result.getItems()) {
                tagList.add(KapuaGwtTagModelConverter.convertTag(tag));
            }
        } catch (KapuaException e) {
            KapuaExceptionHandler.handle(e);
        }
        return tagList;
    }

    @Override
    public PagingLoadResult<GwtTag> findByDeviceId(PagingLoadConfig loadConfig, String gwtScopeId, String gwtDeviceId) throws GwtKapuaException {
        KapuaId scopeId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtScopeId);
        KapuaId deviceId = GwtKapuaCommonsModelConverter.convertKapuaId(gwtDeviceId);

        try {
            Device device = deviceRegistryService.find(scopeId, deviceId);
            if (device.getTagIds().isEmpty()) {
                return new BasePagingLoadResult<GwtTag>(new ArrayList<GwtTag>(), 0, 0);
            }

            GwtTagQuery gwtTagQuery = new GwtTagQuery();
            gwtTagQuery.setScopeId(gwtScopeId);

            List<String> gwtTagIds = new ArrayList<String>();
            for (KapuaId tagId : device.getTagIds()) {
                gwtTagIds.add(KapuaGwtCommonsModelConverter.convertKapuaId(tagId));
            }
            gwtTagQuery.setIds(gwtTagIds);

            return query(loadConfig, gwtTagQuery);
        } catch (KapuaException e) {
            KapuaExceptionHandler.handle(e);
            return new BasePagingLoadResult<GwtTag>(new ArrayList<GwtTag>(), 0, 0);
        }
    }
}
