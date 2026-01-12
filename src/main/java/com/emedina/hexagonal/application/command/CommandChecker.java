package com.emedina.hexagonal.application.command;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import io.vavr.control.Validation;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties;
import com.emedina.sharedkernel.application.annotation.Adapter;
import com.emedina.sharedkernel.application.annotation.ApplicationService;
import com.emedina.sharedkernel.application.annotation.OutputPort;
import com.emedina.sharedkernel.application.annotation.UseCase;
import com.emedina.sharedkernel.command.Command;
import com.emedina.sharedkernel.domain.factory.annotation.Factory;
import com.emedina.sharedkernel.domain.identity.annotation.Identity;
import com.emedina.sharedkernel.domain.model.annotation.ValueObject;
import com.emedina.sharedkernel.domain.repository.annotation.Repository;
import com.emedina.sharedkernel.domain.service.annotation.DomainService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checker for Hexagonal Architecture rules on Commands.
 *
 * @author Enrique Medina Montenegro
 */
public class CommandChecker {

    private static HexagonalArchitectureProperties properties;

    public CommandChecker(final HexagonalArchitectureProperties properties) {
        CommandChecker.properties = properties;
    }

    /**
     * Checks the rules for commands.
     * <p>
     * This method performs the following checks:
     * 1. Ensures that commands only depend on allowed libraries.
     * 2. Checks that certain prohibited annotations are not used on commands.
     * 3. Verifies that commands are always classes that implement Command.class.
     * 4. Ensures that commands always define a public static factory method named validateThenCreate.
     * 5. Checks that commands cannot be instantiated with the default constructor.
     *
     * @param javaClasses the Java classes to check
     * @throws IllegalStateException if the commands FQDN is not configured in the properties
     */
    @ArchTest
    public static void checkRules(final JavaClasses javaClasses) {
        if (properties == null) {
            throw new IllegalStateException("HexagonalArchitectureProperties have not been initialized");
        }

        final List<String> commandsPackage = properties.command().fqdns();
        if (commandsPackage == null || commandsPackage.isEmpty()) {
            throw new IllegalStateException("Commands FQDN is not configured in the properties");
        }

        final List<String> allowedLibraries = getAllowedLibraries();
        final String[] allowedPackages = getAllowedPackages(allowedLibraries);

        // Rule 1: Allowed dependencies
        final ArchRule commandDependencyRule = classes()
            .that().resideInAnyPackage(commandsPackage.toArray(new String[0]))
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(allowedPackages);

        // Rule 2: Forbidden annotations
        final ArchRule forbiddenAnnotationsRule = noClasses()
            .that().resideInAnyPackage(commandsPackage.toArray(new String[0]))
            .should().beAnnotatedWith(UseCase.class)
            .orShould().beAnnotatedWith(OutputPort.class)
            .orShould().beAnnotatedWith(Adapter.class)
            .orShould().beAnnotatedWith(ApplicationService.class)
            .orShould().beAnnotatedWith(Repository.class)
            .orShould().beAnnotatedWith(DomainService.class)
            .orShould().beAnnotatedWith(ValueObject.class)
            .orShould().beAnnotatedWith(Identity.class)
            .orShould().beAnnotatedWith(Factory.class);

        // Rule 3: Must implement Command.class
        final ArchRule mustImplementCommandRule = classes()
            .that().resideInAnyPackage(commandsPackage.toArray(new String[0]))
            .should().beAssignableTo(Command.class);

        // Rule 4: Must have validateThenCreate method
        final ArchRule mustHaveValidateThenCreateMethod = classes()
            .that().resideInAnyPackage(commandsPackage.toArray(new String[0]))
            .should(haveValidateThenCreateMethod());

        // Rule 5: No default constructor
        final ArchRule noDefaultConstructorRule = classes()
            .that().resideInAnyPackage(commandsPackage.toArray(new String[0]))
            .should(notHavePublicDefaultConstructor());

        // Apply and freeze the rules
        FreezingArchRule.freeze(commandDependencyRule).check(javaClasses);
        FreezingArchRule.freeze(forbiddenAnnotationsRule).check(javaClasses);
        FreezingArchRule.freeze(mustImplementCommandRule).check(javaClasses);
        FreezingArchRule.freeze(mustHaveValidateThenCreateMethod).check(javaClasses);
        FreezingArchRule.freeze(noDefaultConstructorRule).check(javaClasses);
    }

    /**
     * Creates an ArchCondition that checks if a class has a valid validateThenCreate method.
     * The method should be public, static, named "validateThenCreate", and return a Validation
     * where the success type is the class itself.
     *
     * @return an ArchCondition that can be used in ArchUnit rules to check for the validateThenCreate method.
     */
    static ArchCondition<JavaClass> haveValidateThenCreateMethod() {
        return new ArchCondition<>("have validateThenCreate method") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean hasValidMethod = item.getMethods().stream()
                    .anyMatch(method ->
                        method.getName().equals("validateThenCreate") &&
                            method.getModifiers().contains(JavaModifier.PUBLIC) &&
                            method.getModifiers().contains(JavaModifier.STATIC) &&
                            method.getRawReturnType().isAssignableTo(Validation.class) &&
                            ((JavaParameterizedType) method.getReturnType()).getActualTypeArguments().size() == 2 &&
                            ((JavaParameterizedType) method.getReturnType()).getActualTypeArguments().get(1).toErasure()
                                .equals(item)
                    );

                if (!hasValidMethod) {
                    events.add(SimpleConditionEvent.violated(item,
                        "Command does not have a valid validateThenCreate method"));
                }
            }
        };
    }

    /**
     * Creates an ArchCondition that checks if a class does not have a public default constructor.
     *
     * @return an ArchCondition that can be used in ArchUnit rules to check for the absence of a public default constructor.
     */
    static ArchCondition<JavaClass> notHavePublicDefaultConstructor() {
        return new ArchCondition<>("not have public default constructor") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean hasPublicDefaultConstructor = item.getConstructors().stream()
                    .anyMatch(constructor ->
                        constructor.getParameters().isEmpty() &&
                            constructor.getModifiers().contains(JavaModifier.PUBLIC)
                    );

                if (hasPublicDefaultConstructor) {
                    events.add(SimpleConditionEvent.violated(item,
                        "Command has a public default constructor"));
                }
            }
        };
    }

    /**
     * Retrieves the list of allowed libraries for commands.
     * This method combines the default allowed libraries with any custom libraries
     * specified in the properties.
     *
     * @return a list of package names representing the allowed libraries.
     */
    static List<String> getAllowedLibraries() {
        final List<String> defaultLibraries = Arrays.asList(
            "java..", "javax..", "lombok..", "io.vavr..", "org.apache.commons.."
        );
        final List<String> customLibraries = properties.command().allowedLibraries();
        if (customLibraries != null && !customLibraries.isEmpty()) {
            customLibraries.addAll(defaultLibraries);
        }

        return customLibraries;
    }

    /**
     * Generates an array of allowed package names for commands.
     * This method combines the allowed libraries with the commands' own package.
     *
     * @param allowedLibraries the list of allowed library package names.
     * @return an array of package names that the commands are allowed to depend on.
     */
    static String[] getAllowedPackages(final List<String> allowedLibraries) {
        final List<String> allowedPackages = new ArrayList<>(allowedLibraries);
        allowedPackages.addAll(properties.command().fqdns());

        return allowedPackages.toArray(new String[0]);
    }

}
