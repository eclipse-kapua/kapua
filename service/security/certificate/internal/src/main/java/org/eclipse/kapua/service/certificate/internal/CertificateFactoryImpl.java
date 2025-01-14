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

import javax.inject.Singleton;

import org.eclipse.kapua.KapuaEntityCloneException;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.certificate.Certificate;
import org.eclipse.kapua.service.certificate.CertificateCreator;
import org.eclipse.kapua.service.certificate.CertificateFactory;
import org.eclipse.kapua.service.certificate.CertificateGenerator;
import org.eclipse.kapua.service.certificate.CertificateUsage;
import org.eclipse.kapua.service.certificate.KeyUsage;
import org.eclipse.kapua.service.certificate.KeyUsageSetting;

/**
 * {@link CertificateFactory} implementation.
 *
 * @since 1.0.0
 */
@Singleton
public class CertificateFactoryImpl implements CertificateFactory {

    @Override
    public Certificate newEntity(KapuaId scopeId) {
        return new CertificateImpl(scopeId);
    }

    @Override
    public CertificateCreator newCreator(KapuaId scopeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CertificateUsage newCertificateUsage(String name) {
        return new CertificateUsageImpl(name);
    }

    @Override
    public KeyUsageSetting newKeyUsageSetting(KeyUsage keyUsage, boolean allowed, Boolean kapuaAllowed) {
        KeyUsageSetting keyUsageSetting = new KeyUsageSettingImpl();

        keyUsageSetting.setKeyUsage(keyUsage);
        keyUsageSetting.setAllowed(allowed);
        keyUsageSetting.setKapuaAllowed(kapuaAllowed);

        return keyUsageSetting;
    }

    @Override
    public CertificateGenerator newCertificateGenerator() {
        return null;
    }

    @Override
    public Certificate clone(Certificate certificate) {
        try {
            return new CertificateImpl(certificate);
        } catch (Exception e) {
            throw new KapuaEntityCloneException(e, Certificate.TYPE, certificate);
        }
    }
}
