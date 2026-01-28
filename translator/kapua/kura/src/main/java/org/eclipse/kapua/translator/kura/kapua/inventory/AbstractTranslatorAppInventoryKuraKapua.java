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
package org.eclipse.kapua.translator.kura.kapua.inventory;

import org.eclipse.kapua.service.device.call.kura.model.inventory.InventoryMetrics;
import org.eclipse.kapua.service.device.call.kura.model.inventory.KuraInventoryItems;
import org.eclipse.kapua.service.device.call.kura.model.inventory.bundles.KuraInventoryBundles;
import org.eclipse.kapua.service.device.call.kura.model.inventory.containers.KuraInventoryContainers;
import org.eclipse.kapua.service.device.call.kura.model.inventory.images.KuraInventoryImages;
import org.eclipse.kapua.service.device.call.kura.model.inventory.packages.KuraInventoryPackages;
import org.eclipse.kapua.service.device.call.kura.model.inventory.system.KuraInventorySystemPackages;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.kura.app.response.KuraResponseMessage;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.inventory.DeviceInventoryManagementFactory;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryResponseChannel;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryResponseMessage;
import org.eclipse.kapua.service.device.management.inventory.internal.message.InventoryResponsePayload;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundle;
import org.eclipse.kapua.service.device.management.inventory.model.bundle.DeviceInventoryBundles;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainer;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainerState;
import org.eclipse.kapua.service.device.management.inventory.model.container.DeviceInventoryContainers;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImage;
import org.eclipse.kapua.service.device.management.inventory.model.image.DeviceInventoryImages;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventory;
import org.eclipse.kapua.service.device.management.inventory.model.inventory.DeviceInventoryItem;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackage;
import org.eclipse.kapua.service.device.management.inventory.model.packages.DeviceInventoryPackages;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackage;
import org.eclipse.kapua.service.device.management.inventory.model.system.DeviceInventorySystemPackages;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.InvalidChannelException;
import org.eclipse.kapua.translator.kura.kapua.AbstractSimpleTranslatorResponseKuraKapua;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * {@link Translator} {@code abstract} implementation from {@link KuraResponseMessage} to {@link InventoryResponseMessage}
 *
 * @since 1.5.0
 */
