package ru.litvinov.patientnotificator.service.tg;

import com.vaadin.flow.component.UI;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.litvinov.patientnotificator.component.pagination.PaginatedGrid;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.service.PatientService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    @Getter
    private final String botUsername;

    private final List<MessageHandler> handlers;

    private final PatientService patientService;

    @Setter
    private PaginatedGrid<Patient> table;

    @Setter
    private UI ui;

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botUsername,
            final List<MessageHandler> handlers,
            final PatientService patientService
    ) {
        super(botToken);
        this.handlers = handlers;
        this.botUsername = botUsername;
        this.patientService = patientService;
    }

    @Override
    public void onUpdateReceived(final Update update) {
        final var messages = handlers.stream()
                .filter(h -> h.check(update))
                .map(h -> h.handle(update))
                .filter(Objects::nonNull)
                .toList();
        for (final SendMessage message : messages) {
            send(message);
        }
        ui.access(() -> table.setItems(patientService.findAll()));
    }

    public void send(final SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("error sending telegram message", e);
        }
    }

}
