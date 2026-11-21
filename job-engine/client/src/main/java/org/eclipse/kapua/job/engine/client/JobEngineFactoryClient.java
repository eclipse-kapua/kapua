/*******************************************************************************
 * Copyright (c) 2021, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.client;

import org.eclipse.kapua.job.engine.JobEngineFactory;
import org.eclipse.kapua.job.engine.JobStartOptions;

import javax.inject.Singleton;

/**
 * {@link JobEngineFactory} remote client implementation
 *
 * @since 1.5.0
 */
@Singleton
public class JobEngineFactoryClient implements JobEngineFactory {

    @Override
    public JobStartOptions newJobStartOptions() {
        return new JobStartOptionsClient();
    } //todo : code smell

}
