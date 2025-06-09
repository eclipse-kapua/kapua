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
package org.eclipse.kapua.service.certificate.info;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * @since 1.1.0
 */
@XmlRootElement(name = "certificateInfoCreator")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
//This class exists only because the Service needed a placeholder.Sigh.
public class CertificateInfoCreator extends KapuaNamedEntityCreator {

    public CertificateInfoCreator() {
        throw new UnsupportedOperationException();
    }

    public CertificateInfoCreator(KapuaId scopeId) {
        throw new UnsupportedOperationException();
    }

    public CertificateInfoCreator(KapuaId scopeId, String name) {
        throw new UnsupportedOperationException();
    }
}
