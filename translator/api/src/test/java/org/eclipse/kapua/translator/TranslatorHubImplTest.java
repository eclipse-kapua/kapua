/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.translator;

import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.translator.exception.TranslatorNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.LinkedHashSet;

@Category(JUnitTests.class)
public class TranslatorHubImplTest {

    private interface FromMessage extends Message {
    }

    private interface SpecificFromMessage extends FromMessage {
    }

    private interface ToMessage extends Message {
    }

    private interface OtherMessage extends Message {
    }

    private static final class TestTranslator<F extends Message, T extends Message> extends Translator<F, T> {

        private final Class<F> classFrom;
        private final Class<T> classTo;

        private TestTranslator(Class<F> classFrom, Class<T> classTo) {
            this.classFrom = classFrom;
            this.classTo = classTo;
        }

        @Override
        public T translate(F message) {
            return null;
        }

        @Override
        public Class<F> getClassFrom() {
            return classFrom;
        }

        @Override
        public Class<T> getClassTo() {
            return classTo;
        }
    }

    private final Translator<SpecificFromMessage, ToMessage> specificToTranslator = new TestTranslator<>(SpecificFromMessage.class, ToMessage.class);
    private final Translator<ToMessage, OtherMessage> toOtherTranslator = new TestTranslator<>(ToMessage.class, OtherMessage.class);
    private final Translator<SpecificFromMessage, ToMessage> anotherSpecificToTranslator = new TestTranslator<>(SpecificFromMessage.class, ToMessage.class);

    private final TranslatorHub translatorHub = newTranslatorHub(specificToTranslator, toOtherTranslator, anotherSpecificToTranslator);

    @Test
    public void getTranslatorForTest() {
        Assert.assertSame(specificToTranslator, translatorHub.getTranslatorFor(SpecificFromMessage.class, ToMessage.class));
        Assert.assertSame(toOtherTranslator, translatorHub.getTranslatorFor(ToMessage.class, OtherMessage.class));
    }

    @Test
    public void getTranslatorForAssignableTest() {
        // Same resolution as before the cache was introduced: Translator.getClassFrom() assignable to the requested class
        Assert.assertSame(specificToTranslator, translatorHub.getTranslatorFor(FromMessage.class, ToMessage.class));
        Assert.assertSame(specificToTranslator, translatorHub.getTranslatorFor(Message.class, ToMessage.class));
    }

    @Test
    public void getTranslatorForRepeatedTest() {
        for (int i = 0; i < 3; i++) {
            Assert.assertSame(specificToTranslator, translatorHub.getTranslatorFor(SpecificFromMessage.class, ToMessage.class));
            Assert.assertSame(toOtherTranslator, translatorHub.getTranslatorFor(ToMessage.class, OtherMessage.class));
        }
    }

    @Test
    public void getTranslatorForNotFoundTest() {
        for (int i = 0; i < 2; i++) {
            try {
                translatorHub.getTranslatorFor(OtherMessage.class, ToMessage.class);
                Assert.fail("TranslatorNotFoundException expected");
            } catch (TranslatorNotFoundException e) {
                Assert.assertEquals(OtherMessage.class, e.getFromMessageClass());
                Assert.assertEquals(ToMessage.class, e.getToMessageClass());
            }
        }
    }

    @Test(expected = TranslatorNotFoundException.class)
    public void getTranslatorForNullFromTest() {
        translatorHub.getTranslatorFor(null, ToMessage.class);
    }

    @Test(expected = TranslatorNotFoundException.class)
    public void getTranslatorForNullToTest() {
        translatorHub.getTranslatorFor(SpecificFromMessage.class, null);
    }

    @Test(expected = TranslatorNotFoundException.class)
    public void getTranslatorForNoTranslatorsTest() {
        new TranslatorHubImpl(new TranslatorHubImpl.TranslatorsHolder()).getTranslatorFor(SpecificFromMessage.class, ToMessage.class);
    }

    //
    // Private methods
    //

    private static TranslatorHub newTranslatorHub(Translator<?, ?>... translators) {
        TranslatorHubImpl.TranslatorsHolder translatorsHolder = new TranslatorHubImpl.TranslatorsHolder();
        translatorsHolder.value = new LinkedHashSet<Translator>(Arrays.asList(translators));
        return new TranslatorHubImpl(translatorsHolder);
    }
}
