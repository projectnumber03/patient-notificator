package ru.litvinov.patientnotificator.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "PRIVILEGES")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Privilege {

    @Id
    @Column(length = 36)
    @EqualsAndHashCode.Include
    UUID id;

    @Column(nullable = false, unique = true)
    String name;

    @Column
    String description;

    @ManyToMany(mappedBy = "privileges")
    Set<Role> roles;

}
