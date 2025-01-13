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
package org.eclipse.kapua.service.certificate;

import org.eclipse.kapua.model.KapuaEntityFactory;

/**
 * {@link CertificateFactory} definition.
 *
 * @see org.eclipse.kapua.model.KapuaEntityFactory
 * @since 1.0.0
 */
public interface CertificateFactory extends KapuaEntityFactory<Certificate, CertificateCreator, CertificateQuery> {

    /**
     * Instantiates a new {@link CertificateUsage}.
     *
     * @param name
     *         The name to set into the {@link CertificateUsage}
     * @return The newly instantiated {@link CertificateUsage}
     * @since 1.0.0
     */
    CertificateUsage newCertificateUsage(String name);

    /**
     * Instantiates a new {@link KeyUsageSetting}.
     *
     * @param usage
     *         The {@link KeyUsage} to set into the {@link KeyUsageSetting}.
     * @param allowed
     *         The allowed to set into the {@link KeyUsageSetting}.
     * @param kapuaAllowed
     *         The kapua allowed to set into the {@link KeyUsageSetting}.
     * @return The newly instantiated {@link KeyUsageSetting}
     * @since 1.0.0
     */
    KeyUsageSetting newKeyUsageSetting(KeyUsage usage, boolean allowed, Boolean kapuaAllowed);

    /**
     * Instantiates a new {@link CertificateGenerator}.
     *
     * @return The newly instantiated {@link CertificateGenerator}.
     * @since 1.0.0
     */
    CertificateGenerator newCertificateGenerator();

}
