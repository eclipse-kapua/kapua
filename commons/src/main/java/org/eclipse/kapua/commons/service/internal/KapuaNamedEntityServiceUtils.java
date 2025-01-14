/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.service.internal;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaDuplicateNameInAnotherAccountError;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.service.KapuaEntityService;

/**
 * Util class that offers checks on the {@link KapuaNamedEntity#getName()} uniqueness in different flavors.
 *
 * @since 2.0.0
 * @deprecated since 2.0.0 - use {@link org.eclipse.kapua.storage.KapuaNamedEntityRepository} instead
 */
@Deprecated
public class KapuaNamedEntityServiceUtils {

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    /**
     * Constructor.
     *
     * @since 2.0.0
     * @deprecated since 2.0.0 - use {@link org.eclipse.kapua.storage.KapuaNamedEntityRepository} instead
     */
    @Deprecated
    private KapuaNamedEntityServiceUtils() {
    }

    /**
     * Checks that the given {@link KapuaNamedEntityCreator#getName()} is unique within the {@link KapuaNamedEntityCreator#getScopeId()}.
     *
     * @param kapuaNamedEntityService
     *         The {@link KapuaEntityService} to use. Usually it is the same service that is using this utility.
     * @param creator
     *         The {@link KapuaNamedEntityCreator} to check.
     * @param <E>
     *         The {@link KapuaNamedEntity} type.
     * @param <C>
     *         The {@link KapuaNamedEntityCreator} type.
     * @throws KapuaDuplicateNameException
     *         if the {@link KapuaNamedEntityCreator#getName()} is duplicated within the {@link KapuaNamedEntityCreator#getScopeId()}.
     * @throws KapuaException
     *         if any other error occurs.
     * @since 2.0.0
     * @deprecated since 2.0.0 - use {@link org.eclipse.kapua.storage.KapuaNamedEntityRepository} instead
     */
    @Deprecated
    public static <E extends KapuaNamedEntity, C extends KapuaNamedEntityCreator<E>> void checkEntityNameUniqueness(@NotNull KapuaEntityService<E, C> kapuaNamedEntityService, @NotNull C creator)
            throws KapuaException {
        checkEntityNameUniqueness(kapuaNamedEntityService, creator, Collections.emptyList());
    }

