/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.app.core.jackson.serializer;

import java.io.IOException;

import org.eclipse.kapua.model.id.KapuaId;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class KapuaIdSerializer extends JsonSerializer<KapuaId> {

    @Override
    public void serialize(KapuaId value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.toCompactId());
    }

}
