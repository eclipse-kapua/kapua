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
package org.eclipse.kapua.job.engine.exception;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "jobStartingExceptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobStartingExceptionInfo extends JobScopedEngineExceptionInfo {

    /**
     * Constructor.
     *
     * @since 1.0.0
     */
    protected JobStartingExceptionInfo() {
        super();
    }

    /**
     * Constructor.
     *
     * @param jobStartingException
     *         The root exception.
     * @since 1.0.0
     */
    public JobStartingExceptionInfo(JobStartingException jobStartingException, boolean showStackTrace) {
        super(500/*Status.INTERNAL_SERVER_ERROR*/, jobStartingException, showStackTrace);
    }

}
