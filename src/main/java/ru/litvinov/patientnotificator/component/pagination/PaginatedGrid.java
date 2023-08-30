package ru.litvinov.patientnotificator.component.pagination;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public class PaginatedGrid<T> extends VerticalLayout {

    @Setter
    private int paginatorSize = 2;

    @Setter
    private int pageSize = 10;

    private List<List<T>> items = Collections.emptyList();

    private final Grid<T> grid;

    private final List<Tab> tabList = new ArrayList<>();

    private final Tabs tabs = new Tabs();

    private int active = 0;

    private Component pagination;

    public PaginatedGrid() {
        this(new Grid<>());
    }

    public PaginatedGrid(final Grid<T> grid) {
        this.grid = grid;
        setWidthFull();
        setHeightFull();
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.CENTER);
        add(this.grid);
    }

    private HorizontalLayout createPagination() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);

        final Button beginButton = new Button("<<");
        beginButton.addClickListener(event -> {
            active = 0;
            tabs.setSelectedTab(tabList.get(active));
            fill();
        });
        layout.add(beginButton);

        final Button previousButton = new Button("<");
        previousButton.addClickListener(event -> {
            active = Math.max(active - 1, 0);
            tabs.setSelectedTab(tabList.get(active));
            fill();
        });
        layout.add(previousButton);

        layout.add(new Label("Страница"));

        for (int i = 1; i <= items.size(); i++) {
            tabList.add(new Tab(String.valueOf(i)));
        }
        fill();
        tabs.add(tabList.toArray(new Tab[0]));
        tabs.addSelectedChangeListener(event -> {
            active = Integer.parseInt(event.getSelectedTab().getLabel()) - 1;
            tabs.setSelectedTab(tabList.get(active));
            fill();
        });
        layout.add(tabs);

        layout.add(new Label("из " + items.size()));

        final Button nextButton = new Button(">");
        nextButton.addClickListener(event -> {
            active = Math.min(active + 1, items.size() - 1);
            tabs.setSelectedTab(tabList.get(active));
            fill();
        });
        layout.add(nextButton);

        final Button endButton = new Button(">>");
        endButton.addClickListener(event -> {
            active = Math.max(items.size() - 1, 0);
            tabs.setSelectedTab(tabList.get(active));
            fill();
        });
        layout.add(endButton);

        return layout;
    }

    private void fill() {
        tabList.forEach(t -> t.setVisible(false));
        final int from = Math.max(active - paginatorSize, 0);
        final int to = Math.min(active + paginatorSize + 1, items.size());
        for (int i = from; i < to; i++) {
            tabList.get(i).setVisible(true);
        }
        if (CollectionUtils.isEmpty(items)) {
            grid.setItems(Collections.emptyList());
            return;
        }
        grid.setItems(items.get(active));
    }

    public void setItems(final Predicate<T> predicate) {
        setItems(items.stream().flatMap(Collection::stream).filter(predicate).toList());
    }

    public void setItems(final Collection<T> items) {
        if (!CollectionUtils.isEmpty(items) && grid.getColumns().isEmpty()) {
            createColumns(items.iterator().next());
        }
        if (Objects.nonNull(pagination)) {
            remove(pagination);
        }
        this.items = ListUtils.partition(new ArrayList<>(items), pageSize);
        if (CollectionUtils.isEmpty(this.items)) {
            fill();
            return;
        }
        pagination = createPagination();
        add(pagination);
    }

    private void createColumns(T item) {
        final Class<?> itemClass = item.getClass();
        for (final Field field : itemClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(GridValue.class)) continue;
            grid
                    .addColumn(i -> {
                        try {
                            return i.getClass().getMethod("get" + StringUtils.capitalize(field.getName())).invoke(i);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .setHeader(field.getAnnotation(GridValue.class).value());
        }
    }

}

