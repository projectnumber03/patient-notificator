package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.litvinov.patientnotificator.model.Layout;
import ru.litvinov.patientnotificator.service.LayoutService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static ru.litvinov.patientnotificator.util.Constants.*;

@PageTitle(LAYOUT)
@RequiredArgsConstructor
@RolesAllowed(value = {"ROLE_ADMIN"})
@Route(value = "layouts/upsert", layout = MainView.class)
public class LayoutUpsertView extends AbstractView implements HasUrlParameter<String>, Validatable {

    private final LayoutService layoutService;

    private final TextField nameField = new TextField("Название");

    private final TextArea messageField = new TextArea("Сообщение");

    private final Label counterField = new Label();

    private final ComboBox<Layout.Type> typeField = new ComboBox<>("Тип");

    private final Button saveButton = new Button(SAVE);

    private Registration saveListener;

    @Override
    @PostConstruct
    protected void initialize() {
        super.initialize();
        vertical.add(nameField);
        vertical.add(createMessageField());
        vertical.add(counterField);
        vertical.add(createTypeField());
        vertical.add(createSaveButton());
        add(vertical);
    }

    private Component createMessageField() {
        messageField.setValueChangeMode(ValueChangeMode.EAGER);
        messageField.addValueChangeListener(e -> counterField.setText(getCounterValue(e.getValue())));

        return messageField;
    }

    private String getCounterValue(final String message) {
        return String.format("%d/%d", (message.length() / 70) + 1, message.length());
    }

    private Component createTypeField() {
        typeField.setItemLabelGenerator(Layout.Type::getName);
        typeField.setItems(Layout.Type.values());

        return typeField;
    }

    private Button createSaveButton() {
        saveListener = saveButton.addClickListener(e -> {
            if (!validate()) return;
            save(new Layout(UUID.randomUUID()));
            saveButton.getUI().ifPresent(ui -> ui.navigate("layouts"));
        });
        return saveButton;
    }

    private void save(final Layout layout) {
        layout.setName(nameField.getValue());
        layout.setCreatedOn(Optional.ofNullable(layout.getCreatedOn()).orElse(LocalDateTime.now()));
        layout.setMessage(messageField.getValue());
        layout.setType(typeField.getValue());

        layoutService.save(layout);
    }

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String s) {
        final var location = event.getLocation();
        final var queryParameters = location.getQueryParameters();
        final var parametersMap = queryParameters.getParameters();
        final var id = parametersMap.getOrDefault("id", Collections.emptyList());
        if (CollectionUtils.isEmpty(id)) return;
        final var layout = layoutService.findById(UUID.fromString(id.iterator().next()));
        if (layout.isEmpty()) return;
        Optional.ofNullable(layout.get().getName()).ifPresent(nameField::setValue);
        Optional.ofNullable(layout.get().getMessage()).ifPresent(value -> {
            messageField.setValue(value);
            counterField.setText(getCounterValue(value));
        });
        Optional.ofNullable(layout.get().getType()).ifPresent(typeField::setValue);
        saveListener.remove();
        saveListener = saveButton.addClickListener(e -> {
            if (!validate()) return;
            save(layout.get());
            saveButton.getUI().ifPresent(ui -> ui.navigate("layouts"));
        });
    }

    @Override
    public boolean validate() {
        final Binder<Layout> binder = new BeanValidationBinder<>(Layout.class);
        binder.forField(nameField).asRequired(REQUIRED_FIELD).bind(Layout::getName, Layout::setName);
        binder.forField(messageField).asRequired(REQUIRED_FIELD).bind(Layout::getMessage, Layout::setMessage);
        binder.forField(typeField).asRequired(REQUIRED_FIELD).bind(Layout::getType, Layout::setType);
        return binder.validate().isOk();
    }

}
