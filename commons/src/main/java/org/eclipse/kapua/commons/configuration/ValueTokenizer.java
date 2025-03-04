/*******************************************************************************
 * Copyright (c) 2005, 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kapua.commons.configuration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTscalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The implementation of this class is inspired from the org.eclipse.equinox.metatype.impl.ValueTokenizer class in the Eclipse Equinox project.
 *
 * @since 1.0
 */
public class ValueTokenizer {

    private static final Logger LOG = LoggerFactory.getLogger(ValueTokenizer.class);

    private static final String VALUE_INVALID = "Value is invalid";
    private static final String NULL_VALUE_INVALID = "Null cannot be validated";
    private static final String CARDINALITY_VIOLATION = "Cardinality violation: \"{0}\" has {1} value(s) but must have between {2} and {3} value(s).";
    private static final String VALUE_OUT_OF_OPTION = "Value {0} is out of Option";
    private static final String VALUE_OUT_OF_RANGE = "Value {0} is out of range";
    private static final String INTERNAL_ERROR = "Internal error: {0}";

    private static final char DELIMITER = ',';
    private static final char ESCAPE = '\\';

    private final List<String> values = new ArrayList<>();

    /**
     * Constructs a tokenizer for a string
     *
     * @param valuesStr
     */
    public ValueTokenizer(String valuesStr) {

        if (valuesStr == null) {
            return;
        }
        // The trick is to strip out unescaped whitespace characters before and
        // after the input string as well as before and after each
        // individual token within the input string without losing any escaped
        // whitespace characters. Whitespace between two non-whitespace
        // characters may or may not be escaped. Also, any character may be
        // escaped. The escape character is '\'. The delimiter is ','.
        StringBuffer buffer = new StringBuffer();
        // Loop over the characters within the input string and extract each
        // value token.
        for (int i = 0; i < valuesStr.length(); i++) {
            char c1 = valuesStr.charAt(i);
            switch (c1) {
            case DELIMITER:
                // When the delimiter is encountered, add the extracted
                // token to the result and prepare the buffer to receive the
                // next token.
                values.add(buffer.toString());
                buffer.delete(0, buffer.length());
                break;
            case ESCAPE:
                // When the escape is encountered, add the immediately
                // following character to the token, unless the end of the
                // input has been reached. Note this will result in loop
                // counter 'i' being incremented twice, once here and once
                // at the end of the loop.
                if (i + 1 < valuesStr.length()) {
                    buffer.append(valuesStr.charAt(++i));
                } else {
                    // If the ESCAPE character occurs as the last character
                    // of the string, log the error and ignore it.
                    LOG.error(VALUE_INVALID);
                }
                break;
            default:
                // For all other characters, add them to the current token
                // unless dealing with unescaped whitespace at the beginning
                // or end. We know the whitespace is unescaped because it
                // would have been handled in the ESCAPE case otherwise.
                if (Character.isWhitespace(c1)) {
                    // Ignore unescaped whitespace at the beginning of the
                    // token.
                    if (buffer.length() == 0) {
                        continue;
                    }
                    // If the whitespace is not at the beginning, look
                    // forward, starting with the next character, to see if
                    // it's in the middle or at the end. Unescaped
                    // whitespace in the middle is okay.
                    for (int j = i + 1; j < valuesStr.length(); j++) {
                        // Keep looping until the end of the string is
                        // reached or a non-whitespace character other than
                        // the escape is seen.
                        char c2 = valuesStr.charAt(j);
                        if (!Character.isWhitespace(c2)) {
                            // If the current character is not the DELIMITER, all whitespace
                            // characters are significant and should be added to the token.
                            // Otherwise, they're at the end and should be ignored. But watch
                            // out for an escape character at the end of the input. Ignore it
                            // and any previous insignificant whitespace if it exists.
                            if (c2 == ESCAPE && j + 1 >= valuesStr.length()) {
                                continue;
                            }
                            if (c2 != DELIMITER) {
                                buffer.append(valuesStr.substring(i, j));
                            }
                            // Let loop counter i catch up with the inner loop but keep in
                            // mind it will still be incremented at the end of the outer loop.
                            i = j - 1;
                            break;
                        }
                    }
                } else {
                    // For non-whitespace characters.
                    buffer.append(c1);
                }
            }
        }
        // Don't forget to add the last token.
        values.add(buffer.toString());
    }

    /**
     * Return values as Vector.
     *
     * @return
     */
    public Collection<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Return values as String[] or null.
     *
     * @return
     */
    public String[] getValuesAsArray() {
        if (values.isEmpty()) {
            return null;
        }

        return values.toArray(new String[0]);
    }

    /**
     * Return values as String
     *
     * @return
     */
    public String getValuesAsString() {
        if (values.isEmpty()) {
            return null;
        }

        if (values.size() == 1) {
            return values.get(0);
        }

        StringBuilder builder = new StringBuilder(values.get(0));
        for (int i = 1; i < values.size(); i++) {
            builder.append(',')
                    .append(values.get(i));
        }
        return builder.toString();
    }

