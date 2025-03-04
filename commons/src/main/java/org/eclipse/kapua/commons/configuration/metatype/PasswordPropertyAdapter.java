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
package org.eclipse.kapua.commons.configuration.metatype;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.kapua.commons.crypto.CryptoUtil;
import org.eclipse.kapua.model.config.metatype.Password;
import org.eclipse.kapua.model.xml.XmlPropertyAdapted;
import org.eclipse.kapua.model.xml.adapters.ClassBasedXmlPropertyAdapterBase;

import com.google.common.base.Strings;

public class PasswordPropertyAdapter extends ClassBasedXmlPropertyAdapterBase<Password> {

    private final CryptoUtil cryptoUtil;

    public PasswordPropertyAdapter(CryptoUtil cryptoUtil) {
        super(Password.class);
        this.cryptoUtil = cryptoUtil;
    }

    @Override
    public boolean doesEncrypt() {
        return true;
    }

    @Override
    public String marshallValue(Object value) {
        return cryptoUtil.encodeBase64(value.toString());
    }

    @Override
    public boolean canUnmarshallEmptyString() {
        return true;
    }

    @Override
    public Password unmarshallValue(String value) {
        return new Password(cryptoUtil.decodeBase64(value));
    }

    /**
     * Unmarshalls the given value according to {@link XmlPropertyAdapted#isEncrypted()}.
     *
     * @param value
     *         The value to unmarshall.
     * @param isEncrypted
     *         The {@link XmlPropertyAdapted#isEncrypted()}.
     * @return The unmarshalled {@link Password}
     * @since 2.1.0
     */
    public Password unmarshallValue(String value, boolean isEncrypted) {
        return isEncrypted ? unmarshallValue(value) : new Password(value);
    }

    @Override
    public Object unmarshallValues(XmlPropertyAdapted<?> property) {
        if (!property.getArray()) {
            String[] values = property.getValues();

            // Values might not have been defined
            // ie:
            //
            // <property name="propertyName" array="false" encrypted="false" type="Integer">
            // </property>
            if (values == null || values.length == 0) {
                return null;
            }

            String value = property.getValues()[0];
            return unmarshallValue(value, property.isEncrypted() && !Strings.isNullOrEmpty(value));
        } else {
            String[] values = property.getValues();

            // Values might not have been defined
            // ie:
            //
            // <property name="propertyName" array="true" encrypted="false" type="Integer">
            // </property>
            if (values == null) {
                return null;
            }

            return Arrays
                    .stream(property.getValues())
                    .map(value -> unmarshallValue(value, property.isEncrypted() && !Strings.isNullOrEmpty(value)))
                    .collect(Collectors.toList()).toArray(new Password[values.length]);
        }
    }
}
