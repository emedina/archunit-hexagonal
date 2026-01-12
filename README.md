# 🔷 ArchUnit Hexagonal

![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Java Version](https://img.shields.io/badge/Java-25-blue)

A specialized testing library for enforcing Hexagonal Architecture (Ports and Adapters) patterns in Java applications using ArchUnit, ensuring clean separation of concerns and architectural compliance.

## 📚 Further Learning

This implementation demonstrates patterns and principles thoroughly explored in:

**English Version**  
*Decoupling by Design: A Pragmatic Approach to Hexagonal Architecture*  

- [PDF](https://leanpub.com/decouplingbydesignapractitionersguidetohexagonalarchitecture)  
- [Kindle](https://a.co/d/4KwauyK)  
- [Paperback](https://a.co/d/cGQI8gX)  

**Versión en Español**  
*Desacoplamiento por Diseño: Una Guía Práctica para la Arquitectura Hexagonal*  

- [PDF](https://leanpub.com/desacoplamientopordiseounaguaprcticaparalaarquitecturahexagonal)  
- [Kindle](https://amzn.eu/d/ic50CoH)  
- [Tapa blanda](https://amzn.eu/d/1fHOpN6)  

The book provides in-depth coverage of:

- Architectural testing strategies for hexagonal systems
- Dependency rule enforcement patterns
- Layer isolation verification techniques
- Real-world architecture validation case studies
- Preventing architectural drift in large systems
- Automated compliance checking approaches
- Evolving architecture while maintaining constraints

## 🎯 Overview

This project provides a comprehensive set of architectural tests and rules to validate that your application correctly implements the Hexagonal Architecture pattern. It uses ArchUnit to enforce boundaries between different layers of your application, ensuring that dependencies flow in the correct direction and that each component adheres to its designated responsibilities.

## ✨ Features

- **🔷 Architectural Validation**: Automated tests to verify hexagonal architecture compliance
- **🧩 Configurable Package Structure**: Flexible configuration for your specific package naming conventions
- **🔒 Dependency Control**: Strict enforcement of allowed dependencies between layers
- **🛡️ Annotation Validation**: Verification of appropriate annotations for each architectural component
- **🔌 Spring Boot Integration**: Seamless integration with Spring Boot applications
- **⚙️ YAML Configuration**: Simple configuration through YAML files
- **🧪 Comprehensive Rules**: Rules for domains, adapters, ports, commands, queries, and handlers

## 📦 Installation

Add the project as a dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.emedina.hexagonal</groupId>
    <artifactId>archunit-hexagonal</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

## 🚀 Quick Start

### 1️⃣ Configure Your Architecture

Create an `archunit-hexagonal.yaml` file in your `src/main/resources` directory:

```yaml
hexagonal:
  architecture:
    shared-kernel:
      fqdns:
        - com.example.app.shared..
      allowed-libraries:
        - java..
        - javax..
        - lombok..
        - io.vavr..
    domain:
      fqdns:
        - com.example.app.domain..
      allowed-libraries:
        - java..
        - javax..
        - lombok..
        - io.vavr..
    # Configure other components similarly
    # ...
```

### 2️⃣ Create Architecture Tests

```java
import com.emedina.hexagonal.adapters.AdapterChecker;
import com.emedina.hexagonal.application.domain.DomainChecker;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("com.example.app");

    @Test
    void testDomainRules() {
        DomainChecker.checkRules(classes);
    }

    @Test
    void testAdapterRules() {
        AdapterChecker.checkRules(classes);
    }
    
    // Add more tests for other components
}
```

## 🏗️ Architecture Rules

The library enforces the following key architectural rules:

### Core Components

#### 🧠 Domain Layer

- Must only depend on allowed libraries and itself
- Must use appropriate domain annotations or no annotations
- Cannot use application-level annotations

#### 🔄 Application Layer

- Handlers can access domain and ports but not adapters
- Commands and queries have restricted dependencies
- Input and output ports have specific dependency rules

#### 🔌 Adapters

- Cannot depend on core modules (domain, handlers) except when implementing repositories
- Must implement appropriate interfaces

## ⚙️ Configuration Options

The library provides configuration options for each component of the hexagonal architecture:

| Component | Configuration | Description |
|-----------|--------------|-------------|
| **Shared Kernel** | `shared-kernel.fqdns` | Package names for shared kernel components |
| **Domain** | `domain.fqdns` | Package names for domain components |
| **Output Ports** | `output-ports.fqdns` | Package names for output port interfaces |
| **Input Ports** | `input-ports.fqdns` | Package names for input port interfaces |
| **Commands** | `command.fqdns` | Package names for command objects |
| **Queries** | `query.fqdns` | Package names for query objects |
| **Handlers** | `handler.fqdns` | Package names for command/query handlers |
| **Adapters** | `adapters.fqdns` | Package names for adapter implementations |

Each component also supports `allowed-libraries` to specify which external dependencies are permitted.

## 🧪 How It Works

1. **📋 Configuration**: Define your architecture's package structure in YAML
2. **🔍 Scanning**: ArchUnit scans your codebase for classes
3. **✅ Rule Verification**: The library applies architectural rules to your classes
4. **📊 Reporting**: Violations are reported as test failures with detailed messages
5. **🔒 Enforcement**: Prevents architectural drift by failing builds when rules are violated

## 📋 Dependencies

| Dependency | Purpose |
|------------|---------|
| **ArchUnit** | Core architecture testing framework |
| **Spring Boot** | Configuration properties support |
| **Lombok** | Boilerplate reduction |
| **Vavr** | Functional programming utilities |

## 🔧 Build Requirements

- **Java 25+**
- **Maven 3.9+**
- **Spring Boot 4.0.1+**

## 🤝 Contributing

1. 🍴 Fork the repository
2. 🌿 Create a feature branch
3. 📝 Update documentation as needed
4. 📤 Submit a pull request

## 📄 License

This project is licensed under the terms of the MIT license.

## 👨‍💻 Author

**Enrique Medina Montenegro**

---

## 🏷️ Tags

`hexagonal-architecture` `archunit` `architecture-testing` `ports-and-adapters` `ddd` `clean-architecture` `java` `testing` `spring-framework` `architectural-validation`

---

*🔷 This library helps you maintain a clean hexagonal architecture by automatically validating your code against architectural rules and constraints.*
