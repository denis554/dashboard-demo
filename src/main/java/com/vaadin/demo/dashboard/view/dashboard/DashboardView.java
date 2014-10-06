package com.vaadin.demo.dashboard.view.dashboard;

import java.util.Collection;
import java.util.Iterator;

import org.vaadin.sparklines.Sparklines;

import com.google.common.eventbus.Subscribe;
import com.vaadin.demo.dashboard.DashboardUI;
import com.vaadin.demo.dashboard.component.TopGrossingMoviesChart;
import com.vaadin.demo.dashboard.component.TopSixTheatersChart;
import com.vaadin.demo.dashboard.component.TopTenMoviesTable;
import com.vaadin.demo.dashboard.data.dummy.DummyDataGenerator;
import com.vaadin.demo.dashboard.domain.DashboardNotification;
import com.vaadin.demo.dashboard.event.DashboardEvent.CloseOpenWindowsEvent;
import com.vaadin.demo.dashboard.event.DashboardEvent.DashboardEditEvent;
import com.vaadin.demo.dashboard.event.DashboardEvent.MaximizeDashboardPanelEvent;
import com.vaadin.demo.dashboard.event.DashboardEvent.MinimizeDashboardPanelEvent;
import com.vaadin.demo.dashboard.event.DashboardEvent.NotificationsCountUpdatedEvent;
import com.vaadin.demo.dashboard.event.DashboardEventBus;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class DashboardView extends Panel implements View {

    private Label titleLabel;
    private NotificationsButton notificationsButton;
    private CssLayout dashboardPanels;
    private VerticalLayout root;

    public DashboardView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        DashboardEventBus.register(this);

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        setContent(root);

        root.addComponent(buildHeader());

        root.addComponent(buildSparklines());

        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);

        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(LayoutClickEvent event) {
                DashboardEventBus.post(new CloseOpenWindowsEvent());
            }
        });
    }

    Component buildSparklines() {
        String maxColor = DummyDataGenerator.chartColors[2].toString();
        String minColor = DummyDataGenerator.chartColors[5].toString();
        String valColor = DummyDataGenerator.chartColors[0].toString();

        CssLayout sparks = new CssLayout();
        sparks.addStyleName("sparks");
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);

        Sparklines s = new Sparklines(null, 0, 0, 0, 100);
        s.setDescription("Metric #1");
        s.setValue(DummyDataGenerator.randomSparklineValues(20, 20, 80));
        s.setMaxColor(maxColor);
        s.setMinColor(minColor);
        s.setValueColor(valColor);
        sparks.addComponent(s);

        s = new Sparklines(null, 0, 0, 0, 100);
        s.setDescription("Metric #2");
        s.setValue(DummyDataGenerator.randomSparklineValues(10, 40, 90));
        s.setMaxColor(maxColor);
        s.setMinColor(minColor);
        s.setValueColor(valColor);
        sparks.addComponent(s);

        s = new Sparklines(null, 0, 0, 0, 100);
        s.setDescription("Metric #3");
        s.setValue(DummyDataGenerator.randomSparklineValues(30, 5, 100));
        s.setMaxColor(maxColor);
        s.setMinColor(minColor);
        s.setValueColor(valColor);
        sparks.addComponent(s);

        s = new Sparklines(null, 0, 0, 0, 100);
        s.setDescription("Metric #4");
        s.setValue(DummyDataGenerator.randomSparklineValues(15, 20, 70));
        s.setMaxColor(maxColor);
        s.setMinColor(minColor);
        s.setValueColor(valColor);
        sparks.addComponent(s);

        return sparks;
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label("Dashboard");
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        notificationsButton = buildNotificationsButton();
        Component edit = buildEdit();
        HorizontalLayout tools = new HorizontalLayout(notificationsButton, edit);
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private NotificationsButton buildNotificationsButton() {
        NotificationsButton notificationsButton = new NotificationsButton();
        notificationsButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                openNotificationsPopup(event);
            }
        });
        return notificationsButton;
    }

    private Component buildEdit() {
        Button edit = new Button();
        edit.setIcon(FontAwesome.EDIT);
        edit.addStyleName("icon-edit");
        edit.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        edit.setDescription("Edit Dashboard");
        edit.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getUI().addWindow(new DashboardEdit(titleLabel.getValue()));
            }
        });
        return edit;
    }

    private Component buildContent() {
        dashboardPanels = new CssLayout();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        dashboardPanels.addComponent(buildTopGrossingMovies());
        dashboardPanels.addComponent(buildNotes());
        dashboardPanels.addComponent(buildTop10TitlesByRevenue());
        dashboardPanels.addComponent(buildPopularMovies());

        return dashboardPanels;
    }

    private Component buildTopGrossingMovies() {
        TopGrossingMoviesChart topGrossingMoviesChart = new TopGrossingMoviesChart();
        topGrossingMoviesChart.setSizeFull();
        return createContentWrapper(topGrossingMoviesChart);
    }

    private Component buildNotes() {
        TextArea notes = new TextArea("Notes");
        notes.setValue("Remember to:\n· Zoom in and out in the Sales view\n· Filter the transactions and drag a set of them to the Reports tab\n· Create a new report\n· Change the schedule of the movie theater");
        notes.setSizeFull();
        notes.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
        Component panel = createContentWrapper(notes);
        panel.addStyleName("notes");
        return panel;
    }

    private Component buildTop10TitlesByRevenue() {
        return createContentWrapper(new TopTenMoviesTable());
    }

    private Component buildPopularMovies() {
        return createContentWrapper(new TopSixTheatersChart());
    }

    private Component createContentWrapper(Component content) {
        final CssLayout slot = new CssLayout();
        slot.addStyleName("dashboard-panel-slot");

        CssLayout card = new CssLayout();
        card.addStyleName(ValoTheme.LAYOUT_CARD);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("dashboard-panel-toolbar");
        toolbar.setWidth("100%");

        Label caption = new Label(content.getCaption());
        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        content.setCaption(null);

        MenuBar tools = new MenuBar();
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuItem max = tools.addItem("", FontAwesome.EXPAND, new Command() {
            int currentIndex = -1;

            @Override
            public void menuSelected(MenuItem selectedItem) {
                if (!slot.getStyleName().contains("max")) {
                    // currentIndex = ((CssLayout) slot.getParent())
                    // .getComponentIndex(slot);
                    // slot.addStyleName("max");
                    // ((CssLayout) slot.getParent()).addComponent(slot, 0);
                    // if (currentIndex > 0) {
                    // currentIndex++;
                    // }
                    selectedItem.setIcon(FontAwesome.COMPRESS);
                    DashboardEventBus
                            .post(new MaximizeDashboardPanelEvent(slot));
                } else {
                    slot.removeStyleName("max");
                    // ((CssLayout) slot.getParent()).addComponent(slot,
                    // currentIndex);
                    selectedItem.setIcon(FontAwesome.EXPAND);

                    DashboardEventBus
                            .post(new MinimizeDashboardPanelEvent(slot));
                }
            }
        });
        max.setStyleName("icon-only");
        MenuItem root = tools.addItem("", FontAwesome.COG, null);
        root.addItem("Configure", null);
        root.addSeparator();
        root.addItem("Close", null);

        toolbar.addComponents(caption, tools);
        toolbar.setExpandRatio(caption, 1);
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

        card.addComponents(toolbar, content);
        slot.addComponent(card);
        return slot;
    }

    private void openNotificationsPopup(ClickEvent event) {
        VerticalLayout notificationsLayout = new VerticalLayout();
        notificationsLayout.setMargin(true);
        notificationsLayout.setSpacing(true);

        Label title = new Label("Notifications");
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        notificationsLayout.addComponent(title);

        Collection<DashboardNotification> notifications = DashboardUI
                .getDataProvider().getNotifications();
        DashboardEventBus.post(new NotificationsCountUpdatedEvent());

        for (DashboardNotification notification : notifications) {
            VerticalLayout notificationLayout = new VerticalLayout();
            notificationLayout.addStyleName("notification-item");

            Label titleLabel = new Label(notification.getFirstName() + " "
                    + notification.getLastName() + " "
                    + notification.getAction());
            titleLabel.addStyleName("notification-title");

            Label timeLabel = new Label(notification.getPrettyTime());
            timeLabel.addStyleName("notification-time");

            Label contentLabel = new Label(notification.getContent());
            contentLabel.addStyleName("notification-content");

            notificationLayout.addComponents(titleLabel, timeLabel,
                    contentLabel);
            notificationsLayout.addComponent(notificationLayout);
        }

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth("100%");
        Button showAll = new Button("View All Notifications",
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification.show("Not implemented in this demo");
                    }
                });
        showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        showAll.addStyleName(ValoTheme.BUTTON_SMALL);
        footer.addComponent(showAll);
        footer.setComponentAlignment(showAll, Alignment.TOP_CENTER);
        notificationsLayout.addComponent(footer);

        Window notificationsWindow = new Window();
        notificationsWindow.setWidth(300.0f, Unit.PIXELS);
        notificationsWindow.addStyleName("notifications");
        notificationsWindow.setClosable(false);
        notificationsWindow.setResizable(false);
        notificationsWindow.setDraggable(false);
        notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null);
        notificationsWindow.setContent(notificationsLayout);

        notificationsWindow.setPositionY(event.getClientY()
                - event.getRelativeY() + 40);
        UI.getCurrent().addWindow(notificationsWindow);
        notificationsWindow.focus();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        notificationsButton.updateNotificationsCount(null);
    }

    @Subscribe
    public void dashboardEdited(DashboardEditEvent event) {
        titleLabel.setValue(event.getName());
    }

    public static class NotificationsButton extends Button {
        private static final String STYLE_UNREAD = "unread";

        public NotificationsButton() {
            setIcon(FontAwesome.BELL);
            addStyleName("notifications");
            addStyleName(ValoTheme.BUTTON_ICON_ONLY);
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
        public void updateNotificationsCount(
                NotificationsCountUpdatedEvent event) {
            setUnreadCount(DashboardUI.getDataProvider()
                    .getUnreadNotificationsCount());
        }

        public void setUnreadCount(int count) {
            setCaption(String.valueOf(count));

            String description = "Notifications";
            if (count > 0) {
                addStyleName(STYLE_UNREAD);
                description += " (" + count + " unread)";
            } else {
                removeStyleName(STYLE_UNREAD);
            }
            setDescription(description);
        }
    }

    @Subscribe
    public void maximizePanel(MaximizeDashboardPanelEvent event) {
        for (Iterator<Component> it = dashboardPanels.iterator(); it.hasNext();) {
            Component c = it.next();
            c.removeStyleName("max");
            c.setVisible(false);
        }
        event.getPanel().addStyleName("max");
        event.getPanel().setVisible(true);

        for (Iterator<Component> it = root.iterator(); it.hasNext();) {
            it.next().setVisible(false);
        }
        dashboardPanels.setVisible(true);
    }

    @Subscribe
    public void minimizePanel(MinimizeDashboardPanelEvent event) {
        for (Iterator<Component> it = dashboardPanels.iterator(); it.hasNext();) {
            Component c = it.next();
            c.removeStyleName("max");
            c.setVisible(true);
        }

        for (Iterator<Component> it = root.iterator(); it.hasNext();) {
            it.next().setVisible(true);
        }
    }
}
