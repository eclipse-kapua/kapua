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
package org.eclipse.kapua.app.api.core.resources;

import java.net.URI;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.id.KapuaId;

/**
 * @since 1.0.0
 */
public abstract class AbstractKapuaResource {

    /**
     * Checks if the given object is {@code null}.
     * <p>
     * If it is {@code null} a {@link NotFoundException} {@link WebApplicationException} is raised.
     * <p>
     * For {@link KapuaEntity} {@link #returnNotNullEntity(Object, String, KapuaId)} is recommended. This is meant for generic {@link Object}s.
     *
     * @param object
     *         The {@link Object} to check.
     * @param <T>
     *         The type of the given {@link Object}
     * @return The given {@link Object} if not {@code null}
     * @throws NotFoundException
     *         if the given {@link Object} is {@code null}
     * @since 1.0.0
     */
    public <T> T returnNotNullEntity(T object) {
        if (object == null) {
            throw new NotFoundException("Requested resource not found");
        }

        return object;
    }

    /**
     * Checks id the given {@link KapuaEntity} is {@code null}.
     *
     * @param entity
     *         The {@link KapuaEntity} to check.
     * @param entityType
     *         The {@link KapuaEntity#getType()}
     * @param entityId
     *         The {@link KapuaEntity#getId()}
     * @param <T>
     *         The {@link KapuaEntity} type.
     * @return The given entity if not {@code null}
     * @throws KapuaEntityNotFoundException
     *         if given {@link KapuaEntity} is {@code null}.
     * @since 2.0.0
     */
    public <T> T returnNotNullEntity(T entity, String entityType, KapuaId entityId) throws KapuaEntityNotFoundException {
        if (entity == null) {
            throw new KapuaEntityNotFoundException(entityType, entityId);
        }

        return entity;
    }

    /**
     * Builds a 200 HTTP Response.
     *
     * <pre>
     * return javax.ws.rs.core.Response.ok().build();
     * </pre>
     *
     * @return A built {@link Response#ok()}
     * @since 1.0.0
     */
    public Response returnOk() {
        return Response.ok().build();
    }

    /**
     * Builds a 200 HTTP Response a content.
     *
     * <pre>
     * return javax.ws.rs.core.Response.ok().entity(entity).build();
     * </pre>
     *
     * @param entity
     *         The entity to return.
     * @return A built {@link Response#ok()}
     * @since 1.0.0
     */
    public Response returnOk(Object entity) {
        return Response.ok().entity(entity).build();
    }

    /**
     * Builds a 201 HTTP Response a content.
     *
     * <pre>
     * return javax.ws.rs.core.Response.status(Status.CREATED).entity(entity).build();
     * </pre>
     *
     * @param entity
     *         The entity to return.
     * @return A built {@link Response#created(URI)}
     * @since 1.2.0
     */
    public Response returnCreated(Object entity) {
        return Response.status(Status.CREATED).entity(entity).build();
    }

    /**
     * Builds a 204 HTTP Response a content.
     *
     * <pre>
     * return javax.ws.rs.core.Response.noContent().build();
     * </pre>
     *
     * @return A built {@link Response#noContent()}
     * @since 1.2.0
     */
    public Response returnNoContent() {
        return Response.noContent().build();
    }

}
