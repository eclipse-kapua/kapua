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
package org.eclipse.kapua.app.console.core.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaIllegalAccessException;
import org.eclipse.kapua.KapuaIllegalArgumentException;
import org.eclipse.kapua.KapuaUnauthenticatedException;
import org.eclipse.kapua.app.console.core.shared.model.KapuaFormFields;
import org.eclipse.kapua.app.console.module.api.server.KapuaRemoteServiceServlet;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSetting;
import org.eclipse.kapua.app.console.module.api.setting.ConsoleSettingKeys;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;
import org.eclipse.kapua.service.device.management.exception.DeviceManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServlet extends KapuaHttpServlet {

    private static final long serialVersionUID = -5016170117606322129L;
    private static final Logger logger = LoggerFactory.getLogger(FileServlet.class);

    private static final String SCOPE_ID_STRING = "scopeIdString";
    private static final String DEVICE_ID_STRING = "deviceIdString";

    private final DeviceConfigurationManagementService deviceConfigurationManagementService;
    private final DeviceCommandManagementService deviceCommandManagementService;
    private final ConsoleSetting config;

    public FileServlet() {
        //Injection not supported here, unfortunately
        KapuaLocator locator = KapuaLocator.getInstance();
        deviceConfigurationManagementService = locator.getService(DeviceConfigurationManagementService.class);
        deviceCommandManagementService = locator.getService(DeviceCommandManagementService.class);
        config = ConsoleSetting.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(CharEncoding.UTF_8);

        String reqPathInfo = req.getPathInfo();
        if (reqPathInfo == null) {
            resp.sendError(404);
            return;
        }

        // BEGIN XSRF - Servlet dependent code
        KapuaFormFields kapuaFormFields = getFormFields(req);
        try {
            GwtXSRFToken token = new GwtXSRFToken(kapuaFormFields.get("xsrfToken"));
            KapuaRemoteServiceServlet.checkXSRFToken(req, token);
        } catch (Exception e) {
            throw new ServletException("Security error: please retry this operation correctly.", e);
        }
        // END XSRF security check

        if (reqPathInfo.equals("/command")) {
            doPostCommand(kapuaFormFields, resp);
        } else if (reqPathInfo.equals("/configuration/snapshot")) {
            doPostConfigurationSnapshot(kapuaFormFields, resp);
        } else {
            resp.sendError(404);
        }
    }

    private void doPostConfigurationSnapshot(KapuaFormFields kapuaFormFields, HttpServletResponse resp)
            throws IOException {
        try {
            List<FileItem> fileItems = kapuaFormFields.getFileItems();
            String scopeIdString = kapuaFormFields.get(SCOPE_ID_STRING);
            String deviceIdString = kapuaFormFields.get(DEVICE_ID_STRING);

            if (scopeIdString == null || scopeIdString.isEmpty()) {
                throw new IllegalArgumentException(SCOPE_ID_STRING);
            }

            if (deviceIdString == null || deviceIdString.isEmpty()) {
                throw new IllegalArgumentException(DEVICE_ID_STRING);
            }

            if (fileItems == null || fileItems.size() != 1) {
                throw new IllegalArgumentException("configuration");
            }

            FileItem fileItem = fileItems.get(0);
            byte[] data = fileItem.get();
            String xmlConfigurationString = new String(data, CharEncoding.UTF_8);

            deviceConfigurationManagementService.put(KapuaEid.parseCompactId(scopeIdString),
                    KapuaEid.parseCompactId(deviceIdString),
                    xmlConfigurationString,
                    null);

        } catch (IllegalArgumentException iae) {
            resp.sendError(400, "Illegal value for query parameter: " + iae.getMessage());
        } catch (KapuaEntityNotFoundException eenfe) {
            resp.sendError(400, eenfe.getMessage());
        } catch (KapuaUnauthenticatedException eiae) {
            resp.sendError(401, eiae.getMessage());
        } catch (KapuaIllegalAccessException eiae) {
            resp.sendError(403, eiae.getMessage());
        } catch (DeviceManagementException edme) {
            logger.error("Device management exception", edme);
            resp.sendError(404, edme.getMessage());
        } catch (KapuaIllegalArgumentException kiae) {
            logger.error("Illegal argument exception", kiae);
            resp.sendError(400, kiae.getArgumentName());
        } catch (Exception e) {
            logger.error("Generic error: {}", e.getMessage(), e);
            resp.sendError(500, e.getMessage());
        }
    }

    private void doPostCommand(KapuaFormFields kapuaFormFields, HttpServletResponse resp)
            throws IOException {
        try {
            List<FileItem> fileItems = kapuaFormFields.getFileItems();

            final String scopeIdString = kapuaFormFields.get(SCOPE_ID_STRING);
            final String deviceIdString = kapuaFormFields.get(DEVICE_ID_STRING);
            final String command = kapuaFormFields.get("command");
            final String password = kapuaFormFields.get("password");
            final String timeoutString = kapuaFormFields.get("timeout");

            if (scopeIdString == null || scopeIdString.isEmpty()) {
                throw new IllegalArgumentException("scopeId");
            }

            if (deviceIdString == null || deviceIdString.isEmpty()) {
                throw new IllegalArgumentException("deviceId");
            }

            if (command == null || command.isEmpty()) {
                throw new IllegalArgumentException("command");
            }

            if (fileItems.size() > 1) {
                throw new IllegalArgumentException("file");
            }

            Integer timeout = null;
            if (timeoutString != null && !timeoutString.isEmpty()) {
                timeout = Integer.parseInt(timeoutString);
            }

            // FIXME: set a max size on the MQtt payload
            byte[] data = fileItems.isEmpty() ? null : fileItems.get(0).get();

            DeviceCommandInput commandInput = new DeviceCommandInput();

            StringTokenizer st = new StringTokenizer(command);
            int count = st.countTokens();

            String cmd = count > 0 ? st.nextToken() : null;
            String[] args = count > 1 ? new String[count - 1] : null;
            int i = 0;
            /* if count == 0 args is null but st will have no tokens so the while loop won't trigger
             Sonar complains that args may not be null: in fact it will never be, but let's make him happy and test
             for args != null as well */
            while (st.hasMoreTokens() && args != null) {
                args[i++] = st.nextToken();
            }

            commandInput.setCommand(cmd);
            commandInput.setPassword(password == null || password.isEmpty() ? null : password);
            commandInput.setArguments(args);
            commandInput.setTimeout(timeout);
            commandInput.setWorkingDir("/tmp/");
            commandInput.setBody(data);

            DeviceCommandOutput deviceCommandOutput = deviceCommandManagementService.exec(KapuaEid.parseCompactId(scopeIdString),
                    KapuaEid.parseCompactId(deviceIdString),
                    commandInput,
                    null);
            resp.setContentType("text/plain");
            if (deviceCommandOutput.getStderr() != null &&
                    deviceCommandOutput.getStderr().length() > 0) {
                resp.getWriter().println(deviceCommandOutput.getStderr());
            }
            if (deviceCommandOutput.getStdout() != null &&
                    deviceCommandOutput.getStdout().length() > 0) {
                resp.getWriter().println(deviceCommandOutput.getStdout());
            }
            if (deviceCommandOutput.getExceptionMessage() != null &&
                    deviceCommandOutput.getExceptionMessage().length() > 0) {
                resp.getWriter().println(deviceCommandOutput.getExceptionMessage());
            }

        } catch (IllegalArgumentException iae) {
            resp.sendError(400, "Illegal value for query parameter: " + iae.getMessage());
        } catch (KapuaEntityNotFoundException eenfe) {
            resp.sendError(400, eenfe.getMessage());
        } catch (KapuaUnauthenticatedException eiae) {
            resp.sendError(401, eiae.getMessage());
        } catch (KapuaIllegalAccessException eiae) {
            resp.sendError(403, eiae.getMessage());
        } catch (DeviceManagementException edme) {
            logger.error("Device management exception", edme);
            resp.sendError(404, edme.getMessage());
        } catch (Exception e) {
            logger.error("Generic error: {}", e.getMessage(), e);
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String reqPathInfo = request.getPathInfo();

        if (reqPathInfo == null) {
            response.sendError(404);
            return;
        }

        if (reqPathInfo.startsWith("/icons")) {
            doGetIconResource(request, response);
        } else {
            response.sendError(404);
        }
    }

    private void doGetIconResource(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        final String id = request.getParameter("id");

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id");
        }

        if (!id.matches("[0-9A-Za-z]{1,}")) {
            throw new IllegalArgumentException("id");
        }

        String tmpPath = System.getProperty("java.io.tmpdir");

        StringBuilder filePathSb = new StringBuilder(tmpPath);

        if (!tmpPath.endsWith("/")) {
            filePathSb.append("/");
        }

        filePathSb.append(config.getString(ConsoleSettingKeys.DEVICE_CONFIGURATION_ICON_FOLDER))
                .append("/")
                .append(id);

        File requestedFile = new File(filePathSb.toString());
        if (!requestedFile.exists()) {
            throw new IllegalArgumentException("id");
        }

        response.setContentType("application/octet-stream");
        response.setCharacterEncoding(CharEncoding.UTF_8);
        OutputStream out = response.getOutputStream();
        IOUtils.copy(new FileInputStream(requestedFile), out);
    }
}
