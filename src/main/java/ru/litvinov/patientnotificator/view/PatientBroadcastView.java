package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.litvinov.patientnotificator.model.Layout;
import ru.litvinov.patientnotificator.model.SchedulerTask;
import ru.litvinov.patientnotificator.service.LayoutService;
import ru.litvinov.patientnotificator.service.PatientService;
import ru.litvinov.patientnotificator.service.SchedulerService;

import java.util.Collections;
import java.util.List;

import static ru.litvinov.patientnotificator.util.Constants.*;

@PermitAll
@PageTitle(SEND_BROADCAST)
@RequiredArgsConstructor
@Route(value = "patients/broadcast", layout = MainView.class)
public class PatientBroadcastView extends AbstractView implements HasUrlParameter<String>, Validatable {

    private final LayoutService layoutService;

    private final PatientService patientService;

    private final SchedulerService schedulerService;

    private final DatePicker.DatePickerI18n i18n;

    private final ComboBox<Layout> layoutField = new ComboBox<>("Шаблон уведомления");

    private final DateTimePicker executionDateField = new DateTimePicker("Дата");

    private final Button saveButton = new Button(SAVE);

    @Override
    @PostConstruct
    protected void initialize() {
        super.initialize();
        vertical.add(createLayoutField());
        vertical.add(createExecutionDateField());
        vertical.add(saveButton);
        add(vertical);
    }

    private Component createLayoutField() {
        layoutField.setItemLabelGenerator(Layout::getName);
        final List<Layout> items = layoutService.findAllByType(Layout.Type.BROADCAST);
        layoutField.setItems(items);
        if (!CollectionUtils.isEmpty(items)) {
            layoutField.setValue(items.iterator().next());
        }
        return layoutField;
    }

    private Component createExecutionDateField() {
        executionDateField.setDatePickerI18n(i18n);
        return executionDateField;
    }

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String s) {
        final var location = event.getLocation();
        final var queryParameters = location.getQueryParameters();
        final var parametersMap = queryParameters.getParameters();
        final var id = parametersMap.getOrDefault("id", Collections.emptyList());
        if (CollectionUtils.isEmpty(id)) return;
        final var patient = patientService.findById(Long.parseLong(id.iterator().next()));
        if (patient.isEmpty()) return;
        saveButton.addClickListener(e -> {
            if (!validate()) return;
            schedulerService.schedule(patient.get(), executionDateField.getValue(), layoutField.getValue());
            saveButton.getUI().ifPresent(ui -> ui.navigate("patients"));
        });
    }

    @Override
    public boolean validate() {
        final Binder<SchedulerTask> binder = new BeanValidationBinder<>(SchedulerTask.class);
        binder.forField(executionDateField).asRequired(REQUIRED_FIELD).bind(SchedulerTask::getExecutionDate, SchedulerTask::setExecutionDate);
        return binder.validate().isOk();
    }

}
