package com.example.codeinspection;

public record SourceCodeFile(
        String fileName,
        int linesOfCode,
        double complexity,
        boolean hardcodedCredentials,
        boolean highRisk,
        int todoCount,
        boolean documented
) implements ProjectElement {

    public SourceCodeFile {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name cannot be empty"
            );
        }

        if (linesOfCode < 0) {
            throw new IllegalArgumentException(
                    "Lines of code cannot be negative"
            );
        }

        if (complexity < 0) {
            throw new IllegalArgumentException(
                    "Complexity cannot be negative"
            );
        }

        if (todoCount < 0) {
            throw new IllegalArgumentException(
                    "TODO count cannot be negative"
            );
        }
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}