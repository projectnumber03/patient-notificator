package ru.litvinov.patientnotificator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.litvinov.patientnotificator.dto.SchedulerTaskDTO;
import ru.litvinov.patientnotificator.dto.SmsDTO;
import ru.litvinov.patientnotificator.model.Report;
import ru.litvinov.patientnotificator.repository.SchedulerTaskRepository;
import ru.litvinov.patientnotificator.service.ReportService;
import ru.litvinov.patientnotificator.service.SmsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class RestExolveIntegrationController {

    private final SmsService smsService;

    private final ReportService reportService;

    private final SchedulerTaskRepository schedulerTaskRepository;

    @PostMapping("/api")
    public String receiveMessage(@RequestBody final SmsDTO smsDTO) {
        log.info("received {}", smsDTO.toString());
        smsService.receive(smsDTO);
        return smsDTO.toString();
    }

    @GetMapping("/task")
    public List<SchedulerTaskDTO> getTasks() {
        final var tasks = schedulerTaskRepository.findAllByExecutionDateLessThanEqual(LocalDateTime.now());
        return tasks.stream().map(SchedulerTaskDTO::new).toList();
    }

    @PostMapping("/report")
    public String generateReports(@RequestBody final List<String> taskIdentities) {
        final var tasks = schedulerTaskRepository.findAllById(taskIdentities.stream().map(UUID::fromString).toList());
        final var reports = tasks.stream().map(t -> new Report(UUID.randomUUID(), t.getPatient(), LocalDate.now())).toList();
        reportService.saveAll(reports);
        schedulerTaskRepository.deleteAll(tasks);
        return reports.toString();
    }

}
