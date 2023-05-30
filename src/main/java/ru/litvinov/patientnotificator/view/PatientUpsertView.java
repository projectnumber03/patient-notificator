package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.service.PatientService;
import ru.litvinov.patientnotificator.service.PhoneNumberService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static ru.litvinov.patientnotificator.util.Constants.*;

@PermitAll
@PageTitle(PATIENT)
@RequiredArgsConstructor
@Route(value = "patients/upsert", layout = MainView.class)
public class PatientUpsertView extends AbstractView implements HasUrlParameter<String>, Validatable {

    private final PatientService patientService;

    private final PhoneNumberService phoneNumberService;

    private final TextField fioField = new TextField("ФИО");

    private final TextField phoneField = new TextField("Номер телефона");

    private final Button saveButton = new Button(SAVE);

    private Registration saveListener;

    @Override
    @PostConstruct
    protected void initialize() {
        super.initialize();
        vertical.add(fioField);
        vertical.add(phoneField);
        vertical.add(createSaveButton());
        add(vertical);
    }

    private Button createSaveButton() {
        saveListener = saveButton.addClickListener(e -> {
            if (!validate()) return;
            save(new Patient());
            saveButton.getUI().ifPresent(ui -> ui.navigate("patients"));
        });
        return saveButton;
    }

    private void save(final Patient patient) {
        patient.setName(fioField.getValue());
        phoneNumberService.format(phoneField.getValue()).ifPresent(patient::setPhone);
        patient.setCreatedOn(Optional.ofNullable(patient.getCreatedOn()).orElse(LocalDateTime.now()));

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
        return binder.validate().isOk();
    }

}
