/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.domain;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaEntityService;
import org.eclipse.kapua.service.KapuaNamedEntityService;
import org.eclipse.kapua.service.KapuaUpdatableEntityService;

/**
 * {@link Actions} implementation {@code enum}.<br> Available actions defined are:
 * <ul>
 * <li>{@link Actions#read}: Represent any type of fetching data from the system.</li>
 * <li>{@link Actions#write}: Represent any type of writing data into the system.</li>
 * <li>{@link Actions#delete}: Represent any type of deleting data from the system.</li>
 * <li>{@link Actions#connect}: Represent any type of connect to resources of the system.</li>
 * <li>{@link Actions#execute}: Represent any type of execution of task in the system.</li>
 * </ul>
 *
 * @since 1.0.0
 */
public enum Actions {
    /**
     * Represent any type of fetching data from the system. <br> Common usage is on:
     * <ul>
     * <li>{@link KapuaEntityService#find(KapuaId, KapuaId)}</li>
     * <li>{@link KapuaEntityService#query(KapuaQuery)}</li>
     * <li>{@link KapuaEntityService#count(KapuaQuery)}</li>
     * <li>{@link KapuaNamedEntityService#findByName(String)}</li>
     * </ul>
     *
     * @since 1.0.0
     */
    read,

    /**
     * Represent any type of writing data into the system. Common usage is on:
     * <ul>
     * <li>{@link KapuaEntityService#create(KapuaEntityCreator)}</li>
     * <li>{@link KapuaUpdatableEntityService#update(Object)}</li>
     * </ul>
     *
     * @since 1.0.0
     */
    write,

    /**
     * Represent any type of deleting data from the system. <br> Common usage is on:
     * <ul>
     * <li>{@link KapuaEntityService#delete(KapuaId, KapuaId)}</li>
     * </ul>
     *
     * @since 1.0.0
     */
    delete,

    /**
     * Represent any type of connect to resources of the system.<br> Examples are:
     * <ul>
     * <li>Connecting to the message broker.</li>
     * </ul>
     *
     * @since 1.0.0
     */
    connect,

    /**
     * Represent any type of execution of task in the system.
     *
     * @since 1.0.0
     */
    execute,
}
