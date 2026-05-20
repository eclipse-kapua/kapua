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
 *     Red Hat Inc
 *******************************************************************************/
package org.eclipse.kapua.locator.guice;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.commons.core.AbstractKapuaModule;
import org.eclipse.kapua.commons.util.log.ConfigurationPrinter;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaLocatorErrorCodes;
import org.eclipse.kapua.locator.LocatorConfig;
import org.eclipse.kapua.locator.initializers.KapuaInitializingMethod;
import org.eclipse.kapua.model.KapuaObjectFactory;
import org.eclipse.kapua.service.KapuaService;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.google.inject.util.Modules;

/**
 * {@link Guice} {@link KapuaLocator} implementation.
 *
 * @since 1.0.0
 */
public class GuiceLocatorImpl extends KapuaLocator implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(GuiceLocatorImpl.class);

    // Default service resource file from which the managed services are read
    private static final String SERVICE_RESOURCE = "locator.xml";

    /**
     * {@link KapuaLocator} implementation classname specified via "System property" constants
     */
    public static final String LOCATOR_GUICE_STAGE_SYSTEM_PROPERTY = "locator.guice.stage";

    /**
     * {@link KapuaLocator} implementation classname specified via "Environment property" constants
     */
    public static final String LOCATOR_GUICE_STAGE_ENVIRONMENT_PROPERTY = "LOCATOR_GUICE_STAGE";

    private Injector injector;

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    public GuiceLocatorImpl() {
        this(SERVICE_RESOURCE);
    }

    private static final AtomicInteger INITIALIZATION_ATTEMPTS = new AtomicInteger(0);

    public GuiceLocatorImpl(@NotNull String resourceName) {
        if (INITIALIZATION_ATTEMPTS.incrementAndGet() != 1) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.LOCATOR_CANNOT_BE_REINITIALIZED);
        }
        try {
            init(resourceName);
        } catch (Exception e) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.INVALID_CONFIGURATION, e);
        }
    }

    /**
     * Gets the {@link Injector} instance.
     *
     * @return The {@link Injector} instance.
     * @since 2.0.0
     */
    public Injector getInjector() {
        return injector;
    }

    @Override
    public <S extends KapuaService> S getService(Class<S> serviceClass) {
        try {
            return injector.getInstance(serviceClass);
        } catch (ConfigurationException e) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.SERVICE_UNAVAILABLE, e, serviceClass);
        }
    }

    @Override
    public <F extends KapuaObjectFactory> F getFactory(Class<F> factoryClass) {
        try {
            return injector.getInstance(factoryClass);
        } catch (ConfigurationException e) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.FACTORY_UNAVAILABLE, factoryClass);
        }
    }

    public <T> T getComponent(Class<T> componentClass) {
        try {
            return injector.getInstance(componentClass);
        } catch (ConfigurationException e) {
            throw new KapuaRuntimeException(KapuaLocatorErrorCodes.COMPONENT_UNAVAILABLE, componentClass);
        }
    }

    @Override
    public <T> T getComponent(Class<T> type, String named) {
        return (T) injector.getInstance(Key.get(TypeLiteral.get(type), Names.named(named)));

    }

    @Override
    public <T> T getComponent(Type type) {
        return (T) injector.getInstance(Key.get(TypeLiteral.get(type)));
    }

    @Override
    public List<KapuaService> getServices() {
        final List<KapuaService> servicesList = new ArrayList<>();
        final Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        for (Binding<?> binding : bindings.values()) {
            final Class<?> clazz = binding.getKey().getTypeLiteral().getRawType();
            if (KapuaService.class.isAssignableFrom(clazz)) {
                servicesList.add(injector.getInstance(clazz.asSubclass(KapuaService.class)));
            }
        }
        return servicesList;
    }

    final Map<Integer, List<AbstractMap.SimpleEntry<Method, Object>>> initMethods = new HashMap<>();

    /**
     * Initializes the {@link KapuaLocator} with the given resource name configuration.
     *
     * @param locatorConfigName
     *         The resource name containing the configuration.
     * @throws Exception
     * @since 1.0.0
     */
    private void init(String locatorConfigName) throws Exception {
        // Print loaded stuff
        final Stage stage = getStage();

        final ConfigurationPrinter configurationPrinter =
                ConfigurationPrinter
                        .create()
                        .withLogger(LOG)
                        .withLogLevel(ConfigurationPrinter.LogLevel.INFO)
                        .withTitle("Kapua Locator Configuration")
                        .addParameter("Resource Name", locatorConfigName)
                        .addParameter("Stage", stage);

        // Read configurations from locator file
        final LocatorConfig locatorConfig = new LocatorConfigurationExtractorImpl(locatorConfigName).fetchLocatorConfig();
        // Scan packages listed in to find KapuaModules
        final Collection<String> packageNames = locatorConfig.getIncludedPackageNames();
        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packageNames.toArray(new String[packageNames.size()]))
                .filterInputsBy(FilterBuilder.parsePackages(packageNames.stream().map(s -> "+" + s).collect(Collectors.joining(", "))))
                .setScanners(Scanners.SubTypes)
        );
        Set<Class<? extends AbstractKapuaModule>> kapuaModuleClasses = reflections.getSubTypesOf(AbstractKapuaModule.class);
        // Instantiate Kapua modules
        List<Module> kapuaModules = new ArrayList<>();
        List<Class<? extends AbstractKapuaModule>> excludedKapuaModules = new ArrayList<>();
        List<Module> overridingModules = new ArrayList<>();
        for (Class<? extends AbstractKapuaModule> moduleClazz : kapuaModuleClasses) {
            final boolean parameterlessConstructorExist = Arrays.stream(moduleClazz.getDeclaredConstructors()).anyMatch(c -> c.getParameterTypes().length == 0);
            if (!parameterlessConstructorExist) {
                excludedKapuaModules.add(moduleClazz);
                continue;
            }
            if (isExcluded(moduleClazz.getName(), locatorConfig.getExcludedPackageNames())) {
                excludedKapuaModules.add(moduleClazz);
                continue;
            }
            if (moduleClazz.getAnnotation(OverridingModule.class) != null) {
                overridingModules.add(moduleClazz.newInstance());
                continue;
            }

            final AbstractKapuaModule kapuaModule = moduleClazz.newInstance();
            kapuaModules.add(kapuaModule);
        }
        // KapuaModule will be removed as soon as bindings will be moved to local modules
        kapuaModules.add(new KapuaModule(locatorConfig));

        kapuaModules.add(new AbstractModule() {

            private boolean hasKapuaInitializingMethodAnnotation(Method method) {
                Annotation[] declaredAnnotations = method.getAnnotations();
                return Arrays.stream(declaredAnnotations).anyMatch(a -> a.annotationType().equals(KapuaInitializingMethod.class));
            }

            @Override
            protected void configure() {
                bindListener(new AbstractMatcher<TypeLiteral<?>>() {

                    @Override
                    public boolean matches(TypeLiteral<?> typeLiteral) {
                        return Arrays.stream(typeLiteral.getRawType().getDeclaredMethods()).anyMatch(m -> hasKapuaInitializingMethodAnnotation(m));
                    }
                }, new TypeListener() {

                    @Override
                    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                        encounter.register(new InjectionListener<I>() {

                            @Override
                            public void afterInjection(I injectee) {
                                Arrays.stream(injectee.getClass().getDeclaredMethods())
                                        .flatMap(m -> {
                                            return Arrays.stream(m.getAnnotations())
                                                    .filter(a -> a.annotationType().equals(KapuaInitializingMethod.class))
                                                    .map(a -> new AbstractMap.SimpleEntry<>(m, (KapuaInitializingMethod) a));
                                        })
                                        .forEach(kv -> {
                                            final int priority = kv.getValue().priority();
                                            initMethods.computeIfAbsent(priority, k -> new ArrayList<>());
                                            initMethods.get(priority).add(new AbstractMap.SimpleEntry<>(kv.getKey(), injectee));
                                        });
                            }
                        });
                    }
                });
            }
        });

        printLoadedKapuaModuleConfiguration(configurationPrinter, locatorConfig, kapuaModules, overridingModules, excludedKapuaModules);
        // Create injector
        try {
            injector = Guice.createInjector(stage, Modules.override(kapuaModules).with(overridingModules));
        } catch (Throwable t) {
            injector = null;
            throw new RuntimeException(t);
        }
    }

    @Override
    protected void runInitializers() {
        initMethods.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(kv -> kv.getValue().stream().forEach(methodAndObject -> {
                    try {
                        LOG.info("Running initializer with priority {} on 'class'.'method': '{}'.'{}'", kv.getKey(), methodAndObject.getValue().getClass().getName(),
                                methodAndObject.getKey().getName());
                        methodAndObject.getKey().invoke(methodAndObject.getValue());
                        LOG.info("Running initializer with priority {} on 'class'.'method': '{}'.'{}' (DONE)", kv.getKey(), methodAndObject.getValue().getClass().getName(),
                                methodAndObject.getKey().getName());
                    } catch (Throwable e) {
                        LOG.error("Running initializer ERROR: {}", e.getMessage(), e);
                        //old code (this doesn't cause the exit of the jvm but breaks the init chain so it's not good in any case IHMO)
                        //I think it's better to log the error and leave the jvm to proceed with the other initialization
                        //otherwise a system.exit() it's the right choice?
                        //throw new RuntimeException(e);
                    }
                }));
    }

    /**
     * Get the Guice Locator stage  {@link GuiceLocatorImpl#LOCATOR_GUICE_STAGE_SYSTEM_PROPERTY} system property or falling back to the
     * {@link GuiceLocatorImpl#LOCATOR_GUICE_STAGE_ENVIRONMENT_PROPERTY} environment variable.
     *
     * @return
     */
    private Stage getStage() {
        return Stream.of(
                        Optional.ofNullable(System.getProperty(LOCATOR_GUICE_STAGE_SYSTEM_PROPERTY)),
                        Optional.ofNullable(System.getenv(LOCATOR_GUICE_STAGE_ENVIRONMENT_PROPERTY))
                )
                .filter(maybeProperty -> maybeProperty.isPresent())
                .map(maybeProperty -> maybeProperty.get())
                .filter(property -> !property.isEmpty())
                .findFirst()
                .map(property -> Stage.valueOf(property))
                .orElse(Stage.DEVELOPMENT);
    }

    /**
     * Checks whether the given {@link Class#getName()} matches one of the excluded {@link Class#getPackage()}
     *
     * @param className
     *         The {@link Class#getName()} to check.
     * @param excludedPackages
     *         The {@link Collection} of excluded {@link Class#getPackage()}.
     * @return {@code true} if matched, {@code false otherwise}
     * @since 2.0.0
     */
    private boolean isExcluded(@NotNull String className, @NotNull Collection<String> excludedPackages) {
        for (String pkg : excludedPackages) {
            if (className.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints the configuration of the loaded {@link KapuaModule}s.
     *
     * @param locatorConfig
     *         The loaded {@link LocatorConfig}.
     * @param kapuaModules
     *         The laaded {@link KapuaModule}s
     * @since 2.0.0
     */
    private void printLoadedKapuaModuleConfiguration(
            @NotNull ConfigurationPrinter configurationPrinter,
            @NotNull LocatorConfig locatorConfig,
            @NotNull List<Module> kapuaModules,
            @NotNull List<Module> overridingModules,
            @NotNull List<Class<? extends AbstractKapuaModule>> excludedKapuaModules) {

        // Packages
        configurationPrinter.logSections("Included packages", locatorConfig.getIncludedPackageNames());
        configurationPrinter.logSections("Excluded packages", locatorConfig.getExcludedPackageNames());

        // Loaded modules
        configurationPrinter.logSections("Loaded Kapua Modules", kapuaModules, a -> a.getClass().getName());
        configurationPrinter.logSections("Overriding Kapua Modules", overridingModules, a -> a.getClass().getName());
        configurationPrinter.logSections("Excluded Kapua Modules", excludedKapuaModules, a -> a.getName());

        // Print it!
        configurationPrinter.printLog();
    }

    @Override
    public void close() throws IOException {
        INITIALIZATION_ATTEMPTS.decrementAndGet();
    }
}
