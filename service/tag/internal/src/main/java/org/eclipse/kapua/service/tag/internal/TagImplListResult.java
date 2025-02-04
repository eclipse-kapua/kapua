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
package org.eclipse.kapua.service.tag.internal;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.query.KapuaListResult;
import org.eclipse.kapua.service.tag.Tag;

/**
 * {@link Tag} list result definition.
 *
 * @since 1.0.0
 */
@XmlRootElement(name = "tags")
@XmlType
public class TagImplListResult extends KapuaListResult<TagImpl> {

    private static final long serialVersionUID = 791258978210911362L;

}
