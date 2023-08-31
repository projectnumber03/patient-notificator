package ru.litvinov.patientnotificator.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REPORTS")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Report {

    @Id
    UUID id;

    @ManyToOne
    Patient patient;

    @Column(name = "REPORT_DATE")
    LocalDate date;

}
