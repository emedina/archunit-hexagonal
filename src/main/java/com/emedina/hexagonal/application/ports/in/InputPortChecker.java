package com.emedina.hexagonal.application.ports.in;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaParameterizedType;
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
import com.emedina.sharedkernel.command.Command;
import com.emedina.sharedkernel.command.core.CommandHandler;
import com.emedina.sharedkernel.domain.factory.annotation.Factory;
import com.emedina.sharedkernel.domain.identity.annotation.Identity;
import com.emedina.sharedkernel.domain.model.annotation.ValueObject;
import com.emedina.sharedkernel.domain.repository.annotation.Repository;
import com.emedina.sharedkernel.domain.service.annotation.DomainService;
import com.emedina.sharedkernel.query.Query;
import com.emedina.sharedkernel.query.core.QueryHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Checker for Hexagonal Architecture rules on the Input Ports.
 *
 * @author Enrique Medina Montenegro
 */
public class InputPortChecker {

    private static HexagonalArchitectureProperties properties;

    public InputPortChecker(final HexagonalArchitectureProperties properties) {
        InputPortChecker.properties = properties;
    }

    /**
     * Checks the rules for the input ports.
     * <p>
     * This method performs the following checks:
     * <ol>
     * <li>Ensures that input ports only depend on allowed libraries.</li>
     * <li>Verifies that input ports are always annotated with @UseCase.</li>
     * <li>Checks that certain prohibited annotations are not used on input ports.</li>
     * <li>Ensures that input ports are always interfaces, never classes.</li>
     * <li>Verifies that input ports extend either CommandHandler or QueryHandler.</li>
     * <li>For CommandHandlers, ensures they use a type extending Command with no result.</li>
     * <li>For QueryHandlers, ensures they use a type extending Query and always produce a result.</li>
     * </ol>
     *
     * @param javaClasses the Java classes to check
     * @throws IllegalStateException if the input ports FQDN is not configured in the properties
     */
    @ArchTest
    public static void checkRules(final JavaClasses javaClasses) {
        if (properties == null) {
            throw new IllegalStateException("HexagonalArchitectureProperties have not been initialized");
        }

        final List<String> inputPortsPackage = properties.inputPorts().fqdns();
        if (inputPortsPackage == null || inputPortsPackage.isEmpty()) {
            throw new IllegalStateException("Input Ports FQDN is not configured in the properties");
        }

        final List<String> allowedLibraries = getAllowedLibraries();
        final String[] allowedPackages = getAllowedPackages(allowedLibraries);

        // Rule 1: Allowed dependencies
        final ArchRule inputPortDependencyRule = classes()
            .that().resideInAnyPackage(inputPortsPackage.toArray(new String[0]))
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(allowedPackages);

        // Rule 2: Must have @UseCase annotation
        final ArchRule mustHaveUseCaseAnnotationRule = classes()
            .that().resideInAnyPackage(inputPortsPackage.toArray(new String[0]))
            .should().beAnnotatedWith(UseCase.class);

        // Rule 3: Forbidden annotations
        final ArchRule forbiddenAnnotationsRule = noClasses()
            .that().resideInAnyPackage(inputPortsPackage.toArray(new String[0]))
            .should().beAnnotatedWith(OutputPort.class)
            .orShould().beAnnotatedWith(Adapter.class)
            .orShould().beAnnotatedWith(ApplicationService.class)
            .orShould().beAnnotatedWith(Repository.class)
            .orShould().beAnnotatedWith(DomainService.class)
            .orShould().beAnnotatedWith(ValueObject.class)
            .orShould().beAnnotatedWith(Identity.class)
            .orShould().beAnnotatedWith(Factory.class);

        // Rule 4: Must be interfaces
        final ArchRule mustBeInterfaceRule = classes()
            .that().resideInAnyPackage(inputPortsPackage.toArray(new String[0]))
            .should().beInterfaces();

        // Rule 5: Must extend CommandHandler or QueryHandler
        final ArchRule mustExtendHandlerRule = classes()
            .that().resideInAnyPackage(inputPortsPackage.toArray(new String[0]))
            .should().beAssignableTo(CommandHandler.class)
            .orShould().beAssignableTo(QueryHandler.class);

        // Rule 6: CommandHandler specifics
        final ArchRule commandHandlerRule = classes()
            .that().resideInAnyPackage(inputPortsPackage.toArray(new String[0]))
            .and().areAssignableTo(CommandHandler.class)
            .should(haveGenericTypeAssignableTo(Command.class, 1));

        // Rule 7: QueryHandler specifics
        final ArchRule queryHandlerRule = classes()
            .that().resideInAnyPackage(inputPortsPackage.toArray(new String[0]))
            .and().areAssignableTo(QueryHandler.class)
            .should(haveGenericTypeAssignableTo(Query.class, 2));

        // Apply and freeze the rules
        FreezingArchRule.freeze(inputPortDependencyRule).check(javaClasses);
        FreezingArchRule.freeze(mustHaveUseCaseAnnotationRule).check(javaClasses);
        FreezingArchRule.freeze(forbiddenAnnotationsRule).check(javaClasses);
        FreezingArchRule.freeze(mustBeInterfaceRule).check(javaClasses);
        FreezingArchRule.freeze(mustExtendHandlerRule).check(javaClasses);
        FreezingArchRule.freeze(commandHandlerRule).check(javaClasses);
        FreezingArchRule.freeze(queryHandlerRule).check(javaClasses);
    }

