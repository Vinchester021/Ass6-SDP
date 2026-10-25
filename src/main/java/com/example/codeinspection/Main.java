package com.example.codeinspection;

public class Main {

    public static void main(String[] args) {

        Directory project =
                FintechProjectFactory.createProject();

        SecurityAuditVisitor securityVisitor =
                new SecurityAuditVisitor();

        ProjectStatisticsVisitor statisticsVisitor =
                new ProjectStatisticsVisitor();

        QualityGateVisitor qualityGateVisitor =
                new QualityGateVisitor();

        ComplianceVisitor complianceVisitor =
                new ComplianceVisitor();

        project.accept(securityVisitor);
        project.accept(statisticsVisitor);
        project.accept(qualityGateVisitor);
        project.accept(complianceVisitor);

        System.out.println(
                "=== SMART CODE INSPECTION & COMPLIANCE ENGINE ==="
        );

        System.out.println();
        System.out.println(securityVisitor.getReport());

        System.out.println();
        System.out.println(statisticsVisitor.getReport());

        System.out.println();
        System.out.println(qualityGateVisitor.getReport());

        System.out.println();
        System.out.println(complianceVisitor.getReport());
    }
}