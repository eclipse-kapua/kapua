/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.module.authorization.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtDomain;
import org.eclipse.kapua.app.console.module.authorization.shared.model.GwtPermission.GwtAction;
import org.eclipse.kapua.app.console.module.authorization.shared.service.GwtDomainRegistryService;
import org.eclipse.kapua.app.console.module.authorization.shared.util.KapuaGwtAuthorizationModelConverter;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.domain.Domain;
import org.eclipse.kapua.service.authorization.domain.DomainFactory;
import org.eclipse.kapua.service.authorization.domain.DomainListResult;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;

public class GwtDomainRegistryServiceImpl extends KapuaRemoteServiceServlet implements GwtDomainRegistryService {

    private static final long serialVersionUID = -699492835893299489L;

    private final KapuaLocator locator = KapuaLocator.getInstance();
    private final DomainRegistryService domainRegistryService = locator.getService(DomainRegistryService.class);
    private final DomainFactory domainFactory = locator.getFactory(DomainFactory.class);

    @Override
    public List<GwtDomain> findAll() throws GwtKapuaException {
        List<GwtDomain> gwtDomainList = new ArrayList<GwtDomain>();
        try {
            KapuaQuery query = new KapuaQuery((KapuaId) null);
            DomainListResult list = domainRegistryService.query(query);

            for (Domain domain : list.getItems()) {
                gwtDomainList.add(KapuaGwtAuthorizationModelConverter.convertDomain(domain));
            }
        } catch (Exception e) {
            KapuaExceptionHandler.handle(e);
        }
        Collections.sort(gwtDomainList);
        return gwtDomainList;
    }

    @Override
    public List<GwtAction> findActionsByDomainName(String domainName) throws GwtKapuaException {
        List<GwtAction> gwtActionList = new ArrayList<GwtAction>();
        try {
            Domain domain = domainRegistryService.findByName(domainName);

            if (domain != null) {
                for (Actions action : domain.getActions()) {
                    gwtActionList.add(KapuaGwtAuthorizationModelConverter.convertAction(action));
                }
                Collections.sort(gwtActionList);
            }

        } catch (Exception ex) {
            KapuaExceptionHandler.handle(ex);
        }
        return gwtActionList;
    }

}
