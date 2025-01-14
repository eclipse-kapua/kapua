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
package org.eclipse.kapua.service.certificate.info.xml;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.certificate.info.CertificateInfo;
import org.eclipse.kapua.service.certificate.info.CertificateInfoCreator;
import org.eclipse.kapua.service.certificate.info.CertificateInfoFactory;

@XmlRegistry
public class CertificateInfoXmlRegistry {

    private final CertificateInfoFactory certificateInfoFactory = KapuaLocator.getInstance().getFactory(CertificateInfoFactory.class);

    public CertificateInfo newCertificateInfo() {
        return certificateInfoFactory.newEntity(null);
    }

    public CertificateInfoCreator newCreator() {
        return certificateInfoFactory.newCreator(null);
    }

}
