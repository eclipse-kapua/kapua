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
package org.eclipse.kapua.service.authorization.domain.shiro;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.locator.initializers.KapuaInitializingMethod;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.domain.Domain;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.access.AccessPermissionRepository;
import org.eclipse.kapua.service.authorization.domain.DomainRepository;
import org.eclipse.kapua.service.authorization.role.RolePermissionRepository;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainsAligner {

    private final TxManager txManager;
    private final DomainRepository domainRepository;
    private final AccessPermissionRepository accessPermissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final Set<Domain> knownDomains;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public DomainsAligner(@Named("authorizationTxManager")
    TxManager txManager,
            DomainRepository domainRepository,
            AccessPermissionRepository accessPermissionRepository, RolePermissionRepository rolePermissionRepository,
            Set<Domain> knownDomains) {
        this.txManager = txManager;
        this.domainRepository = domainRepository;
        this.accessPermissionRepository = accessPermissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.knownDomains = knownDomains;
    }

    @KapuaInitializingMethod(priority = 20)
    public void populate() {
        logger.info("Domain alignment commencing. Found {} domain declarations in wiring", knownDomains.size());
        final Map<String, Domain> knownDomainsByName = knownDomains
                .stream()
                .collect(Collectors.toMap(d -> d.getName(), d -> d));
        final List<String> declaredDomainsNotInDb = new ArrayList<>(knownDomainsByName.keySet());
        try {
            KapuaSecurityUtils.doPrivileged(() -> {
                txManager.execute(tx -> {
                    final List<org.eclipse.kapua.service.authorization.domain.Domain> dbDomainEntries = domainRepository.query(tx, new KapuaQuery()).getItems();
                    logger.info("Found {} domain declarations in database", dbDomainEntries.size());

                    for (final org.eclipse.kapua.service.authorization.domain.Domain dbDomainEntry : dbDomainEntries) {
                        if (!knownDomainsByName.containsKey(dbDomainEntry.getName())) {
                            //Leave it be. As we share the database with other components, it might have been created by such components and be hidden from us
                            logger.warn("Domain '{}' is only present in the database but has no current declaration! Details: {}", dbDomainEntry.getName(), dbDomainEntry.getDomain());
                            continue;
                        }
                        //Good news, it's both declared in wiring and present in the db!
                        declaredDomainsNotInDb.remove(dbDomainEntry.getName());
                        //Trigger fetch of Actions collection from db, otherwise the toString would not show the details
                        dbDomainEntry.getActions();
                        final Domain wiredDomain = knownDomainsByName.get(dbDomainEntry.getName());
                        if (dbDomainEntry.getDomain().equals(wiredDomain)) {
                            //We are happy!
                            logger.debug("Domain '{}' is ok: {}", dbDomainEntry.getName(), dbDomainEntry.getDomain());
                            continue;
                        }
                        //Align them!
                        alignDomains(tx, dbDomainEntry, wiredDomain);
                    }
                    //                    createMissingDomains(tx, declaredDomainsNotInDb, knownDomainsByName);
                    logger.info("Domain alignment complete!");
                    return null;
                });
            });
        } catch (KapuaException e) {
            throw new RuntimeException(e);
        }
    }

    private void createMissingDomains(TxContext tx, List<String> declaredDomainsNotInDb, Map<String, Domain> knownDomainsByName) throws KapuaException {
        if (declaredDomainsNotInDb.size() > 0) {
            logger.info("Found {} declared domains that have no counterpart in the database!", declaredDomainsNotInDb.size());
            //Create wired domains not present in the db
            for (final String declaredOnlyName : declaredDomainsNotInDb) {
                final Domain expected = knownDomainsByName.get(declaredOnlyName);
                createDomainInDb(tx, expected);
            }
        }
    }

    private void createDomainInDb(TxContext tx, Domain expected) throws KapuaException {
        logger.info("To be added: {}", expected);
        final org.eclipse.kapua.service.authorization.domain.Domain newEntity = new DomainImpl();
        newEntity.setName(expected.getName());
        newEntity.setActions(expected.getActions());
        newEntity.setGroupable(expected.getGroupable());
        newEntity.setServiceName(expected.getServiceName());
        domainRepository.create(tx, newEntity);
    }

    private void alignDomains(TxContext tx, org.eclipse.kapua.service.authorization.domain.Domain dbDomainEntry, Domain wiredDomain) throws KapuaException {
        logger.error("Domain  mismatch for name '{}'! Details:" +
                        "\n\tDb entry: '{}', " +
                        "\n\texpected: '{}'",
                dbDomainEntry.getName(),
                dbDomainEntry.getDomain(),
                wiredDomain);

        //Remove actions that are defined in the db but not in the wiring
        final EnumSet<Actions> actionsInExcessOnTheDb = EnumSet.copyOf(dbDomainEntry.getActions());
        actionsInExcessOnTheDb.removeAll(wiredDomain.getActions());
        if (!actionsInExcessOnTheDb.isEmpty()) {
            removeActionsInExcess(tx, dbDomainEntry.getName(), actionsInExcessOnTheDb);
            // Thank you JPA for autoupdating the entity on transaction close
            dbDomainEntry.getActions().removeAll(actionsInExcessOnTheDb);
        }

        //Add to the db actions that are only defined in the wiring
        final EnumSet<Actions> actionsMissingInTheDb = EnumSet.copyOf(wiredDomain.getActions());
        actionsMissingInTheDb.removeAll(dbDomainEntry.getActions());
        //Do not remove this if. For some reason adding empty enumset to the embedded list breaks down tests
        if (!actionsMissingInTheDb.isEmpty()) {
            // Thank you JPA for autoupdating the entity on transaction close
            dbDomainEntry.getActions().addAll(actionsMissingInTheDb);
        }
    }

    private void removeActionsInExcess(TxContext tx, String domainName, EnumSet<Actions> actionsInExcessOnTheDb) throws KapuaException {
        for (final Actions actionToDelete : actionsInExcessOnTheDb) {
            logger.info("Removing action '{}' from domain '{}'", actionToDelete, domainName);
            accessPermissionRepository.deleteAllByDomainAndAction(tx, domainName, actionToDelete);
            rolePermissionRepository.deleteAllByDomainAndAction(tx, domainName, actionToDelete);
        }
    }
}
