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
package org.eclipse.kapua.job.engine.app.core.deserializer;

import java.io.IOException;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class KapuaIdDeserializer extends JsonDeserializer<KapuaId> {

    private final KapuaIdFactory kapuaIdFactory = KapuaLocator.getInstance().getFactory(KapuaIdFactory.class);

    @Override
    public KapuaId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return kapuaIdFactory.newKapuaId(p.getValueAsString());
    }

}
