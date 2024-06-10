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
package org.eclipse.kapua.commons.rest.jersey;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

@Category(JUnitTests.class)
public class ListBodyWriterTest {

    ListBodyWriter listBodyWriter;
    Type genericType;
    Annotation[] annotations;
    MediaType mediaType;
    List list;
    MultivaluedMap<String, Object> httpHeaders;
    JAXBContext jaxbContext;
    Marshaller marshaller;
    OutputStream outputStream;

    @Before
    public void initialize() {
        listBodyWriter = new ListBodyWriter();
        genericType = Mockito.mock(Type.class);
        annotations = new Annotation[] { Mockito.mock(Annotation.class), Mockito.mock(Annotation.class) };
        mediaType = MediaType.valueOf("some/text");
        list = new LinkedList();
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
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(String.class, genericType, annotations, mediaType));
    }

    @Test
    public void isWriteableNullTypeTest() {
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(null, genericType, annotations, mediaType));
    }

    @Test
    public void isWriteableNullGenericTypeTest() {
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(String.class, null, annotations, mediaType));
    }

    @Test
    public void isWriteableNullAnnotationsTest() {
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(String.class, genericType, null, mediaType));
    }

    @Test
    public void isWriteableNullMediaTypeTest() {
        Assert.assertTrue("True expected.", listBodyWriter.isWriteable(String.class, genericType, annotations, null));
    }

    @Test
    public void getSizeTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, String.class, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullListTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(null, String.class, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullTypeTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, null, genericType, annotations, mediaType));
    }

    @Test
    public void getSizeNullGenericTypeTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, String.class, null, annotations, mediaType));
    }

    @Test
    public void getSizeNullAnnotationsTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, String.class, genericType, null, mediaType));
    }

    @Test
    public void getSizeNullMediaTypeTest() {
        Assert.assertEquals("Expected and actual values should be the same.", 0, listBodyWriter.getSize(list, String.class, genericType, annotations, null));
    }

    @Test(expected = WebApplicationException.class)
    public void writeToNullProvidersTest() throws IOException {
        listBodyWriter.providers = null;

        listBodyWriter.writeTo(list, String.class, Mockito.mock(Type.class), annotations, mediaType, httpHeaders, outputStream);
    }

    @Test(expected = WebApplicationException.class)
    public void writeToNullJaxbContextTest() throws IOException {
        listBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(null);

        listBodyWriter.writeTo(list, String.class, Mockito.mock(Type.class), annotations, mediaType, httpHeaders, outputStream);
    }

    @Test
    public void writeToTest() throws Exception {
        listBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(jaxbContext);
        Mockito.when(jaxbContext.createMarshaller()).thenReturn(marshaller);

        try {
            listBodyWriter.writeTo(list, String.class, Mockito.mock(Type.class), annotations, mediaType, httpHeaders, outputStream);
        } catch (Exception e) {
            Assert.fail("Exception not expected.");
        }
    }

    @Test(expected = WebApplicationException.class)
    public void writeToInternalServerErrorTest() throws Exception {
        listBodyWriter.providers = Mockito.mock(Providers.class);

        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE)).thenReturn(Mockito.mock(ContextResolver.class));
        Mockito.when(listBodyWriter.providers.getContextResolver(JAXBContext.class, MediaType.APPLICATION_XML_TYPE).getContext(JAXBContext.class)).thenReturn(jaxbContext);
        Mockito.when(jaxbContext.createMarshaller()).thenThrow(new JAXBException("message"));

        listBodyWriter.writeTo(list, String.class, Mockito.mock(Type.class), annotations, mediaType, httpHeaders, outputStream);
    }
}