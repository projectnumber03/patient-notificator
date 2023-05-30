package ru.litvinov.patientnotificator.service.tg;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.litvinov.patientnotificator.service.PatientService;

@Component
@AllArgsConstructor
public class StartMessageHandler implements MessageHandler {

    private final PatientService patientService;

    @Override
    public boolean check(final Update update) {
        final var inMessage = update.getMessage();
        return "/start".equals(inMessage.getText());
    }

    @Override
    public SendMessage handle(final Update update) {
        return null;
    }

}
