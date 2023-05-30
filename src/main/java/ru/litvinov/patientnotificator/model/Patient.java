package ru.litvinov.patientnotificator.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "PATIENTS")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Patient {

    @Id
    @GeneratedValue
    Long id;

    @Column
    String name;

    @Column
    String phone;

    @Column(name = "CREATED_ON")
    LocalDateTime createdOn;

    @Column(name = "CHAT_ID")
    Long chatId;

}
