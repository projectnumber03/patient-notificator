package ru.litvinov.patientnotificator.config;

import com.vaadin.flow.component.datepicker.DatePicker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class BaseConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public DatePicker.DatePickerI18n i18n() {
        final var russianI18n = new DatePicker.DatePickerI18n();
        russianI18n.setMonthNames(List.of("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"));
        russianI18n.setWeekdays(List.of("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"));
        russianI18n.setWeekdaysShort(List.of("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"));
        russianI18n.setToday("Сегодня");
        russianI18n.setCancel("Отмена");
        russianI18n.setFirstDayOfWeek(1);
        return russianI18n;
    }

}
