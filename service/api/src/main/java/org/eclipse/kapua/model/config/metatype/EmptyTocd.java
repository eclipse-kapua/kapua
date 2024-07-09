/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.model.config.metatype;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public class EmptyTocd extends KapuaTocd {

    private String id;
    private String name;

    public EmptyTocd() {
    }

    public EmptyTocd(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void setOtherAttributes(Map<QName, String> otherAttributes) {
        // No OP implementation
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setIcon(List<KapuaTicon> icon) {
        // No OP implementation
    }

    @Override
    public void setDescription(String value) {
        // No OP implementation
    }

    @Override
    public void setAny(List<Object> any) {
        // No OP implementation
    }

    @Override
    public void setAD(List<KapuaTad> icon) {
        // No OP implementation
    }

    @Override
    public Map<QName, String> getOtherAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<KapuaTicon> getIcon() {
        return Collections.emptyList();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<Object> getAny() {
        return Collections.emptyList();
    }

    @Override
    public List<KapuaTad> getAD() {
        return Collections.emptyList();
    }
}
