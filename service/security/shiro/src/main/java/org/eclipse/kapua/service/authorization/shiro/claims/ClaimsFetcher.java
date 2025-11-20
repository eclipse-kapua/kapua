/*******************************************************************************
 * Copyright (c) 2021, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authorization.shiro.claims;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.id.KapuaId;

import java.util.Set;

public interface ClaimsFetcher {

    Set<String> fetchUserClaims(KapuaId inScope) throws KapuaException;

}
