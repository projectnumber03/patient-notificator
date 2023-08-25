package ru.litvinov.patientnotificator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender getJavaMailSender(
            @Value("${spring.mail.host}") final String host,
            @Value("${spring.mail.port}") final int port,
            @Value("${spring.mail.username}") final String username,
            @Value("${spring.mail.password}") final String password,
            @Value("${spring.mail.protocol}") final String protocol
    ) {
        final var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        final var props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
