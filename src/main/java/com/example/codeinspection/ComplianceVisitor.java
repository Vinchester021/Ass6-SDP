package com.example.codeinspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ComplianceVisitor implements ProjectVisitor {

    private static final Set<String> ALLOWED_LICENSES = Set.of(
            "MIT",
            "Apache-2.0",
            "BSD-3-Clause"
    );

    private final List<String> violations = new ArrayList<>();

    @Override
    public void visit(SourceCodeFile file) {
        if (!file.documented()) {
            violations.add(
                    file.fileName() + ": source code is not documented"
            );
        }
    }

    @Override
    public void visit(TestFile file) {
        if (file.testedClassName().isBlank()) {
            violations.add(
                    file.fileName() + ": tested class is not specified"
            );
        }
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.containsSecrets()) {
            violations.add(
                    file.fileName() + ": configuration contains secrets"
            );
        }

        if (!file.secureSettings()) {
            violations.add(
                    file.fileName() + ": insecure configuration settings"
            );
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {

            if (!dependency.hasSpecifiedVersion()) {
                violations.add(
                        dependency.name() + ": dependency version is missing"
                );
            }

            if (dependency.prohibited()) {
                violations.add(
                        dependency.name() + ": prohibited dependency"
                );
            }

            if (!ALLOWED_LICENSES.contains(dependency.license())) {
                violations.add(
                        dependency.name()
                                + ": license is not allowed: "
                                + dependency.license()
                );
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        if (directory.getName().isBlank()) {
            violations.add("Directory name must not be blank");
        }
    }

    public boolean isCompliant() {
        return violations.isEmpty();
    }

    public int getViolationCount() {
        return violations.size();
    }

    public List<String> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public String getReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== COMPLIANCE REPORT ===")
                .append(System.lineSeparator());

        report.append("Status: ")
                .append(isCompliant() ? "COMPLIANT" : "NON-COMPLIANT")
                .append(System.lineSeparator());

        report.append("Violations: ")
                .append(getViolationCount())
                .append(System.lineSeparator());

        if (violations.isEmpty()) {
            report.append("All compliance requirements are satisfied.");
        } else {
            for (String violation : violations) {
                report.append("- ")
                        .append(violation)
                        .append(System.lineSeparator());
            }
        }

        return report.toString();
    }

    public void reset() {
        violations.clear();
    }
}