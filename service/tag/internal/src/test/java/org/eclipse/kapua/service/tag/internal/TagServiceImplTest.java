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
package org.eclipse.kapua.service.tag.internal;

import java.math.BigInteger;
import java.util.function.BiConsumer;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.configuration.ServiceConfigurationManager;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdImpl;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagCreator;
import org.eclipse.kapua.service.tag.TagFactory;
import org.eclipse.kapua.service.tag.TagRepository;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.kapua.storage.TxManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.mockito.Mockito;

@org.junit.jupiter.api.Tag("org.eclipse.kapua.qa.markers.junit.JUnitTests")
public class TagServiceImplTest {

    private static final MockSettings FAIL_ON_UNPREDICTED_METHOD_CALL = Mockito.withSettings()
            .defaultAnswer(invocation -> {
                throw new UnsupportedOperationException(invocation.toString());
            });
    public static final Permission FAKE_PERMISSION = new Permission("fakeDomain", Actions.execute, new KapuaIdImpl(BigInteger.ONE), new KapuaIdImpl(BigInteger.TEN), true);
    private PermissionFactory permissionFactory;
    private AuthorizationService authorizationService;
    private ServiceConfigurationManager serviceConfigurationManager;
    private TagRepository tagRepository;
    private TagServiceImpl instance;
    private TagFactory tagFactory;

    @BeforeEach
    public void setUp() throws KapuaException {
        permissionFactory = Mockito.mock(PermissionFactory.class);
        Mockito.when(permissionFactory.newPermission(Mockito.<String>any(), Mockito.any(), Mockito.any()))
                .thenReturn(FAKE_PERMISSION);
        authorizationService = Mockito.mock(AuthorizationService.class);
        serviceConfigurationManager = Mockito.mock(ServiceConfigurationManager.class);
        tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.create(Mockito.<TxContext>any(), Mockito.<Tag>any()))
                .thenAnswer(invocation -> invocation.getArgumentAt(0, Tag.class));
        tagFactory = Mockito.mock(TagFactory.class);
        final TxManager txManager = new TxManager() {

            @Override
            public <R> R execute(TxConsumer<R> transactionConsumer, BiConsumer<TxContext, R>... afterCommitConsumers) throws KapuaException {
                return null;
            }

            @Override
            public TxContext getTxContext() {
                return null;
            }
        };
        Mockito.when(tagFactory.newEntity(Mockito.any()))
                .thenAnswer(invocation -> new TagImpl(invocation.<KapuaId>getArgumentAt(0, KapuaId.class)));

        instance = new TagServiceImpl(
                permissionFactory,
                authorizationService,
                serviceConfigurationManager,
                txManager,
                tagRepository,
                tagFactory
        );
    }

    @Test
    public void createTagPerformsInputValidation() {
        Assertions.assertThrows(KapuaIllegalNullArgumentException.class,
                () -> instance.create(null),
                "Does not accept null tagCreator");
        Assertions.assertThrows(KapuaIllegalNullArgumentException.class,
                () -> instance.create(new TagCreator(null, "testTag")),
                "Does not accept tagCreator with null scope id");
        Assertions.assertThrows(KapuaIllegalNullArgumentException.class,
                () -> instance.create(new TagCreator(new KapuaIdImpl(BigInteger.ONE), null)),
                "Does not accept tagCreator with null name");
    }
}