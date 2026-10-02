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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.service.storeengine.client.model;

/**
 * Insert {@link Response} definition.
 *
 * @since 1.0.0
 */
public class InsertResponse extends Response {

    /**
     * Constructor.
     *
     * @param id             Ihe inserted record id
     * @param index          The index
     * @since 1.0.0
     */
    public InsertResponse(String id, String index) {
        super(id, index);
    }

}
