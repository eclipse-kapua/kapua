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

import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.model.type.ByteArrayConverter;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettings;
import org.eclipse.kapua.service.datastore.internal.setting.DatastoreSettingsKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Datastore utility class
 *
 * @since 1.0.0
 */
public class DatastoreUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DatastoreUtils.class);

    private enum IndexType { CHANNEL, CLIENT, METRIC }

    private DatastoreUtils() {
    }

    private static final char SPECIAL_DOT = '.';
    private static final String SPECIAL_DOT_ESC = "$2e";

    private static final char SPECIAL_DOLLAR = '$';
    private static final String SPECIAL_DOLLAR_ESC = "$24";

    private static final String UNKNOWN_TYPE = "Unknown type [%s]";
    private static final String TYPE_CANNOT_BE_CONVERTED = "Type [%s] cannot be converted to Double!";

    public static final CharSequence ILLEGAL_CHARS = "\"\\/*?<>|,. ";

    public static final String CLIENT_METRIC_TYPE_STRING = "string";
    public static final String CLIENT_METRIC_TYPE_INTEGER = "integer";
    public static final String CLIENT_METRIC_TYPE_LONG = "long";
    public static final String CLIENT_METRIC_TYPE_FLOAT = "float";
    public static final String CLIENT_METRIC_TYPE_DOUBLE = "double";
    public static final String CLIENT_METRIC_TYPE_DATE = "date";
    public static final String CLIENT_METRIC_TYPE_BOOLEAN = "boolean";
    public static final String CLIENT_METRIC_TYPE_BINARY = "binary";

    public static final String CLIENT_METRIC_TYPE_STRING_ACRONYM = "str";
    public static final String CLIENT_METRIC_TYPE_INTEGER_ACRONYM = "int";
    public static final String CLIENT_METRIC_TYPE_LONG_ACRONYM = "lng";
    public static final String CLIENT_METRIC_TYPE_FLOAT_ACRONYM = "flt";
    public static final String CLIENT_METRIC_TYPE_DOUBLE_ACRONYM = "dbl";
    public static final String CLIENT_METRIC_TYPE_DATE_ACRONYM = "dte";
    public static final String CLIENT_METRIC_TYPE_BOOLEAN_ACRONYM = "bln";
    public static final String CLIENT_METRIC_TYPE_BINARY_ACRONYM = "bin";

    public static final String INDEXING_WINDOW_OPTION_WEEK = "week";
    public static final String INDEXING_WINDOW_OPTION_DAY = "day";
    public static final String INDEXING_WINDOW_OPTION_HOUR = "hour";

    public static final String DATASTORE_DATE_FORMAT = "8" + KapuaDateUtils.ISO_DATE_PATTERN; // example 2017-01-24T11:22:10.999Z

    private static final DateTimeFormatter DATA_INDEX_FORMATTER_WEEK = new DateTimeFormatterBuilder()
            .parseDefaulting(WeekFields.ISO.dayOfWeek(), 1)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .appendPattern("YYYY-ww")
            .toFormatter(KapuaDateUtils.getLocale())
            .withLocale(KapuaDateUtils.getLocale())
            .withResolverStyle(ResolverStyle.STRICT)
            .withZone(KapuaDateUtils.getTimeZone());
    private static final DateTimeFormatter DATA_INDEX_FORMATTER_DAY = new DateTimeFormatterBuilder()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .appendPattern("YYYY-ww-ee")
            .toFormatter(KapuaDateUtils.getLocale())
            .withLocale(KapuaDateUtils.getLocale())
            .withResolverStyle(ResolverStyle.STRICT)
            .withZone(KapuaDateUtils.getTimeZone());
    private static final DateTimeFormatter DATA_INDEX_FORMATTER_HOUR = new DateTimeFormatterBuilder()
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .appendPattern("YYYY-ww-ee-HH")
            .toFormatter(KapuaDateUtils.getLocale())
            .withLocale(KapuaDateUtils.getLocale())
            .withResolverStyle(ResolverStyle.STRICT)
            .withZone(KapuaDateUtils.getTimeZone());

    /**
     * Return the hash code for the provided components (typically components are a sequence of account - client id - channel ...)
     *
     * @param components
     * @return
     */
    public static String getHashCode(String... components) {
        String concatString = "";
        for (String str : components) {
            concatString = concatString.concat(str);
        }

        byte[] hashCode = Hashing.sha256()
                .hashString(concatString, StandardCharsets.UTF_8)
                .asBytes();

        // ES 5.2 FIX
        // return Base64.encodeBytes(hashCode);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashCode);
    }

    private static String normalizeIndexName(String name) {
        String normName = null;
        try {
            DatastoreUtils.checkIdxAliasName(name);
            normName = name;
        } catch (IllegalArgumentException exc) {
            LOG.trace(exc.getMessage(), exc);
            normName = name.toLowerCase().replace(ILLEGAL_CHARS, "_");
            DatastoreUtils.checkIdxAliasName(normName);
        }
        return normName;
    }

    /**
     * Normalize the metric name to be compliant to Kapua/Elasticserach constraints.<br>
     * It escapes the '$' and '.'
     *
     * @param name
     * @return The normalized metric name
     * @since 1.0.0
     */
    public static String normalizeMetricName(String name) {
        String newName = name;
        if (newName.contains(".")) {
            newName = newName.replace(String.valueOf(SPECIAL_DOLLAR), SPECIAL_DOLLAR_ESC);
            newName = newName.replace(String.valueOf(SPECIAL_DOT), SPECIAL_DOT_ESC);
            LOG.trace(String.format("Metric %s contains a special char '%s' that will be replaced with '%s'", name, String.valueOf(SPECIAL_DOT), SPECIAL_DOT_ESC));
        }
        return newName;
    }

    /**
     * Restore the metric name, so switch back to the 'not escaped' values for '$' and '.'
     *
     * @param normalizedName
     * @return The restored metric name
     * @since 1.0.0
     */
    public static String restoreMetricName(String normalizedName) {
        String oldName = normalizedName;
        oldName = oldName.replace(SPECIAL_DOT_ESC, String.valueOf(SPECIAL_DOT));
        oldName = oldName.replace(SPECIAL_DOLLAR_ESC, String.valueOf(SPECIAL_DOLLAR));
        return oldName;
    }

    /**
     * Return the metric parts for the composed metric name (split the metric name by '.')
     *
     * @param fullName
     * @return
     */
    public static String[] getMetricParts(String fullName) {
        return fullName == null ? null : fullName.split(Pattern.quote("."));
    }

    /**
     * Check the index alias correctness.<br>
     * The alias cnnot be null, starts with '_', contains uppercase character or contains {@link DatastoreUtils#ILLEGAL_CHARS}
     *
     * @param alias
     * @since 1.0.0
     */
    public static void checkIdxAliasName(String alias) {
        if (alias == null || alias.isEmpty()) {
            throw new IllegalArgumentException(String.format("Alias name cannot be %s", alias == null ? "null" : "empty"));
        }
        if (alias.startsWith("_")) {
            throw new IllegalArgumentException(String.format("Alias name cannot start with _"));
        }
        for (int i = 0; i < alias.length(); i++) {
            if (Character.isUpperCase(alias.charAt(i))) {
                throw new IllegalArgumentException(String.format("Alias name cannot contain uppercase chars [found %s]", alias.charAt(i)));
            }
        }
        if (alias.contains(ILLEGAL_CHARS)) {
            throw new IllegalArgumentException(String.format("Alias name cannot contain special chars [found oneof %s]", ILLEGAL_CHARS));
        }
    }

    /**
     * Check the index name ({@link DatastoreUtils#checkIdxAliasName(String index)}
     *
     * @param index
     * @since 1.0.0
     */
    public static void checkIdxName(String index) {
        DatastoreUtils.checkIdxAliasName(index);
    }

    /**
     * Normalize the index alias name and replace the '-' with '_'
     *
     * @param alias
     * @return The normalized index alias
     * @since 1.0.0
     */
    public static String normalizeIndexAliasName(String alias) {
        String aliasName = normalizeIndexName(alias);
        aliasName = aliasName.replace("-", "_");
        return aliasName;
    }

    /**
     * Normalize the account index name and and the suffix '-*'
     *
     * @param scopeId
     * @return
     */
    public static String getDataIndexName(KapuaId scopeId) {
        final StringBuilder sb = new StringBuilder();
        final String prefix = DatastoreSettings.getInstance().getString(DatastoreSettingsKey.INDEX_PREFIX);
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(prefix).append("-");
        }
        String indexName = DatastoreUtils.normalizedIndexName(scopeId.toStringId());
        sb.append(indexName).append("-").append("data-message").append("-*");
        return sb.toString();
    }

    /**
     * Get the data index for the specified base name and timestamp
     *
     * @param scopeId
     * @param timestamp
     * @return
     */
    public static String getDataIndexName(KapuaId scopeId, long timestamp, String indexingWindowOption) throws KapuaException {
        final StringBuilder sb = new StringBuilder();
        final String prefix = DatastoreSettings.getInstance().getString(DatastoreSettingsKey.INDEX_PREFIX);
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(prefix).append("-");
        }
        final String actualName = DatastoreUtils.normalizedIndexName(scopeId.toStringId());
        sb.append(actualName).append('-').append("data-message").append('-');
        DateTimeFormatter formatter;
        switch (indexingWindowOption) {
            default:
            case INDEXING_WINDOW_OPTION_WEEK:
                formatter = DATA_INDEX_FORMATTER_WEEK;
                break;
            case INDEXING_WINDOW_OPTION_DAY:
                formatter = DATA_INDEX_FORMATTER_DAY;
                break;
            case INDEXING_WINDOW_OPTION_HOUR:
                formatter = DATA_INDEX_FORMATTER_HOUR;
                break;
        }
        formatter.formatTo(Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC), sb);
        return sb.toString();
    }

    public static String getChannelIndexName(KapuaId scopeId) {
        return getRegistryIndexName(scopeId, IndexType.CHANNEL);
    }

    public static String getClientIndexName(KapuaId scopeId) {
        return getRegistryIndexName(scopeId, IndexType.CLIENT);
    }

    public static String getMetricIndexName(KapuaId scopeId) {
        return getRegistryIndexName(scopeId, IndexType.METRIC);
    }

    /**
     * Get the Kapua index name for the specified base name
     *
     * @param scopeId
     * @return The Kapua index name
     * @since 1.0.0
     */
    private static String getRegistryIndexName(KapuaId scopeId, IndexType indexType) {
        final StringBuilder sb = new StringBuilder();
        final String prefix = DatastoreSettings.getInstance().getString(DatastoreSettingsKey.INDEX_PREFIX);
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(prefix).append("-");
        }
        String indexName = DatastoreUtils.normalizedIndexName(scopeId.toStringId());
        sb.append(indexName);
        sb.append("-data-").append(indexType.name().toLowerCase());
        return sb.toString();
    }

    /**
     * Normalize the index ({@link DatastoreUtils#normalizeIndexName(String index)}
     *
     * @param index
     * @return
     */
    public static String normalizedIndexName(String index) {
        return normalizeIndexName(index);
    }

    /**
     * Return the list of the data indexes between start and windowEnd instant by scope id.
     * Only the indexes that will be *FULLY* included in the list (i.e. with a starting date ON OR AFTER the window start AND
     * the end date ON OR BEFORE the window end will be returned
     * Only indexes matching Kapua data index name pattern will be inserted inside the result list, namely this format:
     *
     * "scopeID-data-message-YYYY-ww"
     * or
     * "scopeID-data-message-YYYY-ww-ee"
     * or
     * "scopeID-data-message-YYYY-ww-ee-HH"
     *
     * @param indexes
     * @param windowStart
     * @param windowEnd
     * @param scopeId
     * @return The list of the data indexes between start and end
     * @throws DatastoreException
     */
    public static String[] convertToDataIndexes(@NotNull String[] indexes, Instant windowStart, Instant windowEnd, KapuaId scopeId) throws DatastoreException {
        boolean skipTemporalValidation = false;
        if (windowStart == null && windowEnd == null) {
            skipTemporalValidation = true;
        }
        List<String> result = new ArrayList<>();
        for (String index : indexes) {
            if (index == null) {
                continue;
            }
            if (scopeId != null && (!validatePrefixIndex(index, scopeId))) {
                continue;
            }
            String strippedIndex = stripPrefixAndAccount(index);
            int fragments = strippedIndex.split("-").length;
            DateTimeFormatter formatter;
            ChronoUnit indexUnit;
            int indexWidth;
            switch (fragments) {
                case 2:
                default:
                    // YYYY-ww
                    formatter = DATA_INDEX_FORMATTER_WEEK;
                    indexUnit = ChronoUnit.DAYS;
                    indexWidth = 7;
                    break;
                case 3:
                    // YYYY-ww-ee
                    formatter = DATA_INDEX_FORMATTER_DAY;
                    indexUnit = ChronoUnit.DAYS;
                    indexWidth = 1;
                    break;
                case 4:
                    // YYYY-ww-ee-HH
                    formatter = DATA_INDEX_FORMATTER_HOUR;
                    indexUnit = ChronoUnit.HOURS;
                    indexWidth = 1;
                    break;
            }
            TemporalAccessor temporalAccessor;
            try {
                temporalAccessor = formatter.parse(strippedIndex);
            } catch (DateTimeParseException e) {
                //unable to parse...the format is not right so don't add this index into result-set
                continue;
            }
            if (skipTemporalValidation) { //this index passed format validation...proceed to next index
                result.add(index);
                continue;
            }
            try {
                Instant indexStart = LocalDateTime.of(
                        temporalAccessor.get(ChronoField.YEAR),
                        temporalAccessor.get(ChronoField.MONTH_OF_YEAR),
                        temporalAccessor.get(ChronoField.DAY_OF_MONTH),
                        temporalAccessor.get(ChronoField.HOUR_OF_DAY),
                        temporalAccessor.get(ChronoField.MINUTE_OF_HOUR),
                        temporalAccessor.get(ChronoField.SECOND_OF_MINUTE)
                ).toInstant(ZoneOffset.UTC);
                Instant indexEnd = indexStart.plus(indexWidth, indexUnit).minusNanos(1);

                if (windowStart == null && isIndexFullyBeforeInstant(indexStart, indexEnd, windowEnd) ||
                        (windowEnd == null && isIndexFullyAfterInstant(indexStart, indexEnd, windowStart)) ||
                        (windowStart != null && windowEnd != null && isIndexFullyAfterInstant(indexStart, indexEnd, windowStart) && isIndexFullyBeforeInstant(indexStart, indexEnd, windowEnd))) {
                    result.add(index);
                }
            } catch (Exception ex) {
                throw new DatastoreException(KapuaErrorCodes.ILLEGAL_ARGUMENT, ex);
            }
        }
        return result.toArray(new String[0]);
    }

    private static boolean validatePrefixIndex(@NotNull String index,@NotNull KapuaId scopeId) {
        String genericIndexFormat = getDataIndexName(scopeId);
        return index.startsWith(genericIndexFormat.substring(0, genericIndexFormat.length() - 1));
    }

    private static boolean isIndexFullyAfterInstant(@NotNull Instant indexStart, @NotNull Instant indexEnd, @NotNull Instant checkpoint) {
        return !indexStart.isBefore(checkpoint) && !indexEnd.isBefore(checkpoint);
    }

    private static boolean isIndexFullyBeforeInstant(@NotNull Instant indexStart, @NotNull Instant indexEnd, @NotNull Instant checkpoint) {
        return !indexStart.isAfter(checkpoint) && !indexEnd.isAfter(checkpoint);
    }

    private static String stripPrefixAndAccount(@NotNull String index) {
        return StringUtils.substringAfter(index, "-data-message-");
    }

    /**
     * Get the full metric name used to store the metric in Elasticsearch.<br>
     * The full metric name is composed by the metric and the type acronym as suffix ('.' is used as separator between the 2 parts)
     *
     * @param name
     * @param type
     * @return
     */
    public static String getMetricValueQualifier(String name, String type) {
        String shortType = DatastoreUtils.getClientMetricFromAcronym(type);
        return String.format("%s.%s", name, shortType);
    }

    /**
     * Get the client metric type from the metric value type
     *
     * @param clazz
     * @return The client metric type
     * @since 1.0.0
     */
    public static String getClientMetricFromType(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Metric value must not be null");
        }
        String value;
        if (clazz == String.class) {
            value = CLIENT_METRIC_TYPE_STRING;
        } else if (clazz == Integer.class) {
            value = CLIENT_METRIC_TYPE_INTEGER;
        } else if (clazz == Long.class) {
            value = CLIENT_METRIC_TYPE_LONG;
        } else if (clazz == Float.class) {
            value = CLIENT_METRIC_TYPE_FLOAT;
        } else if (clazz == Double.class) {
            value = CLIENT_METRIC_TYPE_DOUBLE;
        } else if (clazz == Boolean.class) {
            value = CLIENT_METRIC_TYPE_BOOLEAN;
        } else if (clazz == Date.class) {
            value = CLIENT_METRIC_TYPE_DATE;
        } else if (clazz == byte[].class) {
            value = CLIENT_METRIC_TYPE_BINARY;
        } else {
            throw new IllegalArgumentException(String.format("Metric value type for "));
        }
        return value;
    }

    /**
     * Get the client metric type acronym for the given client metric type full name
     *
     * @param acronym
     * @return The client metric type acronym
     * @since 1.0.0
     */
    public static String getClientMetricFromAcronym(String acronym) {
        if (CLIENT_METRIC_TYPE_STRING.equals(acronym)) {
            return CLIENT_METRIC_TYPE_STRING_ACRONYM;
        }
        if (CLIENT_METRIC_TYPE_INTEGER.equals(acronym)) {
            return CLIENT_METRIC_TYPE_INTEGER_ACRONYM;
        }
        if (CLIENT_METRIC_TYPE_LONG.equals(acronym)) {
            return CLIENT_METRIC_TYPE_LONG_ACRONYM;
        }
        if (CLIENT_METRIC_TYPE_FLOAT.equals(acronym)) {
            return CLIENT_METRIC_TYPE_FLOAT_ACRONYM;
        }
        if (CLIENT_METRIC_TYPE_DOUBLE.equals(acronym)) {
            return CLIENT_METRIC_TYPE_DOUBLE_ACRONYM;
        }
        if (CLIENT_METRIC_TYPE_BOOLEAN.equals(acronym)) {
            return CLIENT_METRIC_TYPE_BOOLEAN_ACRONYM;
        }
        if (CLIENT_METRIC_TYPE_DATE.equals(acronym)) {
            return CLIENT_METRIC_TYPE_DATE_ACRONYM;
        }
        if (CLIENT_METRIC_TYPE_BINARY.equals(acronym)) {
            return CLIENT_METRIC_TYPE_BINARY_ACRONYM;
        }
        throw new IllegalArgumentException(String.format(UNKNOWN_TYPE, acronym));
    }

    /**
     * Check if the metric type is date
     *
     * @param acronym
     * @return
     */
    public static boolean isDateMetric(String acronym) {
        return CLIENT_METRIC_TYPE_DATE_ACRONYM.equals(acronym);
    }

    /**
     * Convert the metric value class type (Kapua side) to the proper string type description (client side)
     *
     * @param aClass
     * @return The metric value type converted to string
     * @since 1.0.0
     */
    public static <T> String convertToClientMetricType(Class<T> aClass) {
        if (aClass == String.class) {
            return CLIENT_METRIC_TYPE_STRING;
        }
        if (aClass == Integer.class) {
            return CLIENT_METRIC_TYPE_INTEGER;
        }
        if (aClass == Long.class) {
            return CLIENT_METRIC_TYPE_LONG;
        }
        if (aClass == Float.class) {
            return CLIENT_METRIC_TYPE_FLOAT;
        }
        if (aClass == Double.class) {
            return CLIENT_METRIC_TYPE_DOUBLE;
        }
        if (aClass == Boolean.class) {
            return CLIENT_METRIC_TYPE_BOOLEAN;
        }
        if (aClass == Date.class) {
            return CLIENT_METRIC_TYPE_DATE;
        }
        if (aClass == byte[].class) {
            return CLIENT_METRIC_TYPE_BINARY;
        }
        throw new IllegalArgumentException(String.format(UNKNOWN_TYPE, aClass.getName()));
    }

    /**
     * Convert the client metric type to the corresponding Kapua type
     *
     * @param clientType
     * @return The concrete metric value type
     * @since 1.0.0
     */
    public static Class<?> convertToKapuaType(String clientType) {
        Class<?> clazz;
        if (CLIENT_METRIC_TYPE_STRING.equals(clientType)) {
            clazz = String.class;
        } else if (CLIENT_METRIC_TYPE_INTEGER.equals(clientType)) {
            clazz = Integer.class;
        } else if (CLIENT_METRIC_TYPE_LONG.equals(clientType)) {
            clazz = Long.class;
        } else if (CLIENT_METRIC_TYPE_FLOAT.equals(clientType)) {
            clazz = Float.class;
        } else if (CLIENT_METRIC_TYPE_DOUBLE.equals(clientType)) {
            clazz = Double.class;
        } else if (CLIENT_METRIC_TYPE_BOOLEAN.equals(clientType)) {
            clazz = Boolean.class;
        } else if (CLIENT_METRIC_TYPE_DATE.equals(clientType)) {
            clazz = Date.class;
        } else if (CLIENT_METRIC_TYPE_BINARY.equals(clientType)) {
            clazz = byte[].class;
        } else {
            throw new IllegalArgumentException(String.format(UNKNOWN_TYPE, clientType));
        }
        return clazz;
    }

    /**
     * Convert the metric value to the correct type using the metric acronym type
     *
     * @param acronymType
     * @param value
     * @return The concrete metric value type
     * @since 1.0.0
     */
    public static Object convertToCorrectType(String acronymType, Object value) {
        Object convertedValue = null;
        if (CLIENT_METRIC_TYPE_DOUBLE_ACRONYM.equals(acronymType)) {
            if (value instanceof Number) {
                convertedValue = new Double(((Number) value).doubleValue());
            } else if (value instanceof String) {
                convertedValue = Double.parseDouble((String) value);
            } else {
                throw new IllegalArgumentException(String.format(TYPE_CANNOT_BE_CONVERTED, getValueClass(value)));
            }
        } else if (CLIENT_METRIC_TYPE_FLOAT_ACRONYM.equals(acronymType)) {
            if (value instanceof Number) {
                convertedValue = new Float(((Number) value).floatValue());
            } else if (value instanceof String) {
                convertedValue = Float.parseFloat((String) value);
            } else {
                throw new IllegalArgumentException(String.format(TYPE_CANNOT_BE_CONVERTED, getValueClass(value)));
            }
        } else if (CLIENT_METRIC_TYPE_INTEGER_ACRONYM.equals(acronymType)) {
            if (value instanceof Number) {
                convertedValue = new Integer(((Number) value).intValue());
            } else if (value instanceof String) {
                convertedValue = Integer.parseInt((String) value);
            } else {
                throw new IllegalArgumentException(String.format(TYPE_CANNOT_BE_CONVERTED, getValueClass(value)));
            }
        } else if (CLIENT_METRIC_TYPE_LONG_ACRONYM.equals(acronymType)) {
            if (value instanceof Number) {
                convertedValue = new Long(((Number) value).longValue());
            } else if (value instanceof String) {
                convertedValue = Long.parseLong((String) value);
            } else {
                throw new IllegalArgumentException(String.format("Type [%s] cannot be converted to Long!", getValueClass(value)));
            }
        } else if (CLIENT_METRIC_TYPE_DATE_ACRONYM.equals(acronymType)) {
            if (value instanceof Date) {
                convertedValue = (Date) value;
            } else if (value instanceof String) {
                try {
                    convertedValue = KapuaDateUtils.parseDate((String) value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException(
                            String.format("Type [%s] cannot be converted to Date. Allowed format [%s] - Value to convert [%s]!", getValueClass(value), DATASTORE_DATE_FORMAT,
                                    value));
                }
            } else {
                throw new IllegalArgumentException(String.format("Type [%s] cannot be converted to Date!", getValueClass(value)));
            }
        } else if (CLIENT_METRIC_TYPE_BINARY_ACRONYM.equals(acronymType)) {
            try {
                convertedValue = ByteArrayConverter.fromString((String) value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format("Type [%s] cannot be converted to Byte[]. Value to convert [%s]", getValueClass(value), value));
            }
        } else {
            // no need to translate for others field type
            convertedValue = value;
        }
        return convertedValue;
    }

    private static String getValueClass(Object value) {
        return value != null ? value.getClass().toString() : "null";
    }

}
