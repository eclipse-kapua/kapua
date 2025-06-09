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
package org.eclipse.kapua.service.user;

import java.util.Arrays;

import org.eclipse.kapua.model.query.predicate.AbstractMatchPredicate;

public class UserMatchPredicate<T> extends AbstractMatchPredicate<T> {

    /**
     * Constructor.
     *
     * @param matchTerm
     * @since 1.3.0
     */
    public UserMatchPredicate(T matchTerm) {
        this.attributeNames = Arrays.asList(
                UserAttributes.NAME,
                UserAttributes.EMAIL,
                UserAttributes.PHONE_NUMBER,
                UserAttributes.DISPLAY_NAME,
                UserAttributes.EXTERNAL_ID,
                UserAttributes.DESCRIPTION,
                UserAttributes.EXTERNAL_USERNAME
        );
        this.matchTerm = matchTerm;
    }
}
