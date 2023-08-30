package ru.litvinov.patientnotificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService implements ReportServiceMBean {

    @Value("${exolve.sms.interval}")
    private Integer interval;

    private final MailService mailService;

    private final PatientService patientService;

    @Scheduled(cron = "${cron.expression.report}")
    public void sendReport() {
        final var patients = patientService.findAlertPatients(LocalDateTime.now().minusMinutes(interval));
        final var text = patients.stream().map(p -> String.format("%s %s", p.getPhone(), p.getName())).collect(Collectors.joining("<br>"));
        mailService.sendMail(text);
    }

}
