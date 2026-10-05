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
package org.eclipse.kapua.service.storeengine.client;

import com.google.common.hash.Hashing;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.model.id.KapuaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
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
import java.util.List;

public abstract class AbstractStoreUtils {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractStoreUtils.class);
    public static final CharSequence ILLEGAL_CHARS = "\"\\/*?<>|,. ";
    protected final DateTimeFormatter dataIndexFormatterWeek;
    protected final DateTimeFormatter dataIndexFormatterDay;
    protected final DateTimeFormatter dataIndexFormatterHour;

    public AbstractStoreUtils() {
        dataIndexFormatterWeek = new DateTimeFormatterBuilder()
                .parseDefaulting(WeekFields.ISO.dayOfWeek(), 1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .appendPattern("YYYY-ww")
                .toFormatter(KapuaDateUtils.getLocale())
                .withLocale(KapuaDateUtils.getLocale())
                .withResolverStyle(ResolverStyle.STRICT)
                .withZone(KapuaDateUtils.getTimeZone());
        dataIndexFormatterDay = new DateTimeFormatterBuilder()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .appendPattern("YYYY-ww-ee")
                .toFormatter(KapuaDateUtils.getLocale())
                .withLocale(KapuaDateUtils.getLocale())
                .withResolverStyle(ResolverStyle.STRICT)
                .withZone(KapuaDateUtils.getTimeZone());
        dataIndexFormatterHour = new DateTimeFormatterBuilder()
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .appendPattern("YYYY-ww-ee-HH")
                .toFormatter(KapuaDateUtils.getLocale())
                .withLocale(KapuaDateUtils.getLocale())
                .withResolverStyle(ResolverStyle.STRICT)
                .withZone(KapuaDateUtils.getTimeZone());
    }

    public String getHashCode(String... components) {
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

    /**
     * Normalize the index ({@link AbstractStoreUtils#normalizeIndexName(String index)}
     *
     * @param index
     * @return
     */
    public String normalizedIndexName(String index) {
        return normalizeIndexName(index);
    }

    protected String normalizeIndexName(String name) {
        String normName = null;
        try {
            checkIdxAliasName(name);
            normName = name;
        } catch (IllegalArgumentException exc) {
            LOG.trace(exc.getMessage(), exc);
            normName = name.toLowerCase().replace(ILLEGAL_CHARS, "_");
            checkIdxAliasName(normName);
        }
        return normName;
    }

    /**
     * Check the index alias correctness.<br>
     * The alias cannot be null, starts with '_', contains uppercase character or contains ILLEGAL_CHARS
     *
     * @param alias
     * @since 2.0.0
     */
    public void checkIdxAliasName(String alias) {
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
     * Check the index name ({@link AbstractStoreUtils#checkIdxAliasName(String index)}
     *
     * @param index
     * @since 2.0.0
     */
    public void checkIdxName(String index) {
        checkIdxAliasName(index);
    }

    /**
     * Normalize the index alias name and replace the '-' with '_'
     *
     * @param alias
     * @return The normalized index alias
     * @since 2.0.0
     */
    public String normalizeIndexAliasName(String alias) {
        String aliasName = normalizeIndexName(alias);
        aliasName = aliasName.replace("-", "_");
        return aliasName;
    }

    /**
     * Return the list of the indexes between start and windowEnd instant by scope id.
     * Only the indexes that will be *FULLY* included in the list (i.e. with a starting date ON OR AFTER the window start AND
     * the end date ON OR BEFORE the window end will be returned
     * Only indexes matching Kapua index name pattern will be inserted inside the result list, namely this format:
     *
     * "scopeID-*-message-YYYY-ww"
     * or
     * "scopeID-*-message-YYYY-ww-ee"
     * or
     * "scopeID-*-message-YYYY-ww-ee-HH"
     *
     * @param indexes
     * @param windowStart
     * @param windowEnd
     * @param scopeId
     * @return The list of the indexes between start and end
     * @throws Exception
     * @since 2.0.0
     */
    public String[] filterIndexesTemporalWindow(@NotNull String[] indexes, Instant windowStart, Instant windowEnd, KapuaId scopeId) throws Exception {
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
                    formatter = dataIndexFormatterWeek;
                    indexUnit = ChronoUnit.DAYS;
                    indexWidth = 7;
                    break;
                case 3:
                    // YYYY-ww-ee
                    formatter = dataIndexFormatterDay;
                    indexUnit = ChronoUnit.DAYS;
                    indexWidth = 1;
                    break;
                case 4:
                    // YYYY-ww-ee-HH
                    formatter = dataIndexFormatterHour;
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
                throw ex;
            }
        }
        return result.toArray(new String[0]);
    }

    private boolean isIndexFullyAfterInstant(@NotNull Instant indexStart, @NotNull Instant indexEnd, @NotNull Instant checkpoint) {
        return !indexStart.isBefore(checkpoint) && !indexEnd.isBefore(checkpoint);
    }

    private boolean isIndexFullyBeforeInstant(@NotNull Instant indexStart, @NotNull Instant indexEnd, @NotNull Instant checkpoint) {
        return !indexStart.isAfter(checkpoint) && !indexEnd.isAfter(checkpoint);
    }

    /**
     * Validates the prefix of the index based on the scopeId to verify that is it compliant with the Kapua format
     * returns true iff the index has a valid prefix
     * The implementation depends on the particular format of the prefix
     *
     * @since 2.0.0
     */
    protected abstract boolean validatePrefixIndex(@NotNull String index,@NotNull KapuaId scopeId);

    /**
     * Extracts from the index the part after the fixed prefix and the accountId
     *
     * @since 2.0.0
     */
    protected abstract String stripPrefixAndAccount(@NotNull String index);


}
