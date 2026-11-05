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

import com.google.inject.Inject;
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.translator.exception.TranslatorNotFoundException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TranslatorHubImpl implements TranslatorHub {
    private final Set<Translator> availableTranslators;

    /**
     * Resolved {@link Translator}s, to avoid scanning {@link #availableTranslators} on each lookup.
     * The available {@link Translator}s never change, so the resolution for a given pair of {@link Message} classes never changes either.
     */
    private final Map<TranslatorKey, Translator> resolvedTranslators = new ConcurrentHashMap<>();

    /**
     * Sometimes just translators-api is imported a dependency - with no implementation class. In such cases, there is not Translator implementation to inject.
     * In order to be able to inject an empty list of Translators, this trick must be used, as java does not support default parameters and guice does not support optional injection in the constructors.
     * The static class uses optional setter injection, providing a default value as fallback at the same time.
     * https://github.com/google/guice/wiki/FrequentlyAskedQuestions#how-do-i-inject-a-method-interceptor
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
    public <FROM_MESSAGE extends Message, TO_MESSAGE extends Message, TRANSLATOR extends Translator<FROM_MESSAGE, TO_MESSAGE>> TRANSLATOR getTranslatorFor(Class<? extends FROM_MESSAGE> fromMessageClass, Class<? extends TO_MESSAGE> toMessageClass) {
        if (fromMessageClass == null || toMessageClass == null) {
            throw new TranslatorNotFoundException(fromMessageClass, toMessageClass);
        }

        TranslatorKey translatorKey = new TranslatorKey(fromMessageClass, toMessageClass);
        Translator translator = resolvedTranslators.get(translatorKey);
        if (translator == null) {
            translator = resolvedTranslators.computeIfAbsent(translatorKey, k -> findTranslatorFor(fromMessageClass, toMessageClass));
        }
        return (TRANSLATOR) translator;
    }

    private Translator findTranslatorFor(Class<?> fromMessageClass, Class<?> toMessageClass) {
        return this.availableTranslators
                .stream()
                .filter(t -> fromMessageClass.isAssignableFrom(t.getClassFrom()))
                .filter(t -> toMessageClass.isAssignableFrom(t.getClassTo()))
                .findFirst()
                .orElseThrow(() -> new TranslatorNotFoundException(fromMessageClass, toMessageClass));
    }

    private static final class TranslatorKey {
        private final Class<?> fromMessageClass;
        private final Class<?> toMessageClass;

        private TranslatorKey(Class<?> fromMessageClass, Class<?> toMessageClass) {
            this.fromMessageClass = fromMessageClass;
            this.toMessageClass = toMessageClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TranslatorKey)) {
                return false;
            }
            TranslatorKey that = (TranslatorKey) o;
            return fromMessageClass == that.fromMessageClass && toMessageClass == that.toMessageClass;
        }

        @Override
        public int hashCode() {
            return 31 * fromMessageClass.hashCode() + toMessageClass.hashCode();
        }
    }
}
