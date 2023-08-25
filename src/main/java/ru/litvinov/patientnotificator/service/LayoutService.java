package ru.litvinov.patientnotificator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.litvinov.patientnotificator.model.Layout;
import ru.litvinov.patientnotificator.repository.LayoutRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class LayoutService {

    private final LayoutRepository layoutRepository;

    public List<Layout> findAll() {
        return layoutRepository.findAll();
    }

    public Optional<Layout> findById(final UUID id) {
        if (Objects.isNull(id)) return Optional.empty();
        return layoutRepository.findById(id);
    }

    public List<Layout> findAllByType(final Layout.Type type) {
        if (Objects.isNull(type)) return Collections.emptyList();
        return layoutRepository.findAllByType(type);
    }

    public void delete(final Layout layout) {
        Optional.ofNullable(layout).ifPresent(layoutRepository::delete);
    }

    public void save(final Layout layout) {
        Optional.ofNullable(layout).ifPresent(layoutRepository::save);
    }

}
