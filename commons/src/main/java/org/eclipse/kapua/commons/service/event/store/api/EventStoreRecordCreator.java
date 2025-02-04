/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.commons.service.event.store.api;

import org.eclipse.kapua.model.KapuaEntityCreator;
import org.eclipse.kapua.model.id.KapuaId;

public class EventStoreRecordCreator extends KapuaEntityCreator {

    private static final long serialVersionUID = 1048699703033893534L;

    public EventStoreRecordCreator() {
    }

    public EventStoreRecordCreator(KapuaId scopeId) {
        super(scopeId);
    }

    public EventStoreRecordCreator(KapuaEntityCreator entityCreator) {
        super(entityCreator);
    }
}
