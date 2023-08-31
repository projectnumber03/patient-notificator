package ru.litvinov.patientnotificator.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor(force = true)
@Table(name = "ROLES")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Role {

    @Id
    @NonNull
    @Column(length = 36)
    @EqualsAndHashCode.Include
    UUID id;

    @Column(nullable = false, unique = true)
    String name;

    @Column
    String description;

    @ManyToMany(mappedBy = "roles")
    Set<User> users;

    @ManyToMany
    @JoinTable(name = "ROLE_PRIVILEGES",
            joinColumns = @JoinColumn(name = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "PRIVILEGE_ID"))
    Set<Privilege> privileges;

}
