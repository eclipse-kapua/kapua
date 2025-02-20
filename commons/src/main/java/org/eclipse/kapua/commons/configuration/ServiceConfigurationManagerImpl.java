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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.xml.namespace.QName;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.configuration.exception.ServiceConfigurationForbiddenException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.StringUtil;
import org.eclipse.kapua.model.KapuaEntityAttributes;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTmetadata;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.config.ServiceComponentConfiguration;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;

public class ServiceConfigurationManagerImpl implements ServiceConfigurationManager {

    protected final String pid;
    protected final TxManager txManager;
    private final String domain;
    private final ServiceConfigRepository serviceConfigRepository;
    private final RootUserTester rootUserTester;
    private final ServiceConfigurationMetadataProvider serviceConfigurationMetadataProvider;

    public ServiceConfigurationManagerImpl(
            String pid,
            String domain, TxManager txManager,
            ServiceConfigRepository serviceConfigRepository,
            RootUserTester rootUserTester,
            ServiceConfigurationMetadataProvider serviceConfigurationMetadataProvider) {
        this.pid = pid;
        this.txManager = txManager;
        this.domain = domain;
        this.serviceConfigRepository = serviceConfigRepository;
        this.rootUserTester = rootUserTester;
        this.serviceConfigurationMetadataProvider = serviceConfigurationMetadataProvider;
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
    protected boolean validateNewConfigValuesCoherence(TxContext txContext, KapuaTocd ocd, Map<String, Object> updatedProps, KapuaId scopeId, Optional<KapuaId> parentId) throws KapuaException {
        return true;
    }

    @Override
    public String getDomain() {
        return domain;
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
    @Override
    public void checkAllowedEntities(TxContext txContext, KapuaId scopeId, String entityType) throws KapuaException {

    }

    @Override
    public void setConfigValues(KapuaId scopeId, Optional<KapuaId> parentId, Map<String, Object> values) throws KapuaException {
        txManager.<Void>execute(tx -> {
            Optional<KapuaTocd> maybeOcd = doGetConfigMetadata(tx, scopeId, false);
            if (!maybeOcd.isPresent()) {
                return null;
                //throw?
            }

            final Optional<KapuaTocd> maybeKapuaTocd = doGetConfigMetadata(tx, scopeId, true);
            if (!maybeKapuaTocd.isPresent()) {
                return null;
                //throw?
            }

            Map<String, Object> originalValues = doGetConfigValues(tx, scopeId, maybeKapuaTocd.get());

            for (KapuaTad ad : maybeOcd.get().getAD()) {
                boolean allowSelfEdit = Boolean.parseBoolean(ad.getOtherAttributes().getOrDefault(new QName("allowSelfEdit"), "false"));

                final KapuaId currentUserId = KapuaSecurityUtils.getSession().getUserId();
                boolean preventChange =
                        // if current user is not root user...
                        !rootUserTester.isRoot(currentUserId) &&
                                // current configuration does not allow self edit...
                                !allowSelfEdit &&
                                // a configuration for the current logged account is about to be changed...
                                KapuaSecurityUtils.getSession().getScopeId().equals(scopeId) &&
                                // and the new value is different from the other one...
                                !originalValues.get(ad.getId()).equals(values.get(ad.getId()));

                if (preventChange) {
                    // ... prevent the change!
                    throw new ServiceConfigurationForbiddenException(scopeId, pid, ad.getId(), String.valueOf(values.get(ad.getId())));
                }
            }

            validateConfigurations(tx, maybeOcd.get(), values, scopeId, parentId);

            ServiceConfigQueryImpl query = new ServiceConfigQueryImpl(scopeId);
            query.setPredicate(
                    query.andPredicate(
                            query.attributePredicate(ServiceConfigAttributes.SERVICE_ID, pid),
                            query.attributePredicate(KapuaEntityAttributes.SCOPE_ID, scopeId)
                    )
            );

            ServiceConfigListResult result = serviceConfigRepository.query(tx, query);

            Properties props = toProperties(values);
            if (result == null || result.isEmpty()) {
                // In not exists create then return
                ServiceConfig serviceConfigNew = new ServiceConfigImpl(scopeId);
                serviceConfigNew.setPid(pid);
                serviceConfigNew.setConfigurations(props);

                createConfig(tx, serviceConfigNew);
            } else {
                // If exists update it
                ServiceConfig serviceConfig = result.getFirstItem();
                serviceConfig.setConfigurations(props);

                updateConfig(tx, serviceConfig);
            }
            return null;
        });
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
    private ServiceConfig createConfig(TxContext txContext, ServiceConfig serviceConfig) throws KapuaException {
        return serviceConfigRepository.create(txContext, serviceConfig);
    }

    /**
     * Updates the given {@link ServiceConfig}.
     *
     * @param serviceConfig
     *         The {@link ServiceConfig} to update.
     * @return The updates {@link ServiceConfig}.
     * @throws KapuaException
     */
    private ServiceConfig updateConfig(TxContext txContext, ServiceConfig serviceConfig)
            throws KapuaException {
        final ServiceConfig oldServiceConfig = serviceConfigRepository.find(txContext, serviceConfig.getScopeId(), serviceConfig.getId())
                .orElseThrow(() -> new KapuaEntityNotFoundException(ServiceConfig.TYPE, serviceConfig.getId()));

        if (!Objects.equals(oldServiceConfig.getScopeId(), serviceConfig.getScopeId())) {
            throw new KapuaIllegalArgumentException("serviceConfiguration.scopeId", serviceConfig.getScopeId().toStringId());
        }

        if (!oldServiceConfig.getPid().equals(serviceConfig.getPid())) {
            throw new KapuaIllegalArgumentException("serviceConfiguration.pid", serviceConfig.getPid());
        }
        // Update
        return serviceConfigRepository.update(txContext, serviceConfig);
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
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (entry.getValue() != null) {
                props.setProperty(entry.getKey(), StringUtil.valueToString(entry.getValue()));
            }
        }

        return props;
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
    private void validateConfigurations(TxContext txContext, KapuaTocd ocd, Map<String, Object> updatedProps, KapuaId scopeId, Optional<KapuaId> parentId)
            throws KapuaException {
        if (ocd == null) {
            return;
        }

        // Get Disabled Properties
        List<KapuaTad> disabledProperties = ocd.getAD().stream().filter(ad -> !isPropertyEnabled(ad, scopeId)).collect(Collectors.toList());

        if (!disabledProperties.isEmpty()) {
            // If there's any disabled property, read current values to overwrite the proposed ones
            Map<String, Object> originalValues = doGetConfigValues(txContext, scopeId, false);
            if (originalValues != null) {
                disabledProperties.forEach(disabledProp -> updatedProps.put(disabledProp.getId(), originalValues.get(disabledProp.getId())));
            }
        }

        // build a map of all the attribute definitions
        Map<String, KapuaTad> attrDefs = ocd.getAD().stream().collect(Collectors.toMap(KapuaTad::getId, ad -> ad));

        // loop over the proposed property values
        // and validate them against the definition
        for (Map.Entry<String, Object> property : updatedProps.entrySet()) {

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

        validateNewConfigValuesCoherence(txContext, ocd, updatedProps, scopeId, parentId);
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
    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");

        return txManager.execute(txContext -> doGetConfigValues(txContext, scopeId, excludeDisabled));
    }

    @Override
    public Map<String, Object> getConfigValues(TxContext txContext, KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");

        return doGetConfigValues(txContext, scopeId, excludeDisabled);
    }

    protected Map<String, Object> doGetConfigValues(TxContext txContext, KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        final Optional<KapuaTocd> metadata = doGetConfigMetadata(txContext, scopeId, excludeDisabled);
        return metadata.map(m -> {
            try {
                return doGetConfigValues(txContext, scopeId, m);
            } catch (KapuaException e) {
                throw new RuntimeException(e);
            }
        }).orElse(Collections.emptyMap());
    }

    protected Map<String, Object> doGetConfigValues(TxContext txContext, KapuaId scopeId, KapuaTocd metadata) throws KapuaException {
        if (metadata == null) {
            return null;
        }
        // Get configuration values
        final ServiceConfigListResult result = serviceConfigRepository.findByScopeAndPid(txContext, scopeId, pid);

        Properties properties = null;
        if (result != null && !result.isEmpty()) {
            properties = result.getFirstItem().getConfigurations();
        }

        return toValues(metadata, properties);
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
    @Override
    public Optional<KapuaTocd> getConfigMetadata(KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        return txManager.execute(txContext -> doGetConfigMetadata(txContext, scopeId, excludeDisabled));
    }

    protected Optional<KapuaTocd> doGetConfigMetadata(TxContext txContext, KapuaId scopeId, boolean excludeDisabled) throws KapuaException {
        // Argument validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        // Check disabled service
        if (!isServiceEnabled(txContext, scopeId)) {
            return Optional.empty();
        }
        // Get the Tocd
        try {
            return processMetadata(txContext, serviceConfigurationMetadataProvider.fetchMetadata(txContext, pid), scopeId, excludeDisabled);
        } catch (Exception e) {
            throw KapuaException.internalError(e);
        }
    }

    @Override
    public Optional<ServiceComponentConfiguration> extractServiceComponentConfiguration(KapuaId scopeId) throws KapuaException {
        return txManager.execute(txContext -> {
            final Optional<KapuaTocd> maybeMetadata = this.doGetConfigMetadata(txContext, scopeId, true);
            if (!maybeMetadata.isPresent()) {
                return Optional.empty();
            }
            final KapuaTocd metadata = maybeMetadata.get();
            final Map<String, Object> values = this.doGetConfigValues(txContext, scopeId, metadata);
            final ServiceComponentConfiguration res = new ServiceComponentConfiguration(metadata.getId());
            res.setDefinition(metadata);
            res.setName(metadata.getName());
            res.setProperties(values);
            return Optional.of(res);
        });
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
    private Optional<KapuaTocd> processMetadata(TxContext txContext, Optional<KapuaTmetadata> metadata, KapuaId scopeId, boolean excludeDisabled) {
        return metadata.flatMap(meta -> {
            for (KapuaTocd ocd : meta.getOCD()) {
                if (ocd.getId() != null && ocd.getId().equals(pid) && isServiceEnabled(txContext, scopeId)) {
                    ocd.getAD().removeIf(ad -> excludeDisabled && !isPropertyEnabled(ad, scopeId));
                    return Optional.of(ocd);
                }
            }
            return Optional.empty();
        });
    }
}