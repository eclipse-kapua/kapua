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
package org.eclipse.kapua.commons.jpa;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.persistence.Embedded;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
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

import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.kapua.KapuaEntityExistsException;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.AbstractKapuaEntity_;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.FieldSortCriteria;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.model.query.SortOrder;
import org.eclipse.kapua.model.query.predicate.AndPredicate;
import org.eclipse.kapua.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.model.query.predicate.MatchPredicate;
import org.eclipse.kapua.model.query.predicate.OrPredicate;
import org.eclipse.kapua.model.query.predicate.QueryPredicate;
import org.eclipse.kapua.storage.KapuaEntityRepository;
import org.eclipse.kapua.storage.TxContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

public class KapuaEntityJpaRepository<E extends KapuaEntity, C extends E, L extends KapuaListResult<E>> implements KapuaEntityRepository<E, L> {

    protected final Class<C> concreteClass;
    protected final String entityName;
    protected final Supplier<? extends L> listSupplier;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String SQL_ERROR_CODE_CONSTRAINT_VIOLATION = "23505";
    private final KapuaJpaRepositoryConfiguration configuration;
    private static final String ATTRIBUTE_SEPARATOR = ".";
    private static final String ATTRIBUTE_SEPARATOR_ESCAPED = "\\.";
    private static final String COMPARE_ERROR_MESSAGE = "Trying to compare a non-comparable value";

    /**
     * @param concreteClass
     *         The concrete class reifying a {@link KapuaEntity} entity, to be retrieved
     * @param entityName
     * @param listSupplier
     *         Generator of new, empty lists
     * @param configuration
     *         Repo configuration, see {@link KapuaJpaRepositoryConfiguration} for configurable details
     */
    public KapuaEntityJpaRepository(
            Class<C> concreteClass,
            String entityName, Supplier<L> listSupplier,
            KapuaJpaRepositoryConfiguration configuration) {
        this.concreteClass = concreteClass;
        this.entityName = entityName;
        this.listSupplier = listSupplier;
        this.configuration = configuration;
    }

    @Override
    public E create(TxContext txContext, E entity) throws KapuaException {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(txContext);
        try {
            em.persist(entity);
            em.flush();
            em.refresh(entity);
            return entity;
        } catch (EntityExistsException e) {
            // this will be intercepted by the calling method, that will decide whether to retry or not
            throw new KapuaEntityExistsException(e, entity.getId());
        } catch (PersistenceException e) {
            //if it is a contraint violation....
            if (isInsertConstraintViolation(e)) {
                //then check if an entity with the same id is already present
                final KapuaEntity entityFound = em.find(entity.getClass(), entity.getId());
                if (entityFound == null) {
                    //if it is not, just propagate the original exception (cannot be an id clash)
                    throw e;
                }
                //if an entity with the same id is already present, treat it as a generated id conflict and bubble up for potential retry
                throw new KapuaEntityExistsException(e, entity.getId());
            } else {
                throw e;
            }
        }
    }

    @Override
    public Optional<E> find(TxContext txContext, KapuaId scopeId, KapuaId entityId) {
        final javax.persistence.EntityManager em = JpaAwareTxContext.extractEntityManager(txContext);
        return doFind(em, scopeId, entityId);
    }

    protected Optional<E> doFind(EntityManager em, KapuaId scopeId, KapuaId entityId) {
        return doFind(em, scopeId, entityId, null);
    }

    protected Optional<E> doFind(EntityManager em, KapuaId scopeId, KapuaId entityId, LockModeType lockModeType) {
        final KapuaEid eId = KapuaEid.parseKapuaId(entityId);
        // Checking existence
        final Optional<E> entityToFind = Optional.ofNullable(em.find(concreteClass, eId, lockModeType));

        return entityToFind
                .filter(e -> scopeId == null || KapuaId.ANY.equals(scopeId)
                        ? true
                        : scopeId.equals(e.getScopeId()));
    }

