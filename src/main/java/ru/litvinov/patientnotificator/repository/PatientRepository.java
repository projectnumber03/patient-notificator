package ru.litvinov.patientnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvinov.patientnotificator.model.Patient;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
