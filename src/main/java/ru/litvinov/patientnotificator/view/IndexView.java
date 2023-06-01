package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;


@PermitAll
@PageTitle("Главная")
@Route(value = "", layout = MainView.class)
public class IndexView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(final BeforeEnterEvent beforeEnterEvent) {
        beforeEnterEvent.rerouteTo(PatientView.class);
    }

}
