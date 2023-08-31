package ru.litvinov.patientnotificator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.model.Report;
import ru.litvinov.patientnotificator.repository.ReportRepository;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService implements ReportServiceMBean {

    private static final String TABLE_PATTERN = """
            <style>
                    table.patient-table{
                        width: 100%;
                        border-collapse:collapse;
                        border-spacing:0;
                        height: auto; }
                    table.patient-table,table.patient-table td, table.patient-table th {
                        border: 1px solid #595959; }
                    table.patient-table td,table.patient-table th {
                        min-height:35px;padding: 3px;\s
                        width: 30px;
                        height: 35px; }
                    table.patient-table th {
                        background: #347c99;
                        color: #fff;
                        font-weight: normal; }
                </style>
                <table class="patient-table">
                    <tbody>
                        <tr>
                            <td>ФИО</td>
                            <td>Номер в системе</td>
                            <td>Номер телефона</td>
                            <td>Самочувствие</td>
                        </tr>
                        :rows
                    </tbody>
                </table>
            """;

    private static final String ROW_PATTERN = """
            <tr>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
                <td>%s</td>
            </tr>
            """;

    private final MailService mailService;

    private final ReportRepository reportRepository;

    @Scheduled(cron = "${cron.expression.report}")
    public void send() {
        final var reports = reportRepository.findAllByDate(LocalDate.now());
        final var rows = reports.stream()
                .map(Report::getPatient)
                .map(p -> String.format(ROW_PATTERN, p.getName(), p.getFileNumber(), p.getPhone(), Optional.ofNullable(p.getState()).map(Patient.State::getDescription).orElse("н/д")))
                .collect(Collectors.joining());
        final var text = TABLE_PATTERN.replace(":rows", rows);
        mailService.sendMail(text);
        reportRepository.deleteAll(reports);
    }

    @Override
    public void clean() {
        reportRepository.deleteAll();
    }

    public void save(final Report report) {
        if (Objects.isNull(report) || reportRepository.existsAllByPatientAndDate(report.getPatient(), report.getDate())) {
            return;
        }
        reportRepository.save(report);
    }

    public void deleteAllByPatient(final Patient patient) {
        if (Objects.isNull(patient)) return;
        reportRepository.deleteAllByPatient(patient);
    }

}
