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
package org.eclipse.kapua.commons.util.xml;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.kapua.commons.core.JaxbClassProvider;
import org.eclipse.kapua.commons.util.log.ConfigurationPrinter;
import org.eclipse.kapua.locator.LocatorConfig;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlRootAnnotatedJaxbClassesScanner implements JaxbClassProvider {

    private final ConfigurationPrinter configurationPrinter;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Collection<String> includedPackageNames;
    private Collection<String> excludedPackageNames;

    public XmlRootAnnotatedJaxbClassesScanner(LocatorConfig locatorConfig) {
        this.configurationPrinter =
                ConfigurationPrinter
                        .create()
                        .withLogger(logger)
                        .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                        .withTitle("JAXB classes discoverer");
        this.includedPackageNames = locatorConfig.getIncludedPackageNames();
        this.excludedPackageNames = locatorConfig.getExcludedPackageNames();
    }

    @Override
    public Collection<Class<?>> getClasses() {
        final Reflections reflections = new Reflections(includedPackageNames);
        // Scan XmlSerializable
        final Map<Boolean, Set<Class<?>>> serializablesByValidity =
                reflections.getTypesAnnotatedWith(XmlRootElement.class)
                        .stream()
                        .filter(c -> c != null)
                        .collect(Collectors.partitioningBy(c -> {
                                    final boolean inExcludedPackage = excludedPackageNames.stream().anyMatch(pn -> c.getName().startsWith(pn));
                                    final boolean lackingAnnotation = !c.isAnnotationPresent(XmlRootElement.class);
                                    return !(inExcludedPackage || lackingAnnotation);
                                },
                                Collectors.toSet()));

        final Set<Class<?>> excludedXmlSerializables = serializablesByValidity.get(false);
        final Set<Class<?>> validXmlSerializables = serializablesByValidity.get(true);
        configurationPrinter.withTitle("Kapua XmlSerializable Configuration");
        //        if (configurationPrinter.getParentLogger().isDebugEnabled()) {
        // Printing like this is highly verbose
        configurationPrinter.logSections("Discovered XmlSerializable Classes", validXmlSerializables.stream().map(Class::getName).collect(Collectors.toList()));
        configurationPrinter.logSections("Discovered but Excluded XmlSerializable Classes", excludedXmlSerializables.stream().map(Class::getName).collect(Collectors.toList()));
        //        } else {
        //            configurationPrinter.addParameter("Discovered XmlSerializable Classes", validXmlSerializables.size());
        //            configurationPrinter.addParameter("Discovered but Excluded XmlSerializable Classes", excludedXmlSerializables.size());
        //        }
        // Print it!
        configurationPrinter.printLog();

        return validXmlSerializables;
    }
}