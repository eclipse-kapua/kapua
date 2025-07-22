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

import org.eclipse.kapua.model.type.DateConverter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * {@link Date} {@link XmlAdapter}
 *
 * @since 1.0.0
 */
public class DateXmlAdapter extends XmlAdapter<String, Date> {

    @Override
    public String marshal(Date date) throws Exception {
        return DateConverter.toString(date);
    }

    @Override
    public Date unmarshal(String stringDate) {
        return DateConverter.fromString(stringDate);
    }
}
