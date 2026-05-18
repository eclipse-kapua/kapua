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
package org.eclipse.kapua.client.gateway.mqtt.paho.internal;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Category(JUnitTests.class)
public class ListenersTest {
    private Runnable success;
    private Consumer<Throwable> failure;
    private IMqttToken asyncActionToken;
    private Throwable exception;
    private CompletableFuture<Object> future;

    @Before
    public void init() {
        success = Mockito.mock(Runnable.class);
        failure = Mockito.mock(Consumer.class);
        asyncActionToken = Mockito.mock(IMqttToken.class);
        exception = Mockito.mock(Throwable.class);
        future = Mockito.mock(CompletableFuture.class);
    }

    @Test
    public void listenersTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Listeners> constructor = Listeners.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void toListenerTest(){
        Listeners.toListener(future).onSuccess(Mockito.mock(IMqttToken.class));
        Listeners.toListener(future).onFailure(Mockito.mock(IMqttToken.class), new Throwable());
    }

    @Test
    public void toListenerRunnableIdAndConsumerIdTest() {
        Assert.assertThat("Instance of IMqttActionListener expected", Listeners.toListener(success, failure), IsInstanceOf.instanceOf(IMqttActionListener.class));
    }

    @Test
    public void toListenerRunnableIdNullAndConsumerIdTest() {
        Assert.assertThat("Instance of IMqttActionListener expected", Listeners.toListener(null, failure), IsInstanceOf.instanceOf(IMqttActionListener.class));
    }

    @Test
    public void toListenerRunnableIdAndConsumerIdNullTest() {
        Assert.assertThat("Instance of IMqttActionListener expected", Listeners.toListener(success, null), IsInstanceOf.instanceOf(IMqttActionListener.class));
    }

    @Test
    public void onSuccessTest() {
        Listeners.toListener(success, failure).onSuccess(asyncActionToken);
        Mockito.verify(success).run();
    }

    @Test
    public void onSuccessSuccessNullTest() {
        Listeners.toListener(null, failure).onSuccess(asyncActionToken);
    }

    @Test
    public void onSuccessFailureNullTest() {
        Listeners.toListener(success, null).onSuccess(asyncActionToken);
        Mockito.verify(success).run();
    }

    @Test
    public void onSuccessSuccessNullAndFailureNullTest() {
        Listeners.toListener(null, null).onSuccess(asyncActionToken);
    }

    @Test
    public void onSuccessIMqttTokenNullTest() {
        Listeners.toListener(success, failure).onSuccess(null);
        Mockito.verify(success).run();
    }

    @Test
    public void onFailureTest() {
        Listeners.toListener(success, failure).onFailure(asyncActionToken, exception);
        Mockito.verify(failure).accept(exception);
    }

    @Test
    public void onFailureSuccessNullTest() {
        Listeners.toListener(null, failure).onFailure(asyncActionToken, exception);
        Mockito.verify(failure).accept(exception);
    }

    @Test
    public void onFailureFailureNullTest() {
        Listeners.toListener(success, null).onFailure(asyncActionToken, exception);
    }

    @Test
    public void onFailureSuccessNullAndFailureNullTest() {
        Listeners.toListener(null, null).onFailure(asyncActionToken, exception);
    }

    @Test
    public void onFailureIMqttTokenNullTest() {
        Listeners.toListener(success, failure).onFailure(null, exception);
        Mockito.verify(failure).accept(exception);
    }

    @Test
    public void onFailureThrowableNullTest() {
        Listeners.toListener(success, failure).onFailure(asyncActionToken, null);
        Mockito.verify(failure).accept(null);
    }

    @Test
    public void toListenerCompletableFutureIdTest() {
        Assert.assertThat("Instance of IMqttActionListener expected", Listeners.toListener(future), IsInstanceOf.instanceOf(IMqttActionListener.class));
    }

    @Test(expected = NullPointerException.class)
    public void toListenerCompletableFutureIdNullTest() {
        Assert.assertThat("Instance of IMqttActionListener expected", Listeners.toListener(null), IsInstanceOf.instanceOf(IMqttActionListener.class));
    }
}