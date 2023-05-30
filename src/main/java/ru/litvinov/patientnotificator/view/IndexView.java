package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;


@PermitAll
@PageTitle("Главная")
@Route(value = "", layout = MainView.class)
public class IndexView extends AbstractView {

    @Override
    @PostConstruct
    protected void initialize() {
        UI.getCurrent().navigate("patients");
    }

}
