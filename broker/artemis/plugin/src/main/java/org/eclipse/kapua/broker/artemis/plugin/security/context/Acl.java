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
package org.eclipse.kapua.broker.artemis.plugin.security.context;

import org.apache.activemq.artemis.core.config.WildcardConfiguration;
import org.apache.activemq.artemis.core.settings.HierarchicalRepository;
import org.apache.activemq.artemis.core.settings.impl.HierarchicalObjectRepository;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.broker.artemis.plugin.security.metric.LoginMetric;
import org.eclipse.kapua.client.security.bean.AuthAcl;
import org.eclipse.kapua.client.security.bean.AuthAcl.Action;
import org.eclipse.kapua.service.authentication.KapuaPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Acl {

    private final Logger logger = LoggerFactory.getLogger(Acl.class);

    private static final char SINGLE_WORD = '+';
    private static final char ANY_WORDS = '#';
    private static final char SEPARATOR = '/';

    private final WildcardConfiguration wildcardConfiguration;
    private final HierarchicalRepository<KapuaPrincipal> read;
    private final HierarchicalRepository<KapuaPrincipal> write;
    private final HierarchicalRepository<KapuaPrincipal> admin;

    public Acl(LoginMetric loginMetric, KapuaPrincipal principal, List<AuthAcl> authAcls) throws KapuaIllegalArgumentException {
        wildcardConfiguration = new WildcardConfiguration().
            setSingleWord(SINGLE_WORD).
            setAnyWords(ANY_WORDS).
            setDelimiter(SEPARATOR);
        if (principal == null) {
            throw new KapuaIllegalArgumentException("principal", null);
        }
        read = new HierarchicalObjectRepository<>(wildcardConfiguration);
        read.setDefault(null);
        write = new HierarchicalObjectRepository<>(wildcardConfiguration);
        write.setDefault(null);
        admin = new HierarchicalObjectRepository<>(wildcardConfiguration);
        admin.setDefault(null);
        StringBuilder aclLog = new StringBuilder();
        if (authAcls != null) {
            authAcls.forEach((authAcl) -> {
                try {
                    add(principal, authAcl.getMatch(), authAcl.getAction());
                    aclLog.append("\n\t").append(authAcl.getMatch()).append(" - ").append(authAcl.getAction()).append(" - ").
                            append(principal.getName()).append("/").append(principal.getAccountId().toStringId()).append("/").append(principal.getClientId());
                } catch (Exception e) {
                    loginMetric.getAclCreationFailure().inc();
                    //no security issue since in case of error no acl is added
                    logger.error("Error adding acl {}", authAcl, e);
                }
            });
        }
        else {
            aclLog.append("no restrictions!");
        }
        logger.info("Acl: {}", aclLog);
    }

    private void add(KapuaPrincipal principal, String match, Action action) throws KapuaIllegalArgumentException {
        if (action == null) {
            throw new KapuaIllegalArgumentException("action", null);
        }
        if (principal == null) {
            throw new KapuaIllegalArgumentException("principal", null);
        }
        if (match == null || match.trim().length() <= 0) {
            throw new KapuaIllegalArgumentException("match", match);
        }
        switch (action) {
            case all:
                read.addMatch(match, principal);
                write.addMatch(match, principal);
                admin.addMatch(match, principal);
                break;
            case read:
                read.addMatch(match, principal);
                break;
            case write:
                write.addMatch(match, principal);
                break;
            case admin:
                admin.addMatch(match, principal);
                break;
            case readAdmin:
                read.addMatch(match, principal);
                admin.addMatch(match, principal);
                break;
            case writeAdmin:
                write.addMatch(match, principal);
                admin.addMatch(match, principal);
                break;
        }
    }

    public boolean canRead(KapuaPrincipal principal, String address) {
        return !containsAnyWordWildcardBeforeLastPosition(address) && principal.equals(read.getMatch(address));
    }

    public boolean canWrite(KapuaPrincipal principal, String address) {
        return !containsWildcards(address) && principal.equals(write.getMatch(address));
    }

    public boolean canManage(KapuaPrincipal principal, String address) {
        return !containsAnyWordWildcardBeforeLastPosition(address) && principal.equals(admin.getMatch(address));
    }

    private boolean containsAnyWordWildcardBeforeLastPosition(String address) {
        return address.indexOf(ANY_WORDS) < address.length() - 1 && address.indexOf(ANY_WORDS) > -1;
    }

    private boolean containsWildcards(String address) {
        return address.indexOf(ANY_WORDS) > -1 || address.indexOf(SINGLE_WORD) > -1;
    }
}
