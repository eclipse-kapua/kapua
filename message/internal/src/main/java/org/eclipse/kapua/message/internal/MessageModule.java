/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.message.internal;

import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.core.JaxbClassProvider;
import org.eclipse.kapua.commons.core.SimpleJaxbClassProvider;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataPayload;

import com.google.inject.multibindings.Multibinder;

public class MessageModule extends AbstractKapuaModule {

    @Override
    protected void configureModule() {
        final Multibinder<JaxbClassProvider> jaxbClassProviderMultibinder = Multibinder.newSetBinder(binder(), JaxbClassProvider.class);
        jaxbClassProviderMultibinder.addBinding()
                .toInstance(new SimpleJaxbClassProvider(
                                KapuaDataChannel.class,
                                KapuaDataPayload.class
                        )
                );
    }
}
