package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import ru.litvinov.patientnotificator.component.ConfirmationDialog;
import ru.litvinov.patientnotificator.component.NewButton;
import ru.litvinov.patientnotificator.model.Layout;
import ru.litvinov.patientnotificator.service.LayoutService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static ru.litvinov.patientnotificator.util.Constants.DELETE;
import static ru.litvinov.patientnotificator.util.Constants.LAYOUTS;

@PageTitle(LAYOUTS)
@RolesAllowed(value = {"ROLE_ADMIN"})
@Route(value = "layouts", layout = MainView.class)
public class LayoutView extends AbstractView {

    private final LayoutService layoutService;

    private final Grid<Layout> table;

    public LayoutView(final LayoutService layoutService) {
        this.layoutService = layoutService;
        this.table = createTable();
    }

    @Override
    @PostConstruct
    protected void initialize() {
        final var layout = new VerticalLayout(new H4(LAYOUTS), createNewButton());
        layout.setPadding(false);
        horizontal.add(layout);
        setHeightFull();
        vertical.setHeightFull();
        vertical.add(table);
        add(vertical);
    }

    private Component createNewButton() {
        return new NewButton("Новый шаблон", "layouts/upsert");
    }

    private Grid<Layout> createTable() {
        final Grid<Layout> grid = new Grid<>();
        grid.addColumn(createEditButtonRenderer()).setHeader("Название");
        grid.addColumn(layout -> layout.getType().getName()).setHeader("Тип");
        grid.addColumn(layout -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(layout.getCreatedOn())).setHeader("Создан");
        grid.addColumn(createActionRenderer()).setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);
        grid.setItems(layoutService.findAll());

        return grid;
    }

    private ComponentRenderer<Button, Layout> createEditButtonRenderer() {
        final SerializableBiConsumer<Button, Layout> editButtonProcessor = (button, layout) -> {
            button.setThemeName("tertiary");
            button.setText(layout.getName());
            button.addClickListener(e -> button.getUI().ifPresent(ui -> ui.navigate("layouts/upsert/", getQueryParameters(layout))));
        };
        return new ComponentRenderer<>(Button::new, editButtonProcessor);
    }

    private QueryParameters getQueryParameters(final Layout layout) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("id", layout.getId().toString());
        parameters.put("name", layout.getName());
        return QueryParameters.simple(parameters);
    }

    private ComponentRenderer<HorizontalLayout, Layout> createActionRenderer() {
        final SerializableBiConsumer<HorizontalLayout, Layout> actionProcessor = (horizontalLayout, layout) -> {
            final ConfirmationDialog.Callback callback = () -> {
                layoutService.delete(layout);
                table.setItems(layoutService.findAll());
            };
            final Button deleteButton = new Button();
            deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            deleteButton.setIcon(VaadinIcon.TRASH.create());
            deleteButton.setText(DELETE);
            deleteButton.addClickListener(e -> new ConfirmationDialog(String.format("Хотите удалить шаблон \"%s\"?", layout.getName()), callback).open());
            horizontalLayout.add(deleteButton);
        };
        return new ComponentRenderer<>(HorizontalLayout::new, actionProcessor);
    }

}
