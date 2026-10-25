# Assignment 6 Report

## Smart Code Inspection & Compliance Engine

**Student:** Vilgelm Feller  
**Design Pattern:** Visitor  
**Language:** Java 17  
**Testing Framework:** JUnit 5

## 1. Introduction

The purpose of this assignment was to implement the Visitor design pattern in a Smart Code Inspection & Compliance Engine.

The system analyses a realistic FinTech project containing source code, tests, configuration, dependencies and nested directories.

## 2. Problem

Different operations must process the same project elements:

- security analysis;
- statistics collection;
- quality verification;
- compliance checking;
- technical debt calculation.

Placing these operations directly inside every element class would create strong coupling and give the element classes too many responsibilities.

The Visitor pattern solves this problem by moving operations into separate Visitor classes.

## 3. Element Hierarchy

All project elements implement the `ProjectElement` interface:

```java
public interface ProjectElement {
    void accept(ProjectVisitor visitor);
}
```

The concrete elements are:

- `SourceCodeFile`;
- `TestFile`;
- `ConfigurationFile`;
- `DependencyFile`;
- `Directory`.

`Directory` can contain other elements and nested directories. This creates a hierarchical project structure.

## 4. Visitor Hierarchy

`ProjectVisitor` declares an overloaded `visit()` method for every element type.

The implemented Visitors are:

### SecurityAuditVisitor

Detects:

- hardcoded credentials;
- high-risk source files;
- insecure configuration;
- vulnerable dependencies;
- prohibited dependencies.

It produces a security score between 0 and 100.

### ProjectStatisticsVisitor

Calculates:

- directory and file count;
- source and test file count;
- total lines of code;
- number of tests;
- number of dependencies;
- average source complexity;
- test/source ratio.

### QualityGateVisitor

Decides whether the project is ready for release using:

- security status;
- average complexity;
- test/source ratio;
- test results;
- additional quality violations.

### ComplianceVisitor

Checks:

- exposed credentials;
- secure configuration;
- specified dependency versions;
- prohibited dependencies;
- approved dependency licences;
- source code documentation.

### TechnicalDebtVisitor

This is the additional Visitor. It calculates debt points for:

- TODO items;
- high complexity;
- missing documentation;
- failing tests;
- insecure configuration;
- dependencies without versions;
- vulnerable dependencies.

## 5. Double Dispatch

Each element implements:

```java
@Override
public void accept(ProjectVisitor visitor) {
    visitor.visit(this);
}
```

The first dispatch happens when `accept()` is called on a concrete element. The second dispatch happens when the element calls the matching overloaded `visit()` method.

This allows the program to select the correct operation without `instanceof` or a large `switch`.

## 6. Demonstration Results

The demonstration FinTech project produced the following results:

- directories: 5;
- files: 6;
- lines of code: 900;
- source files: 2;
- test files: 2;
- test methods: 20;
- dependencies: 4;
- average complexity: 15.25;
- test/source ratio: 1.00;
- security score: 0/100;
- critical security issues: 6;
- security warnings: 3;
- compliance violations: 6;
- Quality Gate result: FAILED;
- technical debt level: HIGH.

The project intentionally contains several problems so that every Visitor can demonstrate meaningful behaviour.

## 7. Testing

The project contains 16 JUnit 5 tests.

The tests cover:

- safe and unsafe source files;
- security score calculation;
- Visitor reset behaviour;
- nested directory traversal;
- empty directory processing;
- project statistics;
- average complexity;
- Quality Gate PASS and FAIL;
- critical security problems;
- failing tests;
- valid and prohibited dependencies;
- configuration secrets;
- low and high technical debt;
- missing dependency versions.

All tests should finish with zero failures and zero errors.

## 8. Advantages

- Operations are separated from the element classes.
- New Visitors can be added without modifying existing elements.
- The code does not use `instanceof`.
- Each Visitor has one clear responsibility.
- The same project structure can be reused for different analyses.

## 9. Limitation

Adding a new element such as `DatabaseMigrationFile` requires:

1. creating the new element class;
2. adding a new method to `ProjectVisitor`;
3. implementing that method in every concrete Visitor.

Therefore, Visitor is most suitable when the element hierarchy changes rarely.

## 10. Conclusion

The assignment demonstrated the Visitor pattern, double dispatch, polymorphism, recursive directory traversal, JUnit testing and SOLID principles.

Visitor made it possible to add the custom `TechnicalDebtVisitor` without modifying any existing element class. This demonstrates the main advantage of the pattern: new operations can be added easily when the object structure remains stable.