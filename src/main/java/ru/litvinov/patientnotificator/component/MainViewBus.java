package ru.litvinov.patientnotificator.component;

import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.litvinov.patientnotificator.view.MainView;

@Getter
@Setter
@UIScope
@Component
@NoArgsConstructor
public class MainViewBus {

    private MainView mainView;

}
