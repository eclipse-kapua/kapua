/*******************************************************************************
 * Copyright (c) 2025, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.api.server.util;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserAttributes;
import org.eclipse.kapua.service.user.UserFactory;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Utils to resolve {@link KapuaEntity#getCreatedBy()} and {@link KapuaUpdatableEntity#getModifiedBy()} into respective {@link User#getName()}.
 *
 * @since 2.1.0
 */
public class UserCreatedByModifiedByUtils {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();
    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);
    private static final UserFactory USER_FACTORY = LOCATOR.getFactory(UserFactory.class);

    /**
     * Constructor
     *
     * @since 2.1.0
     */
    private UserCreatedByModifiedByUtils() {
    }

    /**
     * Creates a map of {@link User#getId()} and {@link User#getName()} starting from a {@link KapuaListResult} of {@link KapuaEntity}/{@link KapuaUpdatableEntity}
     * <p>
     * Retrieves all {@link KapuaEntity#getCreatedBy()} and {@link KapuaUpdatableEntity#getModifiedBy()}.
     * Then queries the {@link UserService} with the list of {@link User#getId()}.
     * </p>
     *
     * @param entities The {@link KapuaListResult} from which to extract data
     * @return A {@link Map} of {@link User#getId()} associated with the {@link User#getName()}.
     * @param <E> The {@link KapuaEntity} type
     * @throws KapuaException
     *
     * @since 2.1.0
     */
    public static <E extends KapuaEntity> Map<KapuaId, String> resolveFromListResult(KapuaListResult<E> entities) throws Exception {
        return resolveFromListResultAndFields(entities, new Callable<Set<KapuaId>>() {
            @Override
            public Set<KapuaId> call() {
                return Collections.emptySet();
            }
        });
    }

    /**
     * Creates a map of {@link User#getId()} and {@link User#getName()} starting from a {@link KapuaListResult} of {@link KapuaEntity}/{@link KapuaUpdatableEntity}
     * <p>
     * Retrieves all {@link KapuaEntity#getCreatedBy()} and {@link KapuaUpdatableEntity#getModifiedBy()} and additionally can extract more {@link User#getId()} from the {@link KapuaEntity} in the {@link KapuaListResult}.
     * Then queries the {@link UserService} with the list of {@link User#getId()}.
     * </p>
     *
     * @param entities The {@link KapuaListResult} from which to extract data
     * @return A {@link Map} of {@link User#getId()} associated with the {@link User#getName()}.
     * @param <E> The {@link KapuaEntity} type
     * @throws KapuaException
     *
     * @since 2.1.0
     */
    public static <E extends KapuaEntity> Map<KapuaId, String> resolveFromListResultAndFields(KapuaListResult<E> entities, Callable<Set<KapuaId>> additionalFieldsToResolve) throws Exception {

        // Build list of ids
        final Set<KapuaId> userIds = new HashSet<KapuaId>();

        for (KapuaEntity entity : entities.getItems()) {
            if (entity.getCreatedBy() != null) {
                userIds.add(entity.getCreatedBy());
            }

            // This handles `KapuaUpdatableEntity.modifiedBy`
            if (entity instanceof KapuaUpdatableEntity) {
                KapuaUpdatableEntity updatableEntity = (KapuaUpdatableEntity) entity;

                if (updatableEntity.getModifiedBy() != null) {
                    userIds.add(updatableEntity.getModifiedBy());
                }
            }
        }

        // Handles additional ids to resolve;
        userIds.addAll(additionalFieldsToResolve.call());

        // Resolve from ids
        return resolveFromIds(userIds);
    }

    /**
     * Resolves the given {@link User#getId()} to its {@link User#getName()}/
     *
     * @param userId The {@link User#getId()} to resolve
     * @return The {@link User#getName()} if found or {@code null}
     * @throws KapuaException
     * @since 2.1.0
     */
    public static String resolveFromId(final KapuaId userId) throws KapuaException {
        if (userId == null) {
            return null;
        }

        User resolvedUser = KapuaSecurityUtils.doPrivileged(new Callable<User>() {
            @Override
            public User call() throws Exception {
                return USER_SERVICE.findById(userId);
            }
        });

        return resolvedUser != null ?
                resolvedUser.getName() :
                null;
    }

    /**
     * Creates a map of {@link User#getId()} and {@link User#getName()} starting from a {@link Set} of {@link User#getId()}
     *
     * @param userIds The {@link Set} of {@link User#getId()}s to resolve
     * @return A {@link Map} of {@link User#getId()} associated with the {@link User#getName()}.
     * @throws KapuaException
     * @since 2.1.0
     */
    public static HashMap<KapuaId, String> resolveFromIds(final Set<KapuaId> userIds) throws KapuaException {
        // Query Users to find matching ids
        UserListResult users = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

            @Override
            public UserListResult call() throws Exception {
                UserQuery userQuery = USER_FACTORY.newQuery(KapuaId.ANY);

                userQuery.setPredicate(
                        userQuery.attributePredicate(UserAttributes.ENTITY_ID, userIds)
                );

                return USER_SERVICE.query(userQuery);
            }
        });

        // Produce map with id and username association
        HashMap<KapuaId, String> idAndUsernameMap = new HashMap<KapuaId, String>();
        for (User user : users.getItems()) {
            idAndUsernameMap.put(user.getId(), user.getName());
        }

        // Return the map
        return idAndUsernameMap;
    }
}
