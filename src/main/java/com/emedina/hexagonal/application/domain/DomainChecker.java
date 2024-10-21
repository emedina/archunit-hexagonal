package com.emedina.hexagonal.application.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checker for Hexagonal Architecture rules on the Domain module.
 *
 * @author Enrique Medina Montenegro
 */
@Component
public class DomainChecker {

    private static HexagonalArchitectureProperties properties;

    public DomainChecker(final HexagonalArchitectureProperties properties) {
        DomainChecker.properties = properties;
    }

    /**
     * Checks the rules for the domain module.
     * <p>
     * This method performs the following checks:
     * <ol>
     * <li>Ensures that the domain only depends on allowed libraries.</li>
     * <li>Verifies that specific domain annotations can be used.</li>
     * <li>Checks that certain prohibited annotations are not defined in the domain.</li>
     * </ol>
     *
     * @param javaClasses the Java classes to check
     * @throws IllegalStateException if the domain FQDN is not configured in the properties
     */
    @ArchTest
    public static void checkRules(final JavaClasses javaClasses) {
        if (properties == null) {
            throw new IllegalStateException("HexagonalArchitectureProperties have not been initialized");
        }

        final List<String> domainPackage = properties.domain().fqdns();
        if (domainPackage == null || domainPackage.isEmpty()) {
            throw new IllegalStateException("Domain FQDN is not configured in the properties");
        }

        final List<String> allowedLibraries = getAllowedLibraries();
        final String[] allowedPackages = getAllowedPackages(allowedLibraries);

        // Rule 1: Allowed dependencies
        final ArchRule domainDependencyRule = classes()
            .that().resideInAnyPackage(domainPackage.toArray(new String[0]))
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(allowedPackages);

        // Rule 2: Allowed annotations or no annotations
        final ArchRule allowedAnnotationsRule = classes()
            .that().resideInAnyPackage(domainPackage.toArray(new String[0]))
            .should(haveAllowedAnnotationsOrNone());

        // Rule 3: Forbidden annotations
        final ArchRule forbiddenAnnotationsRule = noClasses()
            .that().resideInAnyPackage(domainPackage.toArray(new String[0]))
            .should().beAnnotatedWith(UseCase.class)
            .orShould().beAnnotatedWith(Adapter.class)
            .orShould().beAnnotatedWith(OutputPort.class)
            .orShould().beAnnotatedWith(ApplicationService.class);

        // Apply and freeze the rules
        FreezingArchRule.freeze(domainDependencyRule).check(javaClasses);
        FreezingArchRule.freeze(allowedAnnotationsRule).check(javaClasses);
        FreezingArchRule.freeze(forbiddenAnnotationsRule).check(javaClasses);
    }

    /**
     * Creates an ArchCondition that checks if a JavaClass has allowed annotations or no annotations at all.
     * <p>
     * This condition is satisfied if:
     * <ol>
     * <li>The class has at least one of the allowed annotations (@Repository, @DomainService, @ValueObject, @Identity, @Factory).</li>
     * <p>
     * OR
     * <li>The class has no annotations at all.</li>
     * </ol>
     * <p>
     * The condition is violated if the class has any annotations other than the allowed ones.
     *
     * @return an ArchCondition that can be used in ArchUnit rules to check for allowed annotations
     * or the absence of annotations.
     */
    private static ArchCondition<JavaClass> haveAllowedAnnotationsOrNone() {
        return new ArchCondition<JavaClass>("have allowed annotations or none") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean hasAllowedAnnotation = item.isAnnotatedWith(Repository.class) ||
                    item.isAnnotatedWith(DomainService.class) ||
                    item.isAnnotatedWith(ValueObject.class) ||
                    item.isAnnotatedWith(Identity.class) ||
                    item.isAnnotatedWith(Factory.class);

                boolean hasAnyAnnotation = !item.getAnnotations().isEmpty();

                if (hasAllowedAnnotation || !hasAnyAnnotation) {
                    events.add(SimpleConditionEvent.satisfied(item, "Class has allowed annotations or no annotations"));
                } else {
                    events.add(
                        SimpleConditionEvent.violated(item, "Class has annotations, but none of the allowed ones"));
                }
            }
        };
    }

    /**
     * Retrieves the list of allowed libraries for the domain.
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
        final List<String> customLibraries = properties.domain().allowedLibraries();
        if (customLibraries != null && !customLibraries.isEmpty()) {
            customLibraries.addAll(defaultLibraries);
        }

        return customLibraries;
    }

    /**
     * Generates an array of allowed package names for the domain.
     * This method combines the allowed libraries with the domain's own package.
     *
     * @param allowedLibraries the list of allowed library package names
     * @return an array of package names that the domain is allowed to depend on
     */
    private static String[] getAllowedPackages(final List<String> allowedLibraries) {
        final List<String> allowedPackages = new ArrayList<>(allowedLibraries);
        allowedPackages.addAll(properties.domain().fqdns());

        return allowedPackages.toArray(new String[0]);
    }

}
