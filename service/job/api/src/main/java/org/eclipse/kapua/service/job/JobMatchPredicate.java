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
package org.eclipse.kapua.service.job;

import java.util.Arrays;

import org.eclipse.kapua.model.query.predicate.AbstractMatchPredicate;

public class JobMatchPredicate<T> extends AbstractMatchPredicate<T> {

    /**
     * Constructor.
     *
     * @param matchTerm
     * @since 2.0.0
     */
    public JobMatchPredicate(T matchTerm) {
        this.attributeNames = Arrays.asList(
                JobAttributes.NAME,
                JobAttributes.DESCRIPTION
        );
        this.matchTerm = matchTerm;
    }

}