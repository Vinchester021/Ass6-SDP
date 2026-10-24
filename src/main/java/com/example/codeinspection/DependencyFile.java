package com.example.codeinspection;

import java.util.List;

public record DependencyFile(
        String fileName,
        List<Dependency> dependencies
) implements ProjectElement {

    public DependencyFile {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name cannot be empty"
            );
        }

        if (dependencies == null) {
            throw new IllegalArgumentException(
                    "Dependencies are required"
            );
        }

        dependencies = List.copyOf(dependencies);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}