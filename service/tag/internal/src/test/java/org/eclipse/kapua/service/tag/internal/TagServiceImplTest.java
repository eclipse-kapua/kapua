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
import org.eclipse.kapua.commons.model.mappers.KapuaBaseMapperImpl;
import org.eclipse.kapua.model.id.KapuaIdImpl;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.tag.Tag;
import org.eclipse.kapua.service.tag.TagCreator;
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
    private AuthorizationService authorizationService;
    private ServiceConfigurationManager serviceConfigurationManager;
    private TagRepository tagRepository;
    private TagServiceImpl instance;

    @BeforeEach
    public void setUp() throws KapuaException {
        authorizationService = Mockito.mock(AuthorizationService.class);
        serviceConfigurationManager = Mockito.mock(ServiceConfigurationManager.class);
        tagRepository = Mockito.mock(TagRepository.class);
        Mockito.when(tagRepository.create(Mockito.<TxContext>any(), Mockito.<TagImpl>any()))
                .thenAnswer(invocation -> invocation.getArgumentAt(0, Tag.class));
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

        instance = new TagServiceImpl(
                authorizationService,
                serviceConfigurationManager,
                txManager,
                tagRepository,
                new TagMapperImpl(new KapuaBaseMapperImpl())
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