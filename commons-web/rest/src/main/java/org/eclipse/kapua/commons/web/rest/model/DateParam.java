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
package org.eclipse.kapua.commons.web.rest.model;

import java.util.Date;

import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.model.xml.DateXmlAdapter;

import jdk.internal.joptsimple.internal.Strings;

/**
 * Adapted for query parameters of type {@link Date}.
 *
 * @since 1.0.0
 */
public class DateParam extends DateXmlAdapter {

    private Date date;

    public DateParam(String stringDate) throws KapuaIllegalArgumentException {
        if (!Strings.isNullOrEmpty(stringDate)) {
            try {
                setDate(super.unmarshal(stringDate));
            } catch (IllegalArgumentException e) {
                throw new KapuaIllegalArgumentException("date", stringDate);
            }
        }
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    private void setDate(Date date) {
        this.date = new Date(date.getTime());
    }

}
