package ru.litvinov.patientnotificator.view;


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
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import ru.litvinov.patientnotificator.component.ConfirmationDialog;
import ru.litvinov.patientnotificator.component.pagination.PaginatedGrid;
import ru.litvinov.patientnotificator.model.SchedulerTask;
import ru.litvinov.patientnotificator.service.SchedulerService;

import java.time.format.DateTimeFormatter;

import static ru.litvinov.patientnotificator.util.Constants.BROADCASTS;
import static ru.litvinov.patientnotificator.util.Constants.DELETE;

@PageTitle(BROADCASTS)
@RolesAllowed(value = {"ROLE_ADMIN"})
@Route(value = "broadcasts", layout = MainView.class)
public class BroadcastView extends AbstractView {

    private final SchedulerService schedulerService;

    private final PaginatedGrid<SchedulerTask> table;

    public BroadcastView(final SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
        this.table = createTable();
    }

    @Override
    @PostConstruct
    protected void initialize() {
        final var layout = new VerticalLayout(new H4(BROADCASTS));
        layout.setPadding(false);
        horizontal.add(layout);
        setHeightFull();
        vertical.setHeightFull();
        vertical.add(table);
        add(vertical);
    }

    private PaginatedGrid<SchedulerTask> createTable() {
        final Grid<SchedulerTask> grid = new Grid<>();
        grid.addColumn(task -> task.getPatient().getName()).setHeader("Пациент");
        grid.addColumn(task -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(task.getExecutionDate())).setHeader("Дата");
        grid.addColumn(task -> task.getLayout().getName()).setHeader("Шаблон");
        grid.addColumn(createActionRenderer()).setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);
        final var paginatedGrid = new PaginatedGrid<>(grid);
        paginatedGrid.setItems(schedulerService.getTaskCache());

        return paginatedGrid;
    }

    private ComponentRenderer<HorizontalLayout, SchedulerTask> createActionRenderer() {
        final SerializableBiConsumer<HorizontalLayout, SchedulerTask> actionProcessor = (horizontalLayout, st) -> {
            final Runnable callback = () -> {
                schedulerService.delete(st);
                table.setItems(schedulerService.getTaskCache());
            };
            final Button deleteButton = new Button();
            deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            deleteButton.setIcon(VaadinIcon.TRASH.create());
            deleteButton.setText(DELETE);
            deleteButton.addClickListener(e -> new ConfirmationDialog("Хотите удалить уведомление?", callback).open());
            horizontalLayout.add(deleteButton);
        };
        return new ComponentRenderer<>(HorizontalLayout::new, actionProcessor);
    }

}
