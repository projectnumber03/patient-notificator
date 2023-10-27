package ru.litvinov.patientnotificator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.litvinov.patientnotificator.model.Sms;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsDTO {

    @JsonProperty("id")
    Long messageId;

    @JsonProperty("received")
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime date;

    @JsonProperty("phone")
    String sender;

    @JsonProperty("to_phone")
    String receiver;

    @JsonProperty("message")
    String text;

    @JsonProperty("direction")
    String direction;

    public Sms toSms() {
        return new Sms(UUID.randomUUID(), sender, receiver, text);
    }

}
