package ru.litvinov.patientnotificator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.litvinov.patientnotificator.model.Layout;

import java.util.List;
import java.util.UUID;

public interface LayoutRepository extends JpaRepository<Layout, UUID> {
    List<Layout> findAllByType(final Layout.Type type);
}
