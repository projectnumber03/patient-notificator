package ru.litvinov.patientnotificator.service;

import ru.litvinov.patientnotificator.model.Layout;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.model.SchedulerTask;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledFuture;

public interface ISchedulerService {
    void init();

    void schedule(Patient patient);

    void schedule(Patient patient, LocalDateTime executionDate, Layout layout);

    ScheduledFuture<?> submit(SchedulerTask schedulerTask);

    void deleteAllByPatient(Patient patient);

    void delete(SchedulerTask schedulerTask);

    ConcurrentLinkedDeque<SchedulerTask> getTaskCache();

}
