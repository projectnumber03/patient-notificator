package ru.litvinov.patientnotificator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class Sms {

    @Id
    @Column(length = 36)
    @EqualsAndHashCode.Include
    UUID id;

    @Column
    Long externalId;

    @Column
    String number;

    @Column
    String destination;

    @Column(name = "MESSAGE")
    String text;

    @Column(name = "SENDING_DATE")
    LocalDateTime sendingDate;

    @Column(name = "RECEIVING_DATE")
    LocalDateTime receivingDate;

    public Sms(final UUID id, final String number, final String destination, final String text) {
        this.id = id;
        this.number = number;
        this.destination = destination;
        this.text = text;
    }

}
