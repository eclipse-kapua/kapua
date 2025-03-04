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
package org.eclipse.kapua.model.config.metatype;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class KapuaTscalarAdapter extends XmlAdapter<String, KapuaTscalar> {

    @Override
    public String marshal(KapuaTscalar v) throws Exception {
        return v.value();
    }

    @Override
    public KapuaTscalar unmarshal(String v) throws Exception {
        return KapuaTscalar.fromValue(v);
    }
}
