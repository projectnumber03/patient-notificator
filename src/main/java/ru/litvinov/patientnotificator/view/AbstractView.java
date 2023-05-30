package ru.litvinov.patientnotificator.view;

import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;

public abstract class AbstractView extends VerticalLayout {

    protected final VerticalLayout vertical = new VerticalLayout();

    protected final HorizontalLayout horizontal = new HorizontalLayout();

    public AbstractView() {
        horizontal.setWidthFull();
        vertical.add(horizontal);
    }

    protected void initialize() {
        horizontal.add(new H4(getClass().getAnnotation(PageTitle.class).value()));
    }

}
