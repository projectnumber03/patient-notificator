package ru.litvinov.patientnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvinov.patientnotificator.model.Layout;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.model.SchedulerTask;

import java.util.List;
import java.util.UUID;

public interface SchedulerTaskRepository extends JpaRepository<SchedulerTask, UUID> {
    List<SchedulerTask> findAllByPatientAndLayout_Type(final Patient patient, final Layout.Type layoutType);

    List<SchedulerTask> findAllByPatient(final Patient patient);

}
