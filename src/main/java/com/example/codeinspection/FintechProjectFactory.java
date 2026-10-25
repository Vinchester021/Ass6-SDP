package com.example.codeinspection;

import java.util.List;

public final class FintechProjectFactory {

    private FintechProjectFactory() {
    }

    public static Directory createProject() {

        Directory mainDirectory = new Directory("main")
                .add(new SourceCodeFile(
                        "PaymentService.java",
                        320,
                        12.5,
                        false,
                        true,
                        2,
                        true
                ))
                .add(new SourceCodeFile(
                        "AuthenticationService.java",
                        260,
                        18.0,
                        true,
                        true,
                        1,
                        false
                ));

        Directory testDirectory = new Directory("test")
                .add(new TestFile(
                        "PaymentServiceTest.java",
                        180,
                        12,
                        "PaymentService",
                        true
                ))
                .add(new TestFile(
                        "AuthenticationServiceTest.java",
                        140,
                        8,
                        "AuthenticationService",
                        false
                ));

        Directory sourceDirectory = new Directory("src")
                .add(mainDirectory)
                .add(testDirectory);

        Directory configDirectory = new Directory("config")
                .add(new ConfigurationFile(
                        "application-prod.yml",
                        "production",
                        true,
                        false
                ));

        DependencyFile dependencyFile = new DependencyFile(
                "pom.xml",
                List.of(
                        new Dependency(
                                "spring-core",
                                "6.1.2",
                                false,
                                false,
                                "Apache-2.0"
                        ),
                        new Dependency(
                                "log4j-core",
                                "2.14.1",
                                true,
                                false,
                                "Apache-2.0"
                        ),
                        new Dependency(
                                "legacy-crypto",
                                "",
                                false,
                                true,
                                "GPL-3.0"
                        ),
                        new Dependency(
                                "jackson-databind",
                                "2.17.0",
                                false,
                                false,
                                "Apache-2.0"
                        )
                )
        );

        return new Directory("fintech-payment-platform")
                .add(sourceDirectory)
                .add(configDirectory)
                .add(dependencyFile);
    }
}