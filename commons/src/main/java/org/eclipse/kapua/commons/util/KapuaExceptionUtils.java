/*******************************************************************************
 * Copyright (c) 2016, 2026 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.util;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.KapuaOptimisticLockingException;
import org.eclipse.kapua.kapuaIntegrityConstraintViolationException;
import org.eclipse.persistence.exceptions.DatabaseException;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Exception utilities
 *
 * @since 1.0
 */
public class KapuaExceptionUtils {

    private static final String FOREIGN_KEY_REFERENCED_ENTITY_MISSING_MESSAGE = "This entity references another entity that does not exist.";
    private static final String FOREIGN_KEY_ENTITY_STILL_REFERENCED_MESSAGE = "This entity is referenced by other entities and cannot be deleted.";
    private static final String FOREIGN_KEY_GENERIC_MESSAGE = "This operation would break a relation between this entity and other entities.";

    private KapuaExceptionUtils() {
    }

    /**
     * Converts a low-level PersistenceException/SQLException to a business-level KapuaException.
     *
     * @param he
     */
    public static KapuaException convertPersistenceException(Exception he) {
        Exception e = he;

        // Handle the case of an incoming KapuaException
        if (he instanceof KapuaException && he.getCause() != null && he.getCause() instanceof PersistenceException) {
            e = (Exception) he.getCause();
        } else if (he instanceof KapuaException) {
            return (KapuaException) e;
        }

        // process the Persistence Exception
        KapuaException ee = null;
        if (e instanceof OptimisticLockException) {
            ee = new KapuaOptimisticLockingException(e);
        } else {
            Throwable t = e.getCause();
            if (t instanceof PersistenceException) {
                t = t.getCause();
            }
            if (t instanceof RollbackException) {
                t = t.getCause();
            }
            if (t instanceof DatabaseException) {

                // Handle Unique Constraints Exception
                DatabaseException cve = (DatabaseException) t;

                int sqlErrorCode = cve.getErrorCode();
                switch (sqlErrorCode) {

                    // SQL Error: 1062, SQLState: 23000 - ER_DUP_KEYNAME - Unique Constraints Exception
                    case 1062: {
                        // Extract the constraint name
                        // e.g. SQL Message: Duplicate entry 'test_account_1,322,584,746,357' for key 'uc_accountName'
                        String message = cve.getInternalException().getMessage();
                        String[] parts = message.split("'");
                        String constraintName = parts[parts.length - 1];
                        // populate the duplicated field name
                        // String duplicateNameField = s_uniqueConstraints.get(constraintName);
                        if (constraintName != null) {
                            ee = new KapuaDuplicateNameException(constraintName);
                        }
                    }
                    break;

                    // SQL Error: 1048, SQLSTATE: 23000 - ER_BAD_NULL_ERROR - Not Null Violation
                    case 1048: {
                        // Extract the name of the null attribute
                        // e.g. SQL Message: Column '%s' cannot be null
                        String message = cve.getInternalException().getMessage();
                        String[] parts = message.split("'");
                        String columnName = null;
                        if (parts.length == 3) {
                            columnName = parts[1];
                        }
                        // populate the null field name
                        if (columnName != null) {
                            ee = new KapuaIllegalNullArgumentException(columnName);
                        }

                    }
                    break;
                    default: {
                        if (cve.getInternalException() instanceof SQLIntegrityConstraintViolationException) {
                            ee = new kapuaIntegrityConstraintViolationException(integrityConstraintViolationMessage(sqlErrorCode, cve.getMessage()));
                        }
                    }
                }
            }
        }
        // Handle all other Exceptions
        if (ee == null) {
            ee = KapuaException.internalError(e, "Error during Persistence Operation");
        }
        return ee;
    }

    /**
     * Builds the detail message of a {@link kapuaIntegrityConstraintViolationException}, telling apart which side of a foreign key has been violated.
     *
     * @param sqlErrorCode The vendor SQL error code.
     * @param sqlMessage   The SQL error message.
     * @return The detail message, or an empty {@link String} if the violation is not related to a foreign key.
     * @since 2.1.0
     */
    private static String integrityConstraintViolationMessage(int sqlErrorCode, String sqlMessage) {
        switch (sqlErrorCode) {
            // MariaDB/MySQL: 1216 ER_NO_REFERENCED_ROW, 1452 ER_NO_REFERENCED_ROW_2 - H2: 23506 REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1
            case 1216:
            case 1452:
            case 23506:
                return FOREIGN_KEY_REFERENCED_ENTITY_MISSING_MESSAGE;
            // MariaDB/MySQL: 1217 ER_ROW_IS_REFERENCED, 1451 ER_ROW_IS_REFERENCED_2 - H2: 23503 REFERENTIAL_INTEGRITY_VIOLATED_CHILD_EXISTS_1
            case 1217:
            case 1451:
            case 23503:
                return FOREIGN_KEY_ENTITY_STILL_REFERENCED_MESSAGE;
            default:
                return sqlMessage != null && sqlMessage.contains("FOREIGN KEY") ? FOREIGN_KEY_GENERIC_MESSAGE : "";
        }
    }

    /**
     * Walks the "cause" hierarchy of a {@link Throwable} until a {@link KapuaException} is found, and returns it
     *
     * @param t A {@link Throwable} whose "cause" hierarchy will be searched for a {@link KapuaException}
     * @return The first {@link KapuaException} in the "cause" hierarchy, or null if none is found
     * @since 1.2.0
     */
    public static KapuaException extractKapuaException(Throwable t) {
        if (t instanceof KapuaException || t == null) {
            return (KapuaException) t;
        } else {
            return extractKapuaException(t.getCause());
        }
    }
}
