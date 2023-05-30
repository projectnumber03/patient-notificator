package ru.litvinov.patientnotificator.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;

public class ConfirmationDialog extends Dialog {

    private final String message;

    private final Runnable callback;

    public ConfirmationDialog(final String message, final Runnable callback) {
        this(message, callback, true);
    }

    public ConfirmationDialog(final String message, final Runnable callback, final boolean closeOnOutsideClick) {
        setCloseOnOutsideClick(closeOnOutsideClick);
        this.message = message;
        this.callback = callback;
        getElement().setAttribute("aria-label", "Подтвердить");
        VerticalLayout dialogLayout = createDialogLayout(this);
        add(dialogLayout);
    }

    private VerticalLayout createDialogLayout(final Dialog dialog) {
        final var headline = new H2("Подтвердить");
        headline.getStyle()
                .set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");
        final var fieldLayout = new VerticalLayout();
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        Optional.ofNullable(message).map(Label::new).ifPresent(fieldLayout::add);
        final var cancelButton = new Button("Отмена", e -> dialog.close());
        final var saveButton = new Button("ОК", e -> {
            Optional.ofNullable(callback).ifPresent(Runnable::run);
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        final var buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        final var dialogLayout = new VerticalLayout(headline, fieldLayout, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");
        return dialogLayout;
    }

}
