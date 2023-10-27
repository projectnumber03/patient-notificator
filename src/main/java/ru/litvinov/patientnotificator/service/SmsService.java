package ru.litvinov.patientnotificator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.litvinov.patientnotificator.component.pagination.PaginatedGrid;
import ru.litvinov.patientnotificator.dto.SmsDTO;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.model.Report;
import ru.litvinov.patientnotificator.model.Sms;
import ru.litvinov.patientnotificator.repository.PatientRepository;
import ru.litvinov.patientnotificator.repository.SmsRepository;
import ru.litvinov.patientnotificator.util.Smsc;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService implements SmsServiceMBean {

    @Value("${smsc.sms.get-url}")
    private String receiveUrl;

    @Setter
    private UI ui;

    @Setter
    private PaginatedGrid<Patient> table;

    private final SmsRepository smsRepository;

    private final PatientRepository patientRepository;

    private final ReportService reportService;

    private final HttpClient httpClient;

    private final Smsc smsc;

    public void send(final Sms sms) {
        log.info("sending sms {}", sms);
        smsc.send_sms(sms.getDestination(), sms.getText(), 0, "", "", 0, "", "");
        sms.setSendingDate(LocalDateTime.now());
        smsRepository.save(sms);
    }

    public void receive() {
        try {
            final var request = HttpRequest.newBuilder()
                    .uri(URI.create(receiveUrl))
                    .GET()
                    .build();
            final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            final var objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final var receivedSms = objectMapper.readValue(response.body(), new TypeReference<List<SmsDTO>>() {});
            receivedSms.stream()
                    .filter(sms -> !smsRepository.existsByExternalId(sms.getMessageId()))
                    .peek(sms -> log.info(sms.toString()))
                    .forEach(this::receive);
        } catch (JsonProcessingException e) {
            final var error = "error while json processing";
            log.error(error, e);
            throw new RuntimeException(error, e);
        } catch (IOException | InterruptedException e) {
            final var error = "error sending request";
            log.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    public void receive(final SmsDTO smsDTO) {
        final var externalId = smsDTO.getMessageId();
        if (Objects.isNull(externalId)) {
            log.info("null external id");
            return;
        }
        if (Arrays.stream(Patient.State.values()).map(Patient.State::getCommand).noneMatch(smsDTO.getText().strip()::equals)) {
            log.info("wrong answer \"{}\"", smsDTO.getText());
            return;
        }
        final var allPatientsByPhoneEndsWith = patientRepository.findAllByPhoneEndsWith(smsDTO.getSender());
        if (CollectionUtils.isEmpty(allPatientsByPhoneEndsWith)) {
            log.info("no patients with telephone number {}", smsDTO.getSender());
            return;
        }
        final var sms = smsDTO.toSms();
        sms.setExternalId(externalId);
        sms.setReceivingDate(LocalDateTime.now());
        smsRepository.save(sms);
        allPatientsByPhoneEndsWith.forEach(p -> {
            Patient.State.getByCommand(sms.getText().strip()).ifPresent(state -> {
                p.setCheckedOn(LocalDateTime.now());
                p.setState(state);
                patientRepository.save(p);
                reportService.save(new Report(UUID.randomUUID(), p, LocalDate.now()));
            });
        });
        if (Objects.isNull(ui)) {
            log.info("null ui");
            return;
        }
        ui.access(() -> table.setItems(patientRepository.findAll()));
    }

    @Override
    public void clean() {
        smsRepository.deleteAll();
    }

}
