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
package org.eclipse.kapua.commons.jersey.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.kapua.KapuaSerializable;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class KapuaSerializableBodyWriterTest {

    KapuaSerializableBodyWriter kapuaSerializableBodyWriter;
    Annotation[] annotations;
    MediaType mediaType;
    KapuaSerializable kapuaSerializable;
    Type genericType;
    MultivaluedMap<String, Object> httpHeaders;
    JAXBContext jaxbContext;
    Marshaller marshaller;
    OutputStream outputStream;

    @Before
    public void initialize() {
        kapuaSerializableBodyWriter = new KapuaSerializableBodyWriter();
        annotations = new Annotation[] { Mockito.mock(Annotation.class), Mockito.mock(Annotation.class) };
        mediaType = MediaType.valueOf("some/text");
        kapuaSerializable = Mockito.mock(KapuaSerializable.class);
        genericType = Mockito.mock(Type.class);
        httpHeaders = Mockito.mock(MultivaluedMap.class);
        jaxbContext = Mockito.mock(JAXBContext.class);
        marshaller = Mockito.mock(Marshaller.class);
        outputStream = new OutputStream() {

            @Override
            public void write(int b) {
            }
        };
    }

    @Test
    public void isWriteableTest() {
        Assert.assertTrue("True expected.", kapuaSerializableBodyWriter.isWriteable(String.class, genericType, annotations, mediaType));
    }

    @Test
    public void isWriteableNullTypeTest() {
        Assert.assertTrue("True expected.", kapuaSerializableBodyWriter.isWriteable(null, genericType, annotations, mediaType));
    }

    @Test
    public void isWriteableNullGenericTypeTest() {
        Assert.assertTrue("True expected.", kapuaSerializableBodyWriter.isWriteable(String.class, null, annotations, mediaType));
    }

    @Test
    public void isWriteableNullAnnotationsTest() {
        Assert.assertTrue("True expected.", kapuaSerializableBodyWriter.isWriteable(String.class, genericType, null, mediaType));
    }

    @Test
    public void isWriteableNullMediaTypeTest() {
        Assert.assertTrue("True expected.", kapuaSerializableBodyWriter.isWriteable(String.class, genericType, annotations, null));
    }

    @Test
    public void getSizeTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, kapuaSerializableBodyWriter.getSize(kapuaSerializable, String.class, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullKapuaSerializableTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, kapuaSerializableBodyWriter.getSize(null, String.class, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullTypeTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, kapuaSerializableBodyWriter.getSize(kapuaSerializable, null, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullGenericTypeTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, kapuaSerializableBodyWriter.getSize(kapuaSerializable, String.class, null, annotations, mediaType));
    }

    @Test
    public void getSizeNullAnnotationsTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, kapuaSerializableBodyWriter.getSize(kapuaSerializable, String.class, genericType, null, mediaType));
    }

    @Test
    public void getSizeNullMediaTypeTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, kapuaSerializableBodyWriter.getSize(kapuaSerializable, String.class, genericType, annotations, null));
    }

    @Test(expected = WebApplicationException.class)
    public void writeToNullProvidersTest() throws IOException {
        kapuaSerializableBodyWriter.providers = null;

        kapuaSerializableBodyWriter.writeTo(kapuaSerializable, String.class, genericType, annotations, mediaType, httpHeaders, outputStream);
    }

    @Test(expected = WebApplicationException.class)
    public void writeToNullJaxbContextTest() throws IOException {
        kapuaSerializableBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(kapuaSerializableBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(kapuaSerializableBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(null);

        kapuaSerializableBodyWriter.writeTo(kapuaSerializable, String.class, genericType, annotations, mediaType, httpHeaders, outputStream);
    }

    @Test
    public void writeToTest() throws Exception {
        kapuaSerializableBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(kapuaSerializableBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(kapuaSerializableBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(jaxbContext);
        Mockito.when(jaxbContext.createMarshaller()).thenReturn(marshaller);

        try {
            kapuaSerializableBodyWriter.writeTo(kapuaSerializable, String.class, genericType, annotations, mediaType, httpHeaders, outputStream);
        } catch (Exception e) {
            Assert.fail("Exception not expected.");
        }
    }

    @Test(expected = WebApplicationException.class)
    public void writeToInternalServerErrorTest() throws Exception {
        kapuaSerializableBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(kapuaSerializableBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(kapuaSerializableBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(jaxbContext);
        Mockito.when(jaxbContext.createMarshaller()).thenThrow(new JAXBException("message"));

        kapuaSerializableBodyWriter.writeTo(kapuaSerializable, String.class, genericType, annotations, mediaType, httpHeaders, outputStream);
    }
}