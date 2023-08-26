package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import lombok.Getter;
import ru.litvinov.patientnotificator.model.Role;
import ru.litvinov.patientnotificator.service.UserService;

import java.util.Optional;
import java.util.stream.Collectors;

import static ru.litvinov.patientnotificator.util.Constants.*;

public class MainView extends AppLayout {

    private final UserService userService;

    @Getter
    private final H3 viewTitle = new H3();

    public MainView(final UserService userService) {
        this.userService = userService;
        final var toggle = new DrawerToggle();
        final var title = new H1(SYSTEM_NAME);
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");
        final var mainMenuHeader = new H6(MAIN_MENU);
        mainMenuHeader.getStyle().set("padding", "10px 25px 10px 15px");
        addToDrawer(mainMenuHeader);
        addToDrawer(getTabs());
        addToNavbar(toggle, title, createUserData());
    }

    private Tabs getTabs() {
        final var tabs = new Tabs();
        tabs.add(
                createTab(VaadinIcon.USERS, PATIENTS, PatientView.class),
                createTab(VaadinIcon.USER_CARD, USERS, UserView.class),
                createTab(VaadinIcon.LAYOUT, LAYOUTS, LayoutView.class),
                createTab(VaadinIcon.ENVELOPE_OPEN, BROADCASTS, BroadcastView.class)
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private Tab createTab(final VaadinIcon viewIcon, final String viewName, final Class<? extends Component> navigationTarget) {
        final var icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");
        final var link = new RouterLink();
        link.add(icon, new Span(viewName));
        link.setTabIndex(-1);
        Optional.ofNullable(navigationTarget).ifPresent(link::setRoute);

        return new Tab(link);
    }

    private VerticalLayout createUserData() {
        final var layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setAlignItems(FlexComponent.Alignment.END);
        layout.setWidthFull();
        final var menuBar = new MenuBar();
        final var user = userService.getAuthenticatedUser();
        final var menuItem = menuBar.addItem(new Html(String.format("<div style=\"width: 400px\">%s<br/>(%s)<div>", user.getName(), user.getRoles().stream().map(Role::getDescription).collect(Collectors.joining(", ")))));
        final var logoutButton = new Button(EXIT);
        logoutButton.setThemeName("tertiary");
        logoutButton.setIcon(VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(event -> {
            VaadinSession.getCurrent().getSession().invalidate();
            logoutButton.getUI().ifPresent(ui -> ui.getPage().reload());
        });
        menuItem.getSubMenu().addItem(logoutButton);
        layout.add(menuBar);

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return getContent().getClass().getAnnotation(PageTitle.class).value();
    }

}
