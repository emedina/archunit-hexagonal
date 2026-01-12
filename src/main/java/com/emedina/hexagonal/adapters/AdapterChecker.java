package com.emedina.hexagonal.adapters;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties;
import com.emedina.sharedkernel.domain.repository.annotation.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Checker for Hexagonal Architecture rules on the Adapters module.
 *
 * @author Enrique Medina Montenegro
 */
@Component
public class AdapterChecker {

    private static HexagonalArchitectureProperties properties;

    public AdapterChecker(final HexagonalArchitectureProperties properties) {
        AdapterChecker.properties = properties;
    }

    /**
     * Checks the dependencies for adapters.
     * <p>
     * This method performs one main check:
     * <ol>
     * <li>It verifies that the adapters do not depend on core modules like the domain or handlers.</li>
     * </ol>
     * <p>
     * The method uses the configured properties to determine the adapters packages.
     * It then creates and evaluates ArchUnit rules to enforce these dependency constraints.
     * <p>
     * If any violations are found, they are printed to the console. Otherwise, a success message is displayed.
     *
     * @param javaClasses the Java classes to check
     * @throws IllegalStateException if the adapters FQDN is not configured in the properties
     */
    @ArchTest
    public static void checkRules(final JavaClasses javaClasses) {
        if (properties == null) {
            throw new IllegalStateException("HexagonalArchitectureProperties have not been initialized");
        }

        final List<String> adaptersPackages = properties.adapters().fqdns();
        if (adaptersPackages == null || adaptersPackages.isEmpty()) {
            throw new IllegalStateException("Adapters FQDN is not configured in the properties");
        }

        // Rule 1. Make sure that core modules are not used in the adapters, unless it's a implementation of a Repository.
        final String[] coreModulesPackages = Stream.concat(properties.domain().fqdns().stream(),
            properties.handler().fqdns().stream()).toArray(String[]::new);
        final ArchRule coreModulesDependencyRule = noClasses()
            .that().resideInAnyPackage(adaptersPackages.toArray(new String[0]))
            .and().areNotAssignableFrom(implementRepositoryInterface())
            .should().dependOnClassesThat()
            .resideInAnyPackage(coreModulesPackages);

        FreezingArchRule.freeze(coreModulesDependencyRule).check(javaClasses);
    }

    /**
     * Creates a DescribedPredicate that checks if a class implements an interface annotated with @Repository.
     *
     * @return a DescribedPredicate that can be used in ArchUnit rules to check for @Repository interface implementation.
     */
    static DescribedPredicate<JavaClass> implementRepositoryInterface() {
        return new DescribedPredicate<>("implement a @Repository interface") {
            @Override
            public boolean test(JavaClass input) {
                return input.getInterfaces().stream()
                    .anyMatch(i -> i.toErasure().isAnnotatedWith(Repository.class));
            }
        };
    }

}