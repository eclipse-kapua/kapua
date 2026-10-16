/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.storeengine.client.utils;

import com.google.common.collect.Lists;
import org.eclipse.kapua.service.storeengine.client.configuration.StoreEngineNode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class InetAddressParser {

    private InetAddressParser() {
    }

    public static List<InetSocketAddress> parseAddresses(List<StoreEngineNode> storeEngineNodes) {
        return storeEngineNodes
                .stream()
                .map(n -> new InetSocketAddress(n.getAddress(), n.getPort()))
                .collect(Collectors.toList());
    }

    public static InetSocketAddress parseAddresses(StoreEngineNode storeEngineNode) {
        return parseAddresses(Lists.newArrayList(storeEngineNode)).get(0);
    }
}
