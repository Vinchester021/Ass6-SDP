package com.example.codeinspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TechnicalDebtVisitor implements ProjectVisitor {

    private final List<String> debtItems = new ArrayList<>();
    private int debtPoints;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.todoCount() > 0) {
            int points = file.todoCount() * 2;
            debtPoints += points;

            debtItems.add(
                    file.fileName()
                            + ": " + file.todoCount()
                            + " TODO item(s), +" + points + " points"
            );
        }

        if (file.complexity() > 10.0) {
            int points = (int) Math.ceil(file.complexity() - 10.0);
            debtPoints += points;

            debtItems.add(
                    file.fileName()
                            + ": high complexity, +"
                            + points + " points"
            );
        }

        if (!file.documented()) {
            debtPoints += 5;

            debtItems.add(
                    file.fileName()
                            + ": missing documentation, +5 points"
            );
        }
    }

    @Override
    public void visit(TestFile file) {
        if (!file.allTestsPassing()) {
            debtPoints += 5;

            debtItems.add(
                    file.fileName()
                            + ": failing tests, +5 points"
            );
        }

        if (file.testCount() == 0) {
            debtPoints += 4;

            debtItems.add(
                    file.fileName()
                            + ": no test methods, +4 points"
            );
        }
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (!file.secureSettings()) {
            debtPoints += 3;

            debtItems.add(
                    file.fileName()
                            + ": configuration must be improved, +3 points"
            );
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {

            if (!dependency.hasSpecifiedVersion()) {
                debtPoints += 3;

                debtItems.add(
                        dependency.name()
                                + ": dependency version is missing, +3 points"
                );
            }

            if (dependency.vulnerable()) {
                debtPoints += 5;

                debtItems.add(
                        dependency.name()
                                + ": vulnerable dependency, +5 points"
                );
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        // Directory traversal is handled by Directory.accept().
    }

    public int getDebtPoints() {
        return debtPoints;
    }

    public String getDebtLevel() {
        if (debtPoints <= 5) {
            return "LOW";
        }

        if (debtPoints <= 15) {
            return "MEDIUM";
        }

        return "HIGH";
    }

    public List<String> getDebtItems() {
        return Collections.unmodifiableList(debtItems);
    }

    public String getReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== TECHNICAL DEBT REPORT ===")
                .append(System.lineSeparator());

        report.append("Debt points: ")
                .append(debtPoints)
                .append(System.lineSeparator());

        report.append("Debt level: ")
                .append(getDebtLevel())
                .append(System.lineSeparator());

        if (debtItems.isEmpty()) {
            report.append("No technical debt detected.");
        } else {
            report.append("Debt items:")
                    .append(System.lineSeparator());

            for (String item : debtItems) {
                report.append("- ")
                        .append(item)
                        .append(System.lineSeparator());
            }
        }

        return report.toString();
    }

    public void reset() {
        debtItems.clear();
        debtPoints = 0;
    }
}