    @Override
    public L query(TxContext txContext, KapuaQuery listQuery) throws KapuaException {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(txContext);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<C> criteriaSelectQuery = cb.createQuery(concreteClass);
        // FROM
        Root<C> entityRoot = criteriaSelectQuery.from(concreteClass);
        EntityType<C> entityType = entityRoot.getModel();
        // SELECT
        criteriaSelectQuery.select(entityRoot).distinct(true);

        // Fetch LAZY attributes if necessary
        for (String fetchAttribute : listQuery.getFetchAttributes()) {
            if (entityType.getAttribute(fetchAttribute).isAssociation()) {
                entityRoot.fetch(entityType.getSingularAttribute(fetchAttribute), JoinType.LEFT);
            } else {
                entityRoot.fetch(fetchAttribute);
            }
        }
        // WHERE
        QueryPredicate kapuaPredicates = listQuery.getPredicate();
        // Add ScopeId to query if has been defined one specific
        if (listQuery.getScopeId() != null && // Support for old method of querying for all ScopeIds (e.g.: query.setScopeId(null)
                !listQuery.getScopeId().equals(KapuaId.ANY)) {// Support for new method of querying for all ScopeIds (e.g.: query.setScopeId(KapuaId.ANY)

            QueryPredicate scopeIdPredicate = listQuery.attributePredicate(AbstractKapuaEntity_.SCOPE_ID, listQuery.getScopeId());

            if (listQuery.getNotScopedEntities()) {
                scopeIdPredicate = listQuery.orPredicate(
                        scopeIdPredicate,
                        listQuery.attributePredicate(AbstractKapuaEntity_.SCOPE_ID, null, AttributePredicate.Operator.IS_NULL)
                );
            }

            AndPredicate scopedAndPredicate = listQuery.andPredicate(scopeIdPredicate);

            // Add existing query predicates
            if (listQuery.getPredicate() != null) {
                scopedAndPredicate.and(listQuery.getPredicate());
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
        // ORDER BY
        // Default to the KapuaEntity id if no ordering is specified.
        Order order;
        if (listQuery.getSortCriteria() != null || listQuery.getDefaultSortCriteria() != null) {
            FieldSortCriteria sortCriteria = (FieldSortCriteria) MoreObjects.firstNonNull(listQuery.getSortCriteria(), listQuery.getDefaultSortCriteria());

            if (SortOrder.DESCENDING.equals(sortCriteria.getSortOrder())) {
                order = cb.desc(extractAttribute(entityRoot, sortCriteria.getAttributeName()));
            } else {
                order = cb.asc(extractAttribute(entityRoot, sortCriteria.getAttributeName()));
            }
        } else {
            order = cb.asc(entityRoot.get(entityType.getSingularAttribute(AbstractKapuaEntity_.ID)));
        }
        criteriaSelectQuery.orderBy(order);

        //S
        // QUERY!
        TypedQuery<C> query = em.createQuery(criteriaSelectQuery);

        // Populate query parameters
        binds.forEach(query::setParameter); // Whoah! This is very magic!

        // Set offset
        if (listQuery.getOffset() != null) {
            query.setFirstResult(listQuery.getOffset());
        }

        // Set limit
        if (listQuery.getLimit() != null) {
            query.setMaxResults(listQuery.getLimit() + 1);
        }

        // Finally querying!
        List<C> result = query.getResultList();
        final L resultContainer = listSupplier.get();

        // Check limit exceeded
        if (listQuery.getLimit() != null &&
                result.size() > listQuery.getLimit()) {
            result.remove(listQuery.getLimit().intValue());
            resultContainer.setLimitExceeded(true);
        }

        if (Boolean.TRUE.equals(listQuery.getAskTotalCount())) {
            resultContainer.setTotalCount(count(txContext, listQuery));
        }

        // Set results
        resultContainer.addItems(result);
        return resultContainer;
    }

    @Override
    public long count(TxContext txContext, KapuaQuery countQuery) throws KapuaException {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(txContext);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaSelectQuery = cb.createQuery(Long.class);
        // FROM
        Root<C> entityRoot = criteriaSelectQuery.from(concreteClass);
        // SELECT
        criteriaSelectQuery.select(cb.countDistinct(entityRoot));
        // WHERE
        QueryPredicate kapuaPredicates = countQuery.getPredicate();
        // Add ScopeId to query if has been defined one specific
        if (countQuery.getScopeId() != null && // Support for old method of querying for all ScopeIds (e.g.: query.setScopeId(null)
                !countQuery.getScopeId().equals(KapuaId.ANY)) {// Support for new method of querying for all ScopeIds (e.g.: query.setScopeId(KapuaId.ANY)

            AndPredicate scopedAndPredicate = countQuery.andPredicate();

            AttributePredicate<KapuaId> scopeId = countQuery.attributePredicate(AbstractKapuaEntity_.SCOPE_ID, countQuery.getScopeId());
            scopedAndPredicate.and(scopeId);

            if (countQuery.getPredicate() != null) {
                scopedAndPredicate.and(countQuery.getPredicate());
            }

            kapuaPredicates = scopedAndPredicate;
        }

        final Map<ParameterExpression, Object> binds = new HashMap<>();
        Expression<Boolean> expr = handleKapuaQueryPredicates(kapuaPredicates,
                binds,
                cb,
                entityRoot,
                entityRoot.getModel());

        if (expr != null) {
            criteriaSelectQuery.where(expr);
        }
        // COUNT!
        TypedQuery<Long> query = em.createQuery(criteriaSelectQuery);

        // Populate query parameters
        binds.forEach(query::setParameter);

        return query.getSingleResult();
    }

    @Override
    public E delete(TxContext txContext, KapuaId scopeId, KapuaId entityId) throws KapuaException {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(txContext);
        // Checking existence
        return doFind(em, scopeId, entityId)
                // Deleting if found
                .map(e -> doDelete(em, e))
                .orElseThrow(() -> new KapuaEntityNotFoundException(entityName, entityId));
    }

    @Override
    public E delete(TxContext txContext, E entityToDelete) {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(txContext);
        //inflate detached entity, making it managed again
        return doDelete(em, em.merge(entityToDelete));
    }

    protected E doDelete(EntityManager em, E entityToDelete) {
        em.remove(entityToDelete);
        em.flush();
        // Returning deleted entity
        return entityToDelete;
    }
    // Private Methods

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
    private <E> Predicate handleKapuaQueryPredicates(@NonNull QueryPredicate queryPredicate,
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
                orPredicate.getPredicates().add(new AttributePredicate<>(attributeName, matchPredicate.getMatchTerm(), AttributePredicate.Operator.STARTS_WITH_IGNORE_CASE));
            }
            predicate = handleOrPredicate(orPredicate, binds, cb, userPermissionRoot, entityType);
        }
        return predicate;
    }

