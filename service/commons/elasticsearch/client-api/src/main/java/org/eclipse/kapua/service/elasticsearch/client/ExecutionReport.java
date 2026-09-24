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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class ExecutionReport {

    private int accountCount;
    private int accountProcessed;
    private int accountErrored;
    private int indexedDeleted;

    private String currentAccount;

    private Logger log;

    private int maxExecutionTimeSeconds;

    private String jobName;

    Function<String, KapuaException> jobTimeoutException;

    public ExecutionReport(Logger logger, int maxExecTimeSeconds, String jobName, Function<String, KapuaException> jobTimeoutException) {
        this.log = logger;
        this.maxExecutionTimeSeconds = maxExecTimeSeconds;
        this.jobName = jobName;
        this.jobTimeoutException = jobTimeoutException;
    }

    public void incAccountProcessed() {
        accountProcessed++;
    }

    public void incAccountErrored() {
        accountErrored++;
    }

    public void incIndexedDeleted(int indexedDeleted) {
        this.indexedDeleted += indexedDeleted;
    }

    public void setAccountCount(int accountCount) {
        this.accountCount = accountCount;
    }

    public void logStartReport() {
        log.info(jobName + "... Found '{}' accounts to be processed", accountCount);
    }

    public void startAccountProcessing(String accountName) {
        currentAccount = accountName;
        log.info(jobName + "... Processing account '{}'", currentAccount);
    }

    public void logAccountTtl(Integer dataTtl, ChronoUnit chronoUnit) {
        log.info(jobName + "... Found TTL '{}' [{}] for account '{}'", (dataTtl != null ? dataTtl : -1), chronoUnit, currentAccount);
    }

    public void checkTimeout(Instant startTime) throws KapuaException {
        long executionTime = Duration.between(startTime, KapuaDateUtils.getKapuaSysDate()).get(ChronoUnit.SECONDS);
        if (executionTime >= maxExecutionTimeSeconds) {
            String message = String.format("Execution time: '%d' [minutes] - limit '%d' [minutes]! The job will be interrupted!", executionTime, maxExecutionTimeSeconds / 60);
            log.warn(message);
            logAborted();
            throw this.jobTimeoutException.apply(message);
        }
    }

    public void endAccountProcessing() {
        incAccountProcessed();
        log.info(jobName + "... Processing account '{}' DONE", currentAccount);
        currentAccount = null;
    }

    public void logEndReport() {
        log.info(jobName + "... DONE. Account processed '{}' - Errored account processing '{}' -Indexes deleted '{}'", accountProcessed, accountErrored, indexedDeleted);
    }

    public void logAborted() {
        log.warn(jobName + "... ABORTED. Account processed '{}' - Indexes deleted '{}'", accountProcessed, indexedDeleted);
    }

    public void customInfoLog(String toLog) {
        log.info(jobName + "... " + toLog);
    }


}
