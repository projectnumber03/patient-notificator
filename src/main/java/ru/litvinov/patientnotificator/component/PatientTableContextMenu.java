package ru.litvinov.patientnotificator.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.QueryParameters;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.service.PatientService;
import ru.litvinov.patientnotificator.service.SchedulerService;

import java.util.Map;

import static ru.litvinov.patientnotificator.util.Constants.*;

public class PatientTableContextMenu {

    public PatientTableContextMenu(final Grid<Patient> grid, final PatientService patientService, final SchedulerService schedulerService) {
        final var menu = grid.addContextMenu();

        final var editItem = menu.addItem(EDIT);
        editItem.addMenuItemClickListener(event -> event.getItem().ifPresent(patient -> editItem.getUI().ifPresent(ui -> ui.navigate("patients/upsert/", getQueryParameters(patient)))));

        final var sendBroadcastItem = menu.addItem(SEND_BROADCAST);
        sendBroadcastItem.addMenuItemClickListener(event -> event.getItem().ifPresent(patient -> sendBroadcastItem.getUI().ifPresent(ui -> ui.navigate("patients/broadcast/", getQueryParameters(patient)))));

        menu.addItem(DELETE, event -> {
            event.getItem().ifPresent(patient -> {
                final Runnable callback = () -> {
                    schedulerService.deleteAllByPatient(patient);
                    patientService.delete(patient);
                    event.getGrid().setItems(patientService.findAll());
                };
                new ConfirmationDialog(String.format("Хотите удалить пациента \"%s\"?", patient.getName()), callback).open();
            });
        });

    }

    private QueryParameters getQueryParameters(final Patient patient) {
        return QueryParameters.simple(Map.of("id", patient.getId().toString()));
    }

}
