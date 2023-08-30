package ru.litvinov.patientnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.litvinov.patientnotificator.model.Patient;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findAllByPhoneEndsWith(final String phone);

    @Query("select p from Patient as p where p.state = 'ILL' or p.checkedOn is null")
    List<Patient> findAlertPatients();

}
