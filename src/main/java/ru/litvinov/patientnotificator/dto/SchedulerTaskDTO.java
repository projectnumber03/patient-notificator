package ru.litvinov.patientnotificator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.litvinov.patientnotificator.model.SchedulerTask;

import java.time.LocalDateTime;
import java.util.UUID;

import static ru.litvinov.patientnotificator.util.Constants.NAME_TAG;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SchedulerTaskDTO {

    @JsonProperty
    UUID id;

    @JsonProperty
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime executionDate;

    @JsonProperty
    String message;

    @JsonProperty
    String phone;

    public SchedulerTaskDTO(final SchedulerTask schedulerTask) {
        this.id = schedulerTask.getId();
        this.executionDate = schedulerTask.getExecutionDate();
        this.message = schedulerTask.getLayout().getMessage().replaceAll(NAME_TAG, schedulerTask.getPatient().getName());
        this.phone = schedulerTask.getPatient().getPhone();
    }

}
