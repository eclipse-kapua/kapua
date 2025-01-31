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
package org.eclipse.kapua.commons.configuration;

import java.util.Map;
import java.util.Optional;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaMaxNumberOfItemsReachedException;
import org.eclipse.kapua.commons.configuration.exception.ServiceConfigurationLimitExceededException;
import org.eclipse.kapua.commons.configuration.exception.ServiceConfigurationParentLimitExceededException;
import org.eclipse.kapua.commons.jpa.CacheFactory;
import org.eclipse.kapua.commons.jpa.EntityCacheFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.KapuaEntityFactory;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

/**
 * Base {@code abstract} {@link KapuaConfigurableService} implementation for services that have a max number of entities allowed.
 * <p>
 * The usually contain properties named:
 * <ul>
 *     <li>infiniteChildEntities</li>
 *     <li>maxNumberChildEntities</li>
 * </ul>
 *
 * @param <E>
 *         The {@link KapuaEntity} type.
 * @param <C>
 *         The {@link KapuaEntityCreator} type.
 * @param <S>
 *         The {@link KapuaEntityService} type.
 * @param <F>
 *         The {@link KapuaEntityFactory} type.
 * @since 1.0.0
 * @deprecated since 2.0.0, in favour of separate configuration component - see {@link ServiceConfigurationManager} and implementations for more details
 */
