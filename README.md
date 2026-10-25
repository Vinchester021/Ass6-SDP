# Smart Code Inspection & Compliance Engine

Assignment 6 for the Software Design Patterns course.

**Pattern:** Visitor  
**Language:** Java 17  
**Build tool:** Maven  
**Testing:** JUnit 5  
**Student:** Vilgelm Feller

## Project Overview

This project implements a Smart Code Inspection & Compliance Engine for analysing the structure of a software project.

The analysed FinTech project contains different element types:

- source code files;
- test files;
- configuration files;
- dependency files;
- nested directories.

The Visitor pattern allows different analysis operations to process the same project structure without placing all analysis logic inside the element classes.

## Implemented Visitors

| Visitor | Responsibility |
|---|---|
| `SecurityAuditVisitor` | Detects credentials, vulnerable dependencies, insecure configuration and high-risk files |
| `ProjectStatisticsVisitor` | Calculates files, lines of code, tests, dependencies and average complexity |
| `QualityGateVisitor` | Decides whether the project is ready for release |
| `ComplianceVisitor` | Checks development and business compliance rules |
| `TechnicalDebtVisitor` | Calculates the technical debt score and level |

`TechnicalDebtVisitor` is the additional custom Visitor.

## Project Structure

```text
fintech-payment-platform/
├── src/
│   ├── main/
│   │   ├── PaymentService.java
│   │   └── AuthenticationService.java
│   └── test/
│       ├── PaymentServiceTest.java
│       └── AuthenticationServiceTest.java
├── config/
│   └── application-prod.yml
└── pom.xml
```

The structure is created in `FintechProjectFactory` and represented by `Directory` and other `ProjectElement` implementations.

## Visitor Pattern Structure

### Element interface

`ProjectElement` declares:

```java
void accept(ProjectVisitor visitor);
```

### Concrete elements

- `SourceCodeFile`
- `TestFile`
- `ConfigurationFile`
- `DependencyFile`
- `Directory`

### Visitor interface

`ProjectVisitor` contains an overloaded `visit()` method for every concrete element type.

### Concrete Visitors

- `SecurityAuditVisitor`
- `ProjectStatisticsVisitor`
- `QualityGateVisitor`
- `ComplianceVisitor`
- `TechnicalDebtVisitor`

## Quality Gate Rules

The project passes the Quality Gate when:

- there are no critical security issues;
- average complexity is not greater than `10.0`;
- test/source ratio is at least `0.40`;
- all test files are passing;
- there are no additional quality violations.

## Running the Project

Run the `Main` class from IntelliJ IDEA.

The program applies all Visitors to the same FinTech project structure and prints:

1. Security Audit;
2. Project Statistics;
3. Quality Gate;
4. Compliance Report;
5. Technical Debt Report.

## Running Tests

Run the tests through IntelliJ IDEA:

```text
Maven → Lifecycle → test
```

Alternatively, if Maven is installed globally:

```bash
mvn clean test
```

The project contains 16 meaningful JUnit 5 tests covering:

- different element types;
- Security Visitor;
- Statistics Visitor;
- Quality Gate PASS and FAIL;
- compliance violations;
- nested directories;
- an empty directory;
- the additional Technical Debt Visitor;
- edge cases.

## UML Diagrams

- [Class Diagram](docs/class-diagram.md)
- [Sequence Diagram](docs/sequence-diagram.md)

## Visitor Pattern Questions

### What problem does Visitor solve in this project?

Visitor separates project elements from the operations performed on them. Source files, test files, configuration files, dependencies and directories only store project data. Security auditing, statistics, quality checks and compliance checks are implemented in separate Visitor classes.

### Where are Element and Visitor?

`ProjectElement` is the Element interface. It is implemented by `SourceCodeFile`, `TestFile`, `ConfigurationFile`, `DependencyFile` and `Directory`.

`ProjectVisitor` is the Visitor interface. It is implemented by all five analysis Visitor classes.

### What is double dispatch?

Double dispatch means that the executed operation depends on two runtime objects: the concrete element and the concrete Visitor.

First, the client calls:

```java
element.accept(visitor);
```

Then the element calls:

```java
visitor.visit(this);
```

Because `this` has a concrete element type, Java selects the correct overloaded `visit()` method without using `instanceof`.

### Why not put all operations directly inside the Element classes?

Doing so would give every element too many responsibilities. Any new analysis operation would require changes to all element classes. Visitor keeps the data model separate from security, statistics, quality and compliance logic.

### Is it easier to add a new Visitor or a new Element? Why?

It is easier to add a new Visitor. A new Visitor implements the existing `ProjectVisitor` interface and does not require modifications to the existing elements.

Adding a new element is more difficult because the Visitor interface and every existing concrete Visitor must receive a new `visit()` method.

### What happens if DatabaseMigrationFile is added?

A new `DatabaseMigrationFile` class must implement `ProjectElement` and define `accept()`.

The following method must be added to `ProjectVisitor`:

```java
void visit(DatabaseMigrationFile file);
```

After that, this method must be implemented in:

- `SecurityAuditVisitor`;
- `ProjectStatisticsVisitor`;
- `QualityGateVisitor`;
- `ComplianceVisitor`;
- `TechnicalDebtVisitor`.

The existing element classes do not need to change, but all Visitors must be updated. This is the main limitation of the Visitor pattern.

### When would Visitor be a bad choice?

Visitor is a bad choice when new element types are added frequently. Every new element requires changes to the Visitor interface and all Visitor implementations.

Visitor works best when the element hierarchy is stable but new operations are added frequently.

### How is Visitor different from Strategy?

Strategy selects one interchangeable algorithm for performing a particular task. For example, different route calculation algorithms can be selected at runtime.

Visitor performs an operation across a structure containing different object types. It uses overloaded `visit()` methods to process each concrete element differently.

### Which SOLID principles are used?

**Single Responsibility Principle:** elements store project data, while Visitors perform analysis.

**Open/Closed Principle:** new operations can be added through new Visitors without modifying existing elements.

**Dependency Inversion Principle:** elements depend on the `ProjectVisitor` abstraction rather than a particular concrete Visitor.

The project also demonstrates separation of concerns and polymorphism.

## Conclusion

The project demonstrates how the Visitor pattern separates a stable object structure from multiple changing operations. New inspection operations can be added without modifying existing project elements. The main trade-off is that adding a new element type requires updating every Visitor.