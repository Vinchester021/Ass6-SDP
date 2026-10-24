package com.example.codeinspection;

public record Dependency(
        String name,
        String version,
        boolean vulnerable,
        boolean prohibited,
        String license
) {
    public Dependency {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Dependency name cannot be empty"
            );
        }

        version = version == null ? "" : version.trim();
        license = license == null ? "" : license.trim();
    }

    public boolean hasSpecifiedVersion() {
        return !version.isBlank();
    }
}