    private <E> Predicate handleAndPredicate(@NonNull AndPredicate andPredicate,
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

    private <E> Predicate handleOrPredicate(@NonNull OrPredicate orPredicate,
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

    private <E> Predicate[] handlePredicate(@NonNull List<QueryPredicate> orPredicates,
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

    private <E> Predicate handleAttributePredicate(@NonNull AttributePredicate<?> attrPred,
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
            final String escapedAttributeValue = Optional.ofNullable(attributeValue)
                    .map(a -> a.toString()
                            .replace(configuration.like, configuration.escape + configuration.like)
                            .replace(configuration.any, configuration.escape + configuration.any))
                    .orElse(null);
            switch (attrPred.getOperator()) {
            case LIKE:
                ParameterExpression<String> pl = cb.parameter(String.class);
                binds.put(pl, configuration.like + escapedAttributeValue + configuration.like);
                expr = cb.like(extractAttribute(entityRoot, attrName), pl);
                break;

            case LIKE_IGNORE_CASE:
                ParameterExpression<String> plci = cb.parameter(String.class);
                binds.put(plci, configuration.like + escapedAttributeValue.toLowerCase() + configuration.like);
                expr = cb.like(cb.lower(extractAttribute(entityRoot, attrName)), plci);
                break;

            case STARTS_WITH:
                ParameterExpression<String> psw = cb.parameter(String.class);
                binds.put(psw, escapedAttributeValue + configuration.like);
                expr = cb.like(extractAttribute(entityRoot, attrName), psw);
                break;

            case STARTS_WITH_IGNORE_CASE:
                ParameterExpression<String> pswci = cb.parameter(String.class);
                binds.put(pswci, escapedAttributeValue.toLowerCase() + configuration.like);
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
    private <E, P> Path<P> extractAttribute(@NonNull Root<E> entityRoot, @NonNull String attributeName) {
        Path<P> expressionPath;
        if (attributeName.contains(ATTRIBUTE_SEPARATOR)) {
            String[] pathComponents = attributeName.split(ATTRIBUTE_SEPARATOR_ESCAPED);
            Path<P> rootPath = entityRoot.get(pathComponents[0]);
            expressionPath = rootPath;
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
     * Check if the given {@link PersistenceException} is a SQL constraint violation error.
     *
     * @param persistenceException
     *         {@link PersistenceException} to check.
     * @return {@code true} if it is a constraint validation error, {@code false} otherwise.
     * @since 1.0.0
     */
    public static boolean isInsertConstraintViolation(@NonNull PersistenceException persistenceException) {
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

    protected Optional<E> doFindByField(@NonNull TxContext txContext,
            @NonNull KapuaId scopeId,
            @NonNull String fieldName,
            @NonNull Object fieldValue) {
        final List<C> result = doFindAllByField(txContext, scopeId, fieldName, fieldValue);
        switch (result.size()) {
        case 0:
            return Optional.empty();
        case 1:
            return Optional.of(result.get(0));
        default:
            throw new NonUniqueResultException(String.format("Multiple %s results found for field %s with value %s", concreteClass.getName(), fieldName, fieldValue.toString()));
        }
    }

    protected List<C> doFindAllByField(TxContext txContext, KapuaId scopeId, String fieldName, Object fieldValue) {
        final EntityManager em = JpaAwareTxContext.extractEntityManager(txContext);
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<C> criteriaSelectQuery = cb.createQuery(concreteClass);
        // FROM
        Root<C> entityRoot = criteriaSelectQuery.from(concreteClass);
        // SELECT
        criteriaSelectQuery.select(entityRoot);

        // fieldName
        ParameterExpression<String> pName = cb.parameter(String.class, fieldName);
        Predicate namePredicate = cb.equal(entityRoot.get(fieldName), pName);

        ParameterExpression<KapuaId> pScopeId = null;

        if (!KapuaId.ANY.equals(scopeId)) {
            pScopeId = cb.parameter(KapuaId.class, AbstractKapuaEntity_.SCOPE_ID);
            Predicate scopeIdPredicate = cb.equal(entityRoot.get(AbstractKapuaEntity_.SCOPE_ID), pScopeId);

            Predicate andPredicate = cb.and(namePredicate, scopeIdPredicate);
            criteriaSelectQuery.where(andPredicate);
        } else {
            criteriaSelectQuery.where(namePredicate);
        }

        TypedQuery<C> query = em.createQuery(criteriaSelectQuery);
        final Object adaptedFieldValue;
        if (fieldValue instanceof KapuaId) {
            adaptedFieldValue = KapuaEid.parseKapuaId((KapuaId) fieldValue);
        } else {
            adaptedFieldValue = fieldValue;
        }
        query.setParameter(pName.getName(), adaptedFieldValue);

        if (pScopeId != null) {
            query.setParameter(pScopeId.getName(), KapuaEid.parseKapuaId(scopeId));
        }
        // QUERY!
        List<C> result = query.getResultList();
        return result;
    }

    protected Predicate mapScopeIdToCriteria(KapuaId scopeId, CriteriaBuilder cb, Root<C> entityRoot) {
        return Optional.ofNullable(scopeId)
                .filter(id -> !KapuaId.ANY.equals(id))
                .map(id -> cb.equal(entityRoot.get(AbstractKapuaEntity_.SCOPE_ID), KapuaEid.parseKapuaId(id)))
                .orElse(cb.conjunction());
    }
}
