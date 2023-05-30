package ru.litvinov.patientnotificator.service.tg;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.litvinov.patientnotificator.service.PatientService;

@Component
@AllArgsConstructor
public class PatientIdMessageHandler implements MessageHandler {

    private final PatientService patientService;

    @Override
    public boolean check(final Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;
        final var inMessage = update.getMessage();
        return inMessage.getText().matches("\\d+");
    }

    @Override
    public SendMessage handle(final Update update) {
        final var inMessage = update.getMessage();
        final var chatId = inMessage.getChatId();
        final var unknownPatientMessage = "Извините, я не узнаю Вас(";
        try {
            final var patientId = Long.parseLong(update.getMessage().getText());
            final var patient = patientService.findById(patientId);
            if (patient.isEmpty()) {
                return new SendMessage(chatId.toString(), unknownPatientMessage);
            }
            final var p = patient.get();
            p.setChatId(chatId);
            patientService.save(p);
            final var response = String.format("Здравствуйте, %s", patient.get().getName());
            return new SendMessage(chatId.toString(), response);
        } catch (Exception e) {
            return new SendMessage(chatId.toString(), unknownPatientMessage);
        }
    }

}
