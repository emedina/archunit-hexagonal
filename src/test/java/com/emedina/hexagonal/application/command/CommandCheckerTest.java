package com.emedina.hexagonal.application.command;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emedina.hexagonal.fixtures.commands.CommandWithNonStaticMethod;
import com.emedina.hexagonal.fixtures.commands.CommandWithPublicDefaultConstructor;
import com.emedina.hexagonal.fixtures.commands.CommandWithWrongMethodSignature;
import com.emedina.hexagonal.fixtures.commands.CommandWithoutValidateThenCreate;
import com.emedina.hexagonal.fixtures.commands.ValidCommand;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CommandChecker} helper methods.
 *
 * @author Enrique Medina Montenegro
 */
@DisplayName("CommandChecker")
class CommandCheckerTest {

    @Nested
    @DisplayName("haveValidateThenCreateMethod condition")
    class HaveValidateThenCreateMethodTest {

        @Test
        @DisplayName("should be satisfied for class with valid validateThenCreate method")
        void shouldBeSatisfiedForValidCommand() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(ValidCommand.class);
            ArchRule rule = classes().should(CommandChecker.haveValidateThenCreateMethod());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be violated for class without validateThenCreate method")
        void shouldBeViolatedForCommandWithoutMethod() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(CommandWithoutValidateThenCreate.class);
            ArchRule rule = classes().should(CommandChecker.haveValidateThenCreateMethod());

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("does not have a valid validateThenCreate method");
        }

        @Test
        @DisplayName("should be violated for class with non-static validateThenCreate method")
        void shouldBeViolatedForCommandWithNonStaticMethod() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(CommandWithNonStaticMethod.class);
            ArchRule rule = classes().should(CommandChecker.haveValidateThenCreateMethod());

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("does not have a valid validateThenCreate method");
        }

        @Test
        @DisplayName("should be violated for class with wrong return type in validateThenCreate")
        void shouldBeViolatedForCommandWithWrongReturnType() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(CommandWithWrongMethodSignature.class);
            ArchRule rule = classes().should(CommandChecker.haveValidateThenCreateMethod());

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("does not have a valid validateThenCreate method");
        }
    }

    @Nested
    @DisplayName("notHavePublicDefaultConstructor condition")
    class NotHavePublicDefaultConstructorTest {

        @Test
        @DisplayName("should be satisfied for class with private constructor only")
        void shouldBeSatisfiedForClassWithPrivateConstructor() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(ValidCommand.class);
            ArchRule rule = classes().should(CommandChecker.notHavePublicDefaultConstructor());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be violated for class with public default constructor")
        void shouldBeViolatedForClassWithPublicDefaultConstructor() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(CommandWithPublicDefaultConstructor.class);
            ArchRule rule = classes().should(CommandChecker.notHavePublicDefaultConstructor());

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("has a public default constructor");
        }

        @Test
        @DisplayName("should be satisfied for class without any default constructor")
        void shouldBeSatisfiedForClassWithoutDefaultConstructor() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(CommandWithoutValidateThenCreate.class);
            ArchRule rule = classes().should(CommandChecker.notHavePublicDefaultConstructor());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }
    }

}
