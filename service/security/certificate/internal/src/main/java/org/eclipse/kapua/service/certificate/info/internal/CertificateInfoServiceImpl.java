/*******************************************************************************
 * Copyright (c) 2018, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.certificate.info.internal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.certificate.CertificateQuery;
import org.eclipse.kapua.service.certificate.CertificateService;
import org.eclipse.kapua.service.certificate.CertificateUsage;
import org.eclipse.kapua.service.certificate.info.CertificateInfo;
import org.eclipse.kapua.service.certificate.info.CertificateInfoCreator;
import org.eclipse.kapua.service.certificate.info.CertificateInfoListResult;
import org.eclipse.kapua.service.certificate.info.CertificateInfoQuery;
import org.eclipse.kapua.service.certificate.info.CertificateInfoService;

@Singleton
public class CertificateInfoServiceImpl implements CertificateInfoService {

    private final CertificateService certificateService;

    @Inject
    public CertificateInfoServiceImpl(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Override
    public CertificateInfo create(CertificateInfoCreator creator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CertificateInfo find(KapuaId scopeId, KapuaId entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CertificateInfoListResult query(KapuaQuery query) throws KapuaException {
        ArgumentValidator.notNull(query, "query");

        CertificateQuery certificateQuery = new CertificateQuery(query);

        CertificateInfoListResult publicCertificates = new CertificateInfoListResult();
        publicCertificates.addItem(certificateService.query(certificateQuery).getFirstItem());

        return publicCertificates;
    }

    @Override
    public long count(KapuaQuery query) throws KapuaException {
        ArgumentValidator.notNull(query, "query");

        CertificateQuery privateQuery = new CertificateQuery(query);
        privateQuery.setIncludeInherited(((CertificateInfoQuery) query).getIncludeInherited());

        return certificateService.count(privateQuery);
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId entityId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CertificateInfo> findAncestorsCertificates(KapuaId scopeId, CertificateUsage usage) {
        throw new UnsupportedOperationException();
    }
}
