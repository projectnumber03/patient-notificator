package ru.litvinov.patientnotificator.component;

import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.litvinov.patientnotificator.model.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Component
public class PatientFilter {

    private String name;
    private String fileNumber;
    private String phone;
    private LocalDate createdOn;
    private LocalDate updatedOn;
    private LocalDate checkedOn;
    private Patient.State state;

    private boolean matches(final String value, final String searchTerm) {
        return !StringUtils.hasText(searchTerm) || value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private boolean matches(final Integer value, final String searchTerm) {
        return Objects.isNull(searchTerm) || String.valueOf(value).toLowerCase().contains(searchTerm.toLowerCase());
    }

    private boolean matches(final LocalDateTime value, final LocalDate searchTerm) {
        return Objects.isNull(searchTerm) || (Objects.nonNull(value) && value.toLocalDate().equals(searchTerm));
    }

    public boolean test(final Patient patient) {
        return matches(patient.getName(), name) &&
                matches(patient.getFileNumber(), fileNumber) &&
                matches(patient.getPhone(), phone) &&
                matches(patient.getCreatedOn(), createdOn) &&
                matches(patient.getUpdatedOn(), updatedOn) &&
                matches(patient.getCheckedOn(), checkedOn) &&
                (Objects.isNull(state) || patient.getState() == state);
    }

}
