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
package org.eclipse.kapua.service.datastore.internal.mediator;

import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.service.datastore.internal.model.DataIndexBy;
import org.eclipse.kapua.service.datastore.internal.model.MessageUniquenessCheck;
import org.eclipse.kapua.service.datastore.internal.model.metric.MetricsIndexBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * Message store configuration parameters (user dependent)
 *
 * @since 1.0
 */
public class MessageStoreConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MessageStoreConfiguration.class);

    /**
     * Expiration date key.<br>
     * <b>The key must be aligned with the key used in org.eclipse.kapua.service.datastore.MessageStoreService.xml meta data configuration file).</b>
     */
    public static final String CONFIGURATION_EXPIRATION_DATE_KEY = "messageStore.expirationDate";

    /**
     * Data storage enabled key.<br>
     * <b>The key must be aligned with the key used in org.eclipse.kapua.service.datastore.MessageStoreService.xml meta data configuration file).</b>
     */
    public static final String CONFIGURATION_DATA_STORAGE_ENABLED_KEY = "enabled";

    /**
     * Data time to live key.<br>
     * <b>The key must be aligned with the key used in org.eclipse.kapua.service.datastore.MessageStoreService.xml meta data configuration file).</b>
     */
    public static final String CONFIGURATION_DATA_TTL_KEY = "dataTTL";

    /**
     * Data received per month limit key.<br>
     * <b>The key must be aligned with the key used in org.eclipse.kapua.service.datastore.MessageStoreService.xml meta data configuration file).</b>
     */
    public static final String CONFIGURATION_RX_BYTE_LIMIT_KEY = "rxByteLimit";

    /**
     * Data index by key (available options are in DataIndexBy enumeration).<br>
     * <b>The key must be aligned with the key used in org.eclipse.kapua.service.datastore.MessageStoreService.xml meta data configuration file).</b>
     */
    public static final String CONFIGURATION_DATA_INDEX_BY_KEY = "dataIndexBy";

    /**
     * Metrics index by key (available options are in MetricsIndexBy enumeration).<br>
     * <b>The key must be aligned with the key used in org.eclipse.kapua.service.datastore.MessageStoreService.xml meta data configuration file).</b>
     */
    public static final String CONFIGURATION_METRICS_INDEX_BY_KEY = "metricsIndexBy";

    /**
     * Message Uniqueness Check key (available options are in MessageUniquenessCheck enumeration).<br>
     * <b>The key must be aligned with the key used in org.eclipse.kapua.service.datastore.MessageStoreService.xml meta data configuration file).</b>
     */
    public static final String CONFIGURATION_MESSAGE_UNIQUENESS_CHECK = "messageUniquenessCheck";

    /**
     * Defines a value in service plan as unlimited resource
     */
    public static final int UNLIMITED = -1;

    /**
     * Defines a value in service plan as disabled resource
     */
    public static final int DISABLED = 0;

    private static final Duration TTL_DEFAULT_DAYS = Duration.ofDays(30);                                         // TODO define as a default configuration

    private Date expirationDate;
    private boolean dataStorageEnabled = true;
    private Duration dataTimeToLive = Duration.ofDays(90);
    private long rxByteLimit = 1000000;
    private DataIndexBy dataIndexBy = DataIndexBy.SERVER_TIMESTAMP;
    private MetricsIndexBy metricsIndexBy = MetricsIndexBy.TIMESTAMP;
    private MessageUniquenessCheck messageUniquenessCheck;

    private Map<String, Object> values;

    /**
     * Construct a new {@link MessageStoreConfiguration} with the given values
     *
     * @param values
     */
    public MessageStoreConfiguration(Map<String, Object> values) {
        this.values = values;
        if (this.values != null) {
            if (this.values.get(CONFIGURATION_EXPIRATION_DATE_KEY) != null) {
                try {
                    setExpirationDate(KapuaDateUtils.parseDate((String) this.values.get(CONFIGURATION_EXPIRATION_DATE_KEY)));
                } catch (ParseException e) {
                    logger.error("Cannot parse the expiration date parameter: {}", e.getMessage(), e);
                }
            }
            if (this.values.get(CONFIGURATION_DATA_STORAGE_ENABLED_KEY) != null) {
                setDataStorageEnabled((Boolean) this.values.get(CONFIGURATION_DATA_STORAGE_ENABLED_KEY));
            }
            if (this.values.get(CONFIGURATION_DATA_TTL_KEY) != null) {
                setDataTimeToLive((Integer) this.values.get(CONFIGURATION_DATA_TTL_KEY));
            }
            if (this.values.get(CONFIGURATION_RX_BYTE_LIMIT_KEY) != null) {
                setRxByteLimit((Long) this.values.get(CONFIGURATION_RX_BYTE_LIMIT_KEY));
            }
            if (this.values.get(CONFIGURATION_DATA_INDEX_BY_KEY) != null) {
                setDataIndexBy(DataIndexBy.valueOf((String) this.values.get(CONFIGURATION_DATA_INDEX_BY_KEY)));
            }
            if (this.values.get(CONFIGURATION_METRICS_INDEX_BY_KEY) != null) {
                setMetricsIndexBy(MetricsIndexBy.valueOf((String) this.values.get(CONFIGURATION_METRICS_INDEX_BY_KEY)));
            }
            if (this.values.get(CONFIGURATION_MESSAGE_UNIQUENESS_CHECK) != null) {
                setMessageUniquenessCheck(MessageUniquenessCheck.valueOf((String) this.values.get(CONFIGURATION_MESSAGE_UNIQUENESS_CHECK)));
            }
        }
    }

    /**
     * Get the expiration date parameter ({@link MessageStoreConfiguration#CONFIGURATION_EXPIRATION_DATE_KEY}
     *
     * @return
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Set the expiration date parameter ({@link MessageStoreConfiguration#CONFIGURATION_EXPIRATION_DATE_KEY}
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Get the data storage enabled parameter ({@link MessageStoreConfiguration#CONFIGURATION_DATA_STORAGE_ENABLED_KEY}
     *
     * @return
     */
    public boolean getDataStorageEnabled() {
        return dataStorageEnabled;
    }

    /**
     * Set the data storage enabled parameter ({@link MessageStoreConfiguration#CONFIGURATION_DATA_STORAGE_ENABLED_KEY}
     */
    public void setDataStorageEnabled(boolean dataStorageEnabled) {
        this.dataStorageEnabled = dataStorageEnabled;
    }

    /**
     * Get the data time to live in millisecond parameter ({@link MessageStoreConfiguration#CONFIGURATION_DATA_TTL_KEY}
     *
     * @return
     */
    public long getDataTimeToLiveMilliseconds() {
        return dataTimeToLive.toMillis();
    }

    /**
     * Get the data time to live parameter ({@link MessageStoreConfiguration#CONFIGURATION_DATA_TTL_KEY}
     *
     * @return
     */
    public long getDataTimeToLive() {
        return dataTimeToLive.toDays();
    }

    /**
     * Set the data time to live parameter ({@link MessageStoreConfiguration#CONFIGURATION_DATA_TTL_KEY}
     */
    public void setDataTimeToLive(int dataTimeToLive) {
        if (dataTimeToLive < 0) {
            this.dataTimeToLive = TTL_DEFAULT_DAYS;
        } else {
            this.dataTimeToLive = Duration.ofDays(dataTimeToLive);
        }
    }

    /**
     * Get the rx byte limit parameter ({@link MessageStoreConfiguration#CONFIGURATION_RX_BYTE_LIMIT_KEY}
     *
     * @return
     */
    public long getRxByteLimit() {
        return rxByteLimit;
    }

    /**
     * Set the rx byte limit parameter ({@link MessageStoreConfiguration#CONFIGURATION_RX_BYTE_LIMIT_KEY}
     */
    public void setRxByteLimit(long rxByteLimit) {
        this.rxByteLimit = rxByteLimit;
    }

    /**
     * Get the data index by parameter ({@link MessageStoreConfiguration#CONFIGURATION_DATA_INDEX_BY_KEY}
     *
     * @return
     */
    public DataIndexBy getDataIndexBy() {
        return dataIndexBy;
    }

    /**
     * Set the data index by parameter ({@link MessageStoreConfiguration#CONFIGURATION_DATA_INDEX_BY_KEY}
     */
    public void setDataIndexBy(DataIndexBy dataIndexBy) {
        this.dataIndexBy = dataIndexBy;
    }

    /**
     * Get the metrics index by parameter ({@link MessageStoreConfiguration#CONFIGURATION_METRICS_INDEX_BY_KEY}
     *
     * @return
     */
    public MetricsIndexBy getMetricsIndexBy() {
        return metricsIndexBy;
    }

    /**
     * Set the metrics index by date parameter ({@link MessageStoreConfiguration#CONFIGURATION_METRICS_INDEX_BY_KEY}
     */
    public void setMetricsIndexBy(MetricsIndexBy metricsIndexBy) {
        this.metricsIndexBy = metricsIndexBy;
    }

    /**
     * Get the message uniqueness check parameter ({@link MessageStoreConfiguration#CONFIGURATION_MESSAGE_UNIQUENESS_CHECK}
     * @return
     */
    public MessageUniquenessCheck getMessageUniquenessCheck() {
        return messageUniquenessCheck;
    }

    /**
     * Set the message uniqueness check parameter ({@link MessageStoreConfiguration#CONFIGURATION_MESSAGE_UNIQUENESS_CHECK}
     */
    public void setMessageUniquenessCheck(MessageUniquenessCheck messageUniquenessCheck) {
        this.messageUniquenessCheck = messageUniquenessCheck;
    }

}
