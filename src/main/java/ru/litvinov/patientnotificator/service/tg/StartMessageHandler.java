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
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;
        final var inMessage = update.getMessage();
        return "/start".equals(inMessage.getText());
    }

    @Override
    public SendMessage handle(final Update update) {
        final var inMessage = update.getMessage();
        final var chatId = inMessage.getChatId();
        final var patient = patientService.findByChatId(chatId);
        if (patient.isPresent()) {
            final var response = String.format("Здравствуйте, %s", patient.get().getName());
            return new SendMessage(chatId.toString(), response);
        }
        final var response = "Здравствуйте, для начала работы введите свой уникальный идентификационный номер";
        return new SendMessage(chatId.toString(), response);
    }

}
