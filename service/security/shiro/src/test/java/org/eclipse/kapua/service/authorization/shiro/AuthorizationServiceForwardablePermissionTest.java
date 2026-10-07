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
package org.eclipse.kapua.service.authorization.shiro;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.security.KapuaSession;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.domain.DomainRegistryService;
import org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.permission.shiro.PermissionFactoryImpl;
import org.eclipse.kapua.service.authorization.shiro.claims.ClaimsFetcher;
import org.eclipse.kapua.service.user.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import com.google.inject.Provider;

/**
 * Tests {@link AuthorizationServiceImpl#checkPermission(Permission)} and {@link AuthorizationServiceImpl#isPermitted(Permission)}
 * against a real Shiro {@link org.apache.shiro.mgt.SecurityManager} and the real {@link PermissionMapperImpl}, focusing on the
 * {@link Permission#getForwardable()} rules.
 * <p>
 * The {@link Subject} permissions are provided by a test {@link AuthorizingRealm} which, like {@link KapuaAuthorizingRealm},
 * maps the {@link Permission}s with the {@link PermissionMapperImpl} at each check.
 * <p>
 * Account hierarchy used by the tests: kapua-sys (id 1) -&gt; subAccount (id 5).
 */
@Category(JUnitTests.class)
public class AuthorizationServiceForwardablePermissionTest {

    private static final KapuaId SYS_ACCOUNT_ID = new KapuaEid(BigInteger.ONE);
    private static final KapuaId SUB_ACCOUNT_ID = new KapuaEid(BigInteger.valueOf(5));
    private static final KapuaId USER_ID = new KapuaEid(BigInteger.valueOf(100));

    private final PermissionFactory permissionFactory = new PermissionFactoryImpl();

    private TestRealm realm;
    private AuthorizationServiceImpl authorizationService;

    @Before
    public void setUp() throws Exception {
        Map<KapuaId, Account> accounts = new HashMap<>();
        accounts.put(SYS_ACCOUNT_ID, mockAccount(null, "/1"));
        accounts.put(SUB_ACCOUNT_ID, mockAccount(SYS_ACCOUNT_ID, "/1/5"));

        AccountService accountService = Mockito.mock(AccountService.class);
        Mockito.when(accountService.find(Mockito.any(KapuaId.class)))
                .thenAnswer(invocation -> accounts.get(new KapuaEid(((KapuaId) invocation.getArguments()[0]).getId())));

        // No Domain found: the target Permission domain is considered not groupable
        DomainRegistryService domainService = Mockito.mock(DomainRegistryService.class);

        PermissionMapperImpl permissionMapper = new PermissionMapperImpl(domainService, accountService);
        @SuppressWarnings("unchecked")
        Provider<ClaimsFetcher> claimsFetcherProvider = Mockito.mock(Provider.class);
        authorizationService = new AuthorizationServiceImpl(Mockito.mock(UserService.class), permissionMapper, claimsFetcherProvider);

        realm = new TestRealm(permissionMapper);
        DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);
        Subject subject = new Subject.Builder(securityManager)
                .principals(new SimplePrincipalCollection("test-user", realm.getName()))
                .buildSubject();