    /**
     * Retrieves the list of allowed libraries for the input ports.
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
        final List<String> customLibraries = properties.inputPorts().allowedLibraries();
        if (customLibraries != null && !customLibraries.isEmpty()) {
            customLibraries.addAll(defaultLibraries);
        }

        return customLibraries;
    }

    /**
     * Generates an array of allowed package names for the input ports.
     * This method combines the allowed libraries with the input ports' own package.
     *
     * @param allowedLibraries the list of allowed library package names
     * @return an array of package names that the input ports are allowed to depend on
     */
    private static String[] getAllowedPackages(final List<String> allowedLibraries) {
        final List<String> allowedPackages = new ArrayList<>(allowedLibraries);
        allowedPackages.addAll(properties.inputPorts().fqdns());

        return allowedPackages.toArray(new String[0]);
    }

    /**
     * Creates an {@link ArchCondition} that verifies if a class implementing a generic interface has a type parameter
     * that is assignable to the specified expected type.
     * <p>
     * This condition inspects classes that implement parametrized interfaces and ensures that the first generic type
     * argument is a subtype of the provided expected type.
     * </p>
     *
     * <p>
     * For example:
     * <ul>
     *     <li>If a class implements {@code CommandHandler<T>}, this condition will check whether {@code T} is assignable to {@code Command}.</li>
     *     <li>If a class implements {@code QueryHandler<R, T>}, this condition can be used to check whether {@code T} is assignable to {@code Query}.</li>
     * </ul>
     * </p>
     *
     * @param expectedType the {@link Class} object representing the expected superclass or interface that the generic type must be assignable to.
     *                     For example, this could be {@code Command.class} or {@code Query.class}.
     * @param position     the position of the generic type argument to check. The first generic type argument is at position 1.
     * @return an {@link ArchCondition} that can be used to check whether a class's first generic type argument is assignable to the given expected type.
     * If the class does not have a type parameter that is assignable to the expected type, the condition will result in a violation.
     */
    private static ArchCondition<JavaClass> haveGenericTypeAssignableTo(final Class<?> expectedType,
        final Integer position) {
        return new ArchCondition<>("have a generic type assignable to " + expectedType.getSimpleName()) {

            @Override
            public void check(JavaClass item, ConditionEvents events) {
                item.getInterfaces().stream()
                    .filter(JavaParameterizedType.class::isInstance)
                    .map(JavaParameterizedType.class::cast)
                    .forEach(paramType -> {
                        // Extract the generic type argument in the specified position,
                        // and check if it's assignable to the expected type
                        if (position <= paramType.getActualTypeArguments().size()) {
                            final JavaClass genericType = paramType.getActualTypeArguments().get(position - 1)
                                .toErasure();
                            if (!genericType.isAssignableTo(expectedType)) {
                                // If the type is not assignable, register a violation event
                                final String message = String.format(
                                    "Class %s has a generic type %s at position %d that is not assignable to %s",
                                    item.getName(), genericType.getSimpleName(), position,
                                    expectedType.getSimpleName());
                                events.add(SimpleConditionEvent.violated(item, message));
                            }
                        }
                    });
            }

        };
    }

}
