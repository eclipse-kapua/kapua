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

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.storeengine.client.configuration.ElasticsearchNode;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Category(JUnitTests.class)
public class InetAddressParserTest {

    /**
     * A hostname which does not exists
     * <p>
     * In case this hostname does exists on your network, which is highly unlikely, it can be overridden.
     */
    private static final String UNKNOWN_HOST = System.getProperty("kapua.service.commons.elasticsearch.client.test.unknownHost", "26-non-existing-host-05.");

    private static final Condition<InetSocketAddress> UNRESOLVED = new Condition<>(InetSocketAddress::isUnresolved, "Host unresolved");

    @Test
    public void testLocal() {
        InetSocketAddress result = InetAddressParser.parseAddresses(new ElasticsearchNode("127.0.0.1", 9300));
        assertThatResolvedAs(result, Inet4Address.class, "127.0.0.1", 9300);
    }

    @Test
    public void testLocalV6() {
        InetSocketAddress result = InetAddressParser.parseAddresses(new ElasticsearchNode("[::1]", 9300));
        assertThatResolvedAs(result, Inet6Address.class, "0:0:0:0:0:0:0:1", 9300);
    }

    @Test
    public void testHostUnknown() {
        InetSocketAddress result = InetAddressParser.parseAddresses(new ElasticsearchNode(UNKNOWN_HOST, 9300));
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).has(UNRESOLVED);
    }

    private void assertThatResolvedAs(InetSocketAddress result, Class<? extends InetAddress> addressClazz, String hostAddress, int port) {
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getAddress()).isNotNull();

        Assertions.assertThat(result).doesNotHave(UNRESOLVED);
        Assertions.assertThat(result.getHostString()).isEqualTo(hostAddress);
        Assertions.assertThat(result.getPort()).isEqualTo(port);
        Assertions.assertThat(result.getAddress()).isInstanceOf(addressClazz);
    }
}
