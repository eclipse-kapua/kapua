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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectSerializer {

    private static final Logger logger = LoggerFactory.getLogger(ObjectSerializer.class);

    public ObjectSerializer() {
    }

    public byte[] convertToBytes(Object obj) {
        if (obj instanceof byte[]) {
            return (byte[])obj;
        }
        else if (obj instanceof Serializable) {
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
                ois.writeObject(obj);
                return boas.toByteArray();
            } catch (Exception e) {
                logger.warn("Error while converting object {} to byte[]", obj, e);
                //TODO add metric?
            }
        }
        //return null instead of exception to allow message to go to DLQ correctly (hopefully additional info in the header will help to debug error reason
        return null;
    }

}