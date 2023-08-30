package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import ru.litvinov.patientnotificator.component.NewButton;
import ru.litvinov.patientnotificator.component.PatientFilter;
import ru.litvinov.patientnotificator.component.PatientTableContextMenu;
import ru.litvinov.patientnotificator.component.pagination.PaginatedGrid;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.service.PatientService;
import ru.litvinov.patientnotificator.service.SchedulerService;
import ru.litvinov.patientnotificator.service.SmsService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import static ru.litvinov.patientnotificator.util.Constants.PATIENTS;

@PermitAll
@PageTitle(PATIENTS)
@Route(value = "patients", layout = MainView.class)
public class PatientView extends AbstractView {

    private final PatientService patientService;

    private final SchedulerService schedulerService;

    private final PatientFilter patientFilter;

    private final PaginatedGrid<Patient> table;

    public PatientView(
            final PatientService patientService,
            final SmsService smsService,
            final SchedulerService schedulerService,
            final PatientFilter patientFilter
    ) {
        this.patientService = patientService;
        this.schedulerService = schedulerService;
        this.patientFilter = patientFilter;
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
        final var nameColumn = grid.addColumn(createEditButtonRenderer()).setHeader("ФИО");
        final var fileNumberColumn = grid.addColumn(Patient::getFileNumber).setHeader("Номер в системе");
        final var phoneColumn = grid.addColumn(Patient::getPhone).setHeader("Номер телефона");
        final var createdOnColumn = grid.addColumn(patient -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(patient.getCreatedOn())).setHeader("Создан");
        final var updatedOnColumn = grid.addColumn(patient -> Optional.ofNullable(patient.getUpdatedOn()).map(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")::format).orElse("н/д")).setHeader("Изменён");
        final var checkedOnColumn = grid.addColumn(patient -> Optional.ofNullable(patient.getCheckedOn()).map(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")::format).orElse("н/д")).setHeader("Дата проверки");
        final var stateColumn = grid.addComponentColumn(patient -> {
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
        new PatientTableContextMenu(grid, patientService, schedulerService, patientFilter);
        final var headerRow = grid.appendHeaderRow();
        final var paginatedGrid = new PaginatedGrid<>(grid);
        headerRow.getCell(nameColumn).setComponent(createTextFilterHeader(v -> {
            paginatedGrid.setItems(patientService.findAll());
            patientFilter.setName(v);
            paginatedGrid.setItems(patientFilter::test);
        }));
        headerRow.getCell(fileNumberColumn).setComponent(createTextFilterHeader(v -> {
            paginatedGrid.setItems(patientService.findAll());
            patientFilter.setFileNumber(v);
            paginatedGrid.setItems(patientFilter::test);
        }));
        headerRow.getCell(phoneColumn).setComponent(createTextFilterHeader(v -> {
            paginatedGrid.setItems(patientService.findAll());
            patientFilter.setPhone(v);
            paginatedGrid.setItems(patientFilter::test);
        }));
        headerRow.getCell(createdOnColumn).setComponent(createDateFilterHeader(v -> {
            paginatedGrid.setItems(patientService.findAll());
            patientFilter.setCreatedOn(v);
            paginatedGrid.setItems(patientFilter::test);
        }));
        headerRow.getCell(updatedOnColumn).setComponent(createDateFilterHeader(v -> {
            paginatedGrid.setItems(patientService.findAll());
            patientFilter.setUpdatedOn(v);
            paginatedGrid.setItems(patientFilter::test);
        }));
        headerRow.getCell(checkedOnColumn).setComponent(createDateFilterHeader(v -> {
            paginatedGrid.setItems(patientService.findAll());
            patientFilter.setCheckedOn(v);
            paginatedGrid.setItems(patientFilter::test);
        }));
        headerRow.getCell(stateColumn).setComponent(createStateFilterHeader(v -> {
            paginatedGrid.setItems(patientService.findAll());
            patientFilter.setState(v);
            paginatedGrid.setItems(patientFilter::test);
        }));

        paginatedGrid.setItems(patientService.findAll());

        return paginatedGrid;
    }

    private Component createTextFilterHeader(final Consumer<String> filterChangeConsumer) {
        final var textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        final var layout = new VerticalLayout(textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    private Component createDateFilterHeader(final Consumer<LocalDate> filterChangeConsumer) {
        final var datePicker = new DatePicker();
        final var russianI18n = new DatePicker.DatePickerI18n();
        russianI18n.setMonthNames(List.of("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"));
        russianI18n.setWeekdays(List.of("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"));
        russianI18n.setWeekdaysShort(List.of("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"));
        russianI18n.setToday("Сегодня");
        russianI18n.setCancel("Отмена");
        russianI18n.setFirstDayOfWeek(1);
        datePicker.setI18n(russianI18n);
        datePicker.setClearButtonVisible(true);
        datePicker.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        datePicker.setWidthFull();
        datePicker.getStyle().set("max-width", "100%");
        datePicker.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        final var layout = new VerticalLayout(datePicker);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    private Component createStateFilterHeader(final Consumer<Patient.State> filterChangeConsumer) {
        final ComboBox<Patient.State> comboBox = new ComboBox<>();
        comboBox.setItems(Patient.State.values());
        comboBox.setItemLabelGenerator(Patient.State::getDescription);
        comboBox.setClearButtonVisible(true);
        comboBox.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
        comboBox.setWidthFull();
        comboBox.getStyle().set("max-width", "100%");
        comboBox.addValueChangeListener(e -> filterChangeConsumer.accept(e.getValue()));
        final var layout = new VerticalLayout(comboBox);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
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
