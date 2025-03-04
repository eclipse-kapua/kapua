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
package org.eclipse.kapua.service.certificate.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.KapuaFileUtils;
import org.eclipse.kapua.model.config.metatype.EmptyTocd;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.certificate.Certificate;
import org.eclipse.kapua.service.certificate.CertificateCreator;
import org.eclipse.kapua.service.certificate.CertificateFactory;
import org.eclipse.kapua.service.certificate.CertificateGenerator;
import org.eclipse.kapua.service.certificate.CertificateListResult;
import org.eclipse.kapua.service.certificate.CertificateService;
import org.eclipse.kapua.service.certificate.CertificateUsage;
import org.eclipse.kapua.service.certificate.KeyUsage;
import org.eclipse.kapua.service.certificate.KeyUsageSetting;
import org.eclipse.kapua.service.certificate.exception.KapuaCertificateErrorCodes;
import org.eclipse.kapua.service.certificate.exception.KapuaCertificateException;
import org.eclipse.kapua.service.certificate.internal.setting.KapuaCertificateSetting;
import org.eclipse.kapua.service.certificate.internal.setting.KapuaCertificateSettingKeys;
import org.eclipse.kapua.service.certificate.util.CertificateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

@Singleton
public class CertificateServiceImpl implements CertificateService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateServiceImpl.class);

    private final AuthorizationService authorizationService;
    private final PermissionFactory permissionFactory;
    private final CertificateFactory certificateFactory;
    private final KapuaCertificateSetting kapuaCertificateSetting;
    private String certificate;
    private String privateKey;
    private KapuaTocd emptyTocd;

    @Inject
    public CertificateServiceImpl(AuthorizationService authorizationService, PermissionFactory permissionFactory, CertificateFactory certificateFactory,
            KapuaCertificateSetting kapuaCertificateSetting) throws KapuaException {
        this.authorizationService = authorizationService;
        this.permissionFactory = permissionFactory;
        this.certificateFactory = certificateFactory;
        this.kapuaCertificateSetting = kapuaCertificateSetting;
        KapuaSecurityUtils.doPrivileged(() -> {
            String privateKeyPath = kapuaCertificateSetting.getString(KapuaCertificateSettingKeys.CERTIFICATE_JWT_PRIVATE_KEY);
            String certificatePath = kapuaCertificateSetting.getString(KapuaCertificateSettingKeys.CERTIFICATE_JWT_CERTIFICATE);

            if (Strings.isNullOrEmpty(privateKeyPath) && Strings.isNullOrEmpty(certificatePath)) {
                LOG.error("No private key and certificate path specified.\nPlease set authentication.session.jwt.private.key and authentication.session.jwt.certificate system properties.");
                throw new KapuaCertificateException(KapuaCertificateErrorCodes.CERTIFICATE_ERROR);
            } else {
                certificate = CertificateUtils.readCertificateAsString(KapuaFileUtils.getAsFile(certificatePath));
                privateKey = CertificateUtils.readPrivateKeyAsString(KapuaFileUtils.getAsFile(privateKeyPath));
            }
        });

        emptyTocd = new EmptyTocd(CertificateService.class.getName(), CertificateService.class.getSimpleName());

    }

    @Override
    public Certificate create(CertificateCreator creator) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Certificate find(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CertificateListResult query(KapuaQuery query) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        // Check Access
        authorizationService.checkPermission(permissionFactory.newPermission(Domains.CERTIFICATE, Actions.read, query.getScopeId()));
        // Create the default certificate
        CertificateUsage jwtCertificateUsage = new CertificateUsageImpl("JWT");
        Set<CertificateUsage> certificateUsages = Sets.newHashSet(jwtCertificateUsage);

        KeyUsageSetting keyUsageSetting = new KeyUsageSettingImpl();
        keyUsageSetting.setKeyUsage(KeyUsage.DIGITAL_SIGNATURE);
        keyUsageSetting.setAllowed(true);
        keyUsageSetting.setKapuaAllowed(true);

        Certificate kapuaCertificate = new CertificateImpl(KapuaId.ONE);
        kapuaCertificate.setPrivateKey(privateKey);
        kapuaCertificate.setCertificate(certificate);
        kapuaCertificate.getKeyUsageSettings().add(keyUsageSetting);
        kapuaCertificate.setCertificateUsages(certificateUsages);
        kapuaCertificate.setPassword(kapuaCertificateSetting.getString(KapuaCertificateSettingKeys.CERTIFICATE_JWT_PRIVATE_KEY_PASSWORD));

        CertificateListResult result = certificateFactory.newListResult();
        result.addItem(kapuaCertificate);

        return result;
    }

    @Override
    public long count(KapuaQuery query) {
        return 1L;
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId entityId) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Certificate findByName(String name) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Certificate update(Certificate entity) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Certificate generate(CertificateGenerator generator) throws KapuaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Certificate> findAncestorsCertificates(KapuaId scopeId, CertificateUsage usage) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KapuaTocd getConfigMetadata(KapuaId scopeId) throws KapuaException {
        return emptyTocd;
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId) throws KapuaException {
        return Collections.emptyMap();
    }

    @Override
    public void setConfigValues(KapuaId scopeId, KapuaId parentId, Map<String, Object> values) throws KapuaException {
        throw new UnsupportedOperationException();
    }

}
