package ru.litvinov.patientnotificator.util;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class LoginGenerator {

    private final Map<String, String> letters = new HashMap<>();

    private long randomUserCounter = 0;

    static {
        letters.put("А", "A");
        letters.put("Б", "B");
        letters.put("В", "V");
        letters.put("Г", "G");
        letters.put("Д", "D");
        letters.put("Е", "E");
        letters.put("Ё", "E");
        letters.put("Ж", "ZH");
        letters.put("З", "Z");
        letters.put("И", "I");
        letters.put("Й", "I");
        letters.put("К", "K");
        letters.put("Л", "L");
        letters.put("М", "M");
        letters.put("Н", "N");
        letters.put("О", "O");
        letters.put("П", "P");
        letters.put("Р", "R");
        letters.put("С", "S");
        letters.put("Т", "T");
        letters.put("У", "U");
        letters.put("Ф", "F");
        letters.put("Х", "H");
        letters.put("Ц", "C");
        letters.put("Ч", "CH");
        letters.put("Ш", "SH");
        letters.put("Щ", "SH");
        letters.put("Ы", "Y");
        letters.put("Э", "E");
        letters.put("Ю", "U");
        letters.put("Я", "YA");
        letters.put("а", "a");
        letters.put("б", "b");
        letters.put("в", "v");
        letters.put("г", "g");
        letters.put("д", "d");
        letters.put("е", "e");
        letters.put("ё", "e");
        letters.put("ж", "zh");
        letters.put("з", "z");
        letters.put("и", "i");
        letters.put("й", "y");
        letters.put("к", "k");
        letters.put("л", "l");
        letters.put("м", "m");
        letters.put("н", "n");
        letters.put("о", "o");
        letters.put("п", "p");
        letters.put("р", "r");
        letters.put("с", "s");
        letters.put("т", "t");
        letters.put("у", "u");
        letters.put("ф", "f");
        letters.put("х", "h");
        letters.put("ц", "c");
        letters.put("ч", "ch");
        letters.put("ш", "sh");
        letters.put("щ", "sh");
        letters.put("ы", "i");
        letters.put("э", "e");
        letters.put("ю", "u");
        letters.put("я", "ya");
    }

    private String toTranslit(final String text) {
        final StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            String l = text.substring(i, i + 1);
            sb.append(letters.getOrDefault(l, l));
        }
        return sb.toString();
    }

    public String generate(final String fio) {
        try {
            final String surname = fio.split(" ")[0];
            final String name = fio.split(" ")[1];
            return toTranslit(name.substring(0, 1).toUpperCase()) + toTranslit(surname.substring(0, 1)).substring(0, 1) + toTranslit(surname).substring(1).toLowerCase();
        } catch (Exception e) {
            randomUserCounter++;
            return "User_" + randomUserCounter;
        }
    }

}