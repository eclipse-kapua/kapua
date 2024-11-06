/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.camel.listener.error;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.eclipse.kapua.service.camel.message.CamelKapuaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectDeserializer {

    private static final Logger logger = LoggerFactory.getLogger(ObjectDeserializer.class);

    public CamelKapuaMessage<?> convertToCamelKapuaMessage(byte[] message) {
        Object convertedObj = toObject(message);
        if (convertedObj instanceof CamelKapuaMessage<?>) {
            return (CamelKapuaMessage<?>)convertedObj;
        }
        else {
            //return null to allow DLQ message processing to end the flow (anyway the message is not processable)
            logger.warn("Cannot convert byte[] message to CamelKapuaMessage. Bad class: {}", convertedObj);
            return null;
        }
    }

    protected Object toObject(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        } catch (Exception e) {
            logger.warn("Cannot convert byte[] message to Object. Error: {}", e.getMessage(), e);
            //the caller perform an instance of that returns false if the instance is null
            return null;
        }
    }

}