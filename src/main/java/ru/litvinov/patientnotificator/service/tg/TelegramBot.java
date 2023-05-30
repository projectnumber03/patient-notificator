package ru.litvinov.patientnotificator.service.tg;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    @Getter
    @Value("${telegram.bot.name}")
    private String botUsername;

    public TelegramBot(@Value("${telegram.bot.token}") final String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(final Update update) {
        try {
            if (!update.hasMessage() || !update.getMessage().hasText()) return;
            final var inMess = update.getMessage();
            final var chatId = inMess.getChatId().toString();
            final var response = parseMessage(inMess.getText());
            final var outMess = new SendMessage(chatId, response);
            execute(outMess);
        } catch (TelegramApiException e) {
            log.error("error processing telegram message", e);
        }
    }

    private String parseMessage(final String text) {
        return "";
    }

}
