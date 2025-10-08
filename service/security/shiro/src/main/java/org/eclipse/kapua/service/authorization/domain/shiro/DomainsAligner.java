/*******************************************************************************
 * Copyright (c) 2024, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authorization.domain.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.log.ConfigurationPrinter;
import org.eclipse.kapua.locator.initializers.KapuaInitializingMethod;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.domain.DomainEntry;
import org.eclipse.kapua.service.authorization.access.AccessPermissionRepository;
import org.eclipse.kapua.service.authorization.domain.DomainRepository;
import org.eclipse.kapua.service.authorization.role.RolePermissionRepository;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aligns {@link org.eclipse.kapua.service.authorization.domain.Domain} registered in the DB with the ones defined in the Kapua Modules.
 * <p>
 * Each Kapua Module can define a {@link DomainEntry} annotated with @{@link com.google.inject.multibindings.ProvidesIntoSet}.
 * The {@link DomainsAligner} will retrieve and match them based on the {@link Domain#getName()}.
 * <p>
 * NOTE: Currently the creation of {@link DomainEntry}es that do not exist on the DB is disabled.
 *
 * @since 2.1.0
 */
public class DomainsAligner {
    private final static Logger LOGGER = LoggerFactory.getLogger(DomainsAligner.class);
    private final TxManager txManager;
    private final DomainRepository domainRepository;
    private final AccessPermissionRepository accessPermissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final Set<Domain> knownDomains;

    @Inject
    public DomainsAligner(
            @Named("authorizationTxManager") TxManager txManager,
            DomainRepository domainRepository,
            AccessPermissionRepository accessPermissionRepository,
            RolePermissionRepository rolePermissionRepository,
            Set<Domain> knownDomains
    ) {
        this.txManager = txManager;
        this.domainRepository = domainRepository;
        this.accessPermissionRepository = accessPermissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.knownDomains = knownDomains;
    }

    @KapuaInitializingMethod(priority = 20)
    public void populate() {
        ConfigurationPrinter configurationPrinter =
                ConfigurationPrinter
                        .create()
                        .withLogger(LOGGER)
                        .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                        .withTitle("Authorization Domain Aligner")
                        .addParameter("Wired domains", knownDomains.size());

        configurationPrinter.printLog();

        final Map<String, Domain> knownDomainsByName = knownDomains
                .stream()
                .collect(Collectors.toMap(Domain::getName, d -> d));

        List<String> declaredDomainsNotInDb = new ArrayList<>(knownDomainsByName.keySet());
        try {
            KapuaSecurityUtils.doPrivileged(() -> {
                txManager.execute(tx -> {

                    List<org.eclipse.kapua.service.authorization.domain.Domain> dbDomainEntries = domainRepository.query(tx, new DomainQueryImpl()).getItems();
                    LOGGER.info("Found {} domain declarations in database", dbDomainEntries.size());

                    for (org.eclipse.kapua.service.authorization.domain.Domain dbDomainEntry : dbDomainEntries) {
                        if (!knownDomainsByName.containsKey(dbDomainEntry.getName())) {
                            // Leave it be. As we share the database with other components, it might have been created by such components and be hidden from us
                            LOGGER.warn("Domain '{}' is only present in the database but has no current declaration! Details: {}", dbDomainEntry.getName(), dbDomainEntry.getDomain());
                            continue;
                        }

                        // Good news, it's both declared in wiring and present in the db!
                        declaredDomainsNotInDb.remove(dbDomainEntry.getName());

                        Domain wiredDomain = knownDomainsByName.get(dbDomainEntry.getName());
                        if (dbDomainEntry.getDomain().equals(wiredDomain)) {
                            // We are happy!
                            LOGGER.debug("Domain '{}' is ok: {}", dbDomainEntry.getName(), dbDomainEntry.getDomain());
                            continue;
                        }

                        // Align them!
                        alignDomains(tx, dbDomainEntry, wiredDomain);
                    }

                    // createMissingDomains(tx, declaredDomainsNotInDb, knownDomainsByName);
                    LOGGER.info("Domain alignment complete!");
                    return null;
                });
            });
        } catch (KapuaException e) {
            throw new RuntimeException(e);
        }
    }

    private void createMissingDomains(TxContext tx, List<String> declaredDomainsNotInDb, Map<String, Domain> knownDomainsByName) throws KapuaException {
        if (declaredDomainsNotInDb.size() > 0) {
            LOGGER.info("Found {} declared domains that have no counterpart in the database!", declaredDomainsNotInDb.size());

            // Create wired domains not present in the db
            for (final String declaredOnlyName : declaredDomainsNotInDb) {
                final Domain expected = knownDomainsByName.get(declaredOnlyName);
                createDomainInDb(tx, expected);
            }
        }
    }

    private void createDomainInDb(TxContext tx, Domain expected) throws KapuaException {
        LOGGER.info("To be added: {}", expected);

        org.eclipse.kapua.service.authorization.domain.Domain newEntity = new DomainImpl();
        newEntity.setName(expected.getName());
        newEntity.setActions(expected.getActions());
        newEntity.setGroupable(expected.getGroupable());
        newEntity.setServiceName(expected.getServiceName());

        domainRepository.create(tx, newEntity);
    }

    private void alignDomains(TxContext tx, org.eclipse.kapua.service.authorization.domain.Domain dbDomainEntry, Domain wiredDomain) throws KapuaException {
        LOGGER.error("Domain mismatch for name '{}'! Details:" +
                        "\n\tDb Entry: '{}', " +
                        "\n\tExpected: '{}'",
                dbDomainEntry.getName(),
                dbDomainEntry.getDomain(),
                wiredDomain);

        // Remove actions that are defined in the db but not in the wiring
        final EnumSet<Actions> actionsInExcessOnTheDb = EnumSet.copyOf(dbDomainEntry.getActions());
        actionsInExcessOnTheDb.removeAll(wiredDomain.getActions());
        if (!actionsInExcessOnTheDb.isEmpty()) {
            removeActionsInExcess(tx, dbDomainEntry.getName(), actionsInExcessOnTheDb);
            // Thank you JPA for auto-updating the entity on transaction close
            dbDomainEntry.getActions().removeAll(actionsInExcessOnTheDb);
        }

        // Add to the db actions that are only defined in the wiring
        final EnumSet<Actions> actionsMissingInTheDb = EnumSet.copyOf(wiredDomain.getActions());
        actionsMissingInTheDb.removeAll(dbDomainEntry.getActions());
        // Do not remove this if. For some reason adding empty enumset to the embedded list breaks down tests
        if (!actionsMissingInTheDb.isEmpty()) {
            // Thank you JPA for autoupdating the entity on transaction close
            dbDomainEntry.getActions().addAll(actionsMissingInTheDb);
        }

        // Align 'groupable' attribute
        dbDomainEntry.setGroupable(wiredDomain.getGroupable());
    }

    private void removeActionsInExcess(TxContext tx, String domainName, EnumSet<Actions> actionsInExcessOnTheDb) throws KapuaException {
        for (Actions actionToDelete : actionsInExcessOnTheDb) {
            LOGGER.info("Removing action '{}' from domain '{}'", actionToDelete, domainName);
            accessPermissionRepository.deleteAllByDomainAndAction(tx, domainName, actionToDelete);
            rolePermissionRepository.deleteAllByDomainAndAction(tx, domainName, actionToDelete);
        }
    }
}
