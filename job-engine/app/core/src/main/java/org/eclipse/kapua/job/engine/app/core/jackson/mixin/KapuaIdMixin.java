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
package org.eclipse.kapua.job.engine.app.core.jackson.mixin;

import org.eclipse.kapua.job.engine.app.core.jackson.deserializer.KapuaIdDeserializer;
import org.eclipse.kapua.job.engine.app.core.jackson.serializer.KapuaIdSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = KapuaIdDeserializer.class)
@JsonSerialize(using = KapuaIdSerializer.class)
public interface KapuaIdMixin { }
