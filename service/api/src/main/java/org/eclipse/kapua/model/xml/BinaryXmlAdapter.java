/*******************************************************************************
 * Copyright (c) 2016, 2025 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.xml;

import org.eclipse.kapua.model.type.ByteArrayConverter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@link Byte}[] {@link XmlAdapter}
 *
 * @since 1.0.0
 */
public class BinaryXmlAdapter extends XmlAdapter<String, byte[]> {

    @Override
    public String marshal(byte[] binary) {
        return ByteArrayConverter.toString(binary);
    }

    @Override
    public byte[] unmarshal(String binary) {
        return ByteArrayConverter.fromString(binary);
    }
}
