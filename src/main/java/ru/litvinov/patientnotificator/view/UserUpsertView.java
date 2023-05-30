package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.litvinov.patientnotificator.model.Role;
import ru.litvinov.patientnotificator.model.User;
import ru.litvinov.patientnotificator.repository.RoleRepository;
import ru.litvinov.patientnotificator.service.UserService;
import ru.litvinov.patientnotificator.util.LoginGenerator;
import ru.litvinov.patientnotificator.util.PasswordGenerator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static ru.litvinov.patientnotificator.util.Constants.*;

@PageTitle(USER)
@RequiredArgsConstructor
@RolesAllowed(value = {"ROLE_ADMIN"})
@Route(value = "users/upsert", layout = MainView.class)
public class UserUpsertView extends AbstractView implements HasUrlParameter<String>, Validatable {

    private final TextField fioField = new TextField("ФИО");

    private final TextField loginField = new TextField("Логин");

    private final Button generateLoginButton = new Button("Создать логин");

    private final PasswordField passwordField = new PasswordField("Пароль");

    private final Button generatePasswordButton = new Button("Создать пароль");

    private final MultiSelectComboBox<Role> rolesField = new MultiSelectComboBox<>("Роли");

    private final Button saveButton = new Button(SAVE);

    private Registration saveListener;

    private final RoleRepository roleRepository;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @PostConstruct
    protected void initialize() {
        super.initialize();
        generateLoginButton.addClickListener(e -> {
            if (StringUtils.hasText(fioField.getValue())) {
                loginField.setValue(generateLogin(fioField.getValue()));
                return;
            }
            final var notification = Notification.show("Введите ФИО");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
        });
        generatePasswordButton.addClickListener(e -> passwordField.setValue(PasswordGenerator.generate()));
        vertical.add(fioField);
        final var loginLayout = new HorizontalLayout(loginField, generateLoginButton);
        loginLayout.setAlignItems(FlexComponent.Alignment.END);
        vertical.add(loginLayout);
        final var passwordLayout = new HorizontalLayout(passwordField, generatePasswordButton);
        passwordLayout.setAlignItems(FlexComponent.Alignment.END);
        vertical.add(passwordLayout);
        rolesField.setItems(roleRepository.findAll());
        rolesField.setItemLabelGenerator(Role::getName);
        vertical.add(rolesField);
        vertical.add(createSaveButton());
        add(vertical);
    }

    private String generateLogin(final String fio) {
        final var login = LoginGenerator.generate(fio);
        if (CollectionUtils.isEmpty(userService.findByLoginLike(login))) return login;
        if (login.matches("\\D+_\\d+")) {
            return login.split("_")[0] + "_" + (Long.parseLong(login.split("_")[1]) + 1);
        }
        return login + "_1";
    }

    private Button createSaveButton() {
        saveListener = saveButton.addClickListener(e -> {
            if (!validate()) return;
            save(new User(UUID.randomUUID()));
            saveButton.getUI().ifPresent(ui -> ui.navigate("users"));
        });
        return saveButton;
    }

    @Transactional
    protected void save(final User user) {
        user.setName(fioField.getValue());
        user.setLogin(loginField.getValue());
        user.setPassword(passwordEncoder.encode(passwordField.getValue()));
        user.setRoles(rolesField.getSelectedItems());
        user.setCreatedOn(Optional.ofNullable(user.getCreatedOn()).orElse(LocalDateTime.now()));
        userService.save(user);
    }

    @Override
    public void setParameter(final BeforeEvent event, @OptionalParameter final String s) {
        final var location = event.getLocation();
        final var queryParameters = location.getQueryParameters();
        final var parametersMap = queryParameters.getParameters();
        final var id = parametersMap.getOrDefault("id", Collections.emptyList());
        if (CollectionUtils.isEmpty(id)) return;
        final var user = userService.findById(UUID.fromString(id.iterator().next()));
        if (user.isEmpty()) return;
        fioField.setValue(user.get().getName());
        loginField.setValue(user.get().getLogin());
        passwordField.setValue(user.get().getPassword());
        rolesField.select(user.get().getRoles());
        saveListener.remove();
        saveListener = saveButton.addClickListener(e -> {
            if (!validate()) return;
            save(user.get());
            saveButton.getUI().ifPresent(ui -> ui.navigate("users"));
        });
    }

    @Override
    public boolean validate() {
        final Binder<User> binder = new BeanValidationBinder<>(User.class);
        binder.forField(fioField).asRequired(REQUIRED_FIELD).bind(User::getName, User::setName);
        binder.forField(loginField).asRequired(REQUIRED_FIELD).bind(User::getName, User::setName);
        binder.forField(passwordField).asRequired(REQUIRED_FIELD).bind(User::getName, User::setName);
        return binder.validate().isOk();
    }

}
