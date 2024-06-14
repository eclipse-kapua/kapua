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
package org.eclipse.kapua.commons.web.rest.filters;

import org.eclipse.kapua.commons.setting.AbstractKapuaSetting;

/**
 * API setting implementation.
 *
 * @since 1.0
 */
public class KapuaRestFiltersSetting extends AbstractKapuaSetting<KapuaRestFiltersSettingKeys> {

    private static final String API_SETTING_RESOURCE = "kapua-api-core-settings.properties";

    public static enum MessageType {

        ActiveMq('A'),
        CazzoNeSoIo('C'),
        TePossino('P');

        private char urlChar;

        MessageType(char urlChar) {
            this.urlChar = urlChar;
        }

        public char getUrlChar() {
            return urlChar;
        }
    }

    /**
     * Construct a new api setting reading settings from {@link KapuaRestFiltersSetting#API_SETTING_RESOURCE}
     */
    public KapuaRestFiltersSetting() {
        super(API_SETTING_RESOURCE);
    }

}
