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
package org.eclipse.kapua.app.console.module.api.setting;

import org.eclipse.kapua.commons.setting.SettingKey;

public enum ConsoleSettingKeys implements SettingKey {

    SKIN_RESOURCE_DIR("console.skin.resource.dir"), //

    LOGIN_BACKGROUND_CREDITS("console.login.background.credits"), //
    LOGIN_GENERIC_SNIPPET("console.login.generic.snippet"), //

    PRODUCT_NAME("console.product.name"), //
    PRODUCT_COPYRIGHT("console.product.copyright"),

    SECURE_COOKIE("console.cookie.secure"),

    DEVICE_CONFIGURATION_ICON_FOLDER("console.device.configuration.icon.folder"), //
    DEVICE_CONFIGURATION_ICON_CACHE_TIME("console.device.configuration.icon.cache.time"), //
    DEVICE_CONFIGURATION_ICON_SIZE_MAX("console.device.configuration.icon.size.max"),

    DEVICE_CONFIGURATION_SERVICE_IGNORE("console.device.configuration.service.ignore"),//

    DEVICE_MAP_ENABLED("console.device.map.enabled"), //
    DEVICE_MAP_TILE_URI("console.device.map.tile.uri"), //

    JOB_STEP_DEFINITION_EXCLUDE_REGEX("console.job.step.definition.exclude.regex"),

    FILE_UPLOAD_SIZE_MAX("console.file.upload.size.max"), //
    FILE_UPLOAD_INMEMORY_SIZE_THRESHOLD("console.file.upload.inmemory.size.threshold"), //

    SSO_OPENID_REDIRECT_URI("console.sso.openid.redirect.uri"), //
    SSO_OPENID_CONSOLE_HOME_URI("console.sso.openid.home.uri"), //
    SSO_OPENID_USER_LOGOUT_ENABLED("console.sso.openid.user.logout.enabled"), //
    SSO_OPENID_SESSION_LISTENER_LOGOUT_ENABLED("console.sso.openid.session_listener.logout.enabled"),  //

    EXPORT_MAX_PAGES("console.export.max.pages"),
    EXPORT_MAX_PAGE_SIZE("console.export.max.pagesize");

    private String key;

    private ConsoleSettingKeys(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }
}
