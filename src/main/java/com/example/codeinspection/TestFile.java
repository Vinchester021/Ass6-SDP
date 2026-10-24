package com.example.codeinspection;

public record TestFile(
        String fileName,
        int linesOfCode,
        int testCount,
        String testedClassName,
        boolean allTestsPassing
) implements ProjectElement {

    public TestFile {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name cannot be empty"
            );
        }

        if (linesOfCode < 0 || testCount < 0) {
            throw new IllegalArgumentException(
                    "Test values cannot be negative"
            );
        }

        if (testedClassName == null
                || testedClassName.isBlank()) {
            throw new IllegalArgumentException(
                    "Tested class name is required"
            );
        }
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}