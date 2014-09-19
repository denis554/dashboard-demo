package com.vaadin.demo.dashboard.component;

import com.google.common.eventbus.Subscribe;
import com.vaadin.demo.dashboard.data.User;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.demo.dashboard.event.QuickTicketsEvent.NotificationsOpenEvent;
import com.vaadin.demo.dashboard.event.QuickTicketsEvent.PostViewChangeEvent;
import com.vaadin.demo.dashboard.event.QuickTicketsEvent.UserLoggedOutEvent;
import com.vaadin.demo.dashboard.event.QuickTicketsEvent.ViewChangeRequestedEvent;
import com.vaadin.demo.dashboard.view.QuickTicketsView;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;

//TODO: Make generic
public class DashboardMenu extends CustomComponent {

    private static final String STYLE_VISIBLE = "valo-menu-visible";

    public DashboardMenu() {
        addStyleName("valo-menu");
        setSizeUndefined();

        setCompositionRoot(buildContent());
    }

    private Component buildContent() {
        final CssLayout menuContent = new CssLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName("valo-menu-part");
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        menuContent.addComponent(buildTitle());
        menuContent.addComponent(buildUserMenu());
        menuContent.addComponent(buildToggleButton());
        menuContent.addComponent(buildMenuItems());

        return menuContent;
    }

    private Component buildTitle() {
        Label logo = new Label("QuickTickets <strong>Dashboard</strong>",
                ContentMode.HTML);
        logo.setSizeUndefined();
        CssLayout logoWrapper = new CssLayout(logo);
        logoWrapper.addStyleName("valo-menu-title");
        return logoWrapper;
    }

    private Component buildUserMenu() {
        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
        final MenuItem settingsItem = settings.addItem(user.getFirstName()
                + " " + user.getLastName(), new ThemeResource(
                "img/profile-pic-300px.jpg"), null);
        settingsItem.addItem("Edit Profile", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                Notification.show("Not implemented in this demo");
            }
        });
        settingsItem.addItem("Preferences", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                Notification.show("Not implemented in this demo");
            }
        });
        settingsItem.addSeparator();
        settingsItem.addItem("Sign Out", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                DashboardEventBus.post(new UserLoggedOutEvent());
            }
        });
        return settings;
    }

    private Component buildToggleButton() {
        Button valoMenuToggleButton = new Button("Menu", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (getCompositionRoot().getStyleName().contains(STYLE_VISIBLE)) {
                    getCompositionRoot().removeStyleName(STYLE_VISIBLE);
                } else {
                    getCompositionRoot().addStyleName(STYLE_VISIBLE);
                }
            }
        });
        valoMenuToggleButton.setIcon(FontAwesome.LIST);
        valoMenuToggleButton.addStyleName("valo-menu-toggle");
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_SMALL);
        return valoMenuToggleButton;
    }

    private Component buildMenuItems() {
        CssLayout menuItemsLayout = new CssLayout();
        menuItemsLayout.addStyleName("valo-menuitems");
        menuItemsLayout.setHeight(100.0f, Unit.PERCENTAGE);

        for (final QuickTicketsView view : QuickTicketsView.values()) {
            Component menuItemButton = new ValoMenuItemButton(view);

            if (view == QuickTicketsView.REPORTS) {
                // Add drop target to reports button
                DragAndDropWrapper reports = new DragAndDropWrapper(
                        menuItemButton);
                reports.setDragStartMode(DragStartMode.NONE);
                reports.setDropHandler(new DropHandler() {

                    @Override
                    public void drop(DragAndDropEvent event) {
                        // clearMenuSelection();
                        // viewNameToMenuButton.get("/reports").addStyleName(
                        // "selected");
                        // autoCreateReport = true;
                        // items = event.getTransferable();
                        // nav.navigateTo("/reports");
                    }

                    @Override
                    public AcceptCriterion getAcceptCriterion() {
                        return AcceptItem.ALL;
                    }

                });
                menuItemButton = reports;
            }

            menuItemsLayout.addComponent(menuItemButton);
        }
        return menuItemsLayout;

    }

    @Override
    public void attach() {
        super.attach();
        DashboardEventBus.register(this);
    }

    @Override
    public void detach() {
        super.detach();
        DashboardEventBus.unregister(this);
    }

    @Subscribe
    public void viewChangeRequestedEvent(ViewChangeRequestedEvent event) {
        getCompositionRoot().removeStyleName(STYLE_VISIBLE);
    }

    @Subscribe
    public void notificationsOpen(NotificationsOpenEvent event) {
        // TODO: Clear notificaitons badge
    }

    public class ValoMenuItemButton extends Button {

        private static final String STYLE_SELECTED = "selected";

        private final QuickTicketsView view;

        public ValoMenuItemButton(final QuickTicketsView view) {
            this.view = view;
            setPrimaryStyleName("valo-menu-item");
            setCaption(view.getViewName());
            setIcon(view.getIcon());
            addClickListener(new ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
                    DashboardEventBus.post(new ViewChangeRequestedEvent(view));
                }
            });
        }

        @Override
        public void attach() {
            super.attach();
            DashboardEventBus.register(this);
        }

        @Override
        public void detach() {
            super.detach();
            DashboardEventBus.unregister(this);
        }

        @Subscribe
        public void postViewChange(PostViewChangeEvent event) {
            removeStyleName(STYLE_SELECTED);
            if (event.getView() == view) {
                addStyleName(STYLE_SELECTED);
            }
        }
    }
}
