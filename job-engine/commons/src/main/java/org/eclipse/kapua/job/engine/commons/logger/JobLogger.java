/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.commons.logger;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.type.DateConverter;
import org.eclipse.kapua.service.job.execution.JobExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Logger for {@link org.eclipse.kapua.service.job.Job} processing.
 * <p>
 * This {@link JobLogger} logs to the standard {@link Logger} of the current Java class (after setting it using {@link #setClassLog(Logger)})
 * and keeps a copy of the log inside. Then, by invoking {@link #flush()} method it is possible to retrieve the copy of the log and store it
 * into the {@link org.eclipse.kapua.service.job.execution.JobExecution#setLog(String)}.
 *
 * @since 1.1.0
 */
public class JobLogger {

    private static final Logger LOG = LoggerFactory.getLogger(JobLogger.class);

    private static final String LF = "\n";

    private static final String PRE_STD_LOG_FORMAT_SCOPE_ID = "ScopeId: {} - ";
    private static final String PRE_STD_LOG_FORMAT_JOB_ID = "JobId: {} - ";
    private static final String PRE_STD_LOG_FORMAT_JOB_NAME = "JobName: {} - ";
    private static final String PRE_STD_LOG_FORMAT_EXECUTION_ID = "ExecutionId: {} - ";

    private static final String PRE_EXEC_LOG_FORMAT_LEVEL_INFO = "[INFO] ";
    private static final String PRE_EXEC_LOG_FORMAT_LEVEL_WARN = "[WARN] ";
    private static final String PRE_EXEC_LOG_FORMAT_LEVEL_ERROR = "[ERROR] ";
    private static final String PRE_EXEC_LOG_FORMAT_DATE = "{} - ";
    private static final String POST_EXEC_LOG_FORMAT_ERROR = " {}";

    private static final String CANNOT_LOG_THIS_LINE = "Cannot log this line: ";

    private Logger containerClassLog;

    private StringBuilder logSb = new StringBuilder();

    private KapuaId scopeId;
    private KapuaId jobId;
    private KapuaId jobExecutionId;
    private String jobName;

    /**
     * Initialize the mandatory info of the {@link JobExecution} to use when logging.
     *
     * @param scopeId The current {@link JobExecution#getScopeId()}
     * @param jobId   The current {@link JobExecution#getJobId()}
     * @param jobName The current jBatch Job Name.
     * @since 1.1.0
     */
    public JobLogger(KapuaId scopeId, KapuaId jobId, String jobName) {
        this.scopeId = scopeId;
        this.jobId = jobId;
        this.jobName = jobName;
    }

    /**
     * Sets the {@link JobExecution#getId()} into the {@link JobLogger} to be printed into the {@link Logger}.
     *
     * @param jobExecutionId The current {@link JobExecution#getId()}
     * @since 1.1.0
     */
    public void setJobExecutionId(KapuaId jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    /**
     * Sets the {@link Logger} of the class that is invoking this {@link JobLogger}.
     * <p>
     * The current set {@link Logger} will be used to print to the standard log of the application
     *
     * @param classLog The {@link Logger} to use to print log.
     * @since 1.1.0
     */
    public void setClassLog(Logger classLog) {
        this.containerClassLog = classLog;
    }

    /**
     * Logs a log line of {@link Level#INFO}.
     *
     * @param message The {@link String} to log.
     * @since 1.1.0
     */
    public void info(String message) {
        info(message, Collections.emptyList().toArray());
    }

    /**
     * Logs a log line of {@link Level#INFO} with the given arguments.
     *
     * @param format    The {@link String} format for the log line.
     * @param arguments The {@link java.util.Objects}... to populate the given format.
     * @since 1.1.0
     */
    public void info(String format, Object... arguments) {

        checkFormatAndArguments(format, arguments);

        try {
            //
            // Standard Logging
            if (containerClassLog.isInfoEnabled()) {
                StringBuilder formatSb = new StringBuilder();
                List<Object> finalArguments = new ArrayList<>();

                buildStdLogFormatArguments(format, arguments, formatSb, finalArguments);

                containerClassLog.info(formatSb.toString(), finalArguments.toArray());
            }

            //
            // Job Execution Logging
            StringBuilder formatSb = new StringBuilder();
            formatSb.append(PRE_EXEC_LOG_FORMAT_LEVEL_INFO);
            formatSb.append(PRE_EXEC_LOG_FORMAT_DATE);
            formatSb.append(format);

            List<Object> finalArguments = new ArrayList<>();
            finalArguments.add(formatDate(new Date()));
            finalArguments.addAll(Arrays.asList(arguments));

            tokenizeFormat(formatSb);

            logSb.append(MessageFormat.format(formatSb.toString(), finalArguments.toArray())).append(LF);
        } catch (Exception e) {
            LOG.error(CANNOT_LOG_THIS_LINE + format, e);
        }
    }

    /**
     * Logs a log line of {@link Level#WARN}.
     *
     * @param message The {@link String} to log.
     * @since 1.1.0
     */
    public void warn(String message) {
        warn(null, message);
    }

    /**
     * Logs a log line of {@link Level#WARN}.
     *
     * @param message   The {@link String} to log.
     * @param arguments The {@link java.util.Objects}... to populate the given format.
     */
    public void warn(String message, Object... arguments) {
        warn(null, message, arguments);
    }

    /**
     * Logs a log line of {@link Level#WARN} with the relative {@link Exception}.
     * <p>
     * Into the {@link JobExecution} log only {@link Exception#getMessage()} will be logged.
     *
     * @param exception The {@link Exception} to log.
     * @param message   The {@link String} to log.
     * @since 1.1.0
     */
    public void warn(Exception exception, String message) {
        warn(exception, message, Collections.emptyList().toArray());
    }

    /**
     * Logs a log line of {@link Level#WARN} with the relative {@link Exception} with the given arguments.
     * <p>
     * Into the {@link JobExecution} log only {@link Exception#getMessage()} will be logged.
     *
     * @param exception The {@link Exception} to log.
     * @param format    The {@link String} to log.
     * @param arguments The {@link java.util.Objects}... to populate the given format.
     * @since 1.1.0
     */
    public void warn(Exception exception, String format, Object... arguments) {

        checkFormatAndArguments(format, arguments);

        try {
            //
            // Standard Logging
            if (containerClassLog.isErrorEnabled()) {
                StringBuilder formatSb = new StringBuilder();
                List<Object> finalArguments = new ArrayList<>();

                buildStdLogFormatArguments(format, arguments, formatSb, finalArguments);

                tokenizeFormat(formatSb);

                containerClassLog.warn(MessageFormat.format(formatSb.toString(), finalArguments.toArray()), exception);
            }

            //
            // Job Execution Logging
            StringBuilder formatSb = new StringBuilder();
            formatSb.append(PRE_EXEC_LOG_FORMAT_LEVEL_WARN);
            formatSb.append(PRE_EXEC_LOG_FORMAT_DATE);
            formatSb.append(format);

            List<Object> finalArguments = new ArrayList<>();
            finalArguments.add(formatDate(new Date()));
            finalArguments.addAll(Arrays.asList(arguments));

            if (exception != null) {
                formatSb.append(POST_EXEC_LOG_FORMAT_ERROR);
                finalArguments.add(exception.getMessage());
            }

            tokenizeFormat(formatSb);

            logSb.append(MessageFormat.format(formatSb.toString(), finalArguments.toArray())).append(LF);
        } catch (Exception e) {
            LOG.error(CANNOT_LOG_THIS_LINE + format, e);
        }
    }

    /**
     * Logs a log line of {@link Level#ERROR}.
     *
     * @param message The {@link String} to log.
     * @since 1.1.0
     */
    public void error(String message) {
        error(null, message);
    }

    /**
     * Logs a log line of {@link Level#ERROR}.
     *
     * @param message   The {@link String} to log.
     * @param arguments The {@link java.util.Objects}... to populate the given format.
     */
    public void error(String message, Object... arguments) {
        error(null, message, arguments);
    }

    /**
     * Logs a log line of {@link Level#ERROR} with the relative {@link Exception}.
     * <p>
     * Into the {@link JobExecution} log only {@link Exception#getMessage()} will be logged.
     *
     * @param exception The {@link Exception} to log.
     * @param message   The {@link String} to log.
     * @since 1.1.0
     */
    public void error(Exception exception, String message) {
        error(exception, message, Collections.emptyList().toArray());
    }

    /**
     * Logs a log line of {@link Level#ERROR} with the relative {@link Exception} with the given arguments.
     * <p>
     * Into the {@link JobExecution} log only {@link Exception#getMessage()} will be logged.
     *
     * @param exception The {@link Exception} to log.
     * @param format    The {@link String} to log.
     * @param arguments The {@link java.util.Objects}... to populate the given format.
     * @since 1.1.0
     */
    public void error(Exception exception, String format, Object... arguments) {

        checkFormatAndArguments(format, arguments);

        try {
            //
            // Standard Logging
            if (containerClassLog.isErrorEnabled()) {
                StringBuilder formatSb = new StringBuilder();
                List<Object> finalArguments = new ArrayList<>();

                buildStdLogFormatArguments(format, arguments, formatSb, finalArguments);

                tokenizeFormat(formatSb);

                containerClassLog.error(MessageFormat.format(formatSb.toString(), finalArguments.toArray()), exception);
            }

            //
            // Job Execution Logging
            StringBuilder formatSb = new StringBuilder();
            formatSb.append(PRE_EXEC_LOG_FORMAT_LEVEL_ERROR);
            formatSb.append(PRE_EXEC_LOG_FORMAT_DATE);
            formatSb.append(format);

            List<Object> finalArguments = new ArrayList<>();
            finalArguments.add(formatDate(new Date()));
            finalArguments.addAll(Arrays.asList(arguments));

            if (exception != null) {
                formatSb.append(POST_EXEC_LOG_FORMAT_ERROR);
                finalArguments.add(exception.getMessage());
            }

            tokenizeFormat(formatSb);

            logSb.append(MessageFormat.format(formatSb.toString(), finalArguments.toArray())).append(LF);
        } catch (Exception e) {
            LOG.error(CANNOT_LOG_THIS_LINE + format, e);
        }
    }

    /**
     * Returns all the log stored into {@code this} JobLogger and clears the current content.
     *
     * @return The current stored log.
     * @since 1.1.0
     */
    public synchronized String flush() {
        String log = logSb.toString();

        logSb = new StringBuilder();

        return log;
    }


    //
    // Private methods
    //

    /**
     * Checks that the number of placeholders in the given format matches the number of arguments given.
     * If they do not match, a {@link Logger#warn(String)} is printed in the {@link JobLogger#LOG}.
     * <p>
     * Counts occurrences of {@code {}} in the given {@code format} parameter and
     * matches with the {@code length} of the {@code arguments} parameter.
     *
     * @param format    The {@link String} format to check.
     * @param arguments The {@link List} of arguments to check.
     * @since 1.1.0
     */
    private void checkFormatAndArguments(String format, Object... arguments) {
        if (StringUtils.countMatches(format, "{}") != arguments.length) {
            LOG.warn("Format string tokens do not match number of arguments");
        }
    }

    /**
     * Build the log line for the standard log of the application.
     * <p>
     * It prepends some {@link org.eclipse.kapua.service.job.Job} info to the log line,
     * appends the given {@link String} format, building also the {@link List} of argument to pass to the {@link Logger}
     *
     * @param format         The user-provided {@link String} format to log.
     * @param arguments      The user-provided {@link List} of arguments to log.
     * @param formatSb       The {@link StringBuilder} to populate, it must be empty.
     * @param finalArguments The {@link List} to populate, it must be empty.
     * @since 1.1.0
     */
    private void buildStdLogFormatArguments(String format, Object[] arguments, StringBuilder formatSb, List<Object> finalArguments) {

        formatSb.append(PRE_STD_LOG_FORMAT_SCOPE_ID);
        formatSb.append(PRE_STD_LOG_FORMAT_JOB_ID);

        finalArguments.add(scopeId);
        finalArguments.add(jobId);

        if (!Strings.isNullOrEmpty(jobName)) {
            formatSb.append(PRE_STD_LOG_FORMAT_JOB_NAME);
            finalArguments.add(jobName);
        }

        if (jobExecutionId != null) {
            formatSb.append(PRE_STD_LOG_FORMAT_EXECUTION_ID);
            finalArguments.add(jobExecutionId);
        }

        formatSb.append(format);
        finalArguments.addAll(Arrays.asList(arguments));
    }

    /**
     * Adapts the {@link String} format required by {@link Logger} to the {@link String} format required by {@link MessageFormat#format(String, Object...)}
     * <p>
     * Example format for {@link Logger}:
     * "This is a log line: {}"
     * Example adaptation for {@link MessageFormat#format(String, Object...)}:
     * "This is a log line: {0}"
     * </p>
     *
     * @param formatSb The {@link String} format to adapt.
     * @since 1.1.0
     */
    private void tokenizeFormat(StringBuilder formatSb) {
        int i = 0;
        int offset;
        while (formatSb.indexOf("{}") != -1) {
            offset = formatSb.indexOf("{}");
            formatSb.insert(offset + 1, i++);
        }
    }

    private String formatDate(Date date) {
        return DateConverter.toString(date);
    }
}
