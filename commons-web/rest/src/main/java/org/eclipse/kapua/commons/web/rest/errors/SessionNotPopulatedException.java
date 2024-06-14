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
package org.eclipse.kapua.commons.web.rest.errors;

import org.eclipse.kapua.commons.web.rest.RestApiErrorCodes;
import org.eclipse.kapua.commons.web.rest.RestApiRuntimeException;

public class SessionNotPopulatedException extends RestApiRuntimeException {

    public SessionNotPopulatedException() {
        super(RestApiErrorCodes.SESSION_NOT_POPULATED);
    }
}
