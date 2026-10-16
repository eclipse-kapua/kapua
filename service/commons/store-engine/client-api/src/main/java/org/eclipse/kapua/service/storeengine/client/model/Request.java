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
 *******************************************************************************/
package org.eclipse.kapua.service.storeengine.client.model;

import org.eclipse.kapua.service.storable.model.Storable;

/**
 * Base {@link Request} definition.
 *
 * @since 1.0.0
 */
public abstract class Request {

    /**
     * The Object identifier
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
     * The Object of the {@link Request}
     */
    private Storable storable;

    /**
     * Constructor.
     *
     * @param index          The index.
     * @param storable       the objetc of the request
     * @since 1.0.0
     */
    protected Request(String id, String index, Storable storable) {
        setId(id);
        setIndex(index);
        setStorable(storable);
    }

    /**
     * Gets the object id.
     *
     * @return The object id.
     * @since 1.0.0
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the object id.
     *
     * @param id The object id.
     * @since 1.0.0
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the Index.
     *
     * @return The index.
     * @since 1.0.0
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the Index.
     *
     * @param index The index
     * @since 1.0.0
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Gets the object of the request.
     *
     * @return The object of the request.
     * @since 1.0.0
     */
    public Storable getStorable() {
        return storable;
    }

    /**
     * Sets the object of the request.
     *
     * @param storable The object of the request.
     * @since 1.0.0
     */
    public void setStorable(Storable storable) {
        this.storable = storable;
    }

}
