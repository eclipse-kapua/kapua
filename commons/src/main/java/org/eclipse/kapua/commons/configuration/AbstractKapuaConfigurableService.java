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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.commons.configuration;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.jpa.CacheFactory;
import org.eclipse.kapua.commons.jpa.EntityCacheFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerContainer;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.eclipse.kapua.commons.jpa.JpaTxContext;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.service.internal.AbstractKapuaService;
import org.eclipse.kapua.commons.service.internal.KapuaServiceDisabledException;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.commons.service.internal.cache.EntityCache;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.ResourceUtils;
import org.eclipse.kapua.commons.util.StringUtil;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTmetadata;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.storage.TxManager;
import org.eclipse.kapua.storage.TxManagerImpl;
import org.xml.sax.SAXException;

/**
 * Base {@code abstract} {@link KapuaConfigurableService} implementation.
 *
 * @since 1.0.0
 * @deprecated since 2.0.0, in favour of separate configuration component - see {@link ServiceConfigurationManager} and implementations for more details. Can be replaced with
 *         {@link KapuaConfigurableServiceBase}
 */
@Deprecated
public abstract class AbstractKapuaConfigurableService extends AbstractKapuaService implements KapuaConfigurableService {

    private static final EntityCache PRIVATE_ENTITY_CACHE = KapuaLocator.getInstance().getComponent(CacheFactory.class).createCache("AbstractKapuaConfigurableServiceCacheId");
    private static final int LOCAL_CACHE_SIZE_MAX = SystemSetting.getInstance().getInt(SystemSettingKey.TMETADATA_LOCAL_CACHE_SIZE_MAXIMUM, 100);

    private final Domain domain;
    private final String pid;

    //TODO: make final as soon as deprecated constructors are removed
    private PermissionFactory permissionFactory;
    private AuthorizationService authorizationService;
    private RootUserTester rootUserTester;
    protected TxManager txManager;

    /**
     * This cache is to hold the {@link KapuaTocd}s that are read from the metatype files.
     * <p>
     * The key is a {@link Triple} composed by:
     * <ul>
     *     <li>The {@link KapuaConfigurableService} PID</li>
     *     <li>The ID of the {@link Account} for the current request</li>
     *     <li>A {@link Boolean} flag indicating whether disabled properties are excluded from the AD or not</li>
     * </ul>
     *
     * @since 1.2.0
     */
    private static final LocalCache<Triple<String, KapuaId, Boolean>, KapuaTocd> KAPUA_TOCD_LOCAL_CACHE = new LocalCache<>(LOCAL_CACHE_SIZE_MAX, null);

    /**
     * This cache only holds the {@link Boolean} value {@literal True} if the {@link KapuaTocd} has been already read from the file at least once, regardless of the value. With this we can know when a
     * read from {@code KAPUA_TOCD_LOCAL_CACHE} returns {@literal null} because of the requested key is not present, and when the key is present but its actual value is {@literal null}.
     *
     * @since 1.2.0
     */
    private static final LocalCache<Triple<String, KapuaId, Boolean>, Boolean> KAPUA_TOCD_EMPTY_LOCAL_CACHE = new LocalCache<>(LOCAL_CACHE_SIZE_MAX, false);

