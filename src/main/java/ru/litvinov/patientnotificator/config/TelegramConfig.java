package ru.litvinov.patientnotificator.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.litvinov.patientnotificator.service.tg.TelegramBot;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramConfig {

    @Bean
    public BotSession botSession(final TelegramBot telegramBot) throws TelegramApiException {
        final var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        return telegramBotsApi.registerBot(telegramBot);
    }

}