        ThreadContext.bind(securityManager);
        ThreadContext.bind(subject);
        KapuaSecurityUtils.setSession(new KapuaSession(null, SYS_ACCOUNT_ID, USER_ID));
    }

    @After
    public void tearDown() {
        KapuaSecurityUtils.clearSession();
        ThreadContext.unbindSubject();
        ThreadContext.unbindSecurityManager();
    }

    //
    // Required permission: account:read on kapua-sys, forwardable (i.e. the check of AccountService.query with null scopeId)

    @Test
    public void accountReadOnSysNotForwardableDoesNotImplyForwardableRequired() throws Exception {
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SYS_ACCOUNT_ID, null, false));

        assertNotPermitted(requiredAccountReadOnSysForwardable());
    }

    @Test
    public void accountReadOnSysForwardableImpliesForwardableRequired() throws Exception {
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SYS_ACCOUNT_ID, null, true));

        assertPermitted(requiredAccountReadOnSysForwardable());
    }

    @Test
    public void accountReadOnNullScopeForwardableImpliesForwardableRequired() throws Exception {
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, null, null, true));

        assertPermitted(requiredAccountReadOnSysForwardable());
    }

    @Test
    public void accountReadOnNullScopeNotForwardableImpliesForwardableRequired() throws Exception {
        // A Permission with null targetScopeId is given on all scopes, so the forwardable flag is irrelevant
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, null, null, false));

        assertPermitted(requiredAccountReadOnSysForwardable());
    }

    @Test
    public void allPermissionsOnNullScopeNotForwardableImpliesForwardableRequired() throws Exception {
        givenUserPermissions(permissionFactory.newPermission(null, null, null, null, false));

        assertPermitted(requiredAccountReadOnSysForwardable());
    }

    @Test
    public void allActionsOnSysNotForwardableDoesNotImplyForwardableRequired() throws Exception {
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, null, SYS_ACCOUNT_ID, null, false));

        assertNotPermitted(requiredAccountReadOnSysForwardable());
    }

    @Test
    public void accountReadOnSubAccountForwardableDoesNotImplyForwardableRequired() throws Exception {
        // Forwarding only applies to child accounts: a Permission on subAccount cannot be forwarded to its parent kapua-sys
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SUB_ACCOUNT_ID, null, true));

        assertNotPermitted(requiredAccountReadOnSysForwardable());
    }

    @Test
    public void otherDomainOnSysForwardableDoesNotImplyForwardableRequired() throws Exception {
        givenUserPermissions(permissionFactory.newPermission(Domains.USER, Actions.read, SYS_ACCOUNT_ID, null, true));

        assertNotPermitted(requiredAccountReadOnSysForwardable());
    }

    @Test
    public void notForwardableAndForwardablePermissionsImplyForwardableRequired() throws Exception {
        // The not forwardable Permission does not imply the required one, but the forwardable one does
        givenUserPermissions(
                permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SYS_ACCOUNT_ID, null, false),
                permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SYS_ACCOUNT_ID, null, true));

        assertPermitted(requiredAccountReadOnSysForwardable());
    }

    //
    // Required permissions not forwardable: the forwardable rules must not break the regular checks

    @Test
    public void accountReadOnSysNotForwardableImpliesNotForwardableRequired() throws Exception {
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SYS_ACCOUNT_ID, null, false));

        assertPermitted(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SYS_ACCOUNT_ID, null, false));
    }

    @Test
    public void accountReadOnSysForwardableImpliesNotForwardableRequiredOnSubAccount() throws Exception {
        // Forwarding of the Permission from kapua-sys to its child subAccount
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SYS_ACCOUNT_ID, null, true));

        assertPermitted(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SUB_ACCOUNT_ID, null, false));
    }

    @Test
    public void accountReadOnSysNotForwardableDoesNotImplyNotForwardableRequiredOnSubAccount() throws Exception {
        givenUserPermissions(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SYS_ACCOUNT_ID, null, false));

        assertNotPermitted(permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, SUB_ACCOUNT_ID, null, false));
    }

    //
    // Utilities

    private Permission requiredAccountReadOnSysForwardable() {
        return permissionFactory.newPermission(Domains.ACCOUNT, Actions.read, KapuaId.ONE, null, true);
    }

    private void givenUserPermissions(Permission... permissions) {
        realm.setUserPermissions(Arrays.asList(permissions));
    }

    private void assertPermitted(Permission requiredPermission) throws Exception {
        Assert.assertTrue("Permission " + requiredPermission + " should be permitted", authorizationService.isPermitted(requiredPermission));
        authorizationService.checkPermission(requiredPermission);
    }

    private void assertNotPermitted(Permission requiredPermission) throws Exception {
        Assert.assertFalse("Permission " + requiredPermission + " should not be permitted", authorizationService.isPermitted(requiredPermission));
        try {
            authorizationService.checkPermission(requiredPermission);
            Assert.fail("SubjectUnauthorizedException expected for Permission " + requiredPermission);
        } catch (SubjectUnauthorizedException e) {
            // Expected
        }
    }

    private static Account mockAccount(KapuaId scopeId, String parentAccountPath) {
        Account account = Mockito.mock(Account.class);
        Mockito.when(account.getScopeId()).thenReturn(scopeId);
        Mockito.when(account.getParentAccountPath()).thenReturn(parentAccountPath);
        return account;
    }

    /**
     * {@link AuthorizingRealm} that provides the configured {@link Permission}s, mapping them with the {@link PermissionMapperImpl}
     * at each check like {@link KapuaAuthorizingRealm} does.
     */
    private static class TestRealm extends AuthorizingRealm {

        private final PermissionMapperImpl permissionMapper;
        private List<Permission> userPermissions;

        TestRealm(PermissionMapperImpl permissionMapper) {
            this.permissionMapper = permissionMapper;
            setAuthorizationCachingEnabled(false);
        }

        void setUserPermissions(List<Permission> userPermissions) {
            this.userPermissions = userPermissions;
        }

        @Override
        protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) { //this emulates KapuaAuthorizingRealm.doGetAuthorizationInfo
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.setObjectPermissions(userPermissions.stream().map(permissionMapper::mapPermission).collect(Collectors.toSet()));
            return info;
        }

        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
            return null;
        }
    }
}
