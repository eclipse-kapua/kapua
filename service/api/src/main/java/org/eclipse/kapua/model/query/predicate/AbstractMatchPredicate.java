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
package org.eclipse.kapua.model.query.predicate;

import java.util.List;

/**
 * {@link MatchPredicate} implementation.
 *
 * @since 1.3.0
 */
public class AbstractMatchPredicate<T> implements MatchPredicate<T> {

    protected List<String> attributeNames;
    protected T matchTerm;

    @Override
    public List<String> getAttributeNames() {
        return attributeNames;
    }

    @Override
    public T getMatchTerm() {
        return matchTerm;
    }

}
