package ru.litvinov.patientnotificator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litvinov.patientnotificator.model.Patient;
import ru.litvinov.patientnotificator.repository.PatientRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findById(final Long id) {
        if (Objects.isNull(id)) return Optional.empty();
        return patientRepository.findById(id);
    }

    public void delete(final Patient patient) {
        Optional.ofNullable(patient).ifPresent(patientRepository::delete);
    }

    public void save(final Patient patient) {
        Optional.ofNullable(patient).ifPresent(patientRepository::saveAndFlush);
    }

}
