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
package org.eclipse.kapua.app.console.module.device.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.server.util.KapuaExceptionHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.device.shared.model.management.keystore.GwtDeviceKeystore;
import org.eclipse.kapua.app.console.module.device.shared.model.management.keystore.GwtDeviceKeystoreCertificate;
import org.eclipse.kapua.app.console.module.device.shared.model.management.keystore.GwtDeviceKeystoreCsr;
import org.eclipse.kapua.app.console.module.device.shared.model.management.keystore.GwtDeviceKeystoreItem;
import org.eclipse.kapua.app.console.module.device.shared.model.management.keystore.GwtDeviceKeystoreKeypair;
import org.eclipse.kapua.app.console.module.device.shared.service.GwtDeviceKeystoreManagementService;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.device.management.keystore.DeviceKeystoreManagementService;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystore;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSR;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCSRInfo;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreCertificate;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItem;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItemQuery;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreItems;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystoreKeypair;
import org.eclipse.kapua.service.device.management.keystore.model.DeviceKeystores;

/**
 * The server side implementation of the Device RPC service.
 */
public class GwtDeviceKeystoreManagementServiceImpl extends KapuaRemoteServiceServlet implements GwtDeviceKeystoreManagementService {

    private static final long serialVersionUID = -1391026997499175151L;

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final DeviceKeystoreManagementService DEVICE_KEYSTORE_MANAGEMENT_SERVICE = LOCATOR.getService(DeviceKeystoreManagementService.class);

    @Override
    public List<GwtDeviceKeystore> getKeystores(String scopeIdString, String deviceIdString) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            DeviceKeystores deviceKeystores = DEVICE_KEYSTORE_MANAGEMENT_SERVICE.getKeystores(scopeId, deviceId, null);

            List<GwtDeviceKeystore> gwtDeviceKeystores = new ArrayList<GwtDeviceKeystore>();
            for (DeviceKeystore deviceKeystore : deviceKeystores.getKeystores()) {

                GwtDeviceKeystore gwtDeviceKeystore = new GwtDeviceKeystore();
                gwtDeviceKeystore.setId(deviceKeystore.getId());
                gwtDeviceKeystore.setKeystoreType(deviceKeystore.getKeystoreType());
                gwtDeviceKeystore.setSize(deviceKeystore.getSize());

                gwtDeviceKeystores.add(gwtDeviceKeystore);
            }

            return gwtDeviceKeystores;
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public List<GwtDeviceKeystoreItem> getKeystoreItems(String scopeIdString, String deviceIdString) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            DeviceKeystoreItems deviceKeystoreItems = DEVICE_KEYSTORE_MANAGEMENT_SERVICE.getKeystoreItems(scopeId, deviceId, null);

            List<GwtDeviceKeystoreItem> gwtDeviceKeystores = new ArrayList<GwtDeviceKeystoreItem>();
            for (DeviceKeystoreItem deviceKeystoreItem : deviceKeystoreItems.getKeystoreItems()) {
                gwtDeviceKeystores.add(convert(deviceKeystoreItem));
            }

            return gwtDeviceKeystores;
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public List<GwtDeviceKeystoreItem> getKeystoreItems(String scopeIdString, String deviceIdString, String keystoreId) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            DeviceKeystoreItemQuery itemQuery = new DeviceKeystoreItemQuery();
            itemQuery.setKeystoreId(keystoreId);

            DeviceKeystoreItems deviceKeystores = DEVICE_KEYSTORE_MANAGEMENT_SERVICE.getKeystoreItems(scopeId, deviceId, itemQuery, null);

            List<GwtDeviceKeystoreItem> gwtDeviceKeystores = new ArrayList<GwtDeviceKeystoreItem>();
            for (DeviceKeystoreItem deviceKeystoreItem : deviceKeystores.getKeystoreItems()) {
                gwtDeviceKeystores.add(convert(deviceKeystoreItem));
            }

