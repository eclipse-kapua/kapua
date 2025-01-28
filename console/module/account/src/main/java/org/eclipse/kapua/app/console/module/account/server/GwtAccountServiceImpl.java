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
package org.eclipse.kapua.app.console.module.account.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccountCreator;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccountQuery;
import org.eclipse.kapua.app.console.module.account.shared.service.GwtAccountService;
import org.eclipse.kapua.app.console.module.account.shared.util.GwtKapuaAccountModelConverter;
import org.eclipse.kapua.app.console.module.account.shared.util.KapuaGwtAccountModelConverter;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSetting;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSettingKeys;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtConfigComponent;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtConfigParameter;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.util.GwtKapuaCommonsModelConverter;
import org.eclipse.kapua.commons.model.domains.Domains;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.service.internal.KapuaServiceDisabledException;
import org.eclipse.kapua.commons.util.ThrowingRunnable;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTicon;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.KapuaService;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.exception.SubjectUnauthorizedException;
import org.eclipse.kapua.service.authorization.permission.Permission;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.authorization.role.RoleCreator;
import org.eclipse.kapua.service.authorization.role.RoleService;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.endpoint.EndpointInfo;
import org.eclipse.kapua.service.endpoint.EndpointInfoListResult;
import org.eclipse.kapua.service.endpoint.EndpointInfoQuery;
import org.eclipse.kapua.service.endpoint.EndpointInfoService;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserQuery;
import org.eclipse.kapua.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.common.collect.Sets;

/**
 * The server side implementation of the RPC service.
 */
public class GwtAccountServiceImpl extends KapuaRemoteServiceServlet implements GwtAccountService {

    private static final long serialVersionUID = 3314502846487119577L;

