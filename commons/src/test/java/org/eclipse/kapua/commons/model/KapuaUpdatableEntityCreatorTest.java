/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.model;

import java.util.Properties;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaUpdatableEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class KapuaUpdatableEntityCreatorTest {

    private class ActualKapuaUpdatableEntityCreator<E extends KapuaEntity> extends KapuaUpdatableEntityCreator<E> {

        public ActualKapuaUpdatableEntityCreator(KapuaId scopeId) {
            super(scopeId);
        }
    }

    @Test
    public void abstractKapuaUpdatableEntityCreatorScopeId() {
        KapuaId scopeId = new KapuaEid();
        KapuaUpdatableEntityCreator<AbstractKapuaEntity> kapuaUpdatableEntityCreator = new ActualKapuaUpdatableEntityCreator<>(scopeId);
        Assert.assertEquals("Actual and expected values are not the same!", scopeId, kapuaUpdatableEntityCreator.getScopeId());
    }

    @Test
    public void getEntityAttributesTest() {
        KapuaId scopeId = new KapuaEid();
        KapuaUpdatableEntityCreator<AbstractKapuaEntity> kapuaUpdatableEntityCreator = new ActualKapuaUpdatableEntityCreator<>(scopeId);
        Properties properties = new Properties();
        kapuaUpdatableEntityCreator.setEntityAttributes(properties);
        Assert.assertEquals("Actual and expected values are not the same!", properties, kapuaUpdatableEntityCreator.getEntityAttributes());
    }
}