            return gwtDeviceKeystores;
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public GwtDeviceKeystoreItem getKeystoreItem(String scopeIdString, String deviceIdString, String keystoreId, String alias) throws GwtKapuaException {
        try {
            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            DeviceKeystoreItem deviceKeystoreItem = DEVICE_KEYSTORE_MANAGEMENT_SERVICE.getKeystoreItem(scopeId, deviceId, keystoreId, alias, null);

            return convert(deviceKeystoreItem);
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public void createKeystoreCertificateRaw(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, GwtDeviceKeystoreCertificate gwtKeystoreCertificate) throws GwtKapuaException {
        try {
            checkXSRFToken(xsrfToken);

            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            DeviceKeystoreCertificate deviceKeystoreCertificate = new DeviceKeystoreCertificate();
            deviceKeystoreCertificate.setKeystoreId(gwtKeystoreCertificate.getKeystoreId());
            deviceKeystoreCertificate.setAlias(gwtKeystoreCertificate.getAlias());
            deviceKeystoreCertificate.setCertificate(gwtKeystoreCertificate.getCertificate());

            DEVICE_KEYSTORE_MANAGEMENT_SERVICE.createKeystoreCertificate(scopeId, deviceId, deviceKeystoreCertificate, null);
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public void createKeystoreCertificateInfo(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, String keystoreId, String alias, String certificateInfoIdString)
            throws GwtKapuaException {
        try {
            checkXSRFToken(xsrfToken);

            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);
            KapuaId certificateInfoId = KapuaEid.parseCompactId(certificateInfoIdString);

            DEVICE_KEYSTORE_MANAGEMENT_SERVICE.createKeystoreCertificate(scopeId, deviceId, keystoreId, alias, certificateInfoId, null);
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public void createKeystoreKeypair(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, GwtDeviceKeystoreKeypair gwtKeystoreKeypair) throws GwtKapuaException {
        try {
            checkXSRFToken(xsrfToken);

            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            DeviceKeystoreKeypair deviceKeystoreKeypair = new DeviceKeystoreKeypair();
            deviceKeystoreKeypair.setKeystoreId(gwtKeystoreKeypair.getKeystoreId());
            deviceKeystoreKeypair.setAlias(gwtKeystoreKeypair.getAlias());
            deviceKeystoreKeypair.setAlgorithm(gwtKeystoreKeypair.getAlgorithm());
            deviceKeystoreKeypair.setSize(gwtKeystoreKeypair.getSize());
            deviceKeystoreKeypair.setSignatureAlgorithm(gwtKeystoreKeypair.getSignatureAlgorithm());
            deviceKeystoreKeypair.setAttributes(gwtKeystoreKeypair.getAttributes());

            DEVICE_KEYSTORE_MANAGEMENT_SERVICE.createKeystoreKeypair(scopeId, deviceId, deviceKeystoreKeypair, null);
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public GwtDeviceKeystoreCertificate createKeystoreCsr(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, GwtDeviceKeystoreCsr gwtKeystoreCsr) throws GwtKapuaException {
        try {
            checkXSRFToken(xsrfToken);

            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            DeviceKeystoreCSRInfo deviceKeystoreCsrInfo = new DeviceKeystoreCSRInfo();
            deviceKeystoreCsrInfo.setKeystoreId(gwtKeystoreCsr.getKeystoreId());
            deviceKeystoreCsrInfo.setAlias(gwtKeystoreCsr.getAlias());
            deviceKeystoreCsrInfo.setSignatureAlgorithm(gwtKeystoreCsr.getSignatureAlgorithm());
            deviceKeystoreCsrInfo.setAttributes(gwtKeystoreCsr.getAttributes());

            DeviceKeystoreCSR deviceKeystoreCSR = DEVICE_KEYSTORE_MANAGEMENT_SERVICE.createKeystoreCSR(scopeId, deviceId, deviceKeystoreCsrInfo, null);

            GwtDeviceKeystoreCertificate gwtKeystoreCertificate = new GwtDeviceKeystoreCertificate();
            gwtKeystoreCertificate.setKeystoreId(gwtKeystoreCsr.getKeystoreId());
            gwtKeystoreCertificate.setAlias(gwtKeystoreCsr.getAlias());
            gwtKeystoreCertificate.setCertificate(deviceKeystoreCSR.getSigningRequest());

            return gwtKeystoreCertificate;
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    @Override
    public void deleteKeystoreItem(GwtXSRFToken xsrfToken, String scopeIdString, String deviceIdString, String keystoreId, String alias) throws GwtKapuaException {
        try {
            checkXSRFToken(xsrfToken);

            KapuaId scopeId = KapuaEid.parseCompactId(scopeIdString);
            KapuaId deviceId = KapuaEid.parseCompactId(deviceIdString);

            DEVICE_KEYSTORE_MANAGEMENT_SERVICE.deleteKeystoreItem(scopeId, deviceId, keystoreId, alias, null);
        } catch (Exception exception) {
            throw KapuaExceptionHandler.buildExceptionFromError(exception);
        }
    }

    private GwtDeviceKeystoreItem convert(DeviceKeystoreItem deviceKeystoreItem) {
        GwtDeviceKeystoreItem gwtDeviceKeystoreItem = new GwtDeviceKeystoreItem();

        gwtDeviceKeystoreItem.setKeystoreId(deviceKeystoreItem.getKeystoreId());
        gwtDeviceKeystoreItem.setItemType(deviceKeystoreItem.getItemType());
        gwtDeviceKeystoreItem.setSize(deviceKeystoreItem.getSize());
        gwtDeviceKeystoreItem.setAlgorithm(deviceKeystoreItem.getAlgorithm());
        gwtDeviceKeystoreItem.setAlias(deviceKeystoreItem.getAlias());
        gwtDeviceKeystoreItem.setSubjectDN(deviceKeystoreItem.getSubjectDN());
        gwtDeviceKeystoreItem.setIssuer(deviceKeystoreItem.getIssuer());
        gwtDeviceKeystoreItem.setNotBefore(deviceKeystoreItem.getNotBefore());
        gwtDeviceKeystoreItem.setNotAfter(deviceKeystoreItem.getNotAfter());
        gwtDeviceKeystoreItem.setCertificate(deviceKeystoreItem.getCertificate());
        gwtDeviceKeystoreItem.setCertificateChain(deviceKeystoreItem.getCertificateChain());

        return gwtDeviceKeystoreItem;
    }
}
