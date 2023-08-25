package ru.litvinov.patientnotificator.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import ru.litvinov.patientnotificator.view.LoginView;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/images/*.png").permitAll());
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Override
    protected void configure(final WebSecurity web) throws Exception {
        web.ignoring().requestMatchers("/api");
        super.configure(web);
    }

}
