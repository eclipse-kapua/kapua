/*******************************************************************************
 * Copyright (c) 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.service.authentication.authentication;

import org.eclipse.kapua.client.security.bean.AclUtils;
import org.eclipse.kapua.client.security.bean.AuthAcl;
import org.eclipse.kapua.client.security.bean.AuthAcl.Action;
import org.eclipse.kapua.commons.setting.system.SystemSetting;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class AclCreator {

    protected final static String HASH = "#";

    protected String addressClassifier;
    protected String addressClassifierHash;

    protected String aclCtrlAccReply;
    protected String aclCtrlAccCliMqttLifeCycle;
    protected String aclCtrlAcc;
    protected String aclCtrlAccCli;
    protected String aclDataAcc;
    protected String aclDataAccCli;
    protected String aclCtrlAccNotify;

    protected List<String> authDestinations = new ArrayList<>();

    @Inject
    public AclCreator() {
        addressClassifier = SystemSetting.getInstance().getMessageClassifier();
        addressClassifierHash = addressClassifier + "/" + HASH;
        aclCtrlAccReply = addressClassifier + "/{0}/+/+/REPLY/#";
        aclCtrlAccCliMqttLifeCycle = addressClassifier + "/{0}/{1}/MQTT/#";
        aclCtrlAcc = addressClassifier + "/{0}/#";
        aclCtrlAccCli = addressClassifier + "/{0}/{1}/#";
        aclDataAcc = "{0}/#";
        aclDataAccCli = "{0}/{1}/#";
        aclCtrlAccNotify = addressClassifier + "/{0}/+/+/NOTIFY/{1}/#";
    }

    public List<AuthAcl> buildAcls(boolean[] permission, String accountName, String clientId, StringBuilder aclDestinationsLog) {
        List<AuthAcl> ael = new ArrayList<>();
        if (permission[UserPermissions.DEVICE_MANAGE_IDX]) {
            ael.add(createAuthorizationEntry(Action.all, formatAcl(aclCtrlAcc, accountName), aclDestinationsLog));
        } else {
            ael.add(createAuthorizationEntry(Action.all, formatAclFull(aclCtrlAccCli, accountName, clientId), aclDestinationsLog));
        }
        if (permission[UserPermissions.DATA_MANAGE_IDX]) {
            ael.add(createAuthorizationEntry(Action.all, formatAcl(aclDataAcc, accountName), aclDestinationsLog));
        } else if (permission[UserPermissions.DATA_VIEW_IDX]) {
            ael.add(createAuthorizationEntry(Action.readAdmin, formatAcl(aclDataAcc, accountName), aclDestinationsLog));
            ael.add(createAuthorizationEntry(Action.write, formatAclFull(aclDataAccCli, accountName, clientId), aclDestinationsLog));
        } else {
            ael.add(createAuthorizationEntry(Action.all, formatAclFull(aclDataAccCli, accountName, clientId), aclDestinationsLog));
        }
        ael.add(createAuthorizationEntry(Action.writeAdmin, formatAcl(aclCtrlAccReply, accountName), aclDestinationsLog));
        // Write notify to any client Id and any application and operation
        ael.add(createAuthorizationEntry(Action.writeAdmin, formatAclFull(aclCtrlAccNotify, accountName, clientId), aclDestinationsLog));
        return ael;
    }

    public List<AuthAcl> buildAdminAcls(String accountName, String clientId, StringBuilder aclDestinationsLog) {
        List<AuthAcl> ael = new ArrayList<>();
        ael.add(createAuthorizationEntry(Action.all, HASH, aclDestinationsLog));
        ael.add(createAuthorizationEntry(Action.all, addressClassifierHash, aclDestinationsLog));
        return ael;
    }

    protected String formatAcl(String pattern, String accountName) {
        return MessageFormat.format(pattern, accountName);
    }

    protected String formatAclFull(String pattern, String accountName, String clientId) {
        return MessageFormat.format(pattern, accountName, clientId);
    }

    protected AuthAcl createAuthorizationEntry(Action action, String address, StringBuilder aclDestinationsLog) {
        AuthAcl entry = new AuthAcl(address, action);
        aclDestinationsLog.append(AclUtils.isRead(action) ? "r" : "_")
                .append("/")
                .append(AclUtils.isWrite(action) ? "w" : "_")
                .append("/")
                .append(AclUtils.isAdmin(action) ? "a" : "_")
                .append(" - ")
                .append(address)
                .append("\n");
        return entry;
    }

}