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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.KapuaEntity;
import org.eclipse.kapua.model.KapuaNamedEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@Category(JUnitTests.class)
@RunWith(value = Parameterized.class)
public class KapuaNamedEntityCreatorTest {

    private final String name;

    private final String description;

    KapuaId scopeId = new KapuaEid();

    KapuaNamedEntityCreator namedEntityCreator = new ActualKapuaNamedEntityCreator(scopeId, null);

    public KapuaNamedEntityCreatorTest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Parameters
    public static Collection<Object[]> strings() {
        return Arrays.asList(new Object[][] {
                { "", "" },
                { "name", "description" },
                { "NAME", "DESCRIPTION" },
                { "&name%", "#description!" },
                { "1234", "5678" },
                { "make space", "make space" },
        });
    }

    private class ActualKapuaNamedEntityCreator<E extends KapuaEntity> extends KapuaNamedEntityCreator {

        protected ActualKapuaNamedEntityCreator(KapuaId scopeId, String name) {
            super(scopeId, name);
        }
    }

    @Test
    public void abstractKapuaNamedEntityCreatorNameTest() {
        namedEntityCreator.setName(name);
        KapuaNamedEntityCreator namedCopyEntityCreator = new ActualKapuaNamedEntityCreator(scopeId, name);
        Assert.assertEquals("Expected and actual values should be the same!", namedEntityCreator.getName(), namedCopyEntityCreator.getName());
    }

    @Test
    public void getDescriptionTest() {
        namedEntityCreator.setDescription(description);
        Assert.assertEquals("Expected and actual values should be the same!", description, namedEntityCreator.getDescription());
    }
}
