package ru.litvinov.patientnotificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import ru.litvinov.patientnotificator.model.Layout;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.model.SchedulerTask;
import ru.litvinov.patientnotificator.repository.SchedulerTaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ScheduledFuture;

@Slf4j
//@Service
@RequiredArgsConstructor
public class AndroidAppSchedulerService implements ISchedulerService{

    @Value("${exolve.sms.interval}")
    private Integer interval;

    private final SchedulerTaskRepository schedulerTaskRepository;

    @Override
    public void init() {
    }

    @Override
    public void schedule(final Patient patient) {
        final List<SchedulerTask> allByPatientAndLayoutType = schedulerTaskRepository.findAllByPatientAndLayout_Type(patient, Layout.Type.QUESTION);
        if (!CollectionUtils.isEmpty(allByPatientAndLayoutType)) {
            schedulerTaskRepository.deleteAll(allByPatientAndLayoutType);
        }
        final var schedulerTask = new SchedulerTask(UUID.randomUUID(), patient.getUpdatedOn().plusMinutes(interval), patient.getLayout(), patient);
        schedulerTaskRepository.saveAndFlush(schedulerTask);
    }

    @Override
    public void schedule(final Patient patient, final LocalDateTime executionDate, final Layout layout) {
        final var schedulerTask = new SchedulerTask(UUID.randomUUID(), executionDate, layout, patient);
        schedulerTaskRepository.saveAndFlush(schedulerTask);
    }

    @Override
    public ScheduledFuture<?> submit(final SchedulerTask schedulerTask) {
        return null;
    }

    @Override
    public void deleteAllByPatient(final Patient patient) {
        final var allByPatient = schedulerTaskRepository.findAllByPatient(patient);
        if (CollectionUtils.isEmpty(allByPatient)) return;
        schedulerTaskRepository.deleteAll(allByPatient);
    }

    @Override
    public void delete(final SchedulerTask schedulerTask) {
        if (Objects.isNull(schedulerTask)) return;
        schedulerTaskRepository.delete(schedulerTask);
    }

    @Override
    public ConcurrentLinkedDeque<SchedulerTask> getTaskCache() {
        return new ConcurrentLinkedDeque<>(schedulerTaskRepository.findAll());
    }

}
