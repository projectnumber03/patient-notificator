package ru.litvinov.patientnotificator.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class NewButton extends VerticalLayout {

    public NewButton(final String label, final String url) {
        setWidth("auto");
        setPadding(false);
        setAlignItems(Alignment.END);
        final var button = new Button(label);
        button.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        button.addClickListener(e -> button.getUI().ifPresent(ui -> ui.navigate(url)));
        add(button);
    }

}
