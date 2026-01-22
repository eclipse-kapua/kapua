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
package org.eclipse.kapua.app.console.module.api.client.ui.dialog;

import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaErrorCode;
import org.eclipse.kapua.app.console.module.api.client.GwtKapuaException;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtSecurityTokenService;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtSecurityTokenServiceAsync;

public abstract class ActionDialog extends KapuaDialog {

    protected static final ConsoleMessages MSGS = GWT.create(ConsoleMessages.class);
    protected final GwtSecurityTokenServiceAsync gwtXSRFService = GWT.create(GwtSecurityTokenService.class);

    protected GwtXSRFToken xsrfToken;

    protected static final int FORM_LABEL_WIDTH = 120;
    public FormPanel formPanel;

    public Button submitButton;
    public Button cancelButton;
    public Status status;

    public Boolean exitStatus;
    public String exitMessage;
    private boolean dateValueNotNull;
    private boolean disabledFormPanelEvents;

    public ActionDialog() {
        super();
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);

        FormLayout formLayout = new FormLayout();
        formLayout.setLabelWidth(FORM_LABEL_WIDTH);

        formPanel = new FormPanel();
        formPanel.setPadding(0);
        formPanel.setFrame(false);
        formPanel.setHeaderVisible(false);
        formPanel.setBodyBorder(false);
        formPanel.setBorders(false);
        formPanel.setLayout(formLayout);
        formPanel.setEncoding(Encoding.MULTIPART);
        formPanel.setMethod(Method.POST);

        addListeners();

        add(formPanel);
        Listener<BaseEvent> listener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                setSubmitButtonState();
            }
        };

        final Listener<BaseEvent> pasteEventListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                if (be.getType() == Events.OnPaste) {
                    final Timer timer = new Timer() {

                        @Override
                        public void run() {
                            setSubmitButtonState();
                        }
                    };
                    timer.schedule(100);
                }
            }
        };

        new KeyNav<ComponentEvent>(formPanel) {

            @Override
            public void onKeyPress(ComponentEvent ce) {
                if (ce.getKeyCode() == KeyCodes.KEY_TAB || ce.getKeyCode() == KeyCodes.KEY_ENTER) {
                    setSubmitButtonState();
                }
            }
        };

        formPanel.addListener(Events.OnMouseUp, listener);
        formPanel.addListener(Events.OnClick, listener);
        formPanel.addListener(Events.OnKeyUp, listener);
        formPanel.addListener(Events.OnPaste, pasteEventListener);
        sinkEvents(Event.ONPASTE);

        if (disabledFormPanelEvents) {
            formPanel.disableEvents(true);
        }
        // Buttons setup
        createButtons();
    }

    /**
     * Add the form listeners
     */
    protected abstract void addListeners();

    @Override
    public void createButtons() {
        super.createButtons();

        status = new Status();
        status.setBusy(MSGS.waitMsg());
        status.hide();
        status.setAutoWidth(true);
        getButtonBar().add(status);

        getButtonBar().add(new FillToolItem());

        submitButton = new Button(getSubmitButtonText());
        submitButton.setSize(60, 25);
        submitButton.setStyleAttribute("margin-right", "2px");
        submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                preSubmit();
            }
        });

        cancelButton = new Button(getCancelButtonText());
        cancelButton.setSize(60, 25);
        cancelButton.setStyleAttribute("margin-left", "3px");
        cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                exitStatus = null;
                hide();
            }
        });

        addButton(submitButton);
        addButton(cancelButton);
    }

    protected String getSubmitButtonText() {
        return MSGS.submitButton();
    }

    protected String getCancelButtonText() {
        return MSGS.cancelButton();
    }

    protected void preSubmit() {
        gwtXSRFService.generateSecurityToken(new AsyncCallback<GwtXSRFToken>() {

            @Override
            public void onFailure(Throwable ex) {
                Timer timer = new Timer() {

                    @Override
                    public void run() {
                        Window.Location.reload();
                    }
                };
                timer.schedule(5000);
                FailureHandler.handle(ex);
            }

            @Override
            public void onSuccess(GwtXSRFToken xsrfToken) {
                setXsrfToken(xsrfToken);

                mask();
                submitButton.disable();
                cancelButton.disable();
                status.show();

                submit();
            }
        });
    }

    public void setXsrfToken(GwtXSRFToken xsrfToken) {
        this.xsrfToken = xsrfToken;
    }

    public abstract void submit();

    public Boolean getExitStatus() {
        return exitStatus;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void maskDialog() {
        formPanel.mask(MSGS.loading());
    }

    public void unmaskDialog() {
        formPanel.unmask();
    }

    public void setSubmitButtonState() {
        if (submitButton != null) {
            if ((formPanel.isDirty() || dateValueNotNull) && formPanel.isValid(true)) {
                submitButton.enable();
            } else {
                submitButton.disable();
            }
        }
    }

    public void setDateValueNotNull(Boolean dateValueNotNull) {
        this.dateValueNotNull = dateValueNotNull;
    }

    public void setDisabledFormPanelEvents(Boolean disabledFormPanelEvents) {
        this.disabledFormPanelEvents = disabledFormPanelEvents;
    }

    /**
     * Method for checking the thrown exception for the SUBJECT_UNAUTHORIZED error code.
     *
     * @param caught The exception thrown
     * @return In case of the SUBJECT_UNAUTHORIZED error code the returned value is true,
     * the dialog is closed and the exitMessage is set. For every other case the returned
     * value is false.
     */
    public boolean isPermissionErrorMessage(Throwable caught) {
        if ((caught instanceof GwtKapuaException)
                && GwtKapuaErrorCode.SUBJECT_UNAUTHORIZED.equals(((GwtKapuaException) caught).getCode())) {
            exitMessage = caught.getLocalizedMessage();
            hide();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Dialog specific load listener class that overrides the default loaderLoadException method.
     * The exception is handled by FailureHandler class's handle() method and the dialog is closed.
     */
    public class DialogLoadListener extends LoadListener {

        @Override
        public void loaderLoadException(LoadEvent loadEvent) {
            super.loaderLoadException(loadEvent);
            if (loadEvent.exception != null) {
                FailureHandler.handle(loadEvent.exception);
                hide();
            }
        }

    }

}
