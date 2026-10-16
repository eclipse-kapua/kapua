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

import org.eclipse.kapua.KapuaSerializable;

/**
 * Base {@link Response} definition.
 *
 * @since 1.0.0
 */
public abstract class Response implements KapuaSerializable {

    /**
     * Record id (it should set by the datastore client component)
     *
     * @since 1.0.0
     */
    private String id;

    /**
     * The index
     *
     * @since 1.0.0
     */
    private String index;

    /**
     * The result of the {@link Response}
     *
     * @since 1.0.0
     */
    private boolean result;

    /**
     * Constructor.
     *
     * @param id             the record id.
     * @param index          the index
     * @since 1.0.0
     */
    protected Response(String id, String index) {
        setId(id);
        setIndex(index);
    }

    /**
     * Gets the object id (the subject of the operation).
     *
     * @return The object id (the subject of the operation).
     * @since 1.0.0
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the object id (the subject of the operation).
     *
     * @param id The object id (the subject of the operation).
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Sets the index
     *
     * @param index The index
     * @since 1.0.0
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Gets index
     *
     * @return The index
     * @since 1.0.0
     */
    public String getIndex() {
        return this.index;
    }

    /**
     * Gets the result condition.
     *
     * @return The result condition.
     * @since 1.0.0
     */
    public boolean isResult() {
        return result;
    }

    /**
     * Sets the result condition.
     *
     * @param result The result condition.
     * @since 1.0.0
     */
    public void setResult(boolean result) {
        this.result = result;
    }

}
