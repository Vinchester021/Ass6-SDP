package com.example.codeinspection;

public record ConfigurationFile(
        String fileName,
        String environment,
        boolean containsSecrets,
        boolean secureSettings
) implements ProjectElement {

    public ConfigurationFile {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name cannot be empty"
            );
        }

        if (environment == null || environment.isBlank()) {
            throw new IllegalArgumentException(
                    "Environment is required"
            );
        }
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}