@Deprecated
public abstract class AbstractKapuaConfigurableResourceLimitedService<
        E extends KapuaEntity,
        C extends KapuaEntityCreator<E>,
        S extends KapuaEntityService<E, C>,
        F extends KapuaEntityFactory<E>
        >
        extends AbstractKapuaConfigurableService
        implements KapuaEntityService<E, C> {

    //TODO: make final as soon as deprecated constructors are removed
    private AccountRelativeFinder accountRelativeFinder;
    private F factory;
    //TODO: remove as soon as deprecated constructors are removed
    private final Class<F> factoryClass;

    /**
     * Constructor.
     *
     * @param pid
     *         The {@link KapuaConfigurableService} id.KapuaEntit
     * @param domain
     *         The {@link Domain} on which check access.
     * @param entityManagerFactory
     *         The {@link EntityManagerFactory} that handles persistence unit
     * @param serviceClass
     *         The {@link KapuaService} type.
     * @param factoryClass
     *         The {@link KapuaEntityFactory} type.
     * @deprecated Since 1.2.0. This constructor will be removed in a next release (may be)
     */
    @Deprecated
    protected AbstractKapuaConfigurableResourceLimitedService(
            String pid,
            Domain domain,
            EntityManagerFactory entityManagerFactory,
            Class<S> serviceClass,
            Class<F> factoryClass) {
        this(pid,
                domain,
                entityManagerFactory,
                null,
                null,
                factoryClass);
    }

    /**
     * Constructor.
     *
     * @param pid
     *         The {@link KapuaConfigurableService} id.
     * @param domain
     *         The {@link Domain} on which check access.
     * @param entityManagerFactory
     *         The {@link EntityManagerFactory} that handles persistence unit
     * @param abstractCacheFactory
     *         The {@link CacheFactory} that handles caching of the entities
     * @since 1.2.0
     * @deprecated Since 2.0.0. Please use
     *         {@link #AbstractKapuaConfigurableResourceLimitedService(String, Domain, EntityManagerFactory, EntityCacheFactory, KapuaEntityFactory, AuthorizationService, AccountRelativeFinder,
     *         RootUserTester)} This constructor may be removed in a next release
     */
    @Deprecated
    protected AbstractKapuaConfigurableResourceLimitedService(
            String pid,
            Domain domain,
            EntityManagerFactory entityManagerFactory,
            EntityCacheFactory abstractCacheFactory,
            Class<S> serviceClass,
            Class<F> factoryClass) {
        super(pid, domain, entityManagerFactory, abstractCacheFactory, null, null);

        /*
        These should be provided by the Locator, but in most cases when this class is instantiated through this constructor the Locator is not yet ready,
        therefore fetching of this instance is demanded to the artificial getter introduced.
        */
        this.factoryClass = factoryClass;
        this.factory = null;
        this.accountRelativeFinder = null;
    }

    /**
     * Constructor.
     *
     * @param pid
     *         The {@link KapuaConfigurableService} id.
     * @param domain
     *         The {@link Domain} on which check access.
     * @param entityManagerFactory
     *         The {@link EntityManagerFactory} that handles persistence unit
     * @param abstractCacheFactory
     *         The {@link CacheFactory} that handles caching of the entities
     * @param factory
     *         The {@link KapuaEntityFactory} instance.
     * @param authorizationService
     *         The {@link AuthorizationService} instance.
     * @param rootUserTester
     *         The {@link RootUserTester} instance.
     */
    protected AbstractKapuaConfigurableResourceLimitedService(String pid,
            Domain domain,
            EntityManagerFactory entityManagerFactory,
            EntityCacheFactory abstractCacheFactory,
            F factory,
            AuthorizationService authorizationService,
            AccountRelativeFinder accountRelativeFinder,
            RootUserTester rootUserTester) {
        super(pid, domain, entityManagerFactory, abstractCacheFactory, authorizationService, rootUserTester);
        this.factory = factory;
        this.factoryClass = null; //TODO: not needed for this construction path, remove as soon as the deprecated constructor is removed
        this.accountRelativeFinder = accountRelativeFinder;
    }

    @Override
    protected boolean validateNewConfigValuesCoherence(KapuaTocd ocd, Map<String, Object> updatedProps, KapuaId scopeId, KapuaId parentId) throws KapuaException {
        super.validateNewConfigValuesCoherence(ocd, updatedProps, scopeId, parentId);

        // Validate against current scope
        long availableChildEntitiesWithNewConfig = allowedChildEntities(scopeId, null, updatedProps);
        if (availableChildEntitiesWithNewConfig < 0) {
            throw new ServiceConfigurationLimitExceededException(getServicePid(), scopeId, -availableChildEntitiesWithNewConfig);
        }

        // Validate against parent scope
        if (parentId != null) {
            long availableParentEntitiesWithCurrentConfig = allowedChildEntities(parentId, scopeId);
            if (availableParentEntitiesWithCurrentConfig - availableChildEntitiesWithNewConfig < 0) {
                throw new ServiceConfigurationParentLimitExceededException(getServicePid(), parentId, -(availableParentEntitiesWithCurrentConfig - availableChildEntitiesWithNewConfig));
            }
        }
        return true;
    }

    /**
     * Checks if the given scope {@link KapuaId} can have more entities for this {@link KapuaConfigurableService}.
     *
     * @param scopeId
     *         The scope {@link KapuaId} to check.
     * @param entityType
     *         The entity type of this {@link KapuaConfigurableService}
     * @throws KapuaException
     * @since 2.0.0
     */
    protected void checkAllowedEntities(KapuaId scopeId, String entityType) throws KapuaException {
        if (allowedChildEntities(scopeId) <= 0) {
            throw new KapuaMaxNumberOfItemsReachedException(entityType);
        }
    }

    /**
     * Gets the number of remaining allowed entity for the given scope, according to the {@link KapuaConfigurableService#getConfigValues(KapuaId)}
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @return The number of entities remaining for the given scope
     * @throws KapuaException
     * @since 1.0.0
     */
    private long allowedChildEntities(KapuaId scopeId) throws KapuaException {
        return allowedChildEntities(scopeId, null, null);
    }

    /**
     * Gets the number of remainisng allowed entity for the given scope, according to the {@link KapuaConfigurableService#getConfigValues(KapuaId)} excluding a specific scope when checking resources
     * available.
     * <p>
     * The exclusion of the scope is required when updating a limit for a target account.
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @param targetScopeId
     *         The excluded scope {@link KapuaId}.
     * @return The number of entities remaining for the given scope
     * @throws KapuaException
     * @since 1.0.0
     */
    private long allowedChildEntities(KapuaId scopeId, KapuaId targetScopeId) throws KapuaException {
        return allowedChildEntities(scopeId, targetScopeId, null);
    }

    /**
     * Gets the number of remaining allowed entity for the given scope, according to the given {@link KapuaConfigurableService} excluding a specific scope when checking resources available.
     * <p>
     * The exclusion of the scope is required when updating a limit for a target account.
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @param targetScopeId
     *         The excluded scope {@link KapuaId}.
     * @param configuration
     *         The configuration to be checked. If not provided will be read from the current service configuration
     * @return The number of entities remaining for the given scope
     * @throws KapuaException
     * @since 1.0.0
     */
    private long allowedChildEntities(KapuaId scopeId, KapuaId targetScopeId, Map<String, Object> configuration) throws KapuaException {
        final Map<String, Object> finalConfig = configuration == null ? getConfigValues(scopeId, false) : configuration;
        boolean allowInfiniteChildEntities = (boolean) finalConfig.get("infiniteChildEntities");
        if (allowInfiniteChildEntities) {
            return Integer.MAX_VALUE;
        }
        return KapuaSecurityUtils.doPrivileged(() -> {
            KapuaQuery countQuery = new KapuaQuery(scopeId);

            // Current used entities
            long currentUsedEntities = this.count(countQuery);

            final KapuaListResult<Account> childAccounts = txManager.execute(tx -> getAccountRelativeFinder().findChildren(scopeId, Optional.ofNullable(targetScopeId)));
            // Resources assigned to children
            long childCount = 0;
            for (Account childAccount : childAccounts.getItems()) {
                Map<String, Object> childConfigValues = getConfigValues(childAccount.getId());
                // maxNumberChildEntities can be null if such property is disabled via the
                // isPropertyEnabled() method in the service implementation. In such case,
                // it makes sense to treat the service as it had 0 available entities
                boolean childAllowInfiniteChildEntities = (boolean) childConfigValues.getOrDefault("infiniteChildEntities", false);
                Integer childMaxNumberChildEntities = (Integer) childConfigValues.getOrDefault("maxNumberChildEntities", 0);
                childCount += childAllowInfiniteChildEntities ? Integer.MAX_VALUE : childMaxNumberChildEntities;
            }

            // Max allowed for this account
            int maxChildAccounts = (int) finalConfig.get("maxNumberChildEntities");
            return maxChildAccounts - currentUsedEntities - childCount;
        });
    }

    /**
     * KapuaEntityFactory instance should be provided by the Locator, but in most cases when this class is instantiated through the deprecated constructor the Locator is not yet ready, therefore
     * fetching of the required instance is demanded to this artificial getter.
     *
     * @return The instantiated (hopefully) {@link KapuaEntityFactory} instance
     */
    //TODO: remove as soon as deprecated constructors are removed
    protected F getFactory() {

        if (factory == null) {
            KapuaLocator locator = KapuaLocator.getInstance();
            this.factory = locator.getFactory(factoryClass);
        }

        return factory;
    }

    /**
     * This instance should be provided by the Locator, but in most cases when this class is instantiated through the deprecated constructor the Locator is not yet ready, therefore fetching of the
     * required instance is demanded to this artificial getter.
     *
     * @return The instantiated (hopefully) {@link AccountRelativeFinder} instance
     */
    private AccountRelativeFinder getAccountRelativeFinder() {
        if (accountRelativeFinder == null) {
            KapuaLocator locator = KapuaLocator.getInstance();
            this.accountRelativeFinder = locator.getService(AccountRelativeFinder.class);
        }

        return accountRelativeFinder;
    }
}
