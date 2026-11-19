/*******************************************************************************
 * Copyright (c) 2024, 2024 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.job.engine.jbatch.overrides.callback;

import com.ibm.jbatch.container.callback.IJobEndCallbackService;
import com.ibm.jbatch.container.callback.JobEndCallback;
import com.ibm.jbatch.container.callback.JobEndCallbackManagerImpl;

/**
 * Kapua {@link IJobEndCallbackService} which extends default {@link JobEndCallbackManagerImpl} to register custom {@link JobEndCallback}s
 *
 * @since 2.1.0
 */
public class KapuaJobEndCallbackManagerImpl extends JobEndCallbackManagerImpl implements IJobEndCallbackService {

    /**
     * Constructor
     *
     * @since 2.1.0
     */
    public KapuaJobEndCallbackManagerImpl() {
        super();

        // Required to delete JobInstanceData after JobExecution ends
        registerJobEndCallback(new JobDataCleanupJobEndCallback());
    }
}