    /**
     * Validate Tad
     *
     * @param ad
     * @return
     */
    public String validate(KapuaTad ad) {
        // An empty list means the original value was null. Null is never valid.
        if (values.isEmpty()) {
            return NULL_VALUE_INVALID;
        }
        try {
            // A value must match the cardinality.
            int cardinality = Math.abs(ad.getCardinality());
            // If the cardinality is zero, the value must contain one and only one token.
            if (cardinality == 0) {
                if (values.size() != 1) {
                    return MessageFormat.format(CARDINALITY_VIOLATION, getValuesAsString(), values.size(), 1, 1);
                }
            }
            // Otherwise, the number of tokens must be between 0 and cardinality, inclusive.
            else if (values.size() > cardinality) {
                return MessageFormat.format(CARDINALITY_VIOLATION, getValuesAsString(), values.size(), 0, cardinality);
            }
            // Now inspect each token.
            for (String s : values) {
                // If options were declared and the value does not match one of them, the value is not valid.
                if (!ad.getOption().isEmpty() && ad.getOption().stream().filter(option -> s.equals(option.getValue())).count() == 0) {
                    return MessageFormat.format(VALUE_OUT_OF_OPTION, s);
                }
                // Check the type. Also check the range if min or max were declared.
                boolean rangeError = false;
                Object minVal = null;
                Object maxVal = null;
                KapuaTscalar adScalarType = KapuaTscalar.fromValue(ad.getType().value());
                switch (adScalarType) {
                case PASSWORD:
                case STRING:
                    minVal = ad.getMin() == null ? null : Integer.valueOf(ad.getMin());
                    maxVal = ad.getMax() == null ? null : Integer.valueOf(ad.getMax());
                    if (minVal != null && s.length() < (Integer) maxVal) {
                        rangeError = true;
                    } else if (maxVal != null && s.length() > (Integer) maxVal) {
                        rangeError = true;
                    }
                    break;
                case INTEGER:
                    minVal = ad.getMin() == null ? null : Integer.valueOf(ad.getMin());
                    maxVal = ad.getMax() == null ? null : Integer.valueOf(ad.getMax());
                    Integer intVal = Integer.valueOf(s);
                    if (minVal != null && intVal.compareTo((Integer) minVal) < 0) {
                        rangeError = true;
                    } else if (maxVal != null && intVal.compareTo((Integer) maxVal) > 0) {
                        rangeError = true;
                    }
                    break;
                case LONG:
                    minVal = ad.getMin() == null ? null : Long.valueOf(ad.getMin());
                    maxVal = ad.getMax() == null ? null : Long.valueOf(ad.getMax());
                    Long longVal = Long.valueOf(s);
                    if (ad.getMin() != null && longVal.compareTo((Long) minVal) < 0) {
                        rangeError = true;
                    } else if (maxVal != null && longVal.compareTo((Long) maxVal) > 0) {
                        rangeError = true;
                    }
                    break;
                case DOUBLE:
                    minVal = ad.getMin() == null ? null : Double.valueOf(ad.getMin());
                    maxVal = ad.getMax() == null ? null : Double.valueOf(ad.getMax());
                    Double doubleVal = Double.valueOf(s);
                    if (minVal != null && doubleVal.compareTo((Double) minVal) < 0) {
                        rangeError = true;
                    } else if (maxVal != null && doubleVal.compareTo((Double) maxVal) > 0) {
                        rangeError = true;
                    }
                    break;
                case BOOLEAN:
                    // Any string can be converted into a boolean via Boolean.valueOf(String).
                    // Seems unnecessary to impose any further restrictions.
                    break;
                case CHAR:
                    minVal = ad.getMin() == null ? null : ad.getMin().charAt(0);
                    maxVal = ad.getMax() == null ? null : ad.getMax().charAt(0);
                    Character charVal = s.charAt(0);
                    if (minVal != null && charVal.compareTo((Character) minVal) < 0) {
                        rangeError = true;
                    } else if (maxVal != null && charVal.compareTo((Character) maxVal) > 0) {
                        rangeError = true;
                    }
                    break;
                case FLOAT:
                    minVal = ad.getMin() == null ? null : Float.valueOf(ad.getMin());
                    maxVal = ad.getMax() == null ? null : Float.valueOf(ad.getMax());
                    Float floatVal = Float.valueOf(s);
                    if (minVal != null && floatVal.compareTo((Float) minVal) < 0) {
                        rangeError = true;
                    } else if (maxVal != null && floatVal.compareTo((Float) maxVal) > 0) {
                        rangeError = true;
                    }
                    break;
                case SHORT:
                    minVal = ad.getMin() == null ? null : Short.valueOf(ad.getMin());
                    maxVal = ad.getMax() == null ? null : Short.valueOf(ad.getMax());
                    Short shortVal = Short.valueOf(s);
                    if (minVal != null && shortVal.compareTo((Short) minVal) < 0) {
                        rangeError = true;
                    } else if (maxVal != null && shortVal.compareTo((Short) maxVal) > 0) {
                        rangeError = true;
                    }
                    break;
                case BYTE:
                    minVal = ad.getMin() == null ? null : Byte.valueOf(ad.getMin());
                    maxVal = ad.getMax() == null ? null : Byte.valueOf(ad.getMax());
                    Byte byteVal = Byte.valueOf(s);
                    if (minVal != null && byteVal.compareTo((Byte) minVal) < 0) {
                        rangeError = true;
                    } else if (maxVal != null && byteVal.compareTo((Byte) maxVal) > 0) {
                        rangeError = true;
                    }
                    break;
                default:
                    throw new IllegalStateException();
                }
                if (rangeError) {
                    return MessageFormat.format(VALUE_OUT_OF_RANGE, s);
                }
            }
            // No problems detected
            return ""; //$NON-NLS-1$
        } catch (Throwable t) {
            String message = MessageFormat.format(INTERNAL_ERROR, t.getMessage());
            LOG.debug(message, t);
            return message;
        }
    }
}