    /**
     * Checks that the given {@link KapuaNamedEntityCreator#getName()} is unique within the {@link KapuaNamedEntityCreator#getScopeId()} with additional {@link QueryPredicate}s to customize the
     * check.
     *
     * @param kapuaNamedEntityService
     *         The {@link KapuaEntityService} to use. Usually it is the same service that is using this utility.
     * @param creator
     *         The {@link KapuaNamedEntityCreator} to check.
     * @param additionalPredicates
     *         The additional {@link QueryPredicate}s to customize the check
     * @param <E>
     *         The {@link KapuaNamedEntity} type.
     * @param <C>
     *         The {@link KapuaNamedEntityCreator} type.
     * @throws KapuaDuplicateNameException
     *         if the {@link KapuaNamedEntityCreator#getName()} is duplicated within the {@link KapuaNamedEntityCreator#getScopeId()}.
     * @throws KapuaException
     *         if any other error occurs.
     * @since 2.0.0
     * @deprecated since 2.0.0 - use {@link org.eclipse.kapua.storage.KapuaNamedEntityRepository} instead
     */
    @Deprecated
    public static <E extends KapuaNamedEntity, C extends KapuaNamedEntityCreator<E>> void checkEntityNameUniqueness(@NotNull KapuaEntityService<E, C> kapuaNamedEntityService, @NotNull C creator,
            @NotNull List<QueryPredicate> additionalPredicates) throws KapuaException {
        KapuaQuery query = new KapuaQuery();
        query.setScopeId(creator.getScopeId());

        AndPredicate andPredicate = query.andPredicate();
        andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.NAME, creator.getName()));

        for (QueryPredicate additionalPredicate : additionalPredicates) {
            andPredicate.and(additionalPredicate);
        }

        query.setPredicate(andPredicate);

        if (kapuaNamedEntityService.count(query) > 0) {
            throw new KapuaDuplicateNameException(creator.getName());
        }
    }

    /**
     * Checks that the given {@link KapuaNamedEntity#getName()} is unique within the {@link KapuaNamedEntity#getScopeId()}.
     *
     * @param kapuaNamedEntityService
     *         The {@link KapuaEntityService} to use. Usually it is the same service that is using this utility.
     * @param entity
     *         The {@link KapuaNamedEntity} to check.
     * @param <E>
     *         The {@link KapuaNamedEntity} type.
     * @throws KapuaDuplicateNameException
     *         if the {@link KapuaNamedEntity#getName()} is duplicated within the {@link KapuaNamedEntity#getScopeId()}.
     * @throws KapuaException
     *         if any other error occurs.
     * @since 2.0.0
     * @deprecated since 2.0.0 - use {@link org.eclipse.kapua.storage.KapuaNamedEntityRepository} instead
     */
    @Deprecated
    public static <E extends KapuaNamedEntity> void checkEntityNameUniqueness(@NotNull KapuaEntityService<E, ?> kapuaNamedEntityService, @NotNull E entity) throws KapuaException {
        checkEntityNameUniqueness(kapuaNamedEntityService, entity, Collections.emptyList());
    }

    /**
     * Checks that the given {@link KapuaNamedEntity#getName()} is unique within the {@link KapuaNamedEntity#getScopeId()} with additional {@link QueryPredicate}s to customize the check.
     *
     * @param kapuaNamedEntityService
     *         The {@link KapuaEntityService} to use. Usually it is the same service that is using this utility.
     * @param entity
     *         The {@link KapuaNamedEntity} to check.
     * @param additionalPredicates
     *         The additional {@link QueryPredicate}s to customize the check
     * @param <E>
     *         The {@link KapuaNamedEntity} type.
     * @throws KapuaDuplicateNameException
     *         if the {@link KapuaNamedEntity#getName()} is duplicated within the {@link KapuaNamedEntity#getScopeId()}.
     * @throws KapuaException
     *         if any other error occurs.
     * @since 2.0.0
     * @deprecated since 2.0.0 - use {@link org.eclipse.kapua.storage.KapuaNamedEntityRepository} instead
     */
    @Deprecated
    public static <E extends KapuaNamedEntity> void checkEntityNameUniqueness(@NotNull KapuaEntityService<E, ?> kapuaNamedEntityService, @NotNull E entity,
            @NotNull List<QueryPredicate> additionalPredicates) throws KapuaException {
        KapuaQuery query = new KapuaQuery();
        query.setScopeId(entity.getScopeId());

        AndPredicate andPredicate = query.andPredicate();
        andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.NAME, entity.getName()));
        andPredicate.and(query.attributePredicate(KapuaNamedEntityAttributes.ENTITY_ID, entity.getId(), AttributePredicate.Operator.NOT_EQUAL));

        for (QueryPredicate additionalPredicate : additionalPredicates) {
            andPredicate.and(additionalPredicate);
        }

        query.setPredicate(andPredicate);

        if (kapuaNamedEntityService.count(query) > 0) {
            throw new KapuaDuplicateNameException(entity.getName());
        }
    }

    /**
     * Checks that the given {@link KapuaNamedEntityCreator#getName()} is unique within all the scopes.
     *
     * @param kapuaNamedEntityService
     *         The {@link KapuaEntityService} to use. Usually it is the same service that is using this utility.
     * @param creator
     *         The {@link KapuaNamedEntityCreator} to check.
     * @param <E>
     *         The {@link KapuaNamedEntity} type.
     * @param <C>
     *         The {@link KapuaNamedEntityCreator} type.
     * @throws KapuaDuplicateNameException
     *         if the {@link KapuaNamedEntityCreator#getName()} is duplicated within all the scopes
     * @throws KapuaException
     *         if any other error occurs.
     * @since 2.0.0
     * @deprecated since 2.0.0 - use {@link org.eclipse.kapua.storage.KapuaNamedEntityRepository} instead
     */
    @Deprecated
    public static <E extends KapuaNamedEntity, C extends KapuaNamedEntityCreator<E>> void checkEntityNameUniquenessInAllScopes(@NotNull KapuaEntityService<E, C> kapuaNamedEntityService,
            @NotNull C creator) throws KapuaException {
        KapuaQuery query = new KapuaQuery();
        query.setPredicate(query.attributePredicate(KapuaNamedEntityAttributes.NAME, creator.getName()));

        if (KapuaSecurityUtils.doPrivileged(() -> kapuaNamedEntityService.count(query) > 0)) {
            throw new KapuaDuplicateNameInAnotherAccountError(creator.getName());
        }
    }

    /**
     * Checks that the given {@link KapuaNamedEntity#getName()} is unique within all the scopes.
     *
     * @param kapuaNamedEntityService
     *         The {@link KapuaEntityService} to use. Usually it is the same service that is using this utility.
     * @param entity
     *         The {@link KapuaNamedEntity} to check.
     * @param <E>
     *         The {@link KapuaNamedEntity} type.
     * @throws KapuaDuplicateNameException
     *         if the {@link KapuaNamedEntity#getName()} is duplicated within all the scopes
     * @throws KapuaException
     *         if any other error occurs.
     * @since 2.0.0
     * @deprecated since 2.0.0 - use {@link org.eclipse.kapua.storage.KapuaNamedEntityRepository} instead
     */
    @Deprecated
    public static <E extends KapuaNamedEntity> void checkEntityNameUniquenessInAllScopes(@NotNull KapuaEntityService<E, ?> kapuaNamedEntityService, @NotNull E entity) throws KapuaException {
        KapuaQuery query = new KapuaQuery();
        query.setPredicate(
                query.andPredicate(
                        query.attributePredicate(KapuaNamedEntityAttributes.NAME, entity.getName()),
                        query.attributePredicate(KapuaNamedEntityAttributes.ENTITY_ID, entity.getId(), AttributePredicate.Operator.NOT_EQUAL)
                )
        );

        if (KapuaSecurityUtils.doPrivileged(() -> kapuaNamedEntityService.count(query) > 0)) {
            throw new KapuaDuplicateNameInAnotherAccountError(entity.getName());
        }
    }
}
