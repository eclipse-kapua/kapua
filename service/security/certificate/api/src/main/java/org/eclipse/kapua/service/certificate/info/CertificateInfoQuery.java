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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaForwardableEntityQuery;
import org.eclipse.kapua.model.query.KapuaQuery;

/**
 * {@link CertificateInfo} {@link KapuaQuery} definition.
 *
 * @see KapuaQuery
 * @since 1.1.0
 */
@XmlRootElement(name = "query")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class CertificateInfoQuery extends KapuaForwardableEntityQuery {

    public CertificateInfoQuery() {
    }

    public CertificateInfoQuery(KapuaId scopeId) {
        super(scopeId);
    }

    public CertificateInfoQuery(KapuaQuery query) {
        super(query);
    }

    @Override
    /**
     * Gets whether or not to get also inherited {@link CertificateInfo}s
     *
     * @return {@code true} if set to get inherited {@link CertificateInfo}s, {@code false} otherwise.
     * @since 1.1.0
     */
    @XmlElement(name = "includeInherited")
    public Boolean getIncludeInherited() {
        return includeInherited;
    }

    @Override
    /**
     * Sets whether or not to get also inherited {@link CertificateInfo}s
     *
     * @param includeInherited {@code true} to get inherited {@link CertificateInfo}s, {@code false} otherwise.
     * @since 1.1.0
     */
    public void setIncludeInherited(Boolean includeInherited) {
        this.includeInherited = includeInherited;
    }

    /**
     * Instantiates a new {@link CertificateInfoMatchPredicate}.
     *
     * @param matchTerm
     *         The term to use to match.
     * @param <T>
     *         The type of the term
     * @return The newly instantiated {@link CertificateInfoMatchPredicate}.
     * @since 2.1.0
     */
    public <T> CertificateInfoMatchPredicate<T> matchPredicate(T matchTerm) {
        return new CertificateInfoMatchPredicate<>(matchTerm);
    }
}
