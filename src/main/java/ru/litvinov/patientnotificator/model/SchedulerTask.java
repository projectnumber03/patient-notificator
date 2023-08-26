package ru.litvinov.patientnotificator.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Data
@Entity
@NoArgsConstructor
@Table(name = "SCHEDULER_TASKS")
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class SchedulerTask {

    @Id
    @Column(length = 36)
    UUID id;

    @Column(name = "EXECUTION_DATE")
    LocalDateTime executionDate;

    @ManyToOne
    Layout layout;

    @ManyToOne
    Patient patient;

    @Transient
    @ToString.Exclude
    ScheduledFuture<?> future;

    public SchedulerTask(final UUID id, final LocalDateTime executionDate, final Layout layout, final Patient patient) {
        this.id = id;
        this.executionDate = executionDate;
        this.layout = layout;
        this.patient = patient;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (SchedulerTask) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
