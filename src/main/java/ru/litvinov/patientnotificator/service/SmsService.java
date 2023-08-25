package ru.litvinov.patientnotificator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.litvinov.patientnotificator.model.Sms;
import ru.litvinov.patientnotificator.repository.PatientRepository;
import ru.litvinov.patientnotificator.repository.SmsRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${exolve.sms.token}")
    private String token;

    @Value("${exolve.sms.url}")
    private String url;

    @Setter
    private UI ui;

    @Setter
    private PaginatedGrid<Patient> table;

    private final SmsRepository smsRepository;

    private final PatientRepository patientRepository;

    private final HttpClient httpClient;

    public void send(final Sms sms) {
        log.info("sending sms {}", sms);
        try {
            final var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(sms.toMap())))
                    .header("Authorization", token)
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            sms.setSendingDate(LocalDateTime.now());
            smsRepository.save(sms);
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
        final var allSmsByExternalId = smsRepository.findAllByExternalId(externalId);
        if (!CollectionUtils.isEmpty(allSmsByExternalId)) return;
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
            });
        });
        ui.access(() -> table.setItems(patientRepository.findAll()));
    }

}
