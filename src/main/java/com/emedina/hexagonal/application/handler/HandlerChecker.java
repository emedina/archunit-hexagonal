package com.emedina.hexagonal.application.handler;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
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
 * Checker for Hexagonal Architecture rules on Handlers.
 *
 * @author Enrique Medina Montenegro
 */
public class HandlerChecker {

    private static HexagonalArchitectureProperties properties;

    public HandlerChecker(final HexagonalArchitectureProperties properties) {
        HandlerChecker.properties = properties;
    }

    /**
     * Checks the rules for handler services.
     * <p>
     * This method performs the following checks:
     * <ol>
     * <li>Ensures that handler services only depend on allowed libraries.</li>
     * <li>Verifies that handler services are always annotated with @ApplicationService.</li>
     * <li>Checks that certain prohibited annotations are not used on handler services.</li>
     * <li>Ensures that if an handler service implements a class annotated with @UseCase,
     * its name follows the convention: [NameUseCase - "UseCase" + "Handler"].</li>
     * </ol>
     *
     * @param javaClasses the Java classes to check
     * @throws IllegalStateException if the handler services FQDN is not configured in the properties
     */
    @ArchTest
    public static void checkRules(final JavaClasses javaClasses) {
        if (properties == null) {
            throw new IllegalStateException("HexagonalArchitectureProperties have not been initialized");
        }

        final List<String> applicationServicesPackage = properties.handler().fqdns();
        if (applicationServicesPackage == null || applicationServicesPackage.isEmpty()) {
            throw new IllegalStateException("Handler FQDN is not configured in the properties");
        }

        final List<String> allowedLibraries = getAllowedLibraries();
        final String[] allowedPackages = getAllowedPackages(allowedLibraries);

        // Rule 1: Allowed dependencies
        final ArchRule allowedDependenciesRule = classes()
            .that().resideInAnyPackage(applicationServicesPackage.toArray(new String[0]))
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(allowedPackages);

        // Rule 2: Must have @ApplicationService annotation if it is a handler
        final ArchRule mustHaveApplicationServiceAnnotationRule = classes()
            .that().resideInAnyPackage(applicationServicesPackage.toArray(new String[0]))
            .and(implementUseCaseInterface())
            .should().beAnnotatedWith(ApplicationService.class);

        // Rule 3: Forbidden annotations
        final ArchRule forbiddenAnnotationsRule = noClasses()
            .that().resideInAnyPackage(applicationServicesPackage.toArray(new String[0]))
            .should().beAnnotatedWith(UseCase.class)
            .orShould().beAnnotatedWith(OutputPort.class)
            .orShould().beAnnotatedWith(Adapter.class)
            .orShould().beAnnotatedWith(Repository.class)
            .orShould().beAnnotatedWith(DomainService.class)
            .orShould().beAnnotatedWith(ValueObject.class)
            .orShould().beAnnotatedWith(Identity.class)
            .orShould().beAnnotatedWith(Factory.class);

        // Rule 4: Naming convention for UseCase implementations
        final ArchRule useCaseNamingConventionRule = classes()
            .that().resideInAnyPackage(applicationServicesPackage.toArray(new String[0]))
            .and(implementUseCaseInterface())
            .should(followUseCaseNamingConvention());

        // Apply and freeze the rules
        FreezingArchRule.freeze(allowedDependenciesRule).check(javaClasses);
        FreezingArchRule.freeze(mustHaveApplicationServiceAnnotationRule).check(javaClasses);
        FreezingArchRule.freeze(forbiddenAnnotationsRule).check(javaClasses);
        FreezingArchRule.freeze(useCaseNamingConventionRule).check(javaClasses);
    }

    /**
     * Creates a DescribedPredicate that checks if a class implements an interface annotated with @UseCase.
     *
     * @return a DescribedPredicate that can be used in ArchUnit rules to check for @UseCase interface implementation.
     */
    static DescribedPredicate<JavaClass> implementUseCaseInterface() {
        return new DescribedPredicate<>("implement a @UseCase interface") {
            @Override
            public boolean test(JavaClass input) {
                return input.getInterfaces().stream()
                    .anyMatch(i -> i.toErasure().isAnnotatedWith(UseCase.class));
            }
        };
    }

    /**
     * Creates an ArchCondition that checks if a class follows the naming convention for UseCase implementations.
     * The class name should be the same as the UseCase interface it implements, minus "UseCase" and plus "Handler".
     *
     * @return an ArchCondition that can be used in ArchUnit rules to check for the UseCase naming convention.
     */
    static ArchCondition<JavaClass> followUseCaseNamingConvention() {
        return new ArchCondition<>("follow UseCase naming convention") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                item.getInterfaces().stream()
                    .filter(i -> i.toErasure().isAnnotatedWith(UseCase.class))
                    .forEach(useCaseInterface -> {
                        final String expectedName =
                            useCaseInterface.toErasure().getSimpleName().replace("UseCase", "") + "Handler";
                        if (!item.getSimpleName().equals(expectedName)) {
                            events.add(SimpleConditionEvent.violated(item,
                                String.format("Handler %s implements %s, but is not named %s",
                                    item.getSimpleName(), useCaseInterface.toErasure().getSimpleName(), expectedName)));
                        }
                    });
            }
        };
    }

    /**
     * Retrieves the list of allowed libraries for handler services.
     * This method combines the default allowed libraries with any custom libraries
     * specified in the properties.
     *
     * @return a list of package names representing the allowed libraries.
     */
    static List<String> getAllowedLibraries() {
        final List<String> defaultLibraries = Arrays.asList(
            "java..", "javax..", "lombok..", "io.vavr..", "org.apache.commons.."
        );
        final List<String> customLibraries = properties.handler().allowedLibraries();
        if (customLibraries != null && !customLibraries.isEmpty()) {
            customLibraries.addAll(defaultLibraries);
        }

        return customLibraries;
    }

    /**
     * Generates an array of allowed package names for handler services.
     * This method combines the allowed libraries with the handler services' own package.
     *
     * @param allowedLibraries the list of allowed library package names.
     * @return an array of package names that the handler services are allowed to depend on.
     */
    static String[] getAllowedPackages(final List<String> allowedLibraries) {
        final List<String> allowedPackages = new ArrayList<>(allowedLibraries);
        allowedPackages.addAll(properties.handler().fqdns());

        return allowedPackages.toArray(new String[0]);
    }

}
