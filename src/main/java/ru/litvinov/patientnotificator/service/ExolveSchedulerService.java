package ru.litvinov.patientnotificator.service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import ru.litvinov.patientnotificator.model.*;
import ru.litvinov.patientnotificator.repository.SchedulerTaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static ru.litvinov.patientnotificator.util.Constants.NAME_TAG;

@Slf4j
//@Service
@RequiredArgsConstructor
public class ExolveSchedulerService implements ISchedulerService {

    @Value("${exolve.sms.phone}")
    private String phone;

    @Value("${exolve.sms.interval}")
    private Integer interval;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Getter
    private final ConcurrentLinkedDeque<SchedulerTask> taskCache = new ConcurrentLinkedDeque<>();

    private final SchedulerTaskRepository schedulerTaskRepository;

    private final ReportService reportService;

    private final SmsService smsService;

    @Override
//    @PostConstruct
    public void init() {
        schedulerTaskRepository.findAll().forEach(st -> {
            st.setFuture(submit(st));
            taskCache.add(st);
        });
        if (CollectionUtils.isEmpty(taskCache)) return;
        log.info("tasks in cache: {}", taskCache.stream().map(SchedulerTask::toString).collect(Collectors.joining("\n")));
    }

    @Override
    public void schedule(final Patient patient) {
        final List<SchedulerTask> allByPatientAndLayoutType = schedulerTaskRepository.findAllByPatientAndLayout_Type(patient, Layout.Type.QUESTION);
        if (!CollectionUtils.isEmpty(allByPatientAndLayoutType)) {
            allByPatientAndLayoutType.stream().filter(sf -> Objects.nonNull(sf.getFuture())).forEach(sf -> sf.getFuture().cancel(true));
            taskCache.removeAll(allByPatientAndLayoutType);
            schedulerTaskRepository.deleteAll(allByPatientAndLayoutType);
        }
        final var schedulerTask = new SchedulerTask(UUID.randomUUID(), patient.getUpdatedOn().plusMinutes(interval), patient.getLayout(), patient);
        schedulerTaskRepository.saveAndFlush(schedulerTask);
        schedulerTask.setFuture(submit(schedulerTask));
        taskCache.add(schedulerTask);
    }

    @Override
    public void schedule(final Patient patient, final LocalDateTime executionDate, final Layout layout) {
        final var schedulerTask = new SchedulerTask(UUID.randomUUID(), executionDate, layout, patient);
        schedulerTaskRepository.saveAndFlush(schedulerTask);
        schedulerTask.setFuture(submit(schedulerTask));
        taskCache.add(schedulerTask);
    }

    @Override
    public ScheduledFuture<?> submit(final SchedulerTask schedulerTask) {
        final Runnable runnable = () -> {
            log.info("executing task {}", schedulerTask);
            final var patient = schedulerTask.getPatient();
            final var layout = schedulerTask.getLayout();
            smsService.send(new Sms(UUID.randomUUID(), phone, patient.getPhone().replace("+", ""), layout.getMessage().replaceAll(NAME_TAG, patient.getName())));
            reportService.save(new Report(UUID.randomUUID(), patient, LocalDate.now()));
            taskCache.remove(schedulerTask);
            schedulerTaskRepository.delete(schedulerTask);
        };
        return executorService.schedule(runnable, ChronoUnit.MINUTES.between(LocalDateTime.now(), schedulerTask.getExecutionDate()), TimeUnit.MINUTES);
    }

    @Override
    public void deleteAllByPatient(final Patient patient) {
        final var allByPatient = schedulerTaskRepository.findAllByPatient(patient);
        if (CollectionUtils.isEmpty(allByPatient)) return;
        allByPatient.forEach(st -> {
            Optional.ofNullable(st.getFuture()).ifPresent(sf -> sf.cancel(true));
            taskCache.remove(st);
        });
        schedulerTaskRepository.deleteAll(allByPatient);
    }

    @Override
    public void delete(final SchedulerTask schedulerTask) {
        if (Objects.isNull(schedulerTask)) return;
        Optional.ofNullable(schedulerTask.getFuture()).ifPresent(sf -> sf.cancel(true));
        taskCache.remove(schedulerTask);
        schedulerTaskRepository.delete(schedulerTask);
    }

}
