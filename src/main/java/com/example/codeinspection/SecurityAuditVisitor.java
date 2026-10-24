package com.example.codeinspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecurityAuditVisitor
        implements ProjectVisitor {

    private final List<String> issues =
            new ArrayList<>();

    private int criticalIssues;
    private int warnings;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hardcodedCredentials()) {
            addCriticalIssue(
                    file.fileName()
                            + ": hardcoded credentials detected"
            );
        }

        if (file.highRisk()) {
            addCriticalIssue(
                    file.fileName()
                            + ": high-risk source file"
            );
        }

        if (file.complexity() > 15) {
            addWarning(
                    file.fileName()
                            + ": very high complexity"
            );
        }
    }

    @Override
    public void visit(TestFile file) {
        if (!file.allTestsPassing()) {
            addWarning(
                    file.fileName()
                            + ": some tests are failing"
            );
        }
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.containsSecrets()) {
            addCriticalIssue(
                    file.fileName()
                            + ": exposed secret detected"
            );
        }

        if (!file.secureSettings()) {
            addWarning(
                    file.fileName()
                            + ": insecure configuration settings"
            );
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {
            if (dependency.vulnerable()) {
                addCriticalIssue(
                        dependency.name()
                                + ": vulnerable dependency"
                );
            }

            if (dependency.prohibited()) {
                addCriticalIssue(
                        dependency.name()
                                + ": prohibited dependency"
                );
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        // Directory contains elements but has no security data.
    }

    public int getSecurityScore() {
        int score =
                100
                        - criticalIssues * 25
                        - warnings * 10;

        return Math.max(score, 0);
    }

    public int getCriticalIssues() {
        return criticalIssues;
    }

    public int getWarnings() {
        return warnings;
    }

    public boolean hasCriticalIssues() {
        return criticalIssues > 0;
    }

    public List<String> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    public String getReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== SECURITY AUDIT ===")
                .append(System.lineSeparator());

        report.append("Security score: ")
                .append(getSecurityScore())
                .append("/100")
                .append(System.lineSeparator());

        report.append("Critical issues: ")
                .append(criticalIssues)
                .append(System.lineSeparator());

        report.append("Warnings: ")
                .append(warnings)
                .append(System.lineSeparator());

        if (issues.isEmpty()) {
            report.append("No security issues found.");
        } else {
            report.append("Issues:")
                    .append(System.lineSeparator());

            for (String issue : issues) {
                report.append("- ")
                        .append(issue)
                        .append(System.lineSeparator());
            }
        }

        return report.toString();
    }

    public void reset() {
        issues.clear();
        criticalIssues = 0;
        warnings = 0;
    }

    private void addCriticalIssue(String message) {
        criticalIssues++;
        issues.add("[CRITICAL] " + message);
    }

    private void addWarning(String message) {
        warnings++;
        issues.add("[WARNING] " + message);
    }
}