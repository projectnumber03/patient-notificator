package ru.litvinov.patientnotificator.dto;

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

    @JsonProperty("message_id")
    Long messageId;

    @JsonProperty("date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime date;

    @JsonProperty("sender")
    String sender;

    @JsonProperty("receiver")
    String receiver;

    @JsonProperty("text")
    String text;

    @JsonProperty("direction")
    String direction;

    public Sms toSms() {
        return new Sms(UUID.randomUUID(), sender, receiver, text);
    }

}
