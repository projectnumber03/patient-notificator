package ru.litvinov.patientnotificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final MailService mailService;

    private final PatientService patientService;

    @Scheduled(cron = "0 0 19 ? * MON-FRI")
    public void sendReport() {

    }

}
