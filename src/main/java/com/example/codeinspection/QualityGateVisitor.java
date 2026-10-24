package com.example.codeinspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QualityGateVisitor implements ProjectVisitor {

    private static final double MAX_AVERAGE_COMPLEXITY = 10.0;
    private static final double MIN_TEST_TO_SOURCE_RATIO = 0.40;

    private final List<String> violations = new ArrayList<>();

    private int sourceFiles;
    private int testFiles;
    private int failingTestFiles;
    private double totalComplexity;

    @Override
    public void visit(SourceCodeFile file) {
        sourceFiles++;
        totalComplexity += file.complexity();

        if (file.todoCount() > 0) {
            violations.add(
                    file.fileName() + ": contains "
                            + file.todoCount() + " TODO item(s)"
            );
        }

        if (!file.documented()) {
            violations.add(
                    file.fileName() + ": documentation is missing"
            );
        }
    }

    @Override
    public void visit(TestFile file) {
        testFiles++;

        if (!file.allTestsPassing()) {
            failingTestFiles++;
            violations.add(
                    file.fileName() + ": tests are failing"
            );
        }
    }

    @Override
    public void visit(ConfigurationFile file) {
        // Configuration files are checked by ComplianceVisitor.
    }

    @Override
    public void visit(DependencyFile file) {
        // Dependencies are checked by ComplianceVisitor.
    }

    @Override
    public void visit(Directory directory) {
        // Directory traversal is handled by Directory.accept().
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

    public boolean isPassed() {
        return violations.isEmpty()
                && failingTestFiles == 0
                && getAverageComplexity() <= MAX_AVERAGE_COMPLEXITY
                && getTestToSourceRatio() >= MIN_TEST_TO_SOURCE_RATIO;
    }

    public List<String> getViolations() {
        List<String> result = new ArrayList<>(violations);

        if (getAverageComplexity() > MAX_AVERAGE_COMPLEXITY) {
            result.add(String.format(
                    Locale.US,
                    "Average complexity %.2f exceeds the allowed value %.2f",
                    getAverageComplexity(),
                    MAX_AVERAGE_COMPLEXITY
            ));
        }

        if (getTestToSourceRatio() < MIN_TEST_TO_SOURCE_RATIO) {
            result.add(String.format(
                    Locale.US,
                    "Test/source ratio %.2f is below the required value %.2f",
                    getTestToSourceRatio(),
                    MIN_TEST_TO_SOURCE_RATIO
            ));
        }

        return Collections.unmodifiableList(result);
    }

    public String getReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== QUALITY GATE ===")
                .append(System.lineSeparator());

        report.append(String.format(
                Locale.US,
                "Average complexity: %.2f%n",
                getAverageComplexity()
        ));

        report.append(String.format(
                Locale.US,
                "Test/source ratio: %.2f%n",
                getTestToSourceRatio()
        ));

        report.append("Failing test files: ")
                .append(failingTestFiles)
                .append(System.lineSeparator());

        report.append("Status: ")
                .append(isPassed() ? "PASSED" : "FAILED")
                .append(System.lineSeparator());

        List<String> allViolations = getViolations();

        if (!allViolations.isEmpty()) {
            report.append("Violations:")
                    .append(System.lineSeparator());

            for (String violation : allViolations) {
                report.append("- ")
                        .append(violation)
                        .append(System.lineSeparator());
            }
        }

        return report.toString();
    }

    public void reset() {
        violations.clear();
        sourceFiles = 0;
        testFiles = 0;
        failingTestFiles = 0;
        totalComplexity = 0.0;
    }
}