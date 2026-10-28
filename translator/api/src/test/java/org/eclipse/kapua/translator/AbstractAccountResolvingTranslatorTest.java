/*******************************************************************************
 * Copyright (c) 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.translator;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.math.BigInteger;

@Category(JUnitTests.class)
public class AbstractAccountResolvingTranslatorTest {

    private static final String ACCOUNT_NAME = "account";
    private static final KapuaId ACCOUNT_ID = new KapuaEid(BigInteger.TEN);

    private AccountService accountService;
    private Account account;

    /**
     * Minimal {@link AbstractAccountResolvingTranslator} exposing {@link AbstractAccountResolvingTranslator#findAccountIdByName(String)}.
     */
    private static class TestTranslator extends AbstractAccountResolvingTranslator<Message, Message> {

        private TestTranslator(AccountService accountService, LocalCache<String, Account> accountsByNameCache) {
            super(accountService, accountsByNameCache);
        }

        @Override
        public Message translate(Message message) {
            return null;
        }

        @Override
        public Class<Message> getClassFrom() {
            return Message.class;
        }

        @Override
        public Class<Message> getClassTo() {
            return Message.class;
        }
    }

    @Before
    public void setUp() {
        accountService = Mockito.mock(AccountService.class);
        account = Mockito.mock(Account.class);
        Mockito.when(account.getId()).thenReturn(ACCOUNT_ID);
    }

    @Test
    public void findAccountIdByNameCachedTest() throws Exception {
        Mockito.when(accountService.findByName(ACCOUNT_NAME)).thenReturn(account);
        TestTranslator translator = new TestTranslator(accountService, newCache());

        Assert.assertEquals(ACCOUNT_ID, translator.findAccountIdByName(ACCOUNT_NAME));
        Assert.assertEquals(ACCOUNT_ID, translator.findAccountIdByName(ACCOUNT_NAME));

        Mockito.verify(accountService, Mockito.times(1)).findByName(ACCOUNT_NAME);
    }

    @Test
    public void findAccountIdByNameSharedCacheTest() throws Exception {
        Mockito.when(accountService.findByName(ACCOUNT_NAME)).thenReturn(account);
        LocalCache<String, Account> sharedCache = newCache();
        TestTranslator translator = new TestTranslator(accountService, sharedCache);
        TestTranslator otherTranslator = new TestTranslator(accountService, sharedCache);

        Assert.assertEquals(ACCOUNT_ID, translator.findAccountIdByName(ACCOUNT_NAME));
        // Cached by the first Translator
        Assert.assertEquals(ACCOUNT_ID, otherTranslator.findAccountIdByName(ACCOUNT_NAME));

        Mockito.verify(accountService, Mockito.times(1)).findByName(ACCOUNT_NAME);
    }

    @Test
    public void findAccountIdByNameNotFoundNotCachedTest() throws Exception {
        Mockito.when(accountService.findByName(ACCOUNT_NAME)).thenReturn(null, account);
        TestTranslator translator = new TestTranslator(accountService, newCache());

        Assert.assertNull(translator.findAccountIdByName(ACCOUNT_NAME));
        // Created after the first lookup: must be found
        Assert.assertEquals(ACCOUNT_ID, translator.findAccountIdByName(ACCOUNT_NAME));
        Assert.assertEquals(ACCOUNT_ID, translator.findAccountIdByName(ACCOUNT_NAME));

        Mockito.verify(accountService, Mockito.times(2)).findByName(ACCOUNT_NAME);
    }

    @Test
    public void findAccountIdByNameErrorNotCachedTest() throws Exception {
        Mockito.when(accountService.findByName(ACCOUNT_NAME)).thenThrow(KapuaException.internalError("test")).thenReturn(account);
        TestTranslator translator = new TestTranslator(accountService, newCache());

        try {
            translator.findAccountIdByName(ACCOUNT_NAME);
            Assert.fail("KapuaException expected");
        } catch (KapuaException e) {
            // expected
        }
        Assert.assertEquals(ACCOUNT_ID, translator.findAccountIdByName(ACCOUNT_NAME));

        Mockito.verify(accountService, Mockito.times(2)).findByName(ACCOUNT_NAME);
    }

    @Test
    public void findAccountIdByNameWithoutCacheTest() throws Exception {
        Mockito.when(accountService.findByName(ACCOUNT_NAME)).thenReturn(account);
        TestTranslator translator = new TestTranslator(accountService, null);

        Assert.assertEquals(ACCOUNT_ID, translator.findAccountIdByName(ACCOUNT_NAME));
        Assert.assertEquals(ACCOUNT_ID, translator.findAccountIdByName(ACCOUNT_NAME));

        Mockito.verify(accountService, Mockito.times(2)).findByName(ACCOUNT_NAME);
    }

    @Test(expected = KapuaIllegalNullArgumentException.class)
    public void findAccountIdByNameNullNameTest() throws Exception {
        new TestTranslator(accountService, newCache()).findAccountIdByName(null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorWithoutAccountServiceTest() {
        new TestTranslator(null, newCache());
    }

    //
    // Private methods
    //

    private static LocalCache<String, Account> newCache() {
        return new LocalCache<>(100, 60, null);
    }
}
