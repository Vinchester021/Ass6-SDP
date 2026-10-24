package com.example.codeinspection;

import java.util.Locale;

public class ProjectStatisticsVisitor implements ProjectVisitor {

    private int totalFiles;
    private int sourceFiles;
    private int testFiles;
    private int directoryCount;
    private int totalLinesOfCode;
    private int totalTests;
    private int dependencyCount;
    private double totalComplexity;

    @Override
    public void visit(SourceCodeFile file) {
        totalFiles++;
        sourceFiles++;
        totalLinesOfCode += file.linesOfCode();
        totalComplexity += file.complexity();
    }

    @Override
    public void visit(TestFile file) {
        totalFiles++;
        testFiles++;
        totalLinesOfCode += file.linesOfCode();
        totalTests += file.testCount();
    }

    @Override
    public void visit(ConfigurationFile file) {
        totalFiles++;
    }

    @Override
    public void visit(DependencyFile file) {
        totalFiles++;
        dependencyCount += file.dependencies().size();
    }

    @Override
    public void visit(Directory directory) {
        directoryCount++;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getSourceFiles() {
        return sourceFiles;
    }

    public int getTestFiles() {
        return testFiles;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getDependencyCount() {
        return dependencyCount;
    }

    public double getAverageComplexity() {
        if (sourceFiles == 0) {
            return 0.0;
        }

        return totalComplexity / sourceFiles;
    }

    public double getTestToSourceRatio() {
        if (sourceFiles == 0) {
            return 0.0;
        }

        return (double) testFiles / sourceFiles;
    }

    public String getReport() {
        return String.format(
                Locale.US,
                """
                === PROJECT STATISTICS ===
                Directories: %d
                Total files: %d
                Source files: %d
                Test files: %d
                Lines of code: %d
                Test methods: %d
                Dependencies: %d
                Average complexity: %.2f
                Test/source ratio: %.2f
                """,
                directoryCount,
                totalFiles,
                sourceFiles,
                testFiles,
                totalLinesOfCode,
                totalTests,
                dependencyCount,
                getAverageComplexity(),
                getTestToSourceRatio()
        );
    }

    public void reset() {
        totalFiles = 0;
        sourceFiles = 0;
        testFiles = 0;
        directoryCount = 0;
        totalLinesOfCode = 0;
        totalTests = 0;
        dependencyCount = 0;
        totalComplexity = 0.0;
    }
}