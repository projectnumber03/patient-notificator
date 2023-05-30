package ru.litvinov.patientnotificator.service.tg;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.service.PatientService;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class PatientStateMessageHandler implements MessageHandler {

    private final PatientService patientService;

    @Override
    public boolean check(final Update update) {
        if (!update.hasCallbackQuery()) return false;
        final var callbackQuery = update.getCallbackQuery();
        final var callbackQueryData = callbackQuery.getData();
        return Arrays.stream(Patient.State.values())
                .map(Patient.State::getCommand)
                .anyMatch(callbackQueryData::equals);
    }

    @Override
    public SendMessage handle(final Update update) {
        final var callbackQuery = update.getCallbackQuery();
        final var inMessage = callbackQuery.getMessage();
        final var chatId = inMessage.getChatId();
        final var patient = patientService.findByChatId(chatId);
        if (patient.isEmpty()) return null;
        final var p = patient.get();
        p.setUpdatedOn(LocalDateTime.now());
        final var state = Patient.State.getByCommand(callbackQuery.getData());
        state.ifPresent(p::setState);
        patientService.save(p);
        if (state.isPresent() && state.get() == Patient.State.GOOD) {
            return new SendMessage(chatId.toString(), "Спасибо за информацию!");
        }
        if (state.isPresent() && state.get() == Patient.State.ILL) {
            return new SendMessage(chatId.toString(), "Спасибо за информацию, ожидайте звонка специалиста!");
        }
        return null;
    }

}
