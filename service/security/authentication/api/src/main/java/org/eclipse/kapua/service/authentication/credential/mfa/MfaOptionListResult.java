/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.credential.mfa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.query.KapuaListResult;

/**
 * {@link MfaOption} {@link KapuaListResult} definition.
 *
 * @since 1.3.0
 */
@XmlRootElement(name = "mfaOptionListResult")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class MfaOptionListResult extends KapuaListResult<MfaOption> {

    private static final long serialVersionUID = -4204695192086365901L;

}
