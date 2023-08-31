package ru.litvinov.patientnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvinov.patientnotificator.model.Patient;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findAllByPhoneEndsWith(final String phone);

}
