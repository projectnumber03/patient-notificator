package ru.litvinov.patientnotificator.service.tg;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageHandler {

    boolean check(final Update update);

    SendMessage handle(final Update update);

}
