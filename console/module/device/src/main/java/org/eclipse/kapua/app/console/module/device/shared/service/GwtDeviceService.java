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
package org.eclipse.kapua.app.console.module.device.shared.service;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceCreator;
import org.eclipse.kapua.app.console.module.device.shared.model.GwtDeviceQuery;
import org.eclipse.kapua.app.console.module.device.shared.model.event.GwtDeviceEvent;

import java.util.Date;
import java.util.List;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("device")
public interface GwtDeviceService extends RemoteService {

    /**
     * Finds device given its deviceId
     *
     * @param scopeIdString
     * @param deviceIdString
     * @return
     */
    GwtDevice findDevice(String scopeIdString, String deviceIdString)
            throws GwtKapuaException;

    /**
     * Finds devices in an account with query
     *
     * @param loadConfig
     * @param gwtDeviceQuery
     * @return
     */
    PagingLoadResult<GwtDevice> query(PagingLoadConfig loadConfig, GwtDeviceQuery gwtDeviceQuery)
            throws GwtKapuaException;

    /**
     * Finds devices in an account with query
     *
     * @param gwtDeviceQuery
     * @return
     */
    List<GwtDevice> query(GwtDeviceQuery gwtDeviceQuery)
            throws GwtKapuaException;

    /**
     * Creates a device entry for the account
     *
     * @param xsrfToken
     * @param gwtDeviceCreator
     * @return
     * @throws GwtKapuaException
     */
    GwtDevice createDevice(GwtXSRFToken xsrfToken, GwtDeviceCreator gwtDeviceCreator)
            throws GwtKapuaException;

    /**
     * Updates a device entity with the given gwtDevice new values for custom attributes, display name and associated tag
     *
     * @param gwtDevice
     * @return
     * @throws GwtKapuaException
     */
    GwtDevice updateAttributes(GwtXSRFToken xsfrToken, GwtDevice gwtDevice)
            throws GwtKapuaException;

    /**
     * Returns a list of device events for a specified device within a specified date range.
     *
     * @param loadConfig
     * @param gwtDevice  the device to return the events of
     * @param startDate  the start of the date range in milliseconds since epoch (Date.getTime())
     * @param endDate    the end of the date range in milliseconds since epoch (Date.getTime())
     * @return
     * @throws GwtKapuaException
     */
    PagingLoadResult<GwtDeviceEvent> findDeviceEvents(PagingLoadConfig loadConfig, GwtDevice gwtDevice, Date startDate, Date endDate)
            throws GwtKapuaException;

    ListLoadResult<GwtGroupedNVPair> findDeviceProfile(String scopeIdString, String clientId)
            throws GwtKapuaException;

    /**
     * Deletes a device
     *
     * @param xsfrToken
     * @param scopeIdString
     * @param clientId
     */
    void deleteDevice(GwtXSRFToken xsfrToken, String scopeIdString, String clientId)
            throws GwtKapuaException;

    // Groups

    void addDeviceGroup(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, String groupIdString) throws GwtKapuaException;

    void deleteDeviceGroup(GwtXSRFToken xsfrToken, String scopeIdString, String deviceIdString, String groupIdString) throws GwtKapuaException;

    // Tags

    /**
     * Adds the association between the Tag and the Device
     *
     * @param xsrfToken
     * @param scopeIdString
     * @param deviceIdString
     * @param tagIdString
     */
    void addDeviceTag(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, String tagIdString) throws GwtKapuaException;

    /**
     * Removes the association between the Tag and the Device
     *
     * @param xsfrToken
     * @param scopeIdString
     * @param deviceId
     * @param tagId
     */
    void deleteDeviceTag(GwtXSRFToken xsfrToken, String scopeIdString, String deviceId, String tagId) throws GwtKapuaException;

    /**
     * Return Maps Tile Endpoint
     */
    String getTileEndpoint()
            throws GwtKapuaException;

    /**
     * Returns whether the Map tab is enabled or not
     *
     * @return whether the Map tab is enabled or not
     */
    boolean isMapEnabled()
            throws GwtKapuaException;
}
