package com.emedina.hexagonal.sharedkernel;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checker for Hexagonal Architecture rules on the Shared Kernel module.
 *
 * @author Enrique Medina Montenegro
 */
@Component
public class SharedKernelChecker {

    private static HexagonalArchitectureProperties properties;

    public SharedKernelChecker(final HexagonalArchitectureProperties properties) {
        SharedKernelChecker.properties = properties;
    }

    /**
     * Checks the dependencies of the shared kernel module.
     * <p>
     * This method performs one main check:
     * <ol>
     * <li>It ensures that the shared kernel only depends on allowed external libraries.</li>
     * </ol>
     * <p>
     * The method uses the configured properties to determine the shared kernel package and allowed libraries.
     * It then creates and evaluates ArchUnit rules to enforce these dependency constraints.
     * <p>
     * If any violations are found, they are printed to the console. Otherwise, a success message is displayed.
     *
     * @param javaClasses the Java classes to check
     * @throws IllegalStateException if the shared kernel FQDN is not configured in the properties
     */
    @ArchTest
    public static void checkRules(final JavaClasses javaClasses) {
        if (properties == null) {
            throw new IllegalStateException("HexagonalArchitectureProperties have not been initialized");
        }

        final List<String> sharedKernelPackage = properties.sharedKernel().fqdns();
        if (sharedKernelPackage == null || sharedKernelPackage.isEmpty()) {
            throw new IllegalStateException("Shared kernel FQDN is not configured in the properties");
        }

        final List<String> allowedLibraries = getAllowedLibraries();
        final String[] allowedPackages = getAllowedPackages(allowedLibraries);

        final ArchRule sharedKernelDependencyRule = classes()
            .that().resideInAnyPackage(sharedKernelPackage.toArray(new String[0]))
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(allowedPackages);

        FreezingArchRule.freeze(sharedKernelDependencyRule).check(javaClasses);
    }

    /**
     * Retrieves the list of allowed libraries for the shared kernel.
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
        final List<String> customLibraries = properties.sharedKernel().allowedLibraries();
        if (customLibraries != null && !customLibraries.isEmpty()) {
            customLibraries.addAll(defaultLibraries);
        }

        return customLibraries;
    }

    /**
     * Generates an array of allowed package names for the shared kernel.
     * This method combines the allowed libraries with the shared kernel's own package.
     *
     * @param allowedLibraries The list of allowed library package names
     * @return an array of package names that the shared kernel is allowed to depend on
     */
    private static String[] getAllowedPackages(final List<String> allowedLibraries) {
        final List<String> allowedPackages = new ArrayList<>(allowedLibraries);
        allowedPackages.addAll(properties.sharedKernel().fqdns());

        return allowedPackages.toArray(new String[0]);
    }

}