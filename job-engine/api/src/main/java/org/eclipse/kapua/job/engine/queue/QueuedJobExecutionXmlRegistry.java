/*******************************************************************************
 * Copyright (c) 2019, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.queue;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

/**
 * {@link QueuedJobExecution} xml factory class
 *
 * @since 1.1.0
 */
@XmlRegistry
public class QueuedJobExecutionXmlRegistry {

    private final QueuedJobExecutionFactory queuedJobExecutionFactory = KapuaLocator.getInstance().getFactory(QueuedJobExecutionFactory.class);

    /**
     * Creates a new job instance
     *
     * @return
     */
    public QueuedJobExecution newQueuedJobExecution() {
        return queuedJobExecutionFactory.newEntity(null);
    }
}
