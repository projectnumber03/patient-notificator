package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import ru.litvinov.patientnotificator.component.NewButton;
import ru.litvinov.patientnotificator.component.PatientTableContextMenu;
import ru.litvinov.patientnotificator.component.pagination.PaginatedGrid;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.service.PatientService;
import ru.litvinov.patientnotificator.service.SchedulerService;
import ru.litvinov.patientnotificator.service.SmsService;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static ru.litvinov.patientnotificator.util.Constants.PATIENTS;

@PermitAll
@PageTitle(PATIENTS)
@Route(value = "patients", layout = MainView.class)
public class PatientView extends AbstractView {

    private final PatientService patientService;

    private final SchedulerService schedulerService;

    private final PaginatedGrid<Patient> table;

    public PatientView(
            final PatientService patientService,
            final SmsService smsService,
            final SchedulerService schedulerService
    ) {
        this.patientService = patientService;
        this.schedulerService = schedulerService;
        this.table = createTable();
        smsService.setUi(UI.getCurrent());
        smsService.setTable(table);
    }

    @Override
    @PostConstruct
    protected void initialize() {
        final var layout = new VerticalLayout(new H4(PATIENTS), createNewButton());
        layout.setPadding(false);
        horizontal.add(layout);
        setHeightFull();
        vertical.setHeightFull();
        vertical.add(table);
        add(vertical);
    }

    private Component createNewButton() {
        return new NewButton("Новый пациент", "patients/upsert");
    }

    private PaginatedGrid<Patient> createTable() {
        final Grid<Patient> grid = new Grid<>();
        grid.addColumn(createEditButtonRenderer()).setHeader("ФИО");
        grid.addColumn(Patient::getFileNumber).setHeader("Номер в системе");
        grid.addColumn(Patient::getPhone).setHeader("Номер телефона");
        grid.addColumn(patient -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(patient.getCreatedOn())).setHeader("Создан");
        grid.addColumn(patient -> Optional.ofNullable(patient.getUpdatedOn()).map(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")::format).orElse("н/д")).setHeader("Дата проверки");
        grid.addComponentColumn(patient -> {
            final var state = patient.getState();
            if (Objects.nonNull(state) && state == Patient.State.GOOD) {
                final var span = new Span(state.getDescription());
                span.getElement().getThemeList().add("badge success");
                return span;
            }
            if (Objects.nonNull(state) && state == Patient.State.ILL) {
                final var span = new Span(state.getDescription());
                span.getElement().getThemeList().add("badge error");
                return span;
            }
            final var span = new Span("н/д");
            span.getElement().getThemeList().add("badge contrast");
            return span;
        }).setHeader("Самочувствие");
        new PatientTableContextMenu(grid, patientService, schedulerService);
        final var paginatedGrid = new PaginatedGrid<>(grid);
        paginatedGrid.setItems(patientService.findAll());

        return paginatedGrid;
    }

    private ComponentRenderer<Button, Patient> createEditButtonRenderer() {
        final SerializableBiConsumer<Button, Patient> editButtonProcessor = (button, patient) -> {
            button.setThemeName("tertiary");
            button.setText(patient.getName());
            button.addClickListener(e -> button.getUI().ifPresent(ui -> ui.navigate("patients/upsert/", getQueryParameters(patient))));
        };
        return new ComponentRenderer<>(Button::new, editButtonProcessor);
    }

    private QueryParameters getQueryParameters(final Patient patient) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("id", patient.getId().toString());
        parameters.put("name", patient.getName());
        return QueryParameters.simple(parameters);
    }

}
