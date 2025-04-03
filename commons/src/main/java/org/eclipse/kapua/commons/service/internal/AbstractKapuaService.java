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
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.service.internal;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.commons.jpa.EntityCacheFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerFactory;
import org.eclipse.kapua.commons.jpa.EntityManagerSession;
import org.eclipse.kapua.commons.service.internal.cache.EntityCache;
import org.eclipse.kapua.event.ServiceEventBus;
import org.eclipse.kapua.event.ServiceEventBusException;
import org.eclipse.kapua.event.ServiceEventBusListener;
import org.eclipse.kapua.event.Subscription;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.KapuaService;

/**
 * {@code abstract} {@link KapuaService} implementation.
 * <p>
 * It handles the {@link EntityManagerFactory} and {@link EntityManagerSession} to avoid redefining each time in the {@link KapuaService}s.
 *
 * @since 1.0.0
 * @deprecated since 2.0.0 - use repository pattern instead - no need for your service to extend this
 */
@Deprecated
public abstract class AbstractKapuaService implements KapuaService {

    //todo: Not needed, but cannot removed due to potential custom implementations extending this.
    protected final EntityManagerFactory entityManagerFactory;
    protected final EntityManagerSession entityManagerSession;
    protected final EntityCache entityCache;
    private final ServiceEventBus serviceEventBus;

    /**
     * Constructor
     *
     * @param entityManagerFactory
     *         The {@link EntityManagerFactory}.
     * @since 1.0.0
     * @deprecated Since 1.2.0. Please make use of {@link #AbstractKapuaService(EntityManagerFactory, EntityCacheFactory)}. This constructor will be removed in a next release (may be).
     */
    @Deprecated
    protected AbstractKapuaService(@NotNull EntityManagerFactory entityManagerFactory) {
        this(entityManagerFactory, null);
    }

    protected AbstractKapuaService(@NotNull EntityManagerFactory entityManagerFactory, EntityCacheFactory entityCacheFactory) {
        this(entityManagerFactory, entityCacheFactory, KapuaLocator.getInstance().getComponent(ServiceEventBus.class));
    }

    /**
     * Constructor.
     *
     * @param entityManagerFactory
     *         The {@link EntityManagerFactory}.
     * @param entityCacheFactory
     *         The {@link EntityCacheFactory}.
     * @since 1.2.0
     */
    protected AbstractKapuaService(@NotNull EntityManagerFactory entityManagerFactory, EntityCacheFactory entityCacheFactory, ServiceEventBus serviceEventBus) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManagerSession = new EntityManagerSession(entityManagerFactory);
        this.serviceEventBus = serviceEventBus;

        if (entityCacheFactory != null) {
            this.entityCache = entityCacheFactory.createCache("Deprecated");
        } else {
            this.entityCache = null;
        }
    }

    /**
     * Gets the {@link EntityManagerSession} of this {@link AbstractKapuaService}.
     *
     * @return The {@link EntityManagerSession} of this {@link AbstractKapuaService}.
     * @since 1.0.0
     */
    public EntityManagerSession getEntityManagerSession() {
        return entityManagerSession;
    }

    /**
     * Registers a {@link ServiceEventBusListener} into the {@link org.eclipse.kapua.event.ServiceEventBus}.
     *
     * @param listener
     *         The {@link ServiceEventBusListener} to register.
     * @param address
     *         The {@link ServiceEventBus} address to subscribe to.
     * @param clazz
     *         The {@link KapuaService} owner of the {@link ServiceEventBusListener}.
     * @throws ServiceEventBusException
     *         If any error occurs during subscription to the address.
     * @since 1.0.0kapua-sew
     */
    protected void registerEventListener(@NotNull ServiceEventBusListener listener, @NotNull String address, @NotNull Class<? extends KapuaService> clazz) throws ServiceEventBusException {
        serviceEventBus.subscribe(new Subscription(address, clazz.getName(), listener));
    }
}
