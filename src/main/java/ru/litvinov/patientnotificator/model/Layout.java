package ru.litvinov.patientnotificator.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "LAYOUTS")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Layout {

    @Id
    @Column(length = 36)
    @EqualsAndHashCode.Include
    UUID id;

    @Column
    String name;

    @Column
    String message;

    @Enumerated(EnumType.STRING)
    Type type;

    @Column(name = "CREATED_ON")
    LocalDateTime createdOn;

    public Layout(final UUID id) {
        this.id = id;
    }

    @AllArgsConstructor
    public enum Type {
        BROADCAST("Рассылка"),
        QUESTION("Вопрос");

        @Getter
        private final String name;

    }

}
