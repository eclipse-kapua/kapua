/*******************************************************************************
 * Copyright (c) 2017, 2026 Eurotech and/or its affiliates and others
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

import java.lang.reflect.Constructor;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.eclipse.kapua.KapuaDuplicateNameException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.KapuaOptimisticLockingException;
import org.eclipse.kapua.kapuaIntegrityConstraintViolationException;
import org.eclipse.kapua.qa.markers.junit.JUnitTests;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import org.mockito.Mockito;


@Category(JUnitTests.class)
public class KapuaExceptionUtilsTest {

    @Test
    public void testConstructor() throws Exception {
        Constructor<KapuaExceptionUtils> exceptionUtil = KapuaExceptionUtils.class.getDeclaredConstructor();
        exceptionUtil.setAccessible(true);
        @SuppressWarnings("unused")
        KapuaExceptionUtils exceptionUtilTest = exceptionUtil.newInstance();
    }

    @Test
    public void convertPersistenceExceptionTest() {
        Throwable t = new Throwable();
        Throwable s = new PersistenceException("Persistence Exception", t);
        KapuaException kapExc0 = KapuaException.internalError(null, null);
        KapuaException kapExc1 = KapuaException.internalError(t, "error");
        KapuaException kapExc2 = KapuaException.internalError("string");
        KapuaException kapExc3 = KapuaException.internalError(t);
        KapuaException kapExc4 = KapuaException.internalError(s);
        OptimisticLockException optimisticLock = new OptimisticLockException();
        Throwable q = new Throwable();
        Throwable w = new RollbackException("Rollback exception", q);
        PersistenceException persistExc0 = new PersistenceException(w);
        Throwable e = new Throwable();
        Throwable r = new RollbackException("Rollback exception", e);
        Throwable a = new RollbackException("rollback2", r);
        PersistenceException persistExc1 = new PersistenceException(a);

        Exception[] listOfExceptions = new Exception[]{kapExc0, kapExc1, kapExc2, kapExc3, kapExc4, persistExc0, persistExc1, optimisticLock};
        Exception[] listOfExpectedExceptions = new Exception[]{kapExc0, kapExc1, kapExc2, kapExc3, KapuaException.internalError(kapExc4, "Error during Persistence Operation"), KapuaException.internalError(persistExc0, "Error during Persistence Operation"), KapuaException.internalError(persistExc1, "Error during Persistence Operation"), new KapuaOptimisticLockingException(optimisticLock)};

        for (int i = 0; i < listOfExceptions.length; i++) {
            Assert.assertEquals("ComparisonFailure not expected for: " + listOfExceptions[i],listOfExpectedExceptions[i].toString(), KapuaExceptionUtils.convertPersistenceException(listOfExceptions[i]).toString());
        }
    }

    @Test
    public void convertPersistenceDatabaseExceptionTest() {
        DatabaseException mockedDatabaseException = Mockito.mock(DatabaseException.class);
        Exception exception = new Exception(mockedDatabaseException);

        String[] listOfSQLMessages1062 = new String[]{"SQL Message: Duplicate entry 'test_account_1,322,584,746,357' for key 'uc_accountName'", "SQL Message: Duplicate entry for nothing", "SQL Message: Duplicate entry 'test_account_1,322,584,746,357'", "SQL Message: Duplicate entry '!#$%&&()=!⁄@‹›€°·‚}{∏Ææ¿ˇÈ~Œ", "SQL Message: Duplicate entry 'test_account_1,322,584,746,357' for key uc_accountName", "SQL Message: Duplicate entry test_account_1,322,584,746,357 for key 'uc_accountName'"};
        String[] listOfValidSQLMessages1048 = new String[]{"SQL Message: Column '%s' cannot be null", "SQL Message: Column ' ' cannot be null"};
        String[] listOfInvalidSQLMessages1048 = new String[]{"SQL Message: Column cannot be null", "SQL Message: Column 'one',column 'two' '%s' cannot be null", "SQL Message: Column 'one', Column 'two', column 'three'", "SQL Message: Cannot be null column '%s'"};

        KapuaDuplicateNameException[] expectedExceptions1062 = new KapuaDuplicateNameException[]{new KapuaDuplicateNameException("uc_accountName"), new KapuaDuplicateNameException("SQL Message: Duplicate entry for nothing"), new KapuaDuplicateNameException("test_account_1,322,584,746,357"), new KapuaDuplicateNameException("!#$%&&()=!⁄@‹›€°·‚}{∏Ææ¿ˇÈ~Œ"), new KapuaDuplicateNameException(" for key uc_accountName"), new KapuaDuplicateNameException("uc_accountName")};
        KapuaIllegalNullArgumentException[] expectedExceptions1048 = new KapuaIllegalNullArgumentException[]{new KapuaIllegalNullArgumentException("%s"), new KapuaIllegalNullArgumentException(" "), new KapuaIllegalNullArgumentException("%s")};
        KapuaException kapuaException = KapuaException.internalError(exception, "Error during Persistence Operation");

        //SQL Error: 1062
        for (int i = 0; i < listOfSQLMessages1062.length; i++) {
            Mockito.when(mockedDatabaseException.getErrorCode()).thenReturn(1062);
            Mockito.when(mockedDatabaseException.getMessage()).thenReturn(listOfSQLMessages1062[i]);
            Mockito.when(mockedDatabaseException.getInternalException()).thenReturn(mockedDatabaseException);
            Assert.assertEquals("ComparisonFailure not expected for: " + exception, expectedExceptions1062[i].toString(), KapuaExceptionUtils.convertPersistenceException(exception).toString());
        }

        //SQL Error: 1048
        for (int i = 0; i < listOfValidSQLMessages1048.length; i++) {
            Mockito.when(mockedDatabaseException.getErrorCode()).thenReturn(1048);
            Mockito.when(mockedDatabaseException.getMessage()).thenReturn(listOfValidSQLMessages1048[i]);
            Mockito.when(mockedDatabaseException.getInternalException()).thenReturn(mockedDatabaseException);
            Assert.assertEquals("ComparisonFailure not expected for: " + exception,expectedExceptions1048[i].toString(), KapuaExceptionUtils.convertPersistenceException(exception).toString());
        }

        for (int i = 0; i < listOfInvalidSQLMessages1048.length; i++) {
            Mockito.when(mockedDatabaseException.getErrorCode()).thenReturn(1048);
            Mockito.when(mockedDatabaseException.getMessage()).thenReturn(listOfInvalidSQLMessages1048[i]);
            Mockito.when(mockedDatabaseException.getInternalException()).thenReturn(mockedDatabaseException);
            Assert.assertEquals("ComparisonFailure not expected for: " + exception,kapuaException.toString(), KapuaExceptionUtils.convertPersistenceException(exception).toString());
        }

        //SQL Error: 2999
        Mockito.when(mockedDatabaseException.getErrorCode()).thenReturn(2999);
        Mockito.when(mockedDatabaseException.getMessage()).thenReturn("SQL Message: Column '%s' cannot be null");
        Mockito.when(mockedDatabaseException.getInternalException()).thenReturn(mockedDatabaseException);
        Assert.assertEquals("ComparisonFailure not expected for: " + exception,kapuaException.toString(), KapuaExceptionUtils.convertPersistenceException(exception).toString());

        //SQL foreign key constraint violation - unknown SQL error code
        SQLIntegrityConstraintViolationException mockedDatabaseException2 = Mockito.mock(SQLIntegrityConstraintViolationException.class);
        Mockito.when(mockedDatabaseException.getInternalException()).thenReturn(mockedDatabaseException2);
        Mockito.when(mockedDatabaseException.getMessage()).thenReturn("FOREIGN KEY");
        kapuaIntegrityConstraintViolationException ke = new kapuaIntegrityConstraintViolationException("This operation would break a relation between this entity and other entities.");
        Assert.assertEquals("ComparisonFailure not expected for: " + exception,ke.toString(), KapuaExceptionUtils.convertPersistenceException(exception).toString());

        //SQL foreign key constraint violation - referenced entity does not exist (MariaDB/MySQL: 1216, 1452 - H2: 23506)
        kapuaIntegrityConstraintViolationException keReferencedMissing = new kapuaIntegrityConstraintViolationException("This entity references another entity that does not exist.");
        for (int sqlErrorCode : new int[]{1216, 1452, 23506}) {
            Mockito.when(mockedDatabaseException.getErrorCode()).thenReturn(sqlErrorCode);
            Assert.assertEquals("ComparisonFailure not expected for SQL error code: " + sqlErrorCode, keReferencedMissing.toString(), KapuaExceptionUtils.convertPersistenceException(exception).toString());
        }

        //SQL foreign key constraint violation - entity still referenced by other entities (MariaDB/MySQL: 1217, 1451 - H2: 23503)
        kapuaIntegrityConstraintViolationException keStillReferenced = new kapuaIntegrityConstraintViolationException("This entity is referenced by other entities and cannot be deleted.");
        for (int sqlErrorCode : new int[]{1217, 1451, 23503}) {
            Mockito.when(mockedDatabaseException.getErrorCode()).thenReturn(sqlErrorCode);
            Assert.assertEquals("ComparisonFailure not expected for SQL error code: " + sqlErrorCode, keStillReferenced.toString(), KapuaExceptionUtils.convertPersistenceException(exception).toString());
        }
        Mockito.when(mockedDatabaseException.getErrorCode()).thenReturn(2999);

        //generic SQL constraint violation
        Mockito.when(mockedDatabaseException.getMessage()).thenReturn("another message different from for3ign key but always SQL integrity constraint violation stuff");
        kapuaIntegrityConstraintViolationException ke2 = new kapuaIntegrityConstraintViolationException("");
        Assert.assertEquals("ComparisonFailure not expected for: " + exception,ke2.toString(), KapuaExceptionUtils.convertPersistenceException(exception).toString());

        Mockito.verify(mockedDatabaseException, Mockito.times(21)).getInternalException();
        Mockito.verify(mockedDatabaseException, Mockito.times(20)).getMessage();
        Mockito.verify(mockedDatabaseException, Mockito.times(21)).getErrorCode();
    }

    @Test
    public void extractKapuaExceptionTest() {
        Throwable t = new Throwable();
        Throwable s = new Exception("Exception", t);

        KapuaException kapExc0 = KapuaException.internalError(null, null);
        KapuaException kapExc1 = KapuaException.internalError(t, "error");
        KapuaException kapExc2 = KapuaException.internalError("string");
        KapuaException kapExc3 = KapuaException.internalError(t);
        KapuaException kapExc4 = KapuaException.internalError(s);

        Exception exception1 = new Exception(t);
        Exception exception2 = new Exception(s);

        Exception[] listOfExceptions = new Exception[]{kapExc0, kapExc1, kapExc2, kapExc3, kapExc4, null};
        Exception[] listOfExpectedExceptions = new Exception[]{kapExc0, kapExc1, kapExc2, kapExc3, kapExc4, null};

        Exception[] listOfNonKapuaExceptions = new Exception[]{exception1, exception2};
        Exception[] listOfExpectedReturnsOfNonKapuaExceptions = new Exception[]{KapuaExceptionUtils.extractKapuaException(exception1.getCause()), KapuaExceptionUtils.extractKapuaException(exception2.getCause())};

        for (int i = 0; i < listOfExceptions.length; i++) {
            Assert.assertSame("Exception not expected for " + listOfExceptions[i], listOfExpectedExceptions[i], KapuaExceptionUtils.extractKapuaException(listOfExceptions[i]));
        }

        for (int i = 0; i < listOfNonKapuaExceptions.length; i++) {
            Assert.assertSame("Exception not expected for " + listOfNonKapuaExceptions[i], listOfExpectedReturnsOfNonKapuaExceptions[i], KapuaExceptionUtils.extractKapuaException(listOfNonKapuaExceptions[i]));
        }
    }
}
