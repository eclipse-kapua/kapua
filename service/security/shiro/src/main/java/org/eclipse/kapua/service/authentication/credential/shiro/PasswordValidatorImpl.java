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
package org.eclipse.kapua.service.authentication.credential.shiro;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.CommonsValidationRegex;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authentication.exception.PasswordLengthException;
import org.eclipse.kapua.storage.TxContext;

/**
 * The {@link PasswordValidator} implementation.
 *
 * @since 2.0.0
 */
public class PasswordValidatorImpl implements PasswordValidator {

    private final AccountPasswordLengthProvider accountPasswordLengthProvider;

    public PasswordValidatorImpl(AccountPasswordLengthProvider accountPasswordLengthProvider) {
        this.accountPasswordLengthProvider = accountPasswordLengthProvider;
    }

    @Override
    public void validatePassword(TxContext txContext, KapuaId scopeId, String plainPassword) throws KapuaException {
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notEmptyOrNull(plainPassword, "plainPassword");

        // Validate Password length
        int minPasswordLength = accountPasswordLengthProvider.getMinimumPasswordLength(txContext, scopeId);
        int maxPasswordLenght = accountPasswordLengthProvider.getMaximumPasswordLength(txContext, scopeId);
        if (plainPassword.length() < minPasswordLength || plainPassword.length() > maxPasswordLenght) {
            throw new PasswordLengthException(minPasswordLength, maxPasswordLenght);
        }
        // Validate Password regex
        ArgumentValidator.match(plainPassword, CommonsValidationRegex.PASSWORD_REGEXP, "plainPassword");
    }

}
