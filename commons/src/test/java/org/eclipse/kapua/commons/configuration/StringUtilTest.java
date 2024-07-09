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
package org.eclipse.kapua.commons.configuration;

import org.assertj.core.api.Assertions;
import org.eclipse.kapua.commons.util.StringUtil;
import org.eclipse.kapua.model.config.metatype.Password;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(JUnitTests.class)
public class StringUtilTest {

    @Test
    public void shouldConvertPasswordsArrayToString() {
        // Given
        Password[] passwords = new Password[] { new Password("foo") };

        // When
        String passwordsString = StringUtil.valueToString(passwords);

        // Then
        Assertions.assertThat(passwordsString).isEqualTo("foo");
    }

}
