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
package org.eclipse.kapua.model.id;

import java.math.BigInteger;
import java.util.Base64;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.model.KapuaEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * {@link KapuaEntity} identifier definition.
 * <p>
 * This object it's used to identify each entity.
 * <b>It should be unique within the same entity.</b>
 *
 * @since 1.0.0
 */
@Schema(example = "Ag", type = "string")
public interface KapuaId extends KapuaSerializable {


    /**
     * Identifies any {@link KapuaId}.
     *
     * @since 1.0.0
     */
    // FIXME: This is maybe worth to be moved to a ScopeId class
    @XmlTransient
    KapuaId ANY = new KapuaIdImpl(BigInteger.ONE.negate());

    /**
     * Identifies the {@link KapuaId} represented by 1.
     *
     * @since 1.0.0
     */
    // FIXME: This is maybe worth to be moved to a ScopeId class
    @XmlTransient
    KapuaId ONE = new KapuaIdImpl(BigInteger.ONE);

    /**
     * Gets the numeric representation of the KapuaId.
     *
     * @return The numeric representation of the KapuaId.
     * @since 1.0.0
     */
    BigInteger getId();

    /**
     * Produces the {@link Base64} representation of the {@link KapuaId}.
     *
     * @return The {@link Base64} representation of the {@link KapuaId}.
     * @since 1.0.0
     */
    default String toCompactId() {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(getId().toByteArray());
    }

    /**
     * Produces the {@link String} representation of the {@link KapuaId}.
     *
     * @return The {@link String} representation of the {@link KapuaId}.
     * @since 1.0.0
     */
    default String toStringId() {
        return KapuaId.toString(this);
    }

    /**
     * Returns the {@link String} representation of the given {@link KapuaId}.
     * <p>
     * It is meant to be used by all {@link KapuaId} implementation to {@code override} the {@link Object#toString()} method.
     *
     * @param kapuaId The {@link KapuaId} to represent in {@link String} form.
     * @return The {@link String} representation of the given {@link KapuaId}.
     * @since 2.0.0
     */
    static String toString(@NotNull KapuaId kapuaId) {
        return kapuaId.getId().toString();
    }

    /**
     * Checks whether two {@link KapuaId}s are equals comparing their {@link KapuaId#getId()}.
     * <p>
     * It is meant to be used by all {@link KapuaId} implementation to {@code override} the {@link Object#equals(Object)} method.
     *
     * @param aKapuaId    A {@link KapuaId}.
     * @param otherObject Another {@link Object} to compare.
     * @return {@code true} if they represent the same {@link KapuaId}, {@code false} otherwise.
     * @since 2.0.0
     */
    static boolean areEquals(KapuaId aKapuaId, Object otherObject) {
        if (aKapuaId == otherObject) {
            return true;
        }

        if (aKapuaId == null || otherObject == null) {
            return false;
        }

        if (!(otherObject instanceof KapuaId)) {
            return false;
        }

        KapuaId otherKapuaId = (KapuaId) otherObject;
        if (aKapuaId.getId() == null) {
            if (otherKapuaId.getId() != null) {
                return false;
            }
        } else if (!aKapuaId.getId().equals(otherKapuaId.getId())) {
            return false;
        }
        return true;
    }

    /**
     * Generates a hash code of the given {@link KapuaId}.
     * <p>
     * It is meant to be used by all {@link KapuaId} implementation to {@code override} the {@link Object#hashCode()} method.
     *
     * @param kapuaId The {@link KapuaId} to hash.
     * @return The hash code of the given {@link KapuaId}.
     * @since 2.0.0
     */
    static int hashCode(@NotNull KapuaId kapuaId) {
        final int prime = 31;
        int result = 1;
        result = prime * result + (kapuaId.getId() == null ? 0 : kapuaId.getId().hashCode());
        return result;
    }

}
