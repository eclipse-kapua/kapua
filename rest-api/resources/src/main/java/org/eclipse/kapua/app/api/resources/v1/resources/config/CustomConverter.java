/*******************************************************************************
 * Copyright (c) 2024 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.api.resources.v1.resources.config;

import java.lang.annotation.Annotation;
import java.util.Iterator;

import org.eclipse.kapua.app.api.core.model.ScopeId;
import org.eclipse.kapua.model.id.KapuaId;
import com.fasterxml.jackson.databind.type.SimpleType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

public class CustomConverter implements ModelConverter {
    @Override
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (type.getType() instanceof SimpleType) {
            final SimpleType simpleType = (SimpleType) type.getType();
            if (simpleType.isTypeOrSubTypeOf(KapuaId.class)) {
                Schema schema = new StringSchema();
                if (simpleType.isTypeOrSubTypeOf(ScopeId.class)) {
                    schema = schema.example("_");
                } else {
                    for (Annotation annotation : type.getCtxAnnotations()) {
                        if (annotation instanceof io.swagger.v3.oas.annotations.media.Schema) {
                            schema = schema.example(((io.swagger.v3.oas.annotations.media.Schema) annotation).example());
                            break;
                        }
                    }
                }
                return schema;
            }
        }
        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain);
        } else {
            return null;
        }
    }
}
