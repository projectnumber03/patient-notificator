package ru.litvinov.patientnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.model.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    void deleteAllByPatient(final Patient patient);
    boolean existsAllByPatientAndDate(final Patient patient, final LocalDate date);
    List<Report> findAllByDate(final LocalDate date);
}
