package ru.litvinov.patientnotificator.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@Table(name = "PATIENTS")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class Patient {

    @Id
    @GeneratedValue
    Long id;

    @Column
    Integer fileNumber;

    @Column
    String name;

    @Column
    String phone;

    @Column(name = "CREATED_ON")
    LocalDateTime createdOn;

    @Column(name = "UPDATED_ON")
    LocalDateTime updatedOn;

    @Column(name = "CHECKED_ON")
    LocalDateTime checkedOn;

    @Enumerated(EnumType.STRING)
    State state;

    @ManyToOne
    Layout layout;

    @Getter
    @AllArgsConstructor
    public enum State {
        GOOD("Хорошее", "1"),
        ILL("Плохое", "2");

        private final String description;

        private final String command;

        public static Optional<State> getByCommand(final String command) {
            if (!StringUtils.hasText(command)) return Optional.empty();
            return Arrays.stream(State.values()).filter(s -> command.equals(s.getCommand())).findAny();
        }

    }

}
