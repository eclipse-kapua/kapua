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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.translator;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.translator.kapua.kura.TranslatorAppAssetKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorAppBundleKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorAppCommandKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorAppConfigurationKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorAppPackageKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorAppRequestKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorAppSnapshotKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.TranslatorDataKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.inventory.TranslatorAppInventoryBundleExecKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.inventory.TranslatorAppInventoryContainerExecKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.inventory.TranslatorAppInventoryEmptyKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.keystore.TranslatorAppKeystoreCertificateKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.keystore.TranslatorAppKeystoreCsrKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.keystore.TranslatorAppKeystoreKeypairKapuaKura;
import org.eclipse.kapua.translator.kapua.kura.keystore.TranslatorAppKeystoreQueryKapuaKura;
import org.eclipse.kapua.translator.kura.kapua.TranslatorAppAssetKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorAppBundleKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorAppCommandKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorAppConfigurationKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorAppNotifyKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorAppPackageKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorAppResponseKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorAppSnapshotKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorDataKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorKuraKapuaUtils;
import org.eclipse.kapua.translator.kura.kapua.TranslatorKuraKapuaUtilsImpl;
import org.eclipse.kapua.translator.kura.kapua.TranslatorLifeAppsKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorLifeBirthKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorLifeDisconnectKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.TranslatorLifeMissingKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.event.TranslatorEventConfigurationKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.inventory.TranslatorAppInventoryBundlesKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.inventory.TranslatorAppInventoryContainersKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.inventory.TranslatorAppInventoryListKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.inventory.TranslatorAppInventoryNoContentKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.inventory.TranslatorAppInventoryPackagesKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.inventory.TranslatorAppInventorySystemPackagesKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.keystore.TranslatorAppKeystoreCsrKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.keystore.TranslatorAppKeystoreItemKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.keystore.TranslatorAppKeystoreItemsKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.keystore.TranslatorAppKeystoreNoContentKuraKapua;
import org.eclipse.kapua.translator.kura.kapua.keystore.TranslatorAppKeystoresKuraKapua;
import org.eclipse.kapua.translator.setting.TranslatorKapuaKuraSettings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class KapuaKuraTranslatorsModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        final Multibinder<Translator> translatorMultibinder = Multibinder.newSetBinder(binder(), Translator.class);
        //org.eclipse.kapua.translator.kapua.kura
        translatorMultibinder.addBinding().to(TranslatorAppAssetKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppBundleKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppCommandKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppConfigurationKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppPackageKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppRequestKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppSnapshotKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorDataKapuaKura.class);
        //org.eclipse.kapua.translator.kapua.kura.inventory
        translatorMultibinder.addBinding().to(TranslatorAppInventoryBundleExecKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppInventoryContainerExecKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppInventoryEmptyKapuaKura.class);
        //org.eclipse.kapua.translator.kapua.kura.keystore
        translatorMultibinder.addBinding().to(TranslatorAppKeystoreCertificateKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppKeystoreCsrKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppKeystoreKeypairKapuaKura.class);
        translatorMultibinder.addBinding().to(TranslatorAppKeystoreQueryKapuaKura.class);
        //org.eclipse.kapua.translator.kura.kapua
        translatorMultibinder.addBinding().to(TranslatorAppAssetKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppBundleKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppCommandKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppConfigurationKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppPackageKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppResponseKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppSnapshotKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorDataKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorLifeAppsKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorLifeBirthKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorLifeDisconnectKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorLifeMissingKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppNotifyKuraKapua.class);
        //org.eclipse.kapua.translator.kura.kapua.event
        translatorMultibinder.addBinding().to(TranslatorEventConfigurationKuraKapua.class);
        //org.eclipse.kapua.translator.kura.kapua.inventory
        translatorMultibinder.addBinding().to(TranslatorAppInventoryBundlesKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppInventoryContainersKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppInventoryListKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppInventoryNoContentKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppInventoryPackagesKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppInventorySystemPackagesKuraKapua.class);
        //org.eclipse.kapua.translator.kura.kapua.keystore
        translatorMultibinder.addBinding().to(TranslatorAppKeystoreItemKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppKeystoreItemsKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppKeystoreNoContentKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppKeystoreCsrKuraKapua.class);
        translatorMultibinder.addBinding().to(TranslatorAppKeystoresKuraKapua.class);

        bind(ObjectMapper.class).toInstance(new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL));

        bind(TranslatorKapuaKuraSettings.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public TranslatorKuraKapuaUtils translatorKuraKapuaUtils() {
        return new TranslatorKuraKapuaUtilsImpl(SystemSetting.getInstance().getMessageClassifier());
    }
}
