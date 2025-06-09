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
package org.eclipse.kapua.translator;

import javax.validation.constraints.NotNull;

import org.eclipse.kapua.message.Message;

public interface TranslatorHub {

    <FROM_MESSAGE extends Message,
            TO_MESSAGE extends Message,
            TRANSLATOR extends Translator<FROM_MESSAGE, TO_MESSAGE>>
    TRANSLATOR getTranslatorFor(
            @NotNull Class<? extends FROM_MESSAGE> fromMessageClass,
            @NotNull Class<? extends TO_MESSAGE> toMessageClass);
}
