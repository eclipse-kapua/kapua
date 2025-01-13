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
package org.eclipse.kapua.service.job.targets;

import javax.xml.bind.annotation.XmlRegistry;

import org.eclipse.kapua.locator.KapuaLocator;

/**
 * {@link JobTarget} xml factory class
 *
 * @since 1.0
 */
@XmlRegistry
public class JobTargetXmlRegistry {

    private final JobTargetFactory jobTargetFactory = KapuaLocator.getInstance().getFactory(JobTargetFactory.class);

    /**
     * Creates a new job instance
     *
     * @return
     */
    public JobTarget newJobTarget() {
        return jobTargetFactory.newEntity(null);
    }

    /**
     * Creates a new job creator instance
     *
     * @return
     */
    public JobTargetCreator newJobTargetCreator() {
        return jobTargetFactory.newCreator(null);
    }

    public JobTargetQuery newQuery() {
        return jobTargetFactory.newQuery(null);
    }
}
