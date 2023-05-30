package ru.litvinov.patientnotificator;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Push
@Theme(value = "common-theme", variant = Lumo.LIGHT)
@SpringBootApplication
public class PatientNotificatorApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(PatientNotificatorApplication.class, args);
    }

}
