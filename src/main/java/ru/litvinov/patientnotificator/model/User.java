package ru.litvinov.patientnotificator.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "USER_TABLE")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class User {

    @Id
    @NonNull
    @Column(length = 36)
    UUID id;

    @Column(name = "NAME", nullable = false, length = 100)
    String name;

    @Column(name = "LOGIN", nullable = false, unique = true, length = 50)
    String login;

    @Column(name = "PASSWORD", nullable = false)
    String password;

    @ManyToMany
    @JoinTable(name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    Set<Role> roles;

    @Column(name = "CREATED_ON")
    LocalDateTime createdOn;

    public User(@NonNull final UUID id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return roles.stream().map(Role::getName).anyMatch("ROLE_ADMIN"::equals);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
