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
 *******************************************************************************/
package org.eclipse.kapua.commons.jpa;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.PessimisticLockException;
import javax.persistence.RollbackException;

import org.eclipse.kapua.KapuaEntityExistsException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.KapuaExceptionUtils;
import org.eclipse.kapua.storage.TxContext;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaTxContext implements JpaAwareTxContext, TxContext {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public final EntityManagerFactory entityManagerFactory;
    Optional<EntityManager> entityManager = Optional.empty();

    public JpaTxContext(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public EntityManager getEntityManager() {
        if (!entityManager.isPresent()) {
            entityManager = Optional.of(entityManagerFactory.createEntityManager());
        }

        final EntityTransaction tx = entityManager.get().getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
        return entityManager.get();
    }

    @Override
    public void commit() {
        entityManager.ifPresent(e -> e.getTransaction().commit());
    }

    @Override
    public void rollback() {
        entityManager.ifPresent(entityManager -> {
            final EntityTransaction tx = entityManager.getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        });
    }

    @Override
    public void close() throws IOException {
        entityManager.ifPresent(entityManager -> entityManager.close());
    }

    @Override
    public KapuaException convertPersistenceException(Exception ex) {
        return KapuaExceptionUtils.convertPersistenceException(ex);
    }

    //https://dev.mysql.com/doc/mysql-errors/8.0/en/server-error-reference.html#error_er_lock_wait_timeout
    private static final int ER_LOCK_WAIT_TIMEOUT = 1205;
    //https://dev.mysql.com/doc/mysql-errors/8.0/en/server-error-reference.html#error_er_lock_deadlock
    private static final int ER_LOCK_DEADLOCK = 1213;
    private final Predicate isLockExceptionTester = t -> {
        if (t instanceof OptimisticLockException || t instanceof PessimisticLockException) {
            return true;
        }
        if (t instanceof DatabaseException) {
            final DatabaseException de = (DatabaseException) t;
            return de.getErrorCode() == ER_LOCK_DEADLOCK || de.getErrorCode() == ER_LOCK_WAIT_TIMEOUT;
        }
        return false;
    };

    @Override
    public boolean isRecoverableException(Exception ex) {
        if (ex instanceof KapuaEntityExistsException || ex instanceof EntityExistsException) {
            /*
             * Most KapuaEntities inherit from KapuaEntityBase, which auto-generates ids via a method marked with @PrePersist and the use of
             * a org.eclipse.kapua.commons.model.id.IdGenerator. Ids are pseudo-randomic. To deal with potential conflicts, a number of retries
             * is allowed. The entity needs to be detached in order for the @PrePersist method to be invoked once more, generating a new id
             * */
            logger.warn("Conflict on entity creation. Cannot insert the entity, trying again!");
            return true;
        }
        final boolean isValidLockException =
                isLockExceptionTester.test(ex)
                        || (ex instanceof RollbackException && isLockExceptionTester.test(ex.getCause()));
        if (isValidLockException) {
            logger.warn("Recoverable Lock Exception");
            return true;
        }
        return false;
    }
}
