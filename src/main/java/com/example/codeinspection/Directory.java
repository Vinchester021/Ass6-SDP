package com.example.codeinspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Directory implements ProjectElement {

    private final String name;
    private final List<ProjectElement> elements =
            new ArrayList<>();

    public Directory(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Directory name cannot be empty"
            );
        }

        this.name = name;
    }

    public Directory add(ProjectElement element) {
        elements.add(
                Objects.requireNonNull(
                        element,
                        "Project element cannot be null"
                )
        );

        return this;
    }

    public String getName() {
        return name;
    }

    public List<ProjectElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);

        for (ProjectElement element : elements) {
            element.accept(visitor);
        }
    }
}