public class AbstractTranslatorAppInventoryKuraKapua<M extends InventoryResponseMessage> extends AbstractSimpleTranslatorResponseKuraKapua<InventoryResponseChannel, InventoryResponsePayload, M> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTranslatorAppInventoryKuraKapua.class);
    private final DeviceInventoryManagementFactory deviceInventoryFactory;

    /**
     * Constructor.
     *
     * @param deviceInventoryFactory
     * @param responseMessageClass   The type of the {@link InventoryResponseMessage}.
     * @since 1.5.0
     */
    @Inject
    public AbstractTranslatorAppInventoryKuraKapua(DeviceManagementSetting deviceManagementSetting, DeviceInventoryManagementFactory deviceInventoryFactory, Class<M> responseMessageClass) {
        super(deviceManagementSetting, responseMessageClass, InventoryResponsePayload.class);
        this.deviceInventoryFactory = deviceInventoryFactory;
    }

    @Override
    protected InventoryResponseChannel translateChannel(KuraResponseChannel kuraResponseChannel) throws InvalidChannelException {
        try {
            translatorKuraKapuaUtils.validateKuraResponseChannel(kuraResponseChannel, InventoryMetrics.APP_ID, InventoryMetrics.APP_VERSION);

            return new InventoryResponseChannel();
        } catch (Exception e) {
            throw new InvalidChannelException(e, kuraResponseChannel);
        }
    }

    /**
     * Translates {@link KuraInventoryItems} to {@link DeviceInventory}
     *
     * @param kuraInventoryItems The {@link KuraInventoryItems} to translate.
     * @return The translated {@link DeviceInventory}.
     * @since 1.5.0
     */
    protected DeviceInventory translate(KuraInventoryItems kuraInventoryItems) {
        DeviceInventory deviceInventory = deviceInventoryFactory.newDeviceInventory();

        kuraInventoryItems.getInventoryItems().forEach(kuraInventoryItem -> {
            DeviceInventoryItem deviceInventoryItem = deviceInventoryFactory.newDeviceInventoryItem();
            deviceInventoryItem.setName(kuraInventoryItem.getName());
            deviceInventoryItem.setVersion(kuraInventoryItem.getVersion());
            deviceInventoryItem.setItemType(kuraInventoryItem.getType());

            deviceInventory.addInventoryItem(deviceInventoryItem);
        });

        return deviceInventory;
    }

    /**
     * Translates {@link KuraInventoryBundles} to {@link DeviceInventoryBundles}
     *
     * @param kuraInventoryBundles The {@link KuraInventoryBundles} to translate.
     * @return The translated {@link DeviceInventoryBundles}.
     * @since 1.5.0
     */
    protected DeviceInventoryBundles translate(KuraInventoryBundles kuraInventoryBundles) {
        DeviceInventoryBundles deviceInventoryBundles = deviceInventoryFactory.newDeviceInventoryBundles();

        kuraInventoryBundles.getInventoryBundles().forEach(kuraInventoryBundle -> {
            DeviceInventoryBundle deviceInventoryBundle = deviceInventoryFactory.newDeviceInventoryBundle();
            deviceInventoryBundle.setId(String.valueOf(kuraInventoryBundle.getId()));
            deviceInventoryBundle.setName(kuraInventoryBundle.getName());
            deviceInventoryBundle.setVersion(kuraInventoryBundle.getVersion());
            deviceInventoryBundle.setStatus(kuraInventoryBundle.getState());
            deviceInventoryBundle.setSigned(kuraInventoryBundle.getSigned());

            deviceInventoryBundles.addInventoryBundle(deviceInventoryBundle);
        });

        return deviceInventoryBundles;
    }

    /**
     * Translates {@link KuraInventoryImages} to {@link DeviceInventoryImages}
     *
     * @param kuraInventoryImages The {@link KuraInventoryImages} to translate.
     * @return The translated {@link DeviceInventoryImages}.
     * @since 2.0.0
     */
    protected DeviceInventoryImages translate(KuraInventoryImages kuraInventoryImages) {
        DeviceInventoryImages deviceInventoryImages = deviceInventoryFactory.newDeviceInventoryImages();

        kuraInventoryImages.getInventoryImages().forEach(kuraInventoryImage -> {
            DeviceInventoryImage deviceInventoryImage = deviceInventoryFactory.newDeviceInventoryImage();
            deviceInventoryImage.setName(kuraInventoryImage.getName());
            deviceInventoryImage.setVersion(kuraInventoryImage.getVersion());
            deviceInventoryImage.setImageType(kuraInventoryImage.getType());

            deviceInventoryImages.addInventoryImage(deviceInventoryImage);
        });

        return deviceInventoryImages;
    }

    /**
     * Translates {@link KuraInventoryContainers} to {@link DeviceInventoryContainers}
     *
     * @param kuraInventoryContainers The {@link KuraInventoryContainers} to translate.
     * @return The translated {@link DeviceInventoryContainers}.
     * @since 2.0.0
     */
    protected DeviceInventoryContainers translate(KuraInventoryContainers kuraInventoryContainers) {
        DeviceInventoryContainers deviceInventoryContainers = deviceInventoryFactory.newDeviceInventoryContainers();

        kuraInventoryContainers.getInventoryContainers().forEach(kuraInventoryContainer -> {
            DeviceInventoryContainer deviceInventoryContainer = deviceInventoryFactory.newDeviceInventoryContainer();
            deviceInventoryContainer.setName(kuraInventoryContainer.getName());
            deviceInventoryContainer.setVersion(kuraInventoryContainer.getVersion());
            deviceInventoryContainer.setContainerType(kuraInventoryContainer.getType());

            if (kuraInventoryContainer.getState() != null) {
                switch (kuraInventoryContainer.getState()) {
                    case "active":
                        deviceInventoryContainer.setState(DeviceInventoryContainerState.ACTIVE);
                        break;
                    case "installed":
                        deviceInventoryContainer.setState(DeviceInventoryContainerState.INSTALLED);
                        break;
                    case "uninstalled":
                        deviceInventoryContainer.setState(DeviceInventoryContainerState.UNINSTALLED);
                        break;
                    case "unknown":
                        deviceInventoryContainer.setState(DeviceInventoryContainerState.UNKNOWN);
                        break;
                    default: {
                        LOG.warn("Unrecognised KuraInventoryContainer.state '{}' received. Defaulting to UNKNOWN state for DeviceInventoryContainer {}", kuraInventoryContainer.getState(), deviceInventoryContainer.getName());
                        deviceInventoryContainer.setState(DeviceInventoryContainerState.UNKNOWN);
                    }

                }
            } else {
                LOG.warn("Property KuraInventoryContainer.state '{}' not present. Defaulting to UNKNOWN state for DeviceInventoryContainer {}", kuraInventoryContainer.getState(), deviceInventoryContainer.getName());
                deviceInventoryContainer.setState(DeviceInventoryContainerState.UNKNOWN);
            }

            deviceInventoryContainers.addInventoryContainer(deviceInventoryContainer);
        });

        return deviceInventoryContainers;
    }

    /**
     * Translates {@link KuraInventorySystemPackages} to {@link DeviceInventorySystemPackages}
     *
     * @param kuraInventorySystemPackages The {@link KuraInventorySystemPackages} to translate.
     * @return The translated {@link DeviceInventorySystemPackages}.
     * @since 1.5.0
     */
    protected DeviceInventorySystemPackages translate(KuraInventorySystemPackages kuraInventorySystemPackages) {
        DeviceInventorySystemPackages deviceInventorySystemPackages = deviceInventoryFactory.newDeviceInventorySystemPackages();

        kuraInventorySystemPackages.getSystemPackages().forEach(kuraInventorySystemPackage -> {
            DeviceInventorySystemPackage deviceInventorySystemPackage = deviceInventoryFactory.newDeviceInventorySystemPackage();
            deviceInventorySystemPackage.setName(kuraInventorySystemPackage.getName());
            deviceInventorySystemPackage.setVersion(kuraInventorySystemPackage.getVersion());
            deviceInventorySystemPackage.setPackageType(kuraInventorySystemPackage.getType());

            deviceInventorySystemPackages.addSystemPackage(deviceInventorySystemPackage);
        });

        return deviceInventorySystemPackages;
    }

    /**
     * Translates {@link KuraInventoryPackages} to {@link DeviceInventoryPackages}
     *
     * @param kuraInventoryPackages The {@link KuraInventorySystemPackages} to translate.
     * @return The translated {@link DeviceInventorySystemPackages}.
     * @since 1.5.0
     */
    protected DeviceInventoryPackages translate(KuraInventoryPackages kuraInventoryPackages) {
        DeviceInventoryPackages deviceInventoryPackages = deviceInventoryFactory.newDeviceInventoryPackages();

        kuraInventoryPackages.getPackages().forEach(kuraInventoryPackage -> {
            DeviceInventoryPackage deviceInventoryPackage = deviceInventoryFactory.newDeviceInventoryPackage();
            deviceInventoryPackage.setName(kuraInventoryPackage.getName());
            deviceInventoryPackage.setVersion(kuraInventoryPackage.getVersion());

            kuraInventoryPackage.getPackageBundles().forEach(kuraInventoryBundle -> {
                DeviceInventoryBundle deviceInventoryBundle = deviceInventoryFactory.newDeviceInventoryBundle();
                deviceInventoryBundle.setId(String.valueOf(kuraInventoryBundle.getId()));
                deviceInventoryBundle.setName(kuraInventoryBundle.getName());
                deviceInventoryBundle.setVersion(kuraInventoryBundle.getVersion());
                deviceInventoryBundle.setStatus(kuraInventoryBundle.getState());
                deviceInventoryBundle.setSigned(kuraInventoryBundle.getSigned());

                deviceInventoryPackage.addPackageBundle(deviceInventoryBundle);
            });

            deviceInventoryPackages.addPackage(deviceInventoryPackage);
        });

        return deviceInventoryPackages;
    }
}
