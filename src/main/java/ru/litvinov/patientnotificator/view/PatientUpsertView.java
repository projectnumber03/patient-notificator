package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import ru.litvinov.patientnotificator.model.Layout;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.service.ISchedulerService;
import ru.litvinov.patientnotificator.service.LayoutService;
import ru.litvinov.patientnotificator.service.PatientService;
import ru.litvinov.patientnotificator.service.PhoneNumberService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.litvinov.patientnotificator.util.Constants.*;

@PermitAll
@PageTitle(PATIENT)
@Route(value = "patients/upsert", layout = MainView.class)
public class PatientUpsertView extends AbstractView implements HasUrlParameter<String>, Validatable {

    private final PatientService patientService;

    private final LayoutService layoutService;

    private final PhoneNumberService phoneNumberService;

    private final ISchedulerService schedulerService;

    private final TextField fioField = new TextField("ФИО");

    private final TextField phoneField = new TextField("Номер телефона");

    private final IntegerField fileNumberField = new IntegerField("Номер в системе");

    private final ComboBox<Layout> layoutField = new ComboBox<>("Шаблон уведомления");

    private final Button saveButton = new Button(SAVE);

    private Registration saveListener;

    public PatientUpsertView(
            final PatientService patientService,
            final LayoutService layoutService,
            final PhoneNumberService phoneNumberService,
            @Qualifier("androidAppSchedulerService") final ISchedulerService schedulerService
    ) {
        this.patientService = patientService;
        this.layoutService = layoutService;
        this.phoneNumberService = phoneNumberService;
        this.schedulerService = schedulerService;
    }

    @Override
    @PostConstruct
    protected void initialize() {
        super.initialize();
        vertical.add(fioField);
        vertical.add(phoneField);
        vertical.add(fileNumberField);
        vertical.add(createLayoutField());
        vertical.add(createSaveButton());
        add(vertical);
    }

    private Component createLayoutField() {
        layoutField.setItemLabelGenerator(Layout::getName);
        final List<Layout> items = layoutService.findAllByType(Layout.Type.QUESTION);
        layoutField.setItems(items);
        if (!CollectionUtils.isEmpty(items)) {
            layoutField.setValue(items.iterator().next());
        }
        return layoutField;
    }

    private Button createSaveButton() {
        saveListener = saveButton.addClickListener(e -> {
            if (!validate()) return;
            final var patient = new Patient();
            save(patient);
            schedulerService.schedule(patient);
            saveButton.getUI().ifPresent(ui -> ui.navigate("patients"));
        });
        return saveButton;
    }

    private void save(final Patient patient) {
        patient.setName(fioField.getValue());
        phoneNumberService.format(phoneField.getValue()).ifPresent(patient::setPhone);
        patient.setFileNumber(fileNumberField.getValue());
        patient.setLayout(layoutField.getValue());
        patient.setCreatedOn(Optional.ofNullable(patient.getCreatedOn()).orElse(LocalDateTime.now()));
        patient.setUpdatedOn(LocalDateTime.now());
        patientService.save(patient);
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
        Optional.ofNullable(patient.get().getName()).ifPresent(fioField::setValue);
        Optional.ofNullable(patient.get().getPhone()).ifPresent(phoneField::setValue);
        Optional.ofNullable(patient.get().getFileNumber()).ifPresent(fileNumberField::setValue);
        Optional.ofNullable(patient.get().getLayout()).ifPresent(layoutField::setValue);
        saveListener.remove();
        saveListener = saveButton.addClickListener(e -> {
            if (!validate()) return;
            save(patient.get());
            saveButton.getUI().ifPresent(ui -> ui.navigate("patients"));
        });
    }

    @Override
    public boolean validate() {
        final Binder<Patient> binder = new BeanValidationBinder<>(Patient.class);
        binder.forField(fioField).asRequired(REQUIRED_FIELD).bind(Patient::getName, Patient::setName);
        binder.forField(phoneField).asRequired(REQUIRED_FIELD).withValidator(phoneNumberService::isValid, "Некорректный номер телефона").bind(Patient::getPhone, Patient::setPhone);
        binder.forField(fileNumberField).asRequired(REQUIRED_FIELD).bind(Patient::getFileNumber, Patient::setFileNumber);
        return binder.validate().isOk();
    }

}
