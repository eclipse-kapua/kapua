/*******************************************************************************
 * Copyright (c) 2026, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.web;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.service.certificate.CertificateFactory;
import org.eclipse.kapua.service.certificate.CertificateService;
import org.mockito.Mockito;

/**
 * Rest API Web App test {@link AbstractKapuaModule}
 *
 * @since 2.1.0
 */
public class RestApiAppTestModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        CertificateService mockCertificateService = Mockito.mock(CertificateService.class);
        bind(CertificateService.class).toInstance(mockCertificateService);
        CertificateFactory mockCertificateFactory = Mockito.mock(CertificateFactory.class);
        bind(CertificateFactory.class).toInstance(mockCertificateFactory);

    }
}
