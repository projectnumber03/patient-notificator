package ru.litvinov.patientnotificator.component;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.QueryParameters;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.service.PatientService;
import ru.litvinov.patientnotificator.service.SchedulerService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static ru.litvinov.patientnotificator.util.Constants.*;

public class PatientTableContextMenu {

    public PatientTableContextMenu(final Grid<Patient> grid, final PatientService patientService, final SchedulerService schedulerService, final PatientFilter patientFilter) {
        final var menu = grid.addContextMenu();

        final var editItem = menu.addItem(EDIT);
        editItem.addMenuItemClickListener(event -> event.getItem().ifPresent(patient -> editItem.getUI().ifPresent(ui -> ui.navigate("patients/upsert/", getQueryParameters(patient)))));

        menu.addItem("Повторный приём", event -> {
            event.getItem().ifPresent(patient -> {
                patient.setState(null);
                patient.setCheckedOn(null);
                patient.setUpdatedOn(LocalDateTime.now());
                patientService.save(patient);
                schedulerService.schedule(patient);
                event.getGrid().setItems(patientService.findAll().stream().filter(patientFilter::test).toList());
            });
        });

        final var sendBroadcastItem = menu.addItem(SEND_BROADCAST);
        sendBroadcastItem.addMenuItemClickListener(event -> event.getItem().ifPresent(patient -> sendBroadcastItem.getUI().ifPresent(ui -> ui.navigate("patients/broadcast/", getQueryParameters(patient)))));

        final var setStatusItem = menu.addItem("Установить статус");
        final var statusItemSubMenu = setStatusItem.getSubMenu();
        statusItemSubMenu.addItem("(нет)", event -> {
            event.getItem().ifPresent(patient -> {
                patient.setState(null);
                patientService.save(patient);
                event.getGrid().setItems(patientService.findAll().stream().filter(patientFilter::test).toList());
            });
        });
        Arrays.stream(Patient.State.values()).forEach(state -> {
            statusItemSubMenu.addItem(state.getDescription(), event -> {
                event.getItem().ifPresent(patient -> {
                    patient.setState(state);
                    patientService.save(patient);
                    event.getGrid().setItems(patientService.findAll().stream().filter(patientFilter::test).toList());
                });
            });
        });

        menu.addItem(DELETE, event -> {
            event.getItem().ifPresent(patient -> {
                final Runnable callback = () -> {
                    schedulerService.deleteAllByPatient(patient);
                    patientService.delete(patient);
                    event.getGrid().setItems(patientService.findAll().stream().filter(patientFilter::test).toList());
                };
                new ConfirmationDialog(String.format("Хотите удалить пациента \"%s\"?", patient.getName()), callback).open();
            });
        });

    }

    private QueryParameters getQueryParameters(final Patient patient) {
        return QueryParameters.simple(Map.of("id", patient.getId().toString()));
    }

}
