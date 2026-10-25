package com.example.codeinspection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitorPatternTest {

    @Test
    void securityDetectsHardcodedCredentials() {
        SourceCodeFile file = new SourceCodeFile(
                "AuthService.java",
                200,
                8.0,
                true,
                false,
                0,
                true
        );

        SecurityAuditVisitor visitor =
                new SecurityAuditVisitor();

        file.accept(visitor);

        assertTrue(visitor.hasCriticalIssues());
        assertEquals(1, visitor.getCriticalIssues());
        assertTrue(
                visitor.getIssues()
                        .get(0)
                        .contains("hardcoded credentials")
        );
    }

    @Test
    void securityReturnsFullScoreForSafeFile() {
        SourceCodeFile file = new SourceCodeFile(
                "SafeService.java",
                100,
                5.0,
                false,
                false,
                0,
                true
        );

        SecurityAuditVisitor visitor =
                new SecurityAuditVisitor();

        file.accept(visitor);

        assertEquals(100, visitor.getSecurityScore());
        assertFalse(visitor.hasCriticalIssues());
    }

    @Test
    void securityResetClearsCollectedData() {
        SourceCodeFile file = new SourceCodeFile(
                "RiskyService.java",
                100,
                20.0,
                true,
                true,
                0,
                true
        );

        SecurityAuditVisitor visitor =
                new SecurityAuditVisitor();

        file.accept(visitor);
        visitor.reset();

        assertEquals(100, visitor.getSecurityScore());
        assertEquals(0, visitor.getCriticalIssues());
        assertEquals(0, visitor.getWarnings());
        assertTrue(visitor.getIssues().isEmpty());
    }

    @Test
    void statisticsCountsNestedProjectElements() {
        Directory project = new Directory("project")
                .add(new Directory("src")
                        .add(new SourceCodeFile(
                                "PaymentService.java",
                                200,
                                8.0,
                                false,
                                false,
                                0,
                                true
                        ))
                        .add(new TestFile(
                                "PaymentServiceTest.java",
                                100,
                                10,
                                "PaymentService",
                                true
                        )));

        ProjectStatisticsVisitor visitor =
                new ProjectStatisticsVisitor();

        project.accept(visitor);

        assertEquals(2, visitor.getDirectoryCount());
        assertEquals(2, visitor.getTotalFiles());
        assertEquals(1, visitor.getSourceFiles());
        assertEquals(1, visitor.getTestFiles());
        assertEquals(300, visitor.getTotalLinesOfCode());
        assertEquals(10, visitor.getTotalTests());
    }

    @Test
    void statisticsCalculatesAverageComplexity() {
        Directory project = new Directory("src")
                .add(new SourceCodeFile(
                        "First.java",
                        100,
                        6.0,
                        false,
                        false,
                        0,
                        true
                ))
                .add(new SourceCodeFile(
                        "Second.java",
                        100,
                        10.0,
                        false,
                        false,
                        0,
                        true
                ));

        ProjectStatisticsVisitor visitor =
                new ProjectStatisticsVisitor();

        project.accept(visitor);

        assertEquals(
                8.0,
                visitor.getAverageComplexity(),
                0.001
        );
    }

    @Test
    void qualityGatePassesCleanProject() {
        Directory project = createCleanProject();

        QualityGateVisitor visitor =
                new QualityGateVisitor();

        project.accept(visitor);

        assertTrue(visitor.isPassed());
        assertTrue(visitor.getViolations().isEmpty());
    }

    @Test
    void qualityGateFailsWhenSourceContainsTodo() {
        Directory project = new Directory("project")
                .add(new SourceCodeFile(
                        "IncompleteService.java",
                        120,
                        5.0,
                        false,
                        false,
                        2,
                        true
                ))
                .add(new TestFile(
                        "IncompleteServiceTest.java",
                        80,
                        5,
                        "IncompleteService",
                        true
                ));

        QualityGateVisitor visitor =
                new QualityGateVisitor();

        project.accept(visitor);

        assertFalse(visitor.isPassed());
        assertTrue(
                visitor.getViolations()
                        .stream()
                        .anyMatch(value -> value.contains("TODO"))
        );
    }

    @Test
    void qualityGateFailsWhenTestsAreFailing() {
        Directory project = new Directory("project")
                .add(new SourceCodeFile(
                        "TransferService.java",
                        120,
                        5.0,
                        false,
                        false,
                        0,
                        true
                ))
                .add(new TestFile(
                        "TransferServiceTest.java",
                        80,
                        5,
                        "TransferService",
                        false
                ));

        QualityGateVisitor visitor =
                new QualityGateVisitor();

        project.accept(visitor);

        assertFalse(visitor.isPassed());
        assertTrue(
                visitor.getViolations()
                        .stream()
                        .anyMatch(value -> value.contains("tests are failing"))
        );
    }

    @Test
    void complianceAcceptsApprovedDependency() {
        DependencyFile file = new DependencyFile(
                "pom.xml",
                List.of(
                        new Dependency(
                                "spring-core",
                                "6.1.2",
                                false,
                                false,
                                "Apache-2.0"
                        )
                )
        );

        ComplianceVisitor visitor =
                new ComplianceVisitor();

        file.accept(visitor);

        assertTrue(visitor.isCompliant());
        assertEquals(0, visitor.getViolationCount());
    }

    @Test
    void complianceRejectsProhibitedDependency() {
        DependencyFile file = new DependencyFile(
                "pom.xml",
                List.of(
                        new Dependency(
                                "legacy-library",
                                "1.0",
                                false,
                                true,
                                "MIT"
                        )
                )
        );

        ComplianceVisitor visitor =
                new ComplianceVisitor();

        file.accept(visitor);

        assertFalse(visitor.isCompliant());
        assertTrue(
                visitor.getViolations()
                        .stream()
                        .anyMatch(value ->
                                value.contains("prohibited dependency")
                        )
        );
    }

    @Test
    void complianceRejectsConfigurationWithSecrets() {
        ConfigurationFile file = new ConfigurationFile(
                "application.yml",
                "production",
                true,
                true
        );

        ComplianceVisitor visitor =
                new ComplianceVisitor();

        file.accept(visitor);

        assertFalse(visitor.isCompliant());
        assertEquals(1, visitor.getViolationCount());
    }

    @Test
    void technicalDebtIsLowForCleanProject() {
        Directory project = createCleanProject();

        TechnicalDebtVisitor visitor =
                new TechnicalDebtVisitor();

        project.accept(visitor);

        assertEquals(0, visitor.getDebtPoints());
        assertEquals("LOW", visitor.getDebtLevel());
    }

    @Test
    void technicalDebtIsHighForProblematicSourceFile() {
        SourceCodeFile file = new SourceCodeFile(
                "LegacyService.java",
                500,
                20.0,
                false,
                false,
                3,
                false
        );

        TechnicalDebtVisitor visitor =
                new TechnicalDebtVisitor();

        file.accept(visitor);

        assertEquals(21, visitor.getDebtPoints());
        assertEquals("HIGH", visitor.getDebtLevel());
    }

    @Test
    void dependencyReportsMissingVersion() {
        Dependency dependency = new Dependency(
                "unknown-library",
                "",
                false,
                false,
                "MIT"
        );

        assertFalse(dependency.hasSpecifiedVersion());
    }


    @Test
    void statisticsProcessesEmptyDirectory() {
        Directory emptyDirectory = new Directory("empty");

        ProjectStatisticsVisitor visitor =
                new ProjectStatisticsVisitor();

        emptyDirectory.accept(visitor);

        assertEquals(1, visitor.getDirectoryCount());
        assertEquals(0, visitor.getTotalFiles());
        assertEquals(0, visitor.getTotalLinesOfCode());
        assertEquals(0.0, visitor.getAverageComplexity());
    }

    @Test
    void qualityGateFailsForCriticalSecurityIssue() {
        Directory project = new Directory("project")
                .add(new SourceCodeFile(
                        "UnsafeService.java",
                        100,
                        5.0,
                        true,
                        false,
                        0,
                        true
                ))
                .add(new TestFile(
                        "UnsafeServiceTest.java",
                        80,
                        5,
                        "UnsafeService",
                        true
                ));

        QualityGateVisitor visitor =
                new QualityGateVisitor();

        project.accept(visitor);

        assertFalse(visitor.isSecurityPassed());
        assertFalse(visitor.isPassed());
        assertEquals(1, visitor.getCriticalSecurityIssues());
    }



    private Directory createCleanProject() {
        return new Directory("clean-project")
                .add(new SourceCodeFile(
                        "CleanService.java",
                        100,
                        5.0,
                        false,
                        false,
                        0,
                        true
                ))
                .add(new TestFile(
                        "CleanServiceTest.java",
                        80,
                        5,
                        "CleanService",
                        true
                ));
    }
}