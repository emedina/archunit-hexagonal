package com.emedina.hexagonal.application.handler;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emedina.hexagonal.fixtures.handlers.HandlerWithWrongNaming;
import com.emedina.hexagonal.fixtures.handlers.NonUseCaseHandler;
import com.emedina.hexagonal.fixtures.handlers.SampleHandler;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link HandlerChecker} helper methods.
 *
 * @author Enrique Medina Montenegro
 */
@DisplayName("HandlerChecker")
class HandlerCheckerTest {

    private JavaClasses fixtureClasses;

    @BeforeEach
    void setUp() {
        fixtureClasses = new ClassFileImporter()
            .importPackages("com.emedina.hexagonal.fixtures.handlers");
    }

    @Nested
    @DisplayName("implementUseCaseInterface predicate")
    class ImplementUseCaseInterfaceTest {

        private DescribedPredicate<JavaClass> predicate;

        @BeforeEach
        void setUp() {
            predicate = HandlerChecker.implementUseCaseInterface();
        }

        @Test
        @DisplayName("should return true for class implementing @UseCase interface")
        void shouldReturnTrueForUseCaseImplementation() {
            // Given
            JavaClass sampleHandler = fixtureClasses.get(SampleHandler.class);

            // When
            boolean result = predicate.test(sampleHandler);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return false for class not implementing @UseCase interface")
        void shouldReturnFalseForNonUseCaseImplementation() {
            // Given
            JavaClass nonUseCaseHandler = fixtureClasses.get(NonUseCaseHandler.class);

            // When
            boolean result = predicate.test(nonUseCaseHandler);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("should return true for class with wrong naming but implementing @UseCase")
        void shouldReturnTrueForWrongNamingButImplementsUseCase() {
            // Given
            JavaClass handlerWithWrongNaming = fixtureClasses.get(HandlerWithWrongNaming.class);

            // When
            boolean result = predicate.test(handlerWithWrongNaming);

            // Then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("followUseCaseNamingConvention condition")
    class FollowUseCaseNamingConventionTest {

        @Test
        @DisplayName("should be satisfied for handler with correct naming convention")
        void shouldBeSatisfiedForCorrectNaming() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(SampleHandler.class);
            ArchRule rule = classes().should(HandlerChecker.followUseCaseNamingConvention());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be violated for handler with wrong naming convention")
        void shouldBeViolatedForWrongNaming() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(HandlerWithWrongNaming.class);
            ArchRule rule = classes().should(HandlerChecker.followUseCaseNamingConvention());

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("implements")
                .hasMessageContaining("but is not named");
        }

        @Test
        @DisplayName("should be satisfied for class not implementing any @UseCase interface")
        void shouldBeSatisfiedForNonUseCaseClass() {
            // Given - NonUseCaseHandler doesn't implement a @UseCase interface
            // so the naming convention check should pass (nothing to check)
            JavaClasses classes = new ClassFileImporter().importClasses(NonUseCaseHandler.class);
            ArchRule rule = classes().should(HandlerChecker.followUseCaseNamingConvention());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }
    }

}
