package ru.litvinov.patientnotificator.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.litvinov.patientnotificator.model.Privilege;
import ru.litvinov.patientnotificator.model.Role;
import ru.litvinov.patientnotificator.model.User;
import ru.litvinov.patientnotificator.repository.PrivilegeRepository;
import ru.litvinov.patientnotificator.repository.RoleRepository;
import ru.litvinov.patientnotificator.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SetupDataLoader {

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${administrator.login}")
    private String login;

    @Value("${administrator.password}")
    private String password;

    @PostConstruct
    protected void initialize() {
        if (!CollectionUtils.isEmpty(privilegeRepository.findAll())) return;
        final Map<String, String> privilegesMap = new HashMap<>() {{
            put("USER_CREATE_PRIVILEGE", "Создание пользователей");
            put("USER_EDIT_PRIVILEGE", "Редактирование пользователей");
            put("PATIENT_CREATE_PRIVILEGE", "Создание пациентов");
            put("PATIENT_EDIT_PRIVILEGE", "Редактирование пациентов");
        }};
        final Set<Privilege> adminPrivileges = privilegesMap.entrySet().stream()
                .map(this::createPrivilegeIfNotFound)
                .collect(Collectors.toSet());
        createAdminIfNotFound(adminPrivileges);
    }

    @Transactional
    protected void createAdminIfNotFound(final Set<Privilege> privileges) {
        final List<User> users = userRepository.findByLoginLike(login);
        final User user = CollectionUtils.isEmpty(users) ? new User() : users.iterator().next();
        user.setId(UUID.randomUUID());
        user.setLogin(login);
        user.setName(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(createAdminRoleIfNotFound(privileges)));
        user.setCreatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    protected Role createAdminRoleIfNotFound(final Set<Privilege> privileges) {
        final String name = "ROLE_ADMIN";
        final Role role = roleRepository.findByName(name);
        if (Objects.nonNull(role)) return role;
        final Role roleToCreate = new Role(UUID.randomUUID());
        roleToCreate.setName(name);
        roleToCreate.setDescription("Администратор");
        roleToCreate.setPrivileges(privileges);
        return roleRepository.save(roleToCreate);
    }

    @Transactional
    protected Privilege createPrivilegeIfNotFound(final Map.Entry<String, String> data) {
        final Privilege privilege = privilegeRepository.findByName(data.getKey());
        if (Objects.nonNull(privilege)) return privilege;
        final Privilege privilegeToCreate = new Privilege();
        privilegeToCreate.setId(UUID.randomUUID());
        privilegeToCreate.setName(data.getKey());
        privilegeToCreate.setDescription(data.getValue());
        return privilegeRepository.save(privilegeToCreate);
    }

}
