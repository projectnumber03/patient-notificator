package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import ru.litvinov.patientnotificator.component.NewButton;
import ru.litvinov.patientnotificator.component.pagination.PaginatedGrid;
import ru.litvinov.patientnotificator.model.Role;
import ru.litvinov.patientnotificator.model.User;
import ru.litvinov.patientnotificator.service.UserService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.litvinov.patientnotificator.util.Constants.USERS;

@PageTitle(USERS)
@RolesAllowed(value = {"ROLE_ADMIN"})
@Route(value = "users", layout = MainView.class)
public class UserView extends AbstractView {

    private final UserService userService;

    private final PaginatedGrid<User> table;

    public UserView(final UserService userService) {
        this.userService = userService;
        this.table = createTable();
    }

    @Override
    @PostConstruct
    protected void initialize() {
        final var layout = new VerticalLayout(new H4(USERS), createNewButton());
        layout.setPadding(false);
        horizontal.add(layout);
        setHeightFull();
        vertical.setHeightFull();
        vertical.add(table);
        add(vertical);
    }

    private Component createNewButton() {
        return new NewButton("Новый пользователь", "users/upsert");
    }

    private PaginatedGrid<User> createTable() {
        final Grid<User> grid = new Grid<>();
        grid.addColumn(createEditButtonRenderer()).setHeader("Пользователь");
        grid.addColumn(User::getName).setHeader("ФИО");
        grid.addColumn(u -> u.getRoles().stream().map(Role::getName).collect(Collectors.joining(", "))).setHeader("Роли");
        grid.addColumn(user -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(user.getCreatedOn())).setHeader("Создан");
        final var paginatedGrid = new PaginatedGrid<>(grid);
        paginatedGrid.setItems(userService.findAllWithRoles());
        return paginatedGrid;
    }

    private ComponentRenderer<Button, User> createEditButtonRenderer() {
        final SerializableBiConsumer<Button, User> editButtonProcessor = (button, user) -> {
            button.setThemeName("tertiary");
            button.setText(user.getLogin());
            button.addClickListener(e -> button.getUI().ifPresent(ui -> ui.navigate("users/upsert/", getQueryParameters(user))));
        };
        return new ComponentRenderer<>(Button::new, editButtonProcessor);
    }

    private QueryParameters getQueryParameters(final User user) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("id", user.getId().toString());
        parameters.put("name", user.getLogin());
        return QueryParameters.simple(parameters);
    }

}
