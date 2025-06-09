/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.EntityExistsException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.validation.constraints.Null;

import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.kapua.KapuaEntityExistsException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.jpa.EntityManager;
import org.eclipse.kapua.commons.model.AbstractKapuaUpdatableEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.KapuaNamedEntity;
import org.eclipse.kapua.model.KapuaNamedEntityAttributes;
import org.eclipse.kapua.model.KapuaUpdatableEntity;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate.Operator;
import org.eclipse.kapua.model.query.predicate.MatchPredicate;
import org.eclipse.kapua.model.query.predicate.OrPredicate;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.service.authorization.access.AccessInfo;
import org.eclipse.kapua.service.authorization.access.AccessInfoFactory;
import org.eclipse.kapua.service.authorization.access.AccessInfoService;
import org.eclipse.kapua.service.authorization.access.AccessPermission;
import org.eclipse.kapua.service.authorization.access.AccessPermissionListResult;
import org.eclipse.kapua.service.authorization.access.AccessPermissionService;
import org.eclipse.kapua.service.authorization.access.AccessRole;
import org.eclipse.kapua.service.authorization.access.AccessRoleListResult;
import org.eclipse.kapua.service.authorization.access.AccessRoleService;
import org.eclipse.kapua.service.authorization.group.Group;
import org.eclipse.kapua.service.authorization.group.Groupable;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.role.Role;
import org.eclipse.kapua.service.authorization.role.RolePermission;
import org.eclipse.kapua.service.authorization.role.RolePermissionListResult;
import org.eclipse.kapua.service.authorization.role.RolePermissionService;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.storage.KapuaEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

/**
 * {@link ServiceDAO} utility methods.
 *
 * @since 1.0.0
 * @deprecated since 2.0.0 - this is not testable, consider using {@link KapuaEntityRepository} instead
 */
