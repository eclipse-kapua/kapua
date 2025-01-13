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
package org.eclipse.kapua.commons.service.event.store.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.query.KapuaListResult;

/**
 * KapuaEvent result list definition.
 *
 * @since 1.0
 */
@XmlRootElement(name = "eventStoreRecords")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType
public class EventStoreRecordListResult extends KapuaListResult<EventStoreRecord> {

    private static final long serialVersionUID = -5118004898345748297L;

}
