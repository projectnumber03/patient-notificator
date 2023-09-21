package ru.litvinov.patientnotificator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.litvinov.patientnotificator.dto.SmsDTO;
import ru.litvinov.patientnotificator.service.SmsService;

@Slf4j
@RestController
@AllArgsConstructor
public class RestExolveIntegrationController {

    private final SmsService smsService;

    @PostMapping("/api")
    public String receiveMessage(@RequestBody final SmsDTO smsDTO) {
        log.info("received {}", smsDTO.toString());
        smsService.receive(smsDTO);
        return smsDTO.toString();
    }

}