@Deprecated
public class ServiceDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceDAO.class);

    private static final AccessInfoService ACCESS_INFO_SERVICE;
    private static final AccessInfoFactory ACCESS_INFO_FACTORY;

    private static final AccessPermissionService ACCESS_PERMISSION_SERVICE;
    private static final AccessRoleService ACCESS_ROLE_SERVICE;

    private static final RoleService ROLE_SERVICE;
    private static final RolePermissionService ROLE_PERMISSION_SERVICE;

    private static final String SQL_ERROR_CODE_CONSTRAINT_VIOLATION = "23505";

    private static final SystemSetting SYSTEM_SETTING = SystemSetting.getInstance();

    private static final String ESCAPE = SYSTEM_SETTING.getString(SystemSettingKey.DB_CHARACTER_ESCAPE, "\\");
    private static final String LIKE = SYSTEM_SETTING.getString(SystemSettingKey.DB_CHARACTER_WILDCARD_ANY, "%");
    private static final String ANY = SYSTEM_SETTING.getString(SystemSettingKey.DB_CHARACTER_WILDCARD_SINGLE, "_");

    private static final String ATTRIBUTE_SEPARATOR = ".";
    private static final String ATTRIBUTE_SEPARATOR_ESCAPED = "\\.";

    private static final String COMPARE_ERROR_MESSAGE = "Trying to compare a non-comparable value";

    static {
        KapuaLocator locator = null;
        try {
            locator = KapuaLocator.getInstance();
        } catch (ExceptionInInitializerError kre) {
            LOG.warn("KapuaLocator not available! Access Group feature may be not supported!", kre);
        }

        if (locator != null) {
            ACCESS_INFO_SERVICE = KapuaLocator.getInstance().getService(AccessInfoService.class);
            ACCESS_INFO_FACTORY = KapuaLocator.getInstance().getFactory(AccessInfoFactory.class);

            ACCESS_PERMISSION_SERVICE = KapuaLocator.getInstance().getService(AccessPermissionService.class);
            ACCESS_ROLE_SERVICE = KapuaLocator.getInstance().getService(AccessRoleService.class);

            ROLE_SERVICE = KapuaLocator.getInstance().getService(RoleService.class);
            ROLE_PERMISSION_SERVICE = KapuaLocator.getInstance().getService(RolePermissionService.class);
        } else {
            ACCESS_INFO_SERVICE = null;
            ACCESS_INFO_FACTORY = null;

            ACCESS_PERMISSION_SERVICE = null;
            ACCESS_ROLE_SERVICE = null;

            ROLE_SERVICE = null;
            ROLE_PERMISSION_SERVICE = null;
        }
    }

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected ServiceDAO() {
    }

    /**
     * Persists the {@link KapuaEntity}.
     * <p>
     * This method checks for the constraint violation and, in this case, it throws a specific exception ({@link KapuaEntityExistsException}).
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param entity
     *         The {@link KapuaEntity} to be persisted.
     * @return The persisted {@link KapuaEntity}.
     * @since 1.0.0
     */
    public static <E extends KapuaEntity> E create(@NonNull EntityManager em, @NonNull E entity) {
        try {
            em.persist(entity);
            em.flush();
            em.refresh(entity);
        } catch (EntityExistsException e) {
            throw new KapuaEntityExistsException(e, entity.getId());
        } catch (PersistenceException e) {
            if (isInsertConstraintViolation(e)) {
                KapuaEntity entityFound = em.find(entity.getClass(), entity.getId());
                if (entityFound == null) {
                    throw e;
                }
                throw new KapuaEntityExistsException(e, entity.getId());
            } else {
                throw e;
            }
        }

        return entity;
    }

    /**
     * Updates the {@link KapuaUpdatableEntity}.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param clazz
     *         The {@link KapuaUpdatableEntity} class. This must be the implementing {@code class}.
     * @param entity
     *         The {@link KapuaUpdatableEntity} to be updated.
     * @return The updated {@link KapuaUpdatableEntity}.
     * @throws KapuaEntityNotFoundException
     *         If the {@link KapuaEntity} does not exist.
     * @since 1.0.0
     */
    public static <E extends KapuaUpdatableEntity> E update(@NonNull EntityManager em, @NonNull Class<E> clazz, @NonNull E entity) throws KapuaEntityNotFoundException {
        //
        // Checking existence
        E entityToUpdate = em.find(clazz, entity.getId());

        //
        // Updating if not null
        if (entityToUpdate != null) {
            AbstractKapuaUpdatableEntity updatableEntity = (AbstractKapuaUpdatableEntity) entity;
            updatableEntity.setCreatedOn(entityToUpdate.getCreatedOn());
            updatableEntity.setCreatedBy(entityToUpdate.getCreatedBy());

            em.merge(entity);
            em.flush();
            em.refresh(entityToUpdate);
        } else {
            throw new KapuaEntityNotFoundException(clazz.getSimpleName(), entity.getId());
        }

        return entityToUpdate;
    }

    /**
     * Finds a {@link KapuaEntity}.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param clazz
     *         The {@link KapuaEntity} class. This must be the implementing {@code class}.
     * @param scopeId
     *         The {@link KapuaEntity#getScopeId()} the entity to be found.
     * @param entityId
     *         The {@link KapuaEntity#getId()} of the entity to be found.
     * @since 1.0.0
     */
    public static <E extends KapuaEntity> E find(@NonNull EntityManager em, @NonNull Class<E> clazz, @Null KapuaId scopeId, @NonNull KapuaId entityId) {
        //
        // Checking existence
        E entityToFind = em.find(clazz, entityId);

        // If 'null' ScopeId has been requested, it means that we need to look for ANY ScopeId.
        KapuaId scopeIdToMatch = scopeId != null ? scopeId : KapuaId.ANY;

        //
        // Return if not null and ScopeIds matches
        if (entityToFind != null) {
            if (KapuaId.ANY.equals(scopeIdToMatch)) { // If requested ScopeId is ANY, return whatever Entity has been found
                return entityToFind;
            } else if (scopeIdToMatch.equals(entityToFind.getScopeId())) { // If a specific ScopeId is requested, return Entity if given ScopeId matches Entity.scopeId
                return entityToFind;
            } else { // If no match, return no result
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Finds a {@link KapuaNamedEntity} by {@link KapuaNamedEntity#getName()}.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param clazz
     *         The {@link KapuaNamedEntity} class. This must be the implementing {@code class}.
     * @param value
     *         The value of the {@link KapuaNamedEntity#getName()} to search.
     * @return The {@link KapuaNamedEntity} found, or {@code null} if not found.
     * @throws NonUniqueResultException
     *         When more than one result is returned
     * @since 2.0.0
     */
    @Nullable
    public static <E extends KapuaNamedEntity> E findByName(@NonNull EntityManager em,
            @NonNull Class<E> clazz,
            @NonNull Object value) {
        return findByName(em, clazz, KapuaId.ANY, value);
    }

    /**
     * Finds a {@link KapuaNamedEntity} by {@link KapuaNamedEntity#getName()}.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param clazz
     *         The {@link KapuaNamedEntity} class. This must be the implementing {@code class}.
     * @param scopeId
     *         The {@link KapuaNamedEntity#getScopeId()} in which to look for results.
     * @param value
     *         The value of the field from which to search.
     * @return The {@link KapuaNamedEntity} found, or {@code null} if not found.
     * @throws NonUniqueResultException
     *         When more than one result is returned.
     * @since 1.0.0
     */
    @Nullable
    public static <E extends KapuaNamedEntity> E findByName(@NonNull EntityManager em,
            @NonNull Class<E> clazz,
            @NonNull KapuaId scopeId,
            @NonNull Object value) {
        return findByField(em, clazz, scopeId, KapuaNamedEntityAttributes.NAME, value);
    }

    /**
     * Find a {@link KapuaEntity} by one of its fields.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param clazz
     *         The {@link KapuaEntity} class. This must be the implementing {@code class}.
     * @param name
     *         The {@link KapuaEntity} name of the field from which to search.
     * @param value
     *         The value of the field from which to search.
     * @return The {@link KapuaEntity} found, or {@code null} if not found.
     * @throws NonUniqueResultException
     *         When more than one result is returned.
     * @since 1.0.0
     */
    @Nullable
    public static <E extends KapuaEntity> E findByField(@NonNull EntityManager em,
            @NonNull Class<E> clazz,
            @NonNull String name,
            @NonNull Object value) {
        return findByField(em, clazz, KapuaId.ANY, name, value);
    }

    /**
     * Find a {@link KapuaEntity} by one of its fields.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param clazz
     *         The {@link KapuaEntity} class. This must be the implementing {@code class}.
     * @param scopeId
     *         The {@link KapuaEntity#getScopeId()} in which to look for results.
     * @param name
     *         The {@link KapuaEntity} name of the field from which to search.
     * @param value
     *         The value of the field from which to search.
     * @return The {@link KapuaEntity} found, or {@code null} if not found.
     * @throws NonUniqueResultException
     *         When more than one result is returned.
     * @since 1.0.0
     */
    @Nullable
    public static <E extends KapuaEntity> E findByField(@NonNull EntityManager em,
            @NonNull Class<E> clazz,
            @NonNull KapuaId scopeId,
            @NonNull String name,
            @NonNull Object value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> criteriaSelectQuery = cb.createQuery(clazz);

        //
        // FROM
        Root<E> entityRoot = criteriaSelectQuery.from(clazz);

        //
        // SELECT
        criteriaSelectQuery.select(entityRoot);

        // name
        ParameterExpression<String> pName = cb.parameter(String.class, name);
        Predicate namePredicate = cb.equal(entityRoot.get(name), pName);

        ParameterExpression<KapuaId> pScopeId = null;

        if (!KapuaId.ANY.equals(scopeId)) {
            pScopeId = cb.parameter(KapuaId.class, KapuaEntityAttributes.SCOPE_ID);
            Predicate scopeIdPredicate = cb.equal(entityRoot.get(KapuaEntityAttributes.SCOPE_ID), pScopeId);

            Predicate andPredicate = cb.and(namePredicate, scopeIdPredicate);
            criteriaSelectQuery.where(andPredicate);
        } else {
            criteriaSelectQuery.where(namePredicate);
        }

        TypedQuery<E> query = em.createQuery(criteriaSelectQuery);
        query.setParameter(pName.getName(), value);

        if (pScopeId != null) {
            query.setParameter(pScopeId.getName(), scopeId);
        }

        //
        // QUERY!
        List<E> result = query.getResultList();
        switch (result.size()) {
        case 0:
            return null;
        case 1:
            return result.get(0);
        default:
            throw new NonUniqueResultException(String.format("Multiple %s results found for field %s with value %s", clazz.getName(), pName, value.toString()));
        }
    }

    /**
     * Queries the {@link KapuaEntity}es.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param interfaceClass
     *         {@link KapuaQuery} result entity interface class
     * @param implementingClass
     *         {@link KapuaQuery} result entity implementation class
     * @param resultContainer
     *         The {@link KapuaListResult} in which load the result. It must be empty.
     * @param kapuaQuery
     *         The {@link KapuaQuery} to perform.
     * @return The reference of the {@code resultContainer} parameter. Results are added to the given {@code resultContainer} parameter.
     * @throws KapuaException
     *         If filter predicates in the {@link KapuaQuery} are incorrect. See {@link #handleKapuaQueryPredicates(QueryPredicate, Map, CriteriaBuilder, Root, EntityType)}.
     * @since 1.0.0
     */
    public static <I extends KapuaEntity, E extends I, L extends KapuaListResult<I>> L query(@NonNull EntityManager em,
            @NonNull Class<I> interfaceClass,
            @NonNull Class<E> implementingClass,
            @NonNull L resultContainer,
            @NonNull KapuaQuery kapuaQuery)
            throws KapuaException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> criteriaSelectQuery = cb.createQuery(implementingClass);

        //
        // FROM
        Root<E> entityRoot = criteriaSelectQuery.from(implementingClass);
        EntityType<E> entityType = entityRoot.getModel();

        //
        // SELECT
        criteriaSelectQuery.select(entityRoot).distinct(true);

        // Fetch LAZY attributes if necessary
        for (String fetchAttribute : kapuaQuery.getFetchAttributes()) {
            if (entityType.getAttribute(fetchAttribute).isAssociation()) {
                entityRoot.fetch(entityType.getSingularAttribute(fetchAttribute), JoinType.LEFT);
            } else {
                entityRoot.fetch(fetchAttribute);
            }
        }

        //
        // WHERE
        QueryPredicate kapuaPredicates = kapuaQuery.getPredicate();
        // Add ScopeId to query if has been defined one specific
        if (kapuaQuery.getScopeId() != null && // Support for old method of querying for all ScopeIds (e.g.: query.setScopeId(null)
                !kapuaQuery.getScopeId().equals(KapuaId.ANY)) {// Support for new method of querying for all ScopeIds (e.g.: query.setScopeId(KapuaId.ANY)

            AndPredicate scopedAndPredicate = kapuaQuery.andPredicate(
                    kapuaQuery.attributePredicate(KapuaEntityAttributes.SCOPE_ID, kapuaQuery.getScopeId())
            );

            // Add existing query predicates
            if (kapuaQuery.getPredicate() != null) {
                scopedAndPredicate.and(kapuaQuery.getPredicate());
            }

            kapuaPredicates = scopedAndPredicate;
        }

        // Manage kapua query predicates to build the where clause.
        Map<ParameterExpression, Object> binds = new HashMap<>();
        Expression<Boolean> expr = handleKapuaQueryPredicates(kapuaPredicates,
                binds,
                cb,
                entityRoot,
                entityRoot.getModel());

        if (expr != null) {
            criteriaSelectQuery.where(expr);
        }

        //
        // ORDER BY
        // Default to the KapuaEntity id if no ordering is specified.
        Order order;
        if (kapuaQuery.getSortCriteria() != null || kapuaQuery.getDefaultSortCriteria() != null) {
            FieldSortCriteria sortCriteria = (FieldSortCriteria) MoreObjects.firstNonNull(kapuaQuery.getSortCriteria(), kapuaQuery.getDefaultSortCriteria());

            if (SortOrder.DESCENDING.equals(sortCriteria.getSortOrder())) {
                order = cb.desc(extractAttribute(entityRoot, sortCriteria.getAttributeName()));
            } else {
                order = cb.asc(extractAttribute(entityRoot, sortCriteria.getAttributeName()));
            }
        } else {
            order = cb.asc(entityRoot.get(entityType.getSingularAttribute(KapuaEntityAttributes.ENTITY_ID)));
        }
        criteriaSelectQuery.orderBy(order);

        //
        // QUERY!
        TypedQuery<E> query = em.createQuery(criteriaSelectQuery);

        // Populate query parameters
        binds.forEach(query::setParameter); // Whoah! This is very magic!

        // Set offset
        if (kapuaQuery.getOffset() != null) {
            query.setFirstResult(kapuaQuery.getOffset());
        }

        // Set limit
        if (kapuaQuery.getLimit() != null) {
            query.setMaxResults(kapuaQuery.getLimit() + 1);
        }

        // Finally querying!
        List<E> result = query.getResultList();

        // Check limit exceeded
        if (kapuaQuery.getLimit() != null &&
                result.size() > kapuaQuery.getLimit()) {
            result.remove(kapuaQuery.getLimit().intValue());
            resultContainer.setLimitExceeded(true);
        }

        if (Boolean.TRUE.equals(kapuaQuery.getAskTotalCount())) {
            resultContainer.setTotalCount(count(em, interfaceClass, implementingClass, kapuaQuery));
        }

        // Set results
        resultContainer.addItems(result);
        return resultContainer;
    }

    /**
     * Counts the {@link KapuaEntity}es.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param interfaceClass
     *         {@link KapuaQuery} result entity interface class
     * @param implementingClass
     *         {@link KapuaQuery} result entity implementation class
     * @param kapuaQuery
     *         The {@link KapuaQuery} to perform.
     * @return The number of {@link KapuaEntity}es that matched the filter predicates.
     * @throws KapuaException
     *         If filter predicates in the {@link KapuaQuery} are incorrect. See {@link #handleKapuaQueryPredicates(QueryPredicate, Map, CriteriaBuilder, Root, EntityType)}.
     * @since 1.0.0
     */
    public static <I extends KapuaEntity, E extends I> long count(@NonNull EntityManager em,
            @NonNull Class<I> interfaceClass,
            @NonNull Class<E> implementingClass,
            @NonNull KapuaQuery kapuaQuery)
            throws KapuaException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaSelectQuery = cb.createQuery(Long.class);

        //
        // FROM
        Root<E> entityRoot = criteriaSelectQuery.from(implementingClass);

        //
        // SELECT
        criteriaSelectQuery.select(cb.countDistinct(entityRoot));

        //
        // WHERE
        QueryPredicate kapuaPredicates = kapuaQuery.getPredicate();
        // Add ScopeId to query if has been defined one specific
        if (kapuaQuery.getScopeId() != null && // Support for old method of querying for all ScopeIds (e.g.: query.setScopeId(null)
                !kapuaQuery.getScopeId().equals(KapuaId.ANY)) {// Support for new method of querying for all ScopeIds (e.g.: query.setScopeId(KapuaId.ANY)

            AndPredicate scopedAndPredicate = kapuaQuery.andPredicate();

            AttributePredicate<KapuaId> scopeId = kapuaQuery.attributePredicate(KapuaEntityAttributes.SCOPE_ID, kapuaQuery.getScopeId());
            scopedAndPredicate.and(scopeId);

            if (kapuaQuery.getPredicate() != null) {
                scopedAndPredicate.and(kapuaQuery.getPredicate());
            }

            kapuaPredicates = scopedAndPredicate;
        }

        Map<ParameterExpression, Object> binds = new HashMap<>();
        Expression<Boolean> expr = handleKapuaQueryPredicates(kapuaPredicates,
                binds,
                cb,
                entityRoot,
                entityRoot.getModel());

        if (expr != null) {
            criteriaSelectQuery.where(expr);
        }

        //
        // COUNT!
        TypedQuery<Long> query = em.createQuery(criteriaSelectQuery);

        // Populate query parameters
        binds.forEach(query::setParameter);

        return query.getSingleResult();
    }

    /**
     * Deletes a {@link KapuaEntity}.
     *
     * @param em
     *         The {@link EntityManager} that holds the transaction.
     * @param clazz
     *         The {@link KapuaEntity} class. This must be the implementing {@code class}.
     * @param scopeId
     *         The {@link KapuaEntity#getScopeId()} of the entity to be deleted.
     * @param entityId
     *         The {@link KapuaEntity#getId()} of the entity to be deleted.
     * @return The deleted {@link KapuaEntity}.
     * @throws KapuaEntityNotFoundException
     *         If the {@link KapuaEntity} does not exists.
     * @since 1.0.0
     */
    public static <E extends KapuaEntity> E delete(@NonNull EntityManager em, @NonNull Class<E> clazz, @NonNull KapuaId scopeId, @NonNull KapuaId entityId)
            throws KapuaEntityNotFoundException {
        //
        // Checking existence
        E entityToDelete = find(em, clazz, scopeId, entityId);

        //
        // Deleting if found
        if (entityToDelete != null) {
            em.remove(entityToDelete);
            em.flush();
        } else {
            throw new KapuaEntityNotFoundException(clazz.getSimpleName(), entityId);
        }

        //
        // Returning deleted entity
        return entityToDelete;
    }

    //
    // Private Methods
    //

    /**
     * Handles {@link QueryPredicate} contained of a {@link KapuaQuery}.
     * <p>
     * It manages different types of {@link QueryPredicate} like:
     * <ul>
     * <li>{@link AttributePredicate}</li>
     * <li>{@link AndPredicate}</li>
     * <li>{@link OrPredicate}</li>
     * </ol>
     * <p>
     * It can be invoked recursively (i.e. to handle {@link AttributePredicate}s of the {@link AndPredicate}.
     *
     * @param queryPredicate
     *         The {@link QueryPredicate} to handle.
     * @param binds
     *         The {@link Map}&lg;{@link String}, {@link Object}&gt; of the query values.
     * @param cb
     *         The JPA {@link CriteriaBuilder} of the {@link javax.persistence.Query}.
     * @param userPermissionRoot
     *         The JPA {@link Root} of the {@link javax.persistence.Query}.
     * @param entityType
     *         The JPA {@link EntityType} of the {@link javax.persistence.Query}.
     * @return The handled {@link Predicate}
     * @throws KapuaException
     *         If any problem occurs.
     */
    private static <E> Predicate handleKapuaQueryPredicates(@NonNull QueryPredicate queryPredicate,
            @NonNull Map<ParameterExpression, Object> binds,
            @NonNull CriteriaBuilder cb,
            @NonNull Root<E> userPermissionRoot,
            @NonNull EntityType<E> entityType)
            throws KapuaException {
        Predicate predicate = null;
        if (queryPredicate instanceof AttributePredicate) {
            AttributePredicate<?> attributePredicate = (AttributePredicate<?>) queryPredicate;
            predicate = handleAttributePredicate(attributePredicate, binds, cb, userPermissionRoot, entityType);
        } else if (queryPredicate instanceof AndPredicate) {
            AndPredicate andPredicate = (AndPredicate) queryPredicate;
            predicate = handleAndPredicate(andPredicate, binds, cb, userPermissionRoot, entityType);
        } else if (queryPredicate instanceof OrPredicate) {
            OrPredicate orPredicate = (OrPredicate) queryPredicate;
            predicate = handleOrPredicate(orPredicate, binds, cb, userPermissionRoot, entityType);
        } else if (queryPredicate instanceof MatchPredicate) {
            MatchPredicate<?> matchPredicate = (MatchPredicate<?>) queryPredicate;
            OrPredicate orPredicate = new OrPredicate();
            for (String attributeName : matchPredicate.getAttributeNames()) {
                orPredicate.getPredicates().add(new AttributePredicate<>(attributeName, matchPredicate.getMatchTerm(), Operator.STARTS_WITH_IGNORE_CASE));
            }
            predicate = handleOrPredicate(orPredicate, binds, cb, userPermissionRoot, entityType);
        }
        return predicate;
    }

    private static <E> Predicate handleAndPredicate(@NonNull AndPredicate andPredicate,
            @NonNull Map<ParameterExpression, Object> binds,
            @NonNull CriteriaBuilder cb,
            @NonNull Root<E> entityRoot,
            @NonNull EntityType<E> entityType)
            throws KapuaException {

        Predicate[] jpaAndPredicates =
                handlePredicate(
                        andPredicate.getPredicates(),
                        binds,
                        cb,
                        entityRoot,
                        entityType);

        return cb.and(jpaAndPredicates);

    }

    private static <E> Predicate handleOrPredicate(@NonNull OrPredicate orPredicate,
            @NonNull Map<ParameterExpression, Object> binds,
            @NonNull CriteriaBuilder cb,
            @NonNull Root<E> entityRoot,
            @NonNull EntityType<E> entityType)
            throws KapuaException {

        Predicate[] jpaOrPredicates =
                handlePredicate(
                        orPredicate.getPredicates(),
                        binds,
                        cb,
                        entityRoot,
                        entityType);

        return cb.or(jpaOrPredicates);
    }

    private static <E> Predicate[] handlePredicate(@NonNull List<QueryPredicate> orPredicates,
            @NonNull Map<ParameterExpression, Object> binds,
            @NonNull CriteriaBuilder cb,
            @NonNull Root<E> entityRoot,
            @NonNull EntityType<E> entityType) throws KapuaException {
        Predicate[] jpaOrPredicates = new Predicate[orPredicates.size()];

        for (int i = 0; i < orPredicates.size(); i++) {
            Predicate expr = handleKapuaQueryPredicates(orPredicates.get(i), binds, cb, entityRoot, entityType);
            jpaOrPredicates[i] = expr;
        }
        return jpaOrPredicates;
    }

    private static <E> Predicate handleAttributePredicate(@NonNull AttributePredicate<?> attrPred,
            @NonNull Map<ParameterExpression, Object> binds,
            @NonNull CriteriaBuilder cb,
            @NonNull Root<E> entityRoot,
            @NonNull EntityType<E> entityType)
            throws KapuaException {
        Predicate expr;
        String attrName = attrPred.getAttributeName();

        // Parse attributes
        Object attributeValue = attrPred.getAttributeValue();
        if (attributeValue instanceof KapuaId && !(attributeValue instanceof KapuaEid)) {
            attributeValue = KapuaEid.parseKapuaId((KapuaId) attributeValue);
        }

        // Fields to query properties of sub attributes of the root entity
        Attribute<?, ?> attribute;
        if (attrName.contains(ATTRIBUTE_SEPARATOR)) {
            attribute = entityType.getAttribute(attrName.split(ATTRIBUTE_SEPARATOR_ESCAPED)[0]);
        } else {
            attribute = entityType.getAttribute(attrName);
        }

        // Convert old Object[] support to List<?>
        if (attributeValue instanceof Object[]) {
            attributeValue = Arrays.asList((Object[]) attributeValue);
        }

        // Support IN clause
        if (attributeValue instanceof Collection) {
            Collection<?> attributeValues = (Collection<?>) attributeValue;

            Expression<?> orPredicate = extractAttribute(entityRoot, attrName);

            Predicate[] orPredicates = attributeValues.stream()
                    .map(value -> {
                        Object predicateValue;
                        if (value instanceof KapuaId && !(value instanceof KapuaEid)) {
                            predicateValue = KapuaEid.parseKapuaId((KapuaId) value);
                        } else {
                            predicateValue = value;
                        }

                        return cb.equal(orPredicate, predicateValue);
                    }).toArray(Predicate[]::new);

            expr = cb.and(cb.or(orPredicates));
        } else {
            String strAttrValue;
            switch (attrPred.getOperator()) {
            case LIKE:
                strAttrValue = attributeValue.toString().replace(LIKE, ESCAPE + LIKE).replace(ANY, ESCAPE + ANY);
                ParameterExpression<String> pl = cb.parameter(String.class);
                binds.put(pl, LIKE + strAttrValue + LIKE);
                expr = cb.like(extractAttribute(entityRoot, attrName), pl);
                break;

            case LIKE_IGNORE_CASE:
                strAttrValue = attributeValue.toString().replace(LIKE, ESCAPE + LIKE).replace(ANY, ESCAPE + ANY).toLowerCase();
                ParameterExpression<String> plci = cb.parameter(String.class);
                binds.put(plci, LIKE + strAttrValue + LIKE);
                expr = cb.like(cb.lower(extractAttribute(entityRoot, attrName)), plci);
                break;

            case STARTS_WITH:
                strAttrValue = attributeValue.toString().replace(LIKE, ESCAPE + LIKE).replace(ANY, ESCAPE + ANY);
                ParameterExpression<String> psw = cb.parameter(String.class);
                binds.put(psw, strAttrValue + LIKE);
                expr = cb.like(extractAttribute(entityRoot, attrName), psw);
                break;

            case STARTS_WITH_IGNORE_CASE:
                strAttrValue = attributeValue.toString().replace(LIKE, ESCAPE + LIKE).replace(ANY, ESCAPE + ANY).toLowerCase();
                ParameterExpression<String> pswci = cb.parameter(String.class);
                binds.put(pswci, strAttrValue + LIKE);
                expr = cb.like(cb.lower(extractAttribute(entityRoot, attrName)), pswci);
                break;

            case IS_NULL:
                expr = cb.isNull(extractAttribute(entityRoot, attrName));
                break;

            case NOT_NULL:
                expr = cb.isNotNull(extractAttribute(entityRoot, attrName));
                break;

            case NOT_EQUAL:
                expr = cb.notEqual(extractAttribute(entityRoot, attrName), attributeValue);
                break;

            case GREATER_THAN:
                if (attributeValue instanceof Comparable && ArrayUtils.contains(attribute.getJavaType().getInterfaces(), Comparable.class)) {
                    Comparable comparableAttrValue = (Comparable<?>) attributeValue;
                    Expression<? extends Comparable> comparableExpression = extractAttribute(entityRoot, attrName);
                    expr = cb.greaterThan(comparableExpression, comparableAttrValue);
                } else {
                    throw new KapuaException(KapuaErrorCodes.ILLEGAL_ARGUMENT, COMPARE_ERROR_MESSAGE);
                }
                break;

            case GREATER_THAN_OR_EQUAL:
                if (attributeValue instanceof Comparable && ArrayUtils.contains(attribute.getJavaType().getInterfaces(), Comparable.class)) {
                    Expression<? extends Comparable> comparableExpression = extractAttribute(entityRoot, attrName);
                    Comparable comparableAttrValue = (Comparable<?>) attributeValue;
                    expr = cb.greaterThanOrEqualTo(comparableExpression, comparableAttrValue);
                } else {
                    throw new KapuaException(KapuaErrorCodes.ILLEGAL_ARGUMENT, COMPARE_ERROR_MESSAGE);
                }
                break;

            case LESS_THAN:
                if (attributeValue instanceof Comparable && ArrayUtils.contains(attribute.getJavaType().getInterfaces(), Comparable.class)) {
                    Expression<? extends Comparable> comparableExpression = extractAttribute(entityRoot, attrName);
                    Comparable comparableAttrValue = (Comparable<?>) attributeValue;
                    expr = cb.lessThan(comparableExpression, comparableAttrValue);
                } else {
                    throw new KapuaException(KapuaErrorCodes.ILLEGAL_ARGUMENT, COMPARE_ERROR_MESSAGE);
                }
                break;
            case LESS_THAN_OR_EQUAL:
                if (attributeValue instanceof Comparable && ArrayUtils.contains(attribute.getJavaType().getInterfaces(), Comparable.class)) {
                    Expression<? extends Comparable> comparableExpression = extractAttribute(entityRoot, attrName);
                    Comparable comparableAttrValue = (Comparable<?>) attributeValue;
                    expr = cb.lessThanOrEqualTo(comparableExpression, comparableAttrValue);
                } else {
                    throw new KapuaException(KapuaErrorCodes.ILLEGAL_ARGUMENT, COMPARE_ERROR_MESSAGE);
                }
                break;

            case EQUAL:
            default:
                expr = cb.equal(extractAttribute(entityRoot, attrName), attributeValue);
            }
        }
        return expr;
    }

    /**
     * Utility method that selects the correct {@link Root} attribute.
     * <p>
     * This method handles {@link Embedded} attributes and nested {@link KapuaEntity}es.
     * <p>
     * Filter predicates takes advantage of the dot notation to access {@link Embedded} attributes and nested {@link KapuaEntity}es.
     *
     * @param entityRoot
     *         The {@link Root} entity from which extract the attribute.
     * @param attributeName
     *         The full attribute name.
     * @return The {@link Path} expression that matches the given {@code attributeName} parameter.
     * @since 1.0.0
     */
    private static <E, P> Path<P> extractAttribute(@NonNull Root<E> entityRoot, @NonNull String attributeName) {
        Path<P> expressionPath = null;
        if (attributeName.contains(ATTRIBUTE_SEPARATOR)) {
            String[] pathComponents = attributeName.split(ATTRIBUTE_SEPARATOR_ESCAPED);
            Path<P> rootPath = entityRoot.get(pathComponents[0]);
            for (int i = 1; i < pathComponents.length; i++) {
                expressionPath = rootPath.get(pathComponents[i]);
                rootPath = expressionPath;
            }
        } else {
            expressionPath = entityRoot.get(attributeName);
        }
        return expressionPath;
    }

    /**
     * Handles the {@link Groupable} property of the {@link KapuaEntity}.
     *
     * @param query
     *         The {@link KapuaQuery} to manage.
     * @param domain
     *         The {@link Domain} inside which the {@link KapuaQuery} param targets.
     * @param groupPredicateName
     *         The name of the {@link Group} id field.
     * @since 1.0.0
     */
    protected static void handleKapuaQueryGroupPredicate(@NonNull KapuaQuery query, @NonNull Domain domain, @NonNull String groupPredicateName) throws KapuaException {
        KapuaSession kapuaSession = KapuaSecurityUtils.getSession();
        if (ACCESS_INFO_FACTORY != null) {
            if (kapuaSession != null && !kapuaSession.isTrustedMode()) {
                handleKapuaQueryGroupPredicate(kapuaSession, query, domain, groupPredicateName);
            }
        } else {
            LOG.warn("'Access Group Permission' feature is disabled");
        }
    }

    /**
     * @param kapuaSession
     * @param query
     * @param domain
     * @param groupPredicateName
     * @throws KapuaException
     * @since 1.0.0
     */
    private static void handleKapuaQueryGroupPredicate(KapuaSession kapuaSession, KapuaQuery query, Domain domain, String groupPredicateName) throws KapuaException {
        try {
            KapuaId userId = kapuaSession.getUserId();

            AccessInfo accessInfo = KapuaSecurityUtils.doPrivileged(() -> ACCESS_INFO_SERVICE.findByUserId(kapuaSession.getScopeId(), userId));

            List<Permission> groupPermissions = new ArrayList<>();
            if (accessInfo != null) {

                AccessPermissionListResult accessPermissions = KapuaSecurityUtils.doPrivileged(() -> ACCESS_PERMISSION_SERVICE.findByAccessInfoId(accessInfo.getScopeId(), accessInfo.getId()));

                for (AccessPermission ap : accessPermissions.getItems()) {
                    if (checkGroupPermission(domain, groupPermissions, ap.getPermission())) {
                        break;
                    }
                }

                AccessRoleListResult accessRoles = KapuaSecurityUtils.doPrivileged(() -> ACCESS_ROLE_SERVICE.findByAccessInfoId(accessInfo.getScopeId(), accessInfo.getId()));

                for (AccessRole ar : accessRoles.getItems()) {
                    KapuaId roleId = ar.getRoleId();

                    Role role = KapuaSecurityUtils.doPrivileged(() -> ROLE_SERVICE.find(ar.getScopeId(), roleId));

                    RolePermissionListResult rolePermissions = KapuaSecurityUtils.doPrivileged(() -> ROLE_PERMISSION_SERVICE.findByRoleId(role.getScopeId(), role.getId()));

                    for (RolePermission rp : rolePermissions.getItems()) {
                        if (checkGroupPermission(domain, groupPermissions, rp.getPermission())) {
                            break;
                        }
                    }
                }
            }

            AndPredicate andPredicate = query.andPredicate();
            if (!groupPermissions.isEmpty()) {
                int i = 0;
                KapuaId[] groupsIds = new KapuaEid[groupPermissions.size()];
                for (Permission p : groupPermissions) {
                    groupsIds[i++] = p.getGroupId();
                }
                andPredicate.and(query.attributePredicate(groupPredicateName, groupsIds));
            }

            if (query.getPredicate() != null) {
                andPredicate.and(query.getPredicate());
            }

            query.setPredicate(andPredicate);
        } catch (Exception e) {
            throw KapuaException.internalError(e, "Error while grouping!");
        }
    }

    /**
     * @param domain
     * @param groupPermissions
     * @param permission
     * @return
     * @since 1.0.0
     */
    private static boolean checkGroupPermission(@NonNull Domain domain, @NonNull List<Permission> groupPermissions, @NonNull Permission permission) {
        if ((permission.getDomain() == null || domain.getName().equals(permission.getDomain())) &&
                (permission.getAction() == null || Actions.read.equals(permission.getAction()))) {
            if (permission.getGroupId() == null) {
                groupPermissions.clear();
                return true;
            } else {
                groupPermissions.add(permission);
            }
        }
        return false;
    }

    /**
     * Check if the given {@link PersistenceException} is a SQL constraint violation error.
     *
     * @param persistenceException
     *         {@link PersistenceException} to check.
     * @return {@code true} if it is a constraint validation error, {@code false} otherwise.
     * @since 1.0.0
     */
    private static boolean isInsertConstraintViolation(@NonNull PersistenceException persistenceException) {
        Throwable cause = persistenceException.getCause();
        while (cause != null && !(cause instanceof SQLException)) {
            cause = cause.getCause();
        }

        if (cause == null) {
            return false;
        }

        SQLException innerExc = (SQLException) cause;
        return SQL_ERROR_CODE_CONSTRAINT_VIOLATION.equals(innerExc.getSQLState());
    }
}