    /**
     * Constructor.
     *
     * @param pid
     *         The {@link KapuaConfigurableService} id.
     * @param domain
     *         The {@link Domain} on which check access.
     * @param entityManagerFactory
     *         The {@link EntityManagerFactory} that handles persistence unit
     * @since 1.0.0
     * @deprecated Since 2.0.0. Please use
     *         {@link AbstractKapuaConfigurableService#AbstractKapuaConfigurableService(String, Domain, EntityManagerFactory, EntityCacheFactory, PermissionFactory, AuthorizationService,
     *         RootUserTester)} This constructor may be removed in a next release
     */
    @Deprecated
    protected AbstractKapuaConfigurableService(String pid, Domain domain, EntityManagerFactory entityManagerFactory) {
        this(pid,
                domain,
                entityManagerFactory,
                /*
                Following service should be provided by the Locator, but in most cases when this class is instantiated through this constructor the Locator is not yet ready,
                therefore fetching of these instances is demanded to the artificial getters introduced.
                */
                null,
                null,
                null,
                null);
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
     *         {@link AbstractKapuaConfigurableService#AbstractKapuaConfigurableService(String, Domain, EntityManagerFactory, EntityCacheFactory, PermissionFactory, AuthorizationService,
     *         RootUserTester)} This constructor may be removed in a next release
     */
    @Deprecated
    protected AbstractKapuaConfigurableService(String pid, Domain domain, EntityManagerFactory entityManagerFactory, EntityCacheFactory abstractCacheFactory) {
        this(pid,
                domain,
                entityManagerFactory,
                abstractCacheFactory,
                /*
                Following service should be provided by the Locator, but in most cases when this class is instantiated through this constructor the Locator is not yet ready,
                therefore fetching of these instances is demanded to the artificial getters introduced.
                */
                null,
                null,
                null);
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
     * @param rootUserTester
     * @since 2.0.0
     */
    protected AbstractKapuaConfigurableService(String pid,
            Domain domain,
            EntityManagerFactory entityManagerFactory,
            EntityCacheFactory abstractCacheFactory,
            PermissionFactory permissionFactory,
            AuthorizationService authorizationService,
            RootUserTester rootUserTester) {
        super(entityManagerFactory, abstractCacheFactory);

        this.pid = pid;
        this.domain = domain;
        this.permissionFactory = permissionFactory;
        this.authorizationService = authorizationService;
        this.rootUserTester = rootUserTester;
        this.txManager = new TxManagerImpl(
                () -> new JpaTxContext(entityManagerFactory.getJpaEntityManagerFactory()),
                SystemSetting.getInstance().getInt(SystemSettingKey.KAPUA_INSERT_MAX_RETRY));
    }

    /**
     * Reads the {@link KapuaTmetadata} for the given {@link KapuaConfigurableService} pid.
     *
     * @param pid
     *         The {@link KapuaConfigurableService} pid
     * @return The {@link KapuaTmetadata} for the given {@link KapuaConfigurableService} pid.
     * @throws Exception
     * @since 1.0.0
     */
    private static KapuaTmetadata readMetadata(String pid) throws JAXBException, SAXException, IOException {
        URL url = ResourceUtils.getResource(String.format("META-INF/metatypes/%s.xml", pid));

        if (url == null) {
            return null;
        }

        return KapuaLocator.getInstance().getComponent(XmlUtil.class).unmarshal(ResourceUtils.openAsReader(url, StandardCharsets.UTF_8), KapuaTmetadata.class);
    }

    /**
     * Validates the given {@link Map} of properties against the given {@link KapuaTocd}.
     *
     * @param ocd
     *         The reference {@link KapuaTocd}.
     * @param updatedProps
     *         The properties to validate.
     * @param scopeId
     *         The scope {@link KapuaId} which is going to be updated.
     * @param parentId
     *         The parent scope {@link KapuaId}.
     * @throws KapuaException
     * @since 1.0.0
     */
    private void validateConfigurations(KapuaTocd ocd, Map<String, Object> updatedProps, KapuaId scopeId, KapuaId parentId)
            throws KapuaException {
        if (ocd == null) {
            return;
        }

        // Get Disabled Properties
        List<KapuaTad> disabledProperties = ocd.getAD().stream().filter(ad -> !isPropertyEnabled(ad, scopeId)).collect(Collectors.toList());

        if (!disabledProperties.isEmpty()) {
            // If there's any disabled property, read current values to overwrite the proposed ones
            Map<String, Object> originalValues = getConfigValues(scopeId, false);
            if (originalValues != null) {
                disabledProperties.forEach(disabledProp -> updatedProps.put(disabledProp.getId(), originalValues.get(disabledProp.getId())));
            }
        }

        // build a map of all the attribute definitions
        Map<String, KapuaTad> attrDefs = ocd.getAD().stream().collect(Collectors.toMap(KapuaTad::getId, ad -> ad));

        // loop over the proposed property values
        // and validate them against the definition
        for (Entry<String, Object> property : updatedProps.entrySet()) {

            String key = property.getKey();
            KapuaTad attrDef = attrDefs.get(key);

            // is attribute undefined?
            if (attrDef == null) {
                // we do not have an attribute descriptor to the validation
                // against
                // As OSGI insert attributes at runtime like service.pid,
                // component.name,
                // for the attribute for which we do not have a definition,
                // just accept them.
                continue;
            }

            // validate the attribute value
            Object objectValue = property.getValue();
            String stringValue = StringUtil.valueToString(objectValue);
            if (stringValue != null) {
                ValueTokenizer tokenizer = new ValueTokenizer(stringValue);
                String result = tokenizer.validate(attrDef);
                if (result != null && !result.isEmpty()) {
                    throw new KapuaIllegalArgumentException(attrDef.getId(), result);
                }
            }
        }

        checkRequiredProperties(ocd, updatedProps);

        validateNewConfigValuesCoherence(ocd, updatedProps, scopeId, parentId);
    }

    /**
     * Check the given {@link Map} of properties against the given {@link KapuaTocd} validating {@link KapuaTad#isRequired()}.
     *
     * @param ocd
     *         The reference {@link KapuaTocd}.
     * @param updatedProps
     *         The properties to validate.
     * @throws KapuaIllegalNullArgumentException
     *         if one of the required {@link KapuaTad} in {@link KapuaTocd} is missing in the given properties.
     * @since 1.0.0
     */
    private void checkRequiredProperties(KapuaTocd ocd, Map<String, Object> updatedProps) throws KapuaIllegalNullArgumentException {
        // Make sure all required properties are set
        for (KapuaTad attrDef : ocd.getAD()) {
            // To the required attributes make sure a value is defined.
            if (Boolean.TRUE.equals(attrDef.isRequired()) && updatedProps.get(attrDef.getId()) == null) {
                // If the default one is not defined, throw exception.
                throw new KapuaIllegalNullArgumentException(attrDef.getId());
            }
        }
    }

    /**
     * Validates that the configurations is coherent.
     * <p>
     * By default returns true, but an extending {@link KapuaConfigurableService}s may have its own logic
     *
     * @param ocd
     *         The reference {@link KapuaTocd}.
     * @param updatedProps
     *         The properties to validate.
     * @param scopeId
     *         The scope {@link KapuaId} which is going to be updated.
     * @param parentId
     *         The parent scope {@link KapuaId}.
     * @return {@literal true} if the configuration is valid, {@literal false} otherwise
     * @throws KapuaException
     * @since 1.0.0
     */
    protected boolean validateNewConfigValuesCoherence(KapuaTocd ocd, Map<String, Object> updatedProps, KapuaId scopeId, KapuaId parentId) throws KapuaException {
        return true;
    }

    /**
     * Converts the given {@link Map} properties map to {@link Properties}.
     *
     * @param values
     *         The {@link Map} properties to convert.
     * @return The converted {@link Properties}
     * @since 1.0.0
     */
    private static Properties toProperties(Map<String, Object> values) {
        Properties props = new Properties();
        for (Entry<String, Object> entry : values.entrySet()) {
            if (entry.getValue() != null) {
                props.setProperty(entry.getKey(), StringUtil.valueToString(entry.getValue()));
            }
        }

        return props;
    }

    /**
     * Converts the given {@link Properties} to a properties {@link Map}.
     *
     * @param ocd
     *         The reference {@link KapuaTocd}.
     * @param props
     *         The {@link Properties} to convert
     * @return The converted {@link Map} properties.
     * @throws KapuaException
     * @since 1.0.0
     */
    private static Map<String, Object> toValues(@NotNull KapuaTocd ocd, Properties props) throws KapuaException {
        Map<String, Object> values = new HashMap<>();
        for (KapuaTad ad : ocd.getAD()) {
            String valueStr = props == null ? ad.getDefault() : props.getProperty(ad.getId(), ad.getDefault());
            Object value = StringUtil.stringToValue(ad.getType().value(), valueStr);
            values.put(ad.getId(), value);
        }

        return values;
    }

    /**
     * Persist the given {@link ServiceConfig}.
     *
     * @param serviceConfig
     *         The {@link ServiceConfig} to persist.
     * @return The persisted {@link ServiceConfig}.
     * @throws KapuaException
     * @since 1.0.0
     */
    private ServiceConfig createConfig(ServiceConfig serviceConfig) throws KapuaException {

        return entityManagerSession.doTransactedAction(
                EntityManagerContainer.<ServiceConfig>create()
                        .onResultHandler(em -> ServiceDAO.create(em, serviceConfig))
                        .onBeforeHandler(() -> {
                            PRIVATE_ENTITY_CACHE.removeList(serviceConfig.getScopeId(), pid);
                            return null;
                        })
        );
    }

    /**
     * Updates the given {@link ServiceConfig}.
     *
     * @param serviceConfig
     *         The {@link ServiceConfig} to update.
     * @return The updates {@link ServiceConfig}.
     * @throws KapuaException
     */
    private ServiceConfig updateConfig(ServiceConfig serviceConfig)
            throws KapuaException {
        return entityManagerSession.doTransactedAction(EntityManagerContainer.<ServiceConfig>create()
                .onResultHandler(em -> {

                    ServiceConfig oldServiceConfig = ServiceConfigDAO.find(em, serviceConfig.getScopeId(), serviceConfig.getId());
                    if (oldServiceConfig == null) {
                        throw new KapuaEntityNotFoundException(ServiceConfig.TYPE, serviceConfig.getId());
                    }

                    if (!Objects.equals(oldServiceConfig.getScopeId(), serviceConfig.getScopeId())) {
                        throw new KapuaIllegalArgumentException("serviceConfiguration.scopeId", serviceConfig.getScopeId().toStringId());
                    }

                    if (!oldServiceConfig.getPid().equals(serviceConfig.getPid())) {
                        throw new KapuaIllegalArgumentException("serviceConfiguration.pid", serviceConfig.getPid());
                    }

                    // Update
                    return ServiceConfigDAO.update(em, serviceConfig);
                })
                .onBeforeHandler(() -> {
                    PRIVATE_ENTITY_CACHE.removeList(serviceConfig.getScopeId(), pid);
                    return null;
                })
        );
    }

    @Override
    public KapuaTocd getConfigMetadata(KapuaId scopeId) throws KapuaException {
        return getConfigMetadata(scopeId, true);
    }

    /**
     * Gets the {@link KapuaTocd} for the given scope {@link KapuaId} and the current {@link KapuaConfigurableService} excluding disabled {@link KapuaTad} if requested.
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @param excludeDisabled
     *         Whether to exclude disabled {@link KapuaTocd}s and {@link KapuaTad}s.
     * @return The {@link KapuaTocd} available for the current {@link KapuaConfigurableService}.
     * @throws KapuaException
     * @since 1.3.0
     */
    private KapuaTocd getConfigMetadata(KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        // Check disabled service
        if (!isServiceEnabled(scopeId)) {
            throw new KapuaServiceDisabledException(pid);
        }
        // Check access
        getAuthorizationService().checkPermission(getPermissionFactory().newPermission(Optional.ofNullable(domain).map(d -> d.getName()).orElse(null), Actions.read, scopeId));
        // Get the Tocd
        // Keep distinct values for service PID, Scope ID and disabled properties included/excluded from AD
        Triple<String, KapuaId, Boolean> cacheKey = Triple.of(pid, scopeId, excludeDisabled);
        try {
            // Check if the OCD is already in cache, but not in the "empty" cache
            KapuaTocd tocd = KAPUA_TOCD_LOCAL_CACHE.get(cacheKey);
            if (tocd == null && !KAPUA_TOCD_EMPTY_LOCAL_CACHE.get(cacheKey)) {
                // If not, read metadata and process it
                tocd = processMetadata(readMetadata(pid), scopeId, excludeDisabled);
                // If null, put it in the "empty" ocd cache, else put it in the "standard" cache
                if (tocd != null) {
                    // If the value is not null, put it in "standard" cache and remove the entry from the "empty" cache if present
                    KAPUA_TOCD_LOCAL_CACHE.put(cacheKey, tocd);
                    KAPUA_TOCD_EMPTY_LOCAL_CACHE.remove(cacheKey);
                } else {
                    // If the value is null, just remember we already read it from file at least once
                    KAPUA_TOCD_EMPTY_LOCAL_CACHE.put(cacheKey, true);
                }
            }
            return tocd;
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    /**
     * Process {@link KapuaTmetadata} to exclude disabled {@link KapuaTocd}s and {@link KapuaTad}s if requested.
     *
     * @param metadata
     *         The {@link KapuaTmetadata} to process.
     * @param excludeDisabled
     *         Whether to exclude disabled {@link KapuaTocd}s and {@link KapuaTad}s.
     * @return The processed {@link KapuaTocd}.
     * @since 1.3.0
     */
    private KapuaTocd processMetadata(KapuaTmetadata metadata, KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        if (metadata != null && metadata.getOCD() != null && !metadata.getOCD().isEmpty()) {
            for (KapuaTocd ocd : metadata.getOCD()) {
                if (ocd.getId() != null && ocd.getId().equals(pid) && isServiceEnabled(scopeId)) {
                    ocd.getAD().removeIf(ad -> excludeDisabled && !isPropertyEnabled(ad, scopeId));
                    return ocd;
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId) throws KapuaException {
        return getConfigValues(scopeId, true);
    }

    /**
     * Gets {@link Map} properties for the given scope {@link KapuaId} and the current {@link KapuaConfigurableService} excluding disabled {@link KapuaTad} if requested.
     *
     * @param scopeId
     *         The scope {@link KapuaId}.
     * @param excludeDisabled
     *         Whether to exclude disabled {@link KapuaTocd}s and {@link KapuaTad}s.
     * @return The {@link Map} properties of the current {@link KapuaConfigurableService}.
     * @throws KapuaException
     * @since 1.3.0
     */
    protected Map<String, Object> getConfigValues(KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        // Check access
        getAuthorizationService().checkPermission(getPermissionFactory().newPermission(Optional.ofNullable(domain).map(d -> d.getName()).orElse(null), Actions.read, scopeId));
        // Get configuration values
        ServiceConfigQueryImpl query = new ServiceConfigQueryImpl(scopeId);

        query.setPredicate(
                query.andPredicate(
                        query.attributePredicate(ServiceConfigAttributes.SERVICE_ID, pid),
                        query.attributePredicate(KapuaEntityAttributes.SCOPE_ID, scopeId)
                )
        );

        ServiceConfigListResult result = entityManagerSession.doAction(EntityManagerContainer.<ServiceConfigListResult>create()
                .onResultHandler(em -> ServiceDAO.query(em, ServiceConfig.class, ServiceConfigImpl.class, new ServiceConfigListResult(), query))
                .onBeforeHandler(() -> (ServiceConfigListResult) PRIVATE_ENTITY_CACHE.getList(scopeId, pid))
                .onAfterHandler(entity -> PRIVATE_ENTITY_CACHE.putList(scopeId, pid, entity)));

        Properties properties = null;
        if (result != null && !result.isEmpty()) {
            properties = result.getFirstItem().getConfigurations();
        }

        KapuaTocd ocd = getConfigMetadata(scopeId, excludeDisabled);

        return ocd == null ? null : toValues(ocd, properties);
    }

    @Override
    public void setConfigValues(KapuaId scopeId, KapuaId parentId, Map<String, Object> values) throws KapuaException {
        KapuaTocd ocd = getConfigMetadata(scopeId, false);
        Map<String, Object> originalValues = getConfigValues(scopeId);

        for (KapuaTad ad : ocd.getAD()) {
            boolean allowSelfEdit = Boolean.parseBoolean(ad.getOtherAttributes().getOrDefault(new QName("allowSelfEdit"), "false"));

            final KapuaId currentUserId = KapuaSecurityUtils.getSession().getUserId();
            boolean preventChange =
                    // if current user is not root user...
                    !txManager.execute(tx -> {
                        final boolean currentUserIsRoot = getRootUserTester().isRoot(currentUserId);
                        return currentUserIsRoot;
                    }) &&
                            // current configuration does not allow self edit...
                            !allowSelfEdit &&
                            // a configuration for the current logged account is about to be changed...
                            KapuaSecurityUtils.getSession().getScopeId().equals(scopeId) &&
                            // and the new value is different from the other one...
                            !originalValues.get(ad.getId()).equals(values.get(ad.getId()));

            if (preventChange) {
                // ... prevent the change!
                throw KapuaException.internalError(String.format("The configuration \"%s\" cannot be changed by this user in this account", ad.getId()));
            }
        }

        getAuthorizationService().checkPermission(getPermissionFactory().newPermission(Optional.ofNullable(domain).map(d -> d.getName()).orElse(null), Actions.write, scopeId));

        validateConfigurations(ocd, values, scopeId, parentId);

        ServiceConfigQueryImpl query = new ServiceConfigQueryImpl(scopeId);
        query.setPredicate(
                query.andPredicate(
                        query.attributePredicate(ServiceConfigAttributes.SERVICE_ID, pid),
                        query.attributePredicate(KapuaEntityAttributes.SCOPE_ID, scopeId)
                )
        );

        ServiceConfigListResult result = entityManagerSession.doAction(EntityManagerContainer.<ServiceConfigListResult>create().
                onResultHandler(em -> ServiceDAO.query(em, ServiceConfig.class, ServiceConfigImpl.class, new ServiceConfigListResult(), query))
        );

        Properties props = toProperties(values);
        if (result == null || result.isEmpty()) {
            // In not exists create then return
            ServiceConfig serviceConfigNew = new ServiceConfigImpl(scopeId);
            serviceConfigNew.setPid(pid);
            serviceConfigNew.setConfigurations(props);

            createConfig(serviceConfigNew);
        } else {
            // If exists update it
            ServiceConfig serviceConfig = result.getFirstItem();
            serviceConfig.setConfigurations(props);

            updateConfig(serviceConfig);
        }
    }

    /**
     * Checks if the given {@link KapuaTad} is enabled for the given scope {@link KapuaId}.
     * <p>
     * By default it returns {@code true}. {@link KapuaConfigurableService}s can change this behaviour if needed.
     *
     * @param ad
     *         The {@link KapuaTad} to check.
     * @param scopeId
     *         The scope {@link KapuaId} for which to check.
     * @return {@code true} if enabled, {@code false} otherwise.
     * @since 1.3.0
     */
    protected boolean isPropertyEnabled(KapuaTad ad, KapuaId scopeId) {
        return true;
    }

    /**
     * Gets the {@link KapuaConfigurableService} pid.
     *
     * @return The {@link KapuaConfigurableService} pid.
     * @since 2.0.0
     */
    public String getServicePid() {
        return pid;
    }

    /**
     * PermissionFactory should be provided by the Locator, but in most cases when this class is instantiated through this constructor the Locator is not yet ready, therefore fetching of the required
     * instance is demanded to this artificial getter.
     *
     * @return The instantiated (hopefully) {@link PermissionFactory} instance
     */
    //TODO: Remove as soon as deprecated constructors are removed, use field directly instead.
    protected PermissionFactory getPermissionFactory() {
        if (permissionFactory == null) {
            permissionFactory = KapuaLocator.getInstance().getFactory(PermissionFactory.class);
        }
        return permissionFactory;
    }

    /**
     * AuthorizationService should be provided by the Locator, but in most cases when this class is instantiated through the deprecated constructor the Locator is not yet ready, therefore fetching of
     * the required instance is demanded to this artificial getter.
     *
     * @return The instantiated (hopefully) {@link AuthorizationService} instance
     */
    //TODO: Remove as soon as deprecated constructors are removed, use field directly instead.
    protected AuthorizationService getAuthorizationService() {
        if (authorizationService == null) {
            authorizationService = KapuaLocator.getInstance().getService(AuthorizationService.class);
        }
        return authorizationService;
    }

    /**
     * AuthorizationService should be provided by the Locator, but in most cases when this class is instantiated through the deprecated constructor the Locator is not yet ready, therefore fetching of
     * the required instance is demanded to this artificial getter.
     *
     * @return The instantiated (hopefully) {@link AuthorizationService} instance
     */
    //TODO: Remove as soon as deprecated constructors are removed, use field directly instead.
    protected RootUserTester getRootUserTester() {
        if (rootUserTester == null) {
            rootUserTester = KapuaLocator.getInstance().getService(RootUserTester.class);
        }
        return rootUserTester;
    }
}
