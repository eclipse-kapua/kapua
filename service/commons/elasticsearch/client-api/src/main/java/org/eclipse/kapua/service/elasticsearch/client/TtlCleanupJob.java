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
package org.eclipse.kapua.service.elasticsearch.client;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.eclipse.kapua.commons.setting.system.SystemSettingKey;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountFactory;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountQuery;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.elasticsearch.client.model.IndexRequest;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for jobs that clean indexes older that a fixed TTL
 * It is meant to be extended for jobs that clean a specific *-store part of the datastore
 *
 * @since 6.0.0
 */

public abstract class TtlCleanupJob implements Job {

    protected final KapuaLocator locator = KapuaLocator.getInstance();
    protected final AccountService accountService = locator.getService(AccountService.class);
    protected final AccountFactory accountFactory = locator.getFactory(AccountFactory.class);

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    // Required by Quartz
    public TtlCleanupJob() {
        super();
    }

    @Override
    public void execute(JobExecutionContext context) {
        ExecutionReport executionReport = initializeExecutionReport(log);
        try {
            if (isCleanupJobEnabled()) {
                executeAllInternal(executionReport);
            } else {
                executionReport.customInfoLog(" Skip execution. Plugin disabled by configuration!");
            }
        } catch (Exception e) {
            log.error("Cleanup job... Error: {}", e.getMessage(), e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private void executeAllInternal(ExecutionReport executionReport) throws KapuaException {
        executionReport.customInfoLog(" Getting account list");

        Account adminAccount = getAdminAccount();

        AccountQuery accountListQuery = accountFactory.newQuery(adminAccount.getId());
        accountListQuery.setLimit(1000);
        accountListQuery.setOffset(0);

        AccountListResult accountListResult = KapuaSecurityUtils.doPrivileged(() -> accountService.findChildrenRecursively(adminAccount.getId()));

        List<Account> accountList = new ArrayList<>();
        accountList.add(adminAccount);
        accountList.addAll(accountListResult.getItems());

        executionReport.setAccountCount(accountList.size());
        executionReport.logStartReport();
        for (Account account : accountList) {
            executeInternal(account, executionReport);
        }
        executionReport.logEndReport();
    }

    private void executeInternal(Account account, ExecutionReport executionReport) throws KapuaException {
        Instant startTime = KapuaDateUtils.getKapuaSysDate();

        String accountName = account.getName();
        try {
            executionReport.startAccountProcessing(accountName);
            KapuaId scopeId = account.getId();
            Integer dataTtl = getIndexesTtl(scopeId);
            Instant endInstant = KapuaDateUtils.getKapuaSysDate().minus(dataTtl, ChronoUnit.DAYS);
            ElasticsearchClient elasticsearchClient = locator.getComponent(ElasticsearchClientProvider.class).getElasticsearchClient();
            AbstractStoreUtils utilsClass = getUtilsClassForThisJob();

            String index = getIndexName(scopeId);
            executionReport.customInfoLog(String.format(" Looking index for %s", index));
            String[] indexes = elasticsearchClient.findIndexes(new IndexRequest(index)).getIndexes();
            if (indexes != null && (indexes.length > 1 || (indexes.length == 1 && !StringUtils.isEmpty(indexes[0])))) {
                String[] filteredList = utilsClass.filterIndexesTemporalWindow(indexes, null, endInstant, scopeId);
                executionReport.customInfoLog(String.format(" Processing account %s Found %s indexes to be deleted", accountName, filteredList.length));
                Arrays.sort(filteredList);
                if (filteredList.length > 0) {
                    KapuaSecurityUtils.doPrivileged(() -> elasticsearchClient.deleteIndexes(filteredList));
                    executionReport.incIndexedDeleted(filteredList.length);
                    executionReport.customInfoLog(" Deleted indexes:");
                    for (String str : filteredList) {
                        executionReport.customInfoLog(String.format("\t%s", str));
                    }
                }
            } else {
                executionReport.customInfoLog(String.format(" Processing account %s - no index to be deleted found", accountName));
            }
            executionReport.endAccountProcessing();
        } catch (KapuaException e) {
            executionReport.incAccountErrored();
            log.error("Cleanup job... error processing account '{}' (Error: {}). Proceed with the next one account", accountName, e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        executionReport.checkTimeout(startTime);
    }

    protected Account getAdminAccount() throws KapuaException {
        return KapuaSecurityUtils.doPrivileged(() -> accountService.findByName(SystemSetting.getInstance().getString(SystemSettingKey.SYS_ADMIN_ACCOUNT)));
    }

    protected abstract ExecutionReport initializeExecutionReport(Logger l);

    protected abstract boolean isCleanupJobEnabled();

    protected abstract Integer getIndexesTtl(KapuaId scopeId) throws KapuaException;

    /**
     * Returns the respective DatastoreUtils class (or subclasses) for this cleanup job (depending on the type of *-store that the job cleans)
     */
    protected abstract AbstractStoreUtils getUtilsClassForThisJob();

    /** returns the "abstract" format of the index depending on the concrete type of *storeUtils */
    protected abstract String getIndexName(KapuaId scopeId);

}
