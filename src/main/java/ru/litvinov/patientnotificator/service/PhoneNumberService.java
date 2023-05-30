package ru.litvinov.patientnotificator.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PhoneNumberService {

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private Optional<Phonenumber.PhoneNumber> parse(final String value) {
        try {
            return Optional.of(phoneUtil.parse(value, "RU"));
        } catch (NumberParseException e) {
            log.error("error parsing phone number: " + value, e);
            return Optional.empty();
        }
    }

    public Optional<String> format(final String value) {
        final Optional<Phonenumber.PhoneNumber> number = parse(value);
        if (number.isEmpty()) return Optional.empty();
        return Optional.of(phoneUtil.format(number.get(), PhoneNumberUtil.PhoneNumberFormat.E164));
    }

    public boolean isValid(final String value) {
        final Optional<Phonenumber.PhoneNumber> number = parse(value);
        if (number.isEmpty()) return false;
        return phoneUtil.isValidNumber(number.get());
    }

}
