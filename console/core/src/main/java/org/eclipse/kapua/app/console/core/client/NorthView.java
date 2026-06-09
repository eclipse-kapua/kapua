/*******************************************************************************
 * Copyright (c) 2016, 2022 Eurotech and/or its affiliates and others
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
package org.eclipse.kapua.app.console.core.client;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.eclipse.kapua.app.console.core.client.util.TokenCleaner;
import org.eclipse.kapua.app.console.core.client.messages.ConsoleCoreMessages;
import org.eclipse.kapua.app.console.core.shared.service.GwtAuthorizationService;
import org.eclipse.kapua.app.console.core.shared.service.GwtAuthorizationServiceAsync;
import org.eclipse.kapua.app.console.core.shared.service.GwtSettingsService;
import org.eclipse.kapua.app.console.core.shared.service.GwtSettingsServiceAsync;
import org.eclipse.kapua.app.console.module.account.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.module.account.shared.model.permission.AccountSessionPermission;
import org.eclipse.kapua.app.console.module.account.shared.service.GwtAccountService;
import org.eclipse.kapua.app.console.module.account.shared.service.GwtAccountServiceAsync;
import org.eclipse.kapua.app.console.module.api.client.messages.ConsoleMessages;
import org.eclipse.kapua.app.console.module.api.client.resources.icons.IconSet;
import org.eclipse.kapua.app.console.module.api.client.ui.view.AbstractView;
import org.eclipse.kapua.app.console.module.api.client.ui.view.descriptor.MainViewDescriptor;
import org.eclipse.kapua.app.console.module.api.client.ui.widget.KapuaMenuItem;
import org.eclipse.kapua.app.console.module.api.client.util.FailureHandler;
import org.eclipse.kapua.app.console.module.api.client.util.UserAgentUtils;
import org.eclipse.kapua.app.console.module.api.shared.model.session.GwtSession;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtConsoleService;
import org.eclipse.kapua.app.console.module.api.shared.service.GwtConsoleServiceAsync;
import org.eclipse.kapua.app.console.module.authentication.client.messages.ConsoleCredentialMessages;
import org.eclipse.kapua.app.console.module.authentication.shared.model.GwtMfaCredentialOptions;
import org.eclipse.kapua.app.console.module.authentication.shared.service.GwtMfaCredentialOptionsService;
import org.eclipse.kapua.app.console.module.authentication.shared.service.GwtMfaCredentialOptionsServiceAsync;

import java.util.List;

public class NorthView extends LayoutContainer {

    private static final ConsoleMessages CONSOLE_MESSAGES = GWT.create(ConsoleMessages.class);
    private static final ConsoleCredentialMessages CONSOLE_CREDENTIAL_MESSAGES = GWT.create(ConsoleCredentialMessages.class);
    private static final ConsoleCoreMessages CONSOLE_CORE_MESSAGES = GWT.create(ConsoleCoreMessages.class);

    private static final GwtAuthorizationServiceAsync GWT_AUTHORIZATION_SERVICE = GWT.create(GwtAuthorizationService.class);
    private static final GwtAccountServiceAsync GWT_ACCOUNT_SERVICE = GWT.create(GwtAccountService.class);
    private static final GwtConsoleServiceAsync GWT_CONSOLE_SERVICE = GWT.create(GwtConsoleService.class);
    private static final GwtSettingsServiceAsync GWT_SETTINGS_SERVICE = GWT.create(GwtSettingsService.class);
    private static final GwtMfaCredentialOptionsServiceAsync GWT_MFA_CREDENTIAL_OPTIONS_SERVICE = GWT.create(GwtMfaCredentialOptionsService.class);

    // UI stuff
    private final KapuaCloudConsole parent;
    private Menu subAccountMenu;
    private Button userActionButton;

    // Data
    private final GwtSession currentSession;

    private final String accountId;
    private final String accountName;

    private final String selectedAccountId;
    private final String selectedAccountName;

    // Following 2 have been commented after deprecation of GwtSession.getRootAccount* methods
    //    private String rootAccountId;
    //    private String rootAccountName;

    private final String userId;
    private final String username;
    private String userDisplayName;

    // Listener
    private final SelectionListener<MenuEvent> switchToAccountListener = new SelectionListener<MenuEvent>() {

        @Override
        public void componentSelected(MenuEvent ce) {
            switchToAccount(ce);
        }
    };

    public NorthView(GwtSession currentSession, KapuaCloudConsole parent) {
        this.parent = parent;

        this.currentSession = currentSession;

        this.accountId = currentSession.getAccountId();
        this.accountName = currentSession.getAccountName();

        this.selectedAccountId = currentSession.getSelectedAccountId();
        this.selectedAccountName = currentSession.getSelectedAccountName();

        // Following 2 have been commented after deprecation of GwtSession.getRootAccount* methods
        // this.rootAccountId = currentSession.getRootAccountId();
        // this.rootAccountName = currentSession.getRootAccountName();

        this.userId = currentSession.getUserId();
        this.username = currentSession.getUserName();
        this.userDisplayName = currentSession.getUserDisplayName();

    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        TableLayout layout = new TableLayout(2);
        layout.setWidth("100%");
        layout.setTableStyle("background-color: transparent");

        ContentPanel panel = new ContentPanel();
        panel.setHeaderVisible(false);
        panel.setBorders(false);
        panel.setBodyBorder(false);
        panel.setId("header-panel");
        panel.setBodyStyle("background-color: transparent");
        panel.setLayout(layout);

        // Logo
        panel.add(getKapuaHeader(),
                new TableData(Style.HorizontalAlignment.LEFT,
                        Style.VerticalAlignment.MIDDLE));

        // User Action Menu
        panel.add(getUserActionMenu(),
                new TableData(Style.HorizontalAlignment.RIGHT,
                        Style.VerticalAlignment.MIDDLE));

        add(panel);
    }

    private Widget getKapuaHeader() {
        SimplePanel logo = new SimplePanel();

        if (!UserAgentUtils.isIE() ||
                UserAgentUtils.getIEDocumentMode() > 8) {
            logo.setStyleName("headerLogo");
        } else {
            logo.setStyleName("headerLogo-ie8");
        }

        return logo;
    }

    private Widget getUserActionMenu() {

        userActionButton = new Button();

        userActionButton.addListener(Events.OnMouseOver, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                Menu userActionMenu = new Menu();

                // Child Accounts menu item
                if (currentSession.hasPermission(AccountSessionPermission.read())) {
                    userActionMenu.add(createAccountNavigationMenuItem());
                    userActionMenu.add(new SeparatorMenuItem());
                }

                // Change Password menu item
                //  (only if the current user is an INTERNAL one; note that an INTERNAL user has no ssoIdToken)
                if (currentSession.getOpenIDIdToken() == null) {
                    KapuaMenuItem changePassword = new KapuaMenuItem();
                    changePassword.setText(CONSOLE_MESSAGES.changePassword());
                    changePassword.setIcon(IconSet.KEY);
                    changePassword.addSelectionListener(new SelectionListener<MenuEvent>() {

                        @Override
                        public void componentSelected(MenuEvent ce) {
                            GWT_MFA_CREDENTIAL_OPTIONS_SERVICE.findByUserId(currentSession.getAccountId(), currentSession.getUserId(), true, new AsyncCallback<GwtMfaCredentialOptions>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    FailureHandler.handle(caught);
                                }

                                @Override
                                public void onSuccess(GwtMfaCredentialOptions gwtMfaCredentialOptions) {
                                    ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(currentSession, gwtMfaCredentialOptions);
                                    changePasswordDialog.show();
                                }
                            });
                        }

                    });
                    userActionMenu.add(changePassword);
                    userActionMenu.add(new SeparatorMenuItem());
                }

                KapuaMenuItem manageMfa = new KapuaMenuItem();
                manageMfa.setText(CONSOLE_CREDENTIAL_MESSAGES.manageMfaMenuItem());
                manageMfa.setIcon(IconSet.LOCK);
                manageMfa.addSelectionListener(new SelectionListener<MenuEvent>() {

                    @Override
                    public void componentSelected(MenuEvent ce) {
                        MfaManagementDialog mfaManagementDialog = new MfaManagementDialog(currentSession);
                        mfaManagementDialog.show();
                    }

                });
                userActionMenu.add(manageMfa);

                userActionMenu.add(new SeparatorMenuItem());
                // "Get SSO URL" menu item — only shown when SSO is enabled
                if (currentSession.isSsoEnabled() &&
                    currentSession.isSsoBrokeringEnabledForSession()) {
                    KapuaMenuItem getSsoUrlMenuItem = new KapuaMenuItem();
                    getSsoUrlMenuItem.setText(CONSOLE_CORE_MESSAGES.ssoUrlMenuItemLabel());
                    getSsoUrlMenuItem.setIcon(IconSet.SIGN_IN);
                    getSsoUrlMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {

                        @Override
                        public void componentSelected(MenuEvent ce) {
                            GWT_SETTINGS_SERVICE.getOpenIDLoginOrganizationUri(new AsyncCallback<String>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    FailureHandler.handle(caught);
                                }

                                @Override
                                public void onSuccess(String ssoUrl) {
                                    SsoUrlDialog dialog = new SsoUrlDialog(ssoUrl);
                                    dialog.show();
                                }
                            });
                        }
                    });
                    userActionMenu.add(getSsoUrlMenuItem);
                    userActionMenu.add(new SeparatorMenuItem());
                }

                // Logout menu item
                KapuaMenuItem userLogoutMenuItem = new KapuaMenuItem();
                userLogoutMenuItem.setText(CONSOLE_MESSAGES.consoleHeaderUserActionLogout());
                userLogoutMenuItem.setIcon(IconSet.SIGN_OUT);
                userLogoutMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {

                    @Override
                    public void componentSelected(MenuEvent ce) {
                        GWT_AUTHORIZATION_SERVICE.logout(new AsyncCallback<Void>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                FailureHandler.handle(caught);
                            }

                            @Override
                            public void onSuccess(Void arg0) {
                                if (currentSession.isSsoEnabled() && currentSession.getOpenIDIdToken() != null) {
                                    GWT_SETTINGS_SERVICE.getOpenIDLogoutUri(currentSession.getOpenIDIdToken(), new AsyncCallback<String>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                            FailureHandler.handle(caught);
                                        }

                                        @Override
                                        public void onSuccess(String result) {
                                            if (!result.isEmpty()) {
                                                Window.Location.assign(result);
                                            } else {
                                                // result is empty, thus the OpenID logout is disabled
                                                TokenCleaner.cleanToken();
                                            }
                                        }
                                    });
                                } else {
                                    TokenCleaner.cleanToken();
                                }
                            }
                        });
                    }

                });
                userActionMenu.add(userLogoutMenuItem);
                userActionButton.setMenu(userActionMenu);

            }
        });
        userActionButton.setId("header-button");
        userActionButton.addStyleName("x-btn-arrow-custom");
        userActionButton.setAutoWidth(true);
        updateUserActionButtonLabel();

        return userActionButton;
    }

    /**
     * Creates an MenuItem with the list of all child accounts of the account of the current logged user,
     * and also add as first sub-MenuItem the name of the root account.
     *
     * @return the MenuItem
     */
    public MenuItem createAccountNavigationMenuItem() {
        KapuaMenuItem mainAccountMenuItem = new KapuaMenuItem();
        mainAccountMenuItem.setText(CONSOLE_MESSAGES.accountSelectorItemYourAccount(accountName));
        mainAccountMenuItem.setToolTip(CONSOLE_MESSAGES.accountSelectorTooltipYourAccount());
        mainAccountMenuItem.setIcon(IconSet.USER_MD);
        mainAccountMenuItem.setId(accountId);
        mainAccountMenuItem.addSelectionListener(switchToAccountListener);

        subAccountMenu = new Menu();
        subAccountMenu.setAutoWidth(true);
        subAccountMenu.setAutoHeight(true);
        subAccountMenu.add(mainAccountMenuItem);
        subAccountMenu.add(new SeparatorMenuItem());

        populateNavigatorMenu(subAccountMenu, accountId);

        KapuaMenuItem switchToAccountMenuItem = new KapuaMenuItem();
        switchToAccountMenuItem.setText(CONSOLE_MESSAGES.consoleHeaderUserActionSwitchToAccount());
        switchToAccountMenuItem.setIcon(IconSet.USERS);
        switchToAccountMenuItem.setSubMenu(subAccountMenu);

        return switchToAccountMenuItem;
    }

    /**
     * Populates a Menu objects with a entry for each child account for the given account.
     * It does this recursively for each child.
     *
     * @param menu      the menu to fill
     * @param accountId the account of the current menu item.
     */
    private void populateNavigatorMenu(final Menu menu, String accountId) {
        final KapuaMenuItem loadingChildAccounts;
        loadingChildAccounts = new KapuaMenuItem(CONSOLE_MESSAGES.accountSelectorLoadingChildAccounts());
        loadingChildAccounts.setToolTip(CONSOLE_MESSAGES.accountSelectorTooltipYourAccount());
        loadingChildAccounts.setIcon(IconSet.USER_MD);
        loadingChildAccounts.setId(accountId);
        loadingChildAccounts.disable();

        menu.add(loadingChildAccounts);

        GWT_ACCOUNT_SERVICE.find(accountId, new AsyncCallback<GwtAccount>() {

            @Override
            public void onFailure(Throwable caught) {
                FailureHandler.handle(caught);
            }

            @Override
            public void onSuccess(GwtAccount result) {
                // If no children are found, add "No Child" label
                if (result.getChildAccounts() != null && result.getChildAccounts().isEmpty()) {
                    menu.remove(loadingChildAccounts);
                    MenuItem noChildMenuItem = new MenuItem(CONSOLE_MESSAGES.accountSelectorItemNoChild());
                    noChildMenuItem.disable();
                    menu.add(noChildMenuItem);
                } else {
                    menu.remove(loadingChildAccounts);
                    // For each child found create a item menu and search for its children
                    for (GwtAccount childAccount : result.getChildAccounts()) {
                        // Add item menu entry
                        KapuaMenuItem childAccountMenuItem = new KapuaMenuItem();
                        childAccountMenuItem.setIcon(IconSet.USER);
                        childAccountMenuItem.setText(childAccount.getName());
                        childAccountMenuItem.setTitle(childAccount.getName());
                        childAccountMenuItem.setId(String.valueOf(childAccount.getId()));
                        menu.add(childAccountMenuItem);

                        // Add selection listener to make the switch happen when selected
                        childAccountMenuItem.addSelectionListener(switchToAccountListener);

                        // Search for its children
                        if (!childAccount.getChildAccounts().isEmpty()) {
                            Menu childMenu = new Menu();
                            childMenu.setAutoWidth(true);
                            childMenu.setAutoHeight(true);
                            childAccountMenuItem.setSubMenu(childMenu);
                            populateNavigatorMenu(childMenu, childAccount.getId());
                        }
                    }
                }
                // Force new show to include new child accounts
                if (menu.isVisible()) {
                    Point currentPosition = menu.getPosition(true);
                    menu.showAt(currentPosition.x, currentPosition.y);
                }
            }
        });
    }

    /**
     * Make the switch between accounts when an account is selected from
     * the account navigator.
     *
     * @param ce The MenuEvent that has been fired with the selection of a menu entry.
     */
    private void switchToAccount(MenuEvent ce) {
        String accountName = ce.getItem().getTitle();
        String accountIdString = ce.getItem().getId();

        // Mask the whole page
        parent.getViewport().mask(CONSOLE_MESSAGES.accountSelectorMenuMaskLoading(accountName));

        GWT_ACCOUNT_SERVICE.find(accountIdString, new AsyncCallback<GwtAccount>() {

            @Override
            public void onFailure(Throwable caught) {
                parent.getViewport().unmask();
                FailureHandler.handle(caught);
            }

            @Override
            public void onSuccess(final GwtAccount result) {
                GWT_CONSOLE_SERVICE.getCustomEntityViews(new AsyncCallback<List<MainViewDescriptor>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        parent.getViewport().unmask();
                        FailureHandler.handle(caught);
                    }

                    @Override
                    public void onSuccess(List<MainViewDescriptor> viewDescriptors) {
                        if (result != null) {
                            currentSession.setSelectedAccountId(result.getId());
                            currentSession.setSelectedAccountName(result.getName());
                            currentSession.setSelectedAccountPath(result.getParentAccountPath());
                        }

                        // Update userActionButtonLabel with the current data
                        updateUserActionButtonLabel();

                        if (((ContentPanel) parent.getCenterView().getItem(0)).getItem(0) instanceof AbstractView) {
                            AbstractView aev = (AbstractView) ((ContentPanel) parent.getCenterView().getItem(0)).getItem(0);
                            aev.onUserChange();
                        }
                        // Force the west view (which contains the navigation menu) to reload available components
                        parent.getWestView().addMenuItems(viewDescriptors);
                        parent.getWestView().setDashboardSelected(true);
                        parent.getWestView().layout(true);

                        // Unmask the whole page
                        parent.getViewport().unmask();
                    }
                });
            }
        });
    }

    /**
     * Updates the UserActionButton label with the current selected informations for username and target account.<br/>
     * The label format is:<br/>
     * <p>
     * If the display name of the user is defined:<br/>
     * {displayName} @ {selectedAccountName}<br/>
     * else:<br/>
     * {username} @ {selectedAccountName}<br/>
     */
    private void updateUserActionButtonLabel() {
        String accountName = currentSession.getSelectedAccountName();

        if (userDisplayName == null || userDisplayName.isEmpty()) {
            userDisplayName = username;
        }

        userActionButton.setText(CONSOLE_MESSAGES.consoleHeaderUserActionButtonLabel(userDisplayName, accountName));
    }

}
