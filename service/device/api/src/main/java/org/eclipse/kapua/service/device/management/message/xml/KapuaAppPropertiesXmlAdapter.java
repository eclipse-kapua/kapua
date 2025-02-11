/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.device.management.message.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.kapua.service.device.management.message.KapuaAppProperties;
import org.eclipse.kapua.service.device.management.message.KapuaAppPropertiesImpl;

/**
 * {@link KapuaAppProperties} {@link XmlAdapter}.
 *
 * @since 1.0.0
 */
public class KapuaAppPropertiesXmlAdapter extends XmlAdapter<String, KapuaAppProperties> {

    @Override
    public String marshal(KapuaAppProperties kapuaAppProperties) throws Exception {
        return kapuaAppProperties.getValue();
    }

    @Override
    public KapuaAppProperties unmarshal(String string) throws Exception {
        return new KapuaAppPropertiesImpl(string);
    }
}
