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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.translator;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.translator.exception.TranslatorNotFoundException;

import com.google.inject.Inject;

public class TranslatorHubImpl implements TranslatorHub {

    private final Set<Translator> availableTranslators;

    /**
     * Sometimes just translators-api is imported a dependency - with no implementation class. In such cases, there is not Translator implementation to inject. In order to be able to inject an empty
     * list of Translators, this trick must be used, as java does not support default parameters and guice does not support optional injection in the constructors. The static class uses optional
     * setter injection, providing a default value as fallback at the same time. https://github.com/google/guice/wiki/FrequentlyAskedQuestions#how-do-i-inject-a-method-interceptor
     */
    static class TranslatorsHolder {

        @Inject(optional = true)
        Set<Translator> value = new HashSet<>();
    }

    @Inject
    public TranslatorHubImpl(TranslatorsHolder availableTranslators) {
        this.availableTranslators = availableTranslators.value;
    }

    @Override
    public <FROM_MESSAGE extends Message,
            TO_MESSAGE extends Message,
            TRANSLATOR extends Translator<FROM_MESSAGE, TO_MESSAGE>>
    TRANSLATOR getTranslatorFor(
            Class<? extends FROM_MESSAGE> fromMessageClass, Class<? extends TO_MESSAGE> toMessageClass) {
        return this.availableTranslators
                .stream()
                .filter(t -> fromMessageClass != null)
                .filter(t -> toMessageClass != null)
                .filter(t -> fromMessageClass.isAssignableFrom(t.getClassFrom()))
                .filter(t -> toMessageClass.isAssignableFrom(t.getClassTo()))
                .map(t -> (TRANSLATOR) t)
                .findFirst()
                .orElseThrow(() -> new TranslatorNotFoundException(fromMessageClass, toMessageClass));
    }
}
