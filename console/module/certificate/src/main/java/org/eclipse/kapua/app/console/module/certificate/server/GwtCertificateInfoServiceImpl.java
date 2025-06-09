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
package org.eclipse.kapua.app.console.module.certificate.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.util.KapuaGwtCommonsModelConverter;
import org.eclipse.kapua.app.console.module.certificate.shared.model.GwtCertificateInfo;
import org.eclipse.kapua.app.console.module.certificate.shared.service.GwtCertificateInfoService;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.certificate.info.CertificateInfo;
import org.eclipse.kapua.service.certificate.info.CertificateInfoListResult;
import org.eclipse.kapua.service.certificate.info.CertificateInfoQuery;
import org.eclipse.kapua.service.certificate.info.CertificateInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GwtCertificateInfoServiceImpl extends KapuaRemoteServiceServlet implements GwtCertificateInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(GwtCertificateInfoServiceImpl.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final CertificateInfoService CERTIFICATE_INFO_SERVICE = LOCATOR.getService(CertificateInfoService.class);

    @Override
    public List<GwtCertificateInfo> findAll(String scopeIdString) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);

            List<GwtCertificateInfo> gwtCertificateInfos = new ArrayList<GwtCertificateInfo>();

            CertificateInfoQuery query = new CertificateInfoQuery(scopeId);
            query.setIncludeInherited(true);

            CertificateInfoListResult certificateInfos = CERTIFICATE_INFO_SERVICE.query(query);

            for (CertificateInfo certificateInfo : certificateInfos.getItems()) {
                GwtCertificateInfo gwtCertificateInfo = new GwtCertificateInfo();
                gwtCertificateInfo.setId(KapuaGwtCommonsModelConverter.convertKapuaId(certificateInfo.getId()));
                gwtCertificateInfo.setName(certificateInfo.getName());

                gwtCertificateInfos.add(gwtCertificateInfo);
            }

            return gwtCertificateInfos;
        } catch (Exception e) {
            throw KapuaExceptionHandler.buildExceptionFromError(e);
        }
    }

    @Override
    public boolean isFindSupported() {
        try {
            CERTIFICATE_INFO_SERVICE.find(null, null);
            return false;
        } catch (UnsupportedOperationException uoe) {
            LOG.warn("The current deployment does not support CertificateInfoService.find(...). Some of the UI features will be disabled...");
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
