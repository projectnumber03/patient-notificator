package ru.litvinov.patientnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvinov.patientnotificator.model.Sms;

import java.util.List;
import java.util.UUID;

public interface SmsRepository extends JpaRepository<Sms, UUID> {
    List<Sms> findAllByExternalId(final Long externalId);
    Boolean existsByExternalId(final Long externalId);
}
