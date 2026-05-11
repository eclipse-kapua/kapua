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
package org.eclipse.kapua.service.authentication.shiro.exceptions;

import org.apache.shiro.authc.ExpiredCredentialsException;

public class InvalidatedAccessTokenException extends ExpiredCredentialsException {

    private static final long serialVersionUID = 3922802106975119679L;

    public InvalidatedAccessTokenException() {
        super("The provided AccessToken has been invalidated in the past");
    }

}
