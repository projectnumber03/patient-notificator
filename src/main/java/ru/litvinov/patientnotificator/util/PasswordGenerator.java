package ru.litvinov.patientnotificator.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;

import java.util.Arrays;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordGenerator {

    org.passay.PasswordGenerator passwordGenerator = new org.passay.PasswordGenerator();

    CharacterRule specialCharacterRule = new CharacterRule(new CharacterData() {
        @Override
        public String getErrorCode() {
            return "SAMPLE_ERROR_CODE";
        }

        @Override
        public String getCharacters() {
            return "!@#$%^&*";
        }
    });

    public String generate() {
        return passwordGenerator.generatePassword(10,
                Arrays.asList(
                        new CharacterRule(EnglishCharacterData.Digit),
                        new CharacterRule(EnglishCharacterData.Alphabetical),
                        specialCharacterRule
                ));
    }

}