    private static final Logger LOG = LoggerFactory.getLogger(GwtAccountServiceImpl.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AccountService ACCOUNT_SERVICE = LOCATOR.getService(AccountService.class);

    private static final EndpointInfoService ENDPOINT_INFO_SERVICE = LOCATOR.getService(EndpointInfoService.class);
    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);

    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);

    private static final RoleService ROLE_SERVICE = LOCATOR.getService(RoleService.class);

    private static final UserService USER_SERVICE = LOCATOR.getService(UserService.class);

    @Override
    public GwtAccount create(GwtXSRFToken xsrfToken, GwtAccountCreator gwtAccountCreator)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        GwtAccount gwtAccount = null;
        KapuaId parentAccountId = KapuaEid.parseCompactId(gwtAccountCreator.getParentAccountId());
        try {
            AccountCreator accountCreator = new AccountCreator(parentAccountId);

            accountCreator.setName(gwtAccountCreator.getAccountName());
            accountCreator.setOrganizationName(gwtAccountCreator.getOrganizationName());
            accountCreator.setOrganizationPersonName(gwtAccountCreator.getOrganizationPersonName());
            accountCreator.setOrganizationEmail(gwtAccountCreator.getOrganizationEmail());
            accountCreator.setOrganizationPhoneNumber(gwtAccountCreator.getOrganizationPhoneNumber());
            accountCreator.setOrganizationAddressLine1(gwtAccountCreator.getOrganizationAddressLine1());
            accountCreator.setOrganizationAddressLine2(gwtAccountCreator.getOrganizationAddressLine2());
            accountCreator.setOrganizationAddressLine3(gwtAccountCreator.getOrganizationAddressLine3());
            accountCreator.setOrganizationCity(gwtAccountCreator.getOrganizationCity());
            accountCreator.setOrganizationZipPostCode(gwtAccountCreator.getOrganizationZipPostCode());
            accountCreator.setOrganizationStateProvinceCounty(gwtAccountCreator.getOrganizationStateProvinceCounty());
            accountCreator.setOrganizationCountry(gwtAccountCreator.getOrganizationCountry());
            accountCreator.setExpirationDate(gwtAccountCreator.getExpirationDate());
            // Create the Account
            final Account account = ACCOUNT_SERVICE.create(accountCreator);
            // Create roles
            KapuaSecurityUtils.doPrivileged(new ThrowingRunnable() {

                @Override
                public void run() throws Exception {
                    // Admin
                    Permission adminPermission = PERMISSION_FACTORY.newPermission((String) null, null, account.getId(), null, true);

                    RoleCreator adminRoleCreator = new RoleCreator(account.getId());
                    adminRoleCreator.setName("Admin");
                    adminRoleCreator.setScopeId(account.getId());
                    adminRoleCreator.setPermissions(Sets.newHashSet(adminPermission));

                    ROLE_SERVICE.create(adminRoleCreator);

                    // Thing
                    Permission thingPermission = PERMISSION_FACTORY.newPermission(Domains.BROKER, Actions.connect, account.getId(), null, false);

                    RoleCreator thingRoleCreator = new RoleCreator(account.getId());
                    thingRoleCreator.setName("Thing");
                    thingRoleCreator.setScopeId(account.getId());
                    thingRoleCreator.setPermissions(Sets.newHashSet(thingPermission));

                    ROLE_SERVICE.create(thingRoleCreator);
                }
            });
            // Convert Account to GwtAccount
            gwtAccount = KapuaGwtAccountModelConverter.convertAccount(account);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return gwtAccount;
    }

    @Override
    public GwtAccount find(String accountIdString)
            throws GwtKapuaException {
        KapuaId accountId = KapuaEid.parseCompactId(accountIdString);

        GwtAccount gwtAccount = null;
        try {
            gwtAccount = KapuaGwtAccountModelConverter.convertAccount(ACCOUNT_SERVICE.find(accountId));
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtAccount;
    }

    @Override
    public ListLoadResult<GwtGroupedNVPair> getAccountInfo(String scopeIdString, String accountIdString)
            throws GwtKapuaException {
        final KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
        KapuaId accountId = KapuaEid.parseCompactId(accountIdString);

        List<GwtGroupedNVPair> accountPropertiesPairs = new ArrayList<GwtGroupedNVPair>();
        try {
            final Account account = ACCOUNT_SERVICE.find(scopeId, accountId);

            //TODO: #LAYER_VIOLATION - user lookup should not be done here
            UserListResult userListResult = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                @Override
                public UserListResult call() throws Exception {
                    return USER_SERVICE.query(new UserQuery(null));
                }
            });

            Map<String, String> usernameMap = new HashMap<String, String>();
            for (User user : userListResult.getItems()) {
                usernameMap.put(user.getId().toCompactId(), user.getName());
            }

            final String entityInfo = "entityInfo";
            accountPropertiesPairs.add(new GwtGroupedNVPair(entityInfo, "expirationDate", account.getExpirationDate()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(entityInfo, "accountCreatedOn", account.getCreatedOn()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(entityInfo, "accountCreatedBy",
                    account.getCreatedBy() != null ? usernameMap.get(account.getCreatedBy().toCompactId()) : null));
            accountPropertiesPairs.add(new GwtGroupedNVPair(entityInfo, "accountModifiedOn", account.getModifiedOn()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(entityInfo, "accountModifiedBy",
                    account.getModifiedBy() != null ? usernameMap.get(account.getModifiedBy().toCompactId()) : null));

            if (account.getScopeId() != null) {
                Account parentAccount = KapuaSecurityUtils.doPrivileged(new Callable<Account>() {

                    @Override
                    public Account call() throws Exception {
                        return ACCOUNT_SERVICE.find(account.getScopeId());
                    }
                });
                accountPropertiesPairs.add(new GwtGroupedNVPair("accountInfo", "accountParent", parentAccount != null ? parentAccount.getName() : null));
            }
            accountPropertiesPairs.add(new GwtGroupedNVPair("accountInfo", "accountName", account.getName()));

            if (AUTHORIZATION_SERVICE.isPermitted(PERMISSION_FACTORY.newPermission(Domains.ENDPOINT_INFO, Actions.read, scopeId))) {
                //TODO: #LAYER_VIOLATION - related entities lookup should not be done here

                EndpointInfoListResult endpointInfos = KapuaSecurityUtils.doPrivileged(new Callable<EndpointInfoListResult>() {

                    @Override
                    public EndpointInfoListResult call() throws Exception {
                        return ENDPOINT_INFO_SERVICE.query(new EndpointInfoQuery(account.getId()));
                    }
                });

                for (EndpointInfo ei : endpointInfos.getItems()) {
                    accountPropertiesPairs.add(new GwtGroupedNVPair("deploymentInfo", ei.getSecure() ? "deploymentNodeUriSecure" : "deploymentNodeUri", ei.toStringURI()));
                }
            }
            final String organizationInfo = "organizationInfo";
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationName", account.getOrganization().getName()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationContactName", account.getOrganization().getPersonName()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationEmail", account.getOrganization().getEmail()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationPhoneNumber", account.getOrganization().getPhoneNumber()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationAddress1", account.getOrganization().getAddressLine1()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationAddress2", account.getOrganization().getAddressLine2()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationAddress3", account.getOrganization().getAddressLine3()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationZip", account.getOrganization().getZipPostCode()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationCity", account.getOrganization().getCity()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationState", account.getOrganization().getStateProvinceCounty()));
            accountPropertiesPairs.add(new GwtGroupedNVPair(organizationInfo, "organizationCountry", account.getOrganization().getCountry()));
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtGroupedNVPair>(accountPropertiesPairs);
    }

    @Override
    public GwtAccount updateAccountProperties(GwtXSRFToken xsrfToken, GwtAccount gwtAccount)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        GwtAccount gwtAccountUpdated = null;
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(gwtAccount.getId());

            Account account = ACCOUNT_SERVICE.find(scopeId);

            // update properties
            Properties property = account.getEntityProperties();
            if (property == null) {
                property = new Properties();
            }

            account.setEntityProperties(property);
            account = ACCOUNT_SERVICE.update(account);

            // convertKapuaId to GwtAccount and return
            gwtAccountUpdated = KapuaGwtAccountModelConverter.convertAccount(account);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return gwtAccountUpdated;
    }

    @Override
    public GwtAccount update(GwtXSRFToken xsrfToken, GwtAccount gwtAccount)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        GwtAccount gwtAccountUpdated = null;
        KapuaId accountId = KapuaEid.parseCompactId(gwtAccount.getId());
        try {
            Account account = ACCOUNT_SERVICE.find(accountId);

            account.getOrganization().setName(gwtAccount.getGwtOrganization().getName());
            account.getOrganization().setPersonName(gwtAccount.getGwtOrganization().getPersonName());
            account.getOrganization().setEmail(gwtAccount.getGwtOrganization().getEmailAddress());
            account.getOrganization().setPhoneNumber(gwtAccount.getGwtOrganization().getPhoneNumber());
            account.getOrganization().setAddressLine1(gwtAccount.getGwtOrganization().getAddressLine1());
            account.getOrganization().setAddressLine2(gwtAccount.getGwtOrganization().getAddressLine2());
            account.getOrganization().setAddressLine3(gwtAccount.getGwtOrganization().getAddressLine3());
            account.getOrganization().setZipPostCode(gwtAccount.getGwtOrganization().getZipPostCode());
            account.getOrganization().setCity(gwtAccount.getGwtOrganization().getCity());
            account.getOrganization().setStateProvinceCounty(gwtAccount.getGwtOrganization().getStateProvinceCounty());
            account.getOrganization().setCountry(gwtAccount.getGwtOrganization().getCountry());
            account.setExpirationDate(gwtAccount.getExpirationDate());
            account.setOptlock(gwtAccount.getOptlock());

            account = ACCOUNT_SERVICE.update(account);

            // convertKapuaId to GwtAccount and return
            gwtAccountUpdated = KapuaGwtAccountModelConverter.convertAccount(account);
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        return gwtAccountUpdated;
    }

    @Override
    public void delete(GwtXSRFToken xsrfToken, GwtAccount gwtAccount)
            throws GwtKapuaException {
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        KapuaId scopeId = gwtAccount.getScopeId() != null ? KapuaEid.parseCompactId(gwtAccount.getScopeId()) : null;
        KapuaId accountId = KapuaEid.parseCompactId(gwtAccount.getId());
        try {
            Account account = ACCOUNT_SERVICE.find(scopeId, accountId);

            if (account != null) {
                ACCOUNT_SERVICE.delete(account.getScopeId(), account.getId());
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
    }

    @Override
    public ListLoadResult<GwtAccount> findAll(String scopeIdString)
            throws GwtKapuaException {

        List<GwtAccount> gwtAccountList = new ArrayList<GwtAccount>();
        KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
        try {
            KapuaQuery query = new KapuaQuery(scopeId);

            KapuaListResult<Account> list = ACCOUNT_SERVICE.query(query);
            for (Account account : list.getItems()) {
                gwtAccountList.add(KapuaGwtAccountModelConverter.convertAccount(account));
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtAccount>(gwtAccountList);
    }

    @Override
    public ListLoadResult<GwtAccount> findChildren(String parentAccountId, boolean includeSelf)
            throws GwtKapuaException {
        KapuaId scopeId = KapuaEid.parseCompactId(parentAccountId);

        List<GwtAccount> gwtAccountList = new ArrayList<GwtAccount>();
        try {
            if (includeSelf) {
                gwtAccountList.add(find(parentAccountId));
            }
            KapuaQuery query = new KapuaQuery(scopeId);

            KapuaListResult<Account> list = ACCOUNT_SERVICE.query(query);
            for (Account account : list.getItems()) {
                gwtAccountList.add(KapuaGwtAccountModelConverter.convertAccount(account));
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtAccount>(gwtAccountList);
    }

    @Override
    public GwtAccount findByAccountName(String accountName)
            throws GwtKapuaException {
        GwtAccount gwtAccount = null;
        try {
            Account account = ACCOUNT_SERVICE.findByName(accountName);
            if (account != null) {
                gwtAccount = KapuaGwtAccountModelConverter.convertAccount(account);
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return gwtAccount;
    }

    @Override
    public List<GwtConfigComponent> findServiceConfigurations(String scopeId) throws GwtKapuaException {
        List<GwtConfigComponent> gwtConfigs = new ArrayList<GwtConfigComponent>();
        try {
            KapuaId kapuaScopeId = GwtKapuaCommonsModelConverter.convertKapuaId(scopeId);
            for (KapuaService service : LOCATOR.getServices()) {
                if (service instanceof KapuaConfigurableService) {
                    KapuaConfigurableService configurableService = (KapuaConfigurableService) service;
                    KapuaTocd tocd;
                    try {
                        tocd = configurableService.getConfigMetadata(GwtKapuaCommonsModelConverter.convertKapuaId(scopeId));
                    } catch (SubjectUnauthorizedException ex) {
                        continue;
                    } catch (KapuaServiceDisabledException ex) {
                        continue;
                    }
                    if (tocd != null && !tocd.isEmpty()) {
                        GwtConfigComponent gwtConfig = new GwtConfigComponent();
                        gwtConfig.setId(tocd.getId());
                        gwtConfig.setName(tocd.getName());
                        gwtConfig.setDescription(tocd.getDescription());
                        if (tocd.getIcon() != null && !tocd.getIcon().isEmpty()) {
                            KapuaTicon icon = tocd.getIcon().get(0);

                            checkIconResource(icon);

                            gwtConfig.setComponentIcon(icon.getResource());
                        }

                        List<GwtConfigParameter> gwtParams = new ArrayList<GwtConfigParameter>();
                        gwtConfig.setParameters(gwtParams);
                        Map<String, Object> values = configurableService.getConfigValues(kapuaScopeId);
                        for (KapuaTad ad : tocd.getAD()) {
                            if (ad != null) {
                                GwtConfigParameter gwtParam = new GwtConfigParameter();
                                gwtParam.setId(ad.getId());
                                gwtParam.setName(ad.getName());
                                gwtParam.setDescription(ad.getDescription());
                                gwtParam.setType(GwtConfigParameter.GwtConfigParameterType.fromString(ad.getType().value()));
                                gwtParam.setRequired(ad.isRequired());
                                gwtParam.setCardinality(ad.getCardinality());
                                if (ad.getOption() != null && !ad.getOption().isEmpty()) {
                                    Map<String, String> options = new HashMap<String, String>();
                                    for (KapuaToption option : ad.getOption()) {
                                        options.put(option.getLabel(), option.getValue());
                                    }
                                    gwtParam.setOptions(options);
                                }
                                gwtParam.setMin(ad.getMin());
                                gwtParam.setMax(ad.getMax());
                                Map<String, String> gwtEntries = new HashMap<String, String>();
                                for (Entry<QName, String> entry : ad.getOtherAttributes().entrySet()) {
                                    gwtEntries.put(entry.getKey().toString(), entry.getValue());
                                }
                                gwtParam.setOtherAttributes(gwtEntries);

                                if (!values.isEmpty()) {
                                    int cardinality = ad.getCardinality();
                                    Object value = values.get(ad.getId());
                                    if (value != null) {
                                        if (cardinality == 0 || cardinality == 1 || cardinality == -1) {
                                            gwtParam.setValue(value.toString());
                                        } else {
                                            // this could be an array value
                                            if (value instanceof Object[]) {
                                                Object[] objValues = (Object[]) value;
                                                List<String> strValues = new ArrayList<String>();
                                                for (Object v : objValues) {
                                                    if (v != null) {
                                                        strValues.add(v.toString());
                                                    }
                                                }
                                                gwtParam.setValues(strValues.toArray(new String[] {}));
                                            }
                                        }
                                    }
                                    gwtParams.add(gwtParam);
                                }
                            }
                        }
                        gwtConfigs.add(gwtConfig);
                    }
                }
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }
        if (!gwtConfigs.isEmpty()) {
            Collections.sort(gwtConfigs, new Comparator<GwtConfigComponent>() {

                @Override
                public int compare(GwtConfigComponent o1, GwtConfigComponent o2) {
                    return o1.getComponentName().compareTo(o2.getComponentName());
                }
            });
        }
        return gwtConfigs;
    }

    private void checkIconResource(KapuaTicon icon) {
        ConsoleSetting config = ConsoleSetting.getInstance();

        String iconResource = icon.getResource();
        // Check if the resource is an HTTP URL or not
        if (iconResource != null &&
                (iconResource.toLowerCase().startsWith("http://") ||
                        iconResource.toLowerCase().startsWith("https://"))) {
            File tmpFile = null;

            try {
                LOG.info("Got configuration component icon from URL: {}", iconResource);
                // Tmp file name creation
                String systemTmpDir = System.getProperty("java.io.tmpdir");
                String iconResourcesTmpDir = config.getString(ConsoleSettingKeys.DEVICE_CONFIGURATION_ICON_FOLDER);
                String tmpFileName = Base64.encodeBase64String(MessageDigest.getInstance("MD5").digest(iconResource.getBytes("UTF-8")));

                // Conversions needed got security reasons!
                // On the file servlet we use the regex [0-9A-Za-z]{1,} to validate the given file id.
                // This validation prevents the caller of the file servlet to try to move out of the directory where the icons are stored.
                tmpFileName = tmpFileName.replaceAll("/", "a");
                tmpFileName = tmpFileName.replaceAll("\\+", "m");
                tmpFileName = tmpFileName.replaceAll("=", "z");
                // Tmp dir check and creation
                StringBuilder tmpDirPathSb = new StringBuilder().append(systemTmpDir);
                if (!systemTmpDir.endsWith("/")) {
                    tmpDirPathSb.append("/");
                }
                tmpDirPathSb.append(iconResourcesTmpDir);

                File tmpDir = new File(tmpDirPathSb.toString());
                if (!tmpDir.exists()) {
                    LOG.info("Creating tmp dir on path: {}", tmpDir);
                    tmpDir.mkdir();
                }
                // Tmp file check and creation
                tmpDirPathSb.append("/")
                        .append(tmpFileName);
                tmpFile = new File(tmpDirPathSb.toString());

                // Check date of modification to avoid caching forever
                if (tmpFile.exists()) {
                    long lastModifiedDate = tmpFile.lastModified();

                    long maxCacheTime = config.getLong(ConsoleSettingKeys.DEVICE_CONFIGURATION_ICON_CACHE_TIME);

                    if (System.currentTimeMillis() - lastModifiedDate > maxCacheTime) {
                        LOG.info("Deleting old cached file: {}", tmpFile);
                        tmpFile.delete();
                    }
                }

                // If file is not cached, download it.
                if (!tmpFile.exists()) {
                    // Url connection
                    URL iconUrl = new URL(iconResource);
                    URLConnection urlConnection = iconUrl.openConnection();
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.setReadTimeout(2000);

                    // Length check
                    String contentLengthString = urlConnection.getHeaderField("Content-Length");

                    long maxLength = config.getLong(ConsoleSettingKeys.DEVICE_CONFIGURATION_ICON_SIZE_MAX);

                    try {
                        Long contentLength = Long.parseLong(contentLengthString);
                        if (contentLength > maxLength) {
                            LOG.warn("Content lenght exceeded ({}/{}) for URL: {}", contentLength, maxLength, iconResource);
                            throw new IOException("Content-Length reported a length of " + contentLength + " which exceeds the maximum allowed size of " + maxLength);
                        }
                    } catch (NumberFormatException nfe) {
                        LOG.warn("Cannot get Content-Length header!");
                    }

                    LOG.info("Creating file: {}", tmpFile);
                    tmpFile.createNewFile();

                    // Icon download
                    InputStream is = urlConnection.getInputStream();
                    try {
                        byte[] buffer = new byte[4096];
                        OutputStream os = new FileOutputStream(tmpFile);
                        try {
                            int len;
                            while ((len = is.read(buffer)) > 0) {
                                os.write(buffer, 0, len);

                                maxLength -= len;

                                if (maxLength < 0) {
                                    LOG.warn("Maximum content lenght exceeded ({}) for URL: {}", maxLength, iconResource);
                                    throw new IOException("Maximum content lenght exceeded (" + maxLength + ") for URL: " + iconResource);
                                }
                            }
                        } finally {
                            os.close();
                        }
                    } finally {
                        is.close();
                    }

                    LOG.info("Downloaded file: {}", tmpFile);

                    // Image metadata content checks
                    ImageFormat imgFormat = Imaging.guessFormat(tmpFile);

                    if (imgFormat.equals(ImageFormats.BMP) ||
                            imgFormat.equals(ImageFormats.GIF) ||
                            imgFormat.equals(ImageFormats.JPEG) ||
                            imgFormat.equals(ImageFormats.PNG)) {
                        LOG.info("Detected image format: {}", imgFormat.getName());
                    } else if (imgFormat.equals(ImageFormats.UNKNOWN)) {
                        LOG.error("Unknown file format for URL: {}", iconResource);
                        throw new IOException("Unknown file format for URL: " + iconResource);
                    } else {
                        LOG.error("Usupported file format ({}) for URL: {}", imgFormat, iconResource);
                        throw new IOException("Unknown file format for URL: {}" + iconResource);
                    }

                    LOG.info("Image validation passed for URL: {}", iconResource);
                } else {
                    LOG.info("Using cached file: {}", tmpFile);
                }
                // Injecting new URL for the icon resource
                String newResourceURL = "img://console/file/icons?id=" +
                        tmpFileName;

                LOG.info("Injecting configuration component icon: {}", newResourceURL);
                icon.setResource(newResourceURL);
            } catch (Exception e) {
                if (tmpFile != null && tmpFile.exists()) {
                    tmpFile.delete();
                }

                icon.setResource("Default");

                LOG.error("Error while checking component configuration icon. Using the default plugin icon.", e);
            }
        }
        // If not, all is fine.
    }

    @Override
    public PagingLoadResult<GwtAccount> query(PagingLoadConfig loadConfig, GwtAccountQuery gwtAccountQuery) throws GwtKapuaException {

        int totalLength = 0;
        List<GwtAccount> gwtAccounts = new ArrayList<GwtAccount>();
        try {
            KapuaQuery query = GwtKapuaAccountModelConverter.convertAccountQuery(loadConfig, gwtAccountQuery);

            KapuaListResult<Account> accounts = ACCOUNT_SERVICE.query(query);
            totalLength = accounts.getTotalCount().intValue();

            if (!accounts.isEmpty()) {
                UserListResult usernames = KapuaSecurityUtils.doPrivileged(new Callable<UserListResult>() {

                    @Override
                    public UserListResult call() throws Exception {
                        return USER_SERVICE.query(new UserQuery(null));
                    }
                });

                Map<String, String> usernameMap = new HashMap<String, String>();
                for (User user : usernames.getItems()) {
                    usernameMap.put(user.getId().toCompactId(), user.getName());
                }

                for (Account a : accounts.getItems()) {
                    GwtAccount gwtAccount = KapuaGwtAccountModelConverter.convertAccount(a);
                    gwtAccount.setCreatedByName(usernameMap.get(gwtAccount.getCreatedBy()));
                    gwtAccount.setModifiedByName(usernameMap.get(gwtAccount.getModifiedBy()));
                    gwtAccounts.add(gwtAccount);
                }
            }
        } catch (Throwable t) {
            KapuaExceptionHandler.handle(t);
        }

        return new BasePagingLoadResult<GwtAccount>(gwtAccounts, loadConfig.getOffset(), totalLength);
    }

}
