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
 *******************************************************************************/
package org.eclipse.kapua.commons.configuration;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.eclipse.kapua.commons.util.ResourceUtils;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.model.config.metatype.KapuaTmetadata;
import org.eclipse.kapua.storage.TxContext;

public class ResourceBasedServiceConfigurationMetadataProvider implements ServiceConfigurationMetadataProvider {

    public ResourceBasedServiceConfigurationMetadataProvider() {
    }

    @Override
    public Optional<KapuaTmetadata> fetchMetadata(TxContext txContext, String pid) {
        URL url = ResourceUtils.getResource(String.format("META-INF/metatypes/%s.xml", pid));

        if (url == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(XmlUtil.unmarshal(ResourceUtils.openAsReader(url, StandardCharsets.UTF_8), KapuaTmetadata.class))
                    .filter(v -> v.getOCD() != null && !v.getOCD().isEmpty());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
