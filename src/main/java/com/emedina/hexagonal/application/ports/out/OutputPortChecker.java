package com.emedina.hexagonal.application.ports.out;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties;
import com.emedina.sharedkernel.application.annotation.Adapter;
import com.emedina.sharedkernel.application.annotation.ApplicationService;
import com.emedina.sharedkernel.application.annotation.OutputPort;
import com.emedina.sharedkernel.application.annotation.UseCase;
import com.emedina.sharedkernel.domain.factory.annotation.Factory;
import com.emedina.sharedkernel.domain.identity.annotation.Identity;
import com.emedina.sharedkernel.domain.model.annotation.ValueObject;
import com.emedina.sharedkernel.domain.repository.annotation.Repository;
import com.emedina.sharedkernel.domain.service.annotation.DomainService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checker for Hexagonal Architecture rules on the Output Ports.
 *
 * @author Enrique Medina Montenegro
 */
public class OutputPortChecker {

    private static HexagonalArchitectureProperties properties;

    public OutputPortChecker(final HexagonalArchitectureProperties properties) {
        OutputPortChecker.properties = properties;
    }

    /**
     * Checks the rules for the output ports.
     * <p>
     * This method performs the following checks:
     * <ol>
     * <li>Ensures that output ports only depend on allowed libraries.</li>
     * <li>Verifies that output ports are always annotated with @OutputPort.</li>
     * <li>Checks that certain prohibited annotations are not used on output ports.</li>
     * <li>Ensures that output ports are always interfaces, never classes.</li>
     * </ol>
     *
     * @param javaClasses the Java classes to check
     * @throws IllegalStateException if the output ports FQDN is not configured in the properties
     */
    @ArchTest
    public static void checkRules(final JavaClasses javaClasses) {
        if (properties == null) {
            throw new IllegalStateException("HexagonalArchitectureProperties have not been initialized");
        }

        final List<String> outputPortsPackage = properties.outputPorts().fqdns();
        if (outputPortsPackage == null || outputPortsPackage.isEmpty()) {
            throw new IllegalStateException("Output Ports FQDN is not configured in the properties");
        }

        final List<String> allowedLibraries = getAllowedLibraries();
        final String[] allowedPackages = getAllowedPackages(allowedLibraries);

        // Rule 1: Allowed dependencies
        final ArchRule outputPortDependencyRule = classes()
            .that().resideInAnyPackage(outputPortsPackage.toArray(new String[0]))
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(allowedPackages);

        // Rule 2: Must have @OutputPort annotation
        final ArchRule mustHaveOutputPortAnnotationRule = classes()
            .that().resideInAnyPackage(outputPortsPackage.toArray(new String[0]))
            .should().beAnnotatedWith(OutputPort.class);

        // Rule 3: Forbidden annotations
        final ArchRule forbiddenAnnotationsRule = noClasses()
            .that().resideInAnyPackage(outputPortsPackage.toArray(new String[0]))
            .should().beAnnotatedWith(UseCase.class)
            .orShould().beAnnotatedWith(Adapter.class)
            .orShould().beAnnotatedWith(ApplicationService.class)
            .orShould().beAnnotatedWith(Repository.class)
            .orShould().beAnnotatedWith(DomainService.class)
            .orShould().beAnnotatedWith(ValueObject.class)
            .orShould().beAnnotatedWith(Identity.class)
            .orShould().beAnnotatedWith(Factory.class);

        // Rule 4: Must be interfaces
        final ArchRule mustBeInterfaceRule = classes()
            .that().resideInAnyPackage(outputPortsPackage.toArray(new String[0]))
            .should().beInterfaces();

        // Apply and freeze the rules
        FreezingArchRule.freeze(outputPortDependencyRule).check(javaClasses);
        FreezingArchRule.freeze(mustHaveOutputPortAnnotationRule).check(javaClasses);
        FreezingArchRule.freeze(forbiddenAnnotationsRule).check(javaClasses);
        FreezingArchRule.freeze(mustBeInterfaceRule).check(javaClasses);
    }

    /**
     * Retrieves the list of allowed libraries for the output ports.
     * <p>
     * This method combines the default allowed libraries with any custom libraries
     * specified in the properties.
     *
     * @return a list of package names representing the allowed libraries
     */
    private static List<String> getAllowedLibraries() {
        final List<String> defaultLibraries = Arrays.asList(
            "java..", "javax..", "lombok..", "io.vavr..", "org.apache.commons.."
        );
        final List<String> customLibraries = properties.outputPorts().allowedLibraries();
        if (customLibraries != null && !customLibraries.isEmpty()) {
            customLibraries.addAll(defaultLibraries);
        }

        return customLibraries;
    }

    /**
     * Generates an array of allowed package names for the output ports.
     * This method combines the allowed libraries with the output ports' own package.
     *
     * @param allowedLibraries the list of allowed library package names
     * @return an array of package names that the output ports are allowed to depend on
     */
    private static String[] getAllowedPackages(final List<String> allowedLibraries) {
        final List<String> allowedPackages = new ArrayList<>(allowedLibraries);
        allowedPackages.addAll(properties.outputPorts().fqdns());

        return allowedPackages.toArray(new String[0]);
    }

}
