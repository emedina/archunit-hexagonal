package com.emedina.hexagonal.application.ports.in;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emedina.hexagonal.fixtures.inputports.ValidCommandUseCase;
import com.emedina.hexagonal.fixtures.inputports.ValidQueryUseCase;
import com.emedina.sharedkernel.command.Command;
import com.emedina.sharedkernel.query.Query;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InputPortChecker} helper methods.
 *
 * @author Enrique Medina Montenegro
 */
@DisplayName("InputPortChecker")
class InputPortCheckerTest {

    @Nested
    @DisplayName("haveGenericTypeAssignableTo condition")
    class HaveGenericTypeAssignableToTest {

        @Test
        @DisplayName("should be satisfied for CommandHandler with Command type at position 1")
        void shouldBeSatisfiedForCommandHandlerWithCommandType() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(ValidCommandUseCase.class);
            ArchRule rule = classes().should(InputPortChecker.haveGenericTypeAssignableTo(Command.class, 1));

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be satisfied for QueryHandler with Query type at position 2")
        void shouldBeSatisfiedForQueryHandlerWithQueryType() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(ValidQueryUseCase.class);
            ArchRule rule = classes().should(InputPortChecker.haveGenericTypeAssignableTo(Query.class, 2));

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be violated for CommandHandler when checking for Query type at position 1")
        void shouldBeViolatedForCommandHandlerWhenCheckingQueryType() {
            // Given - Checking if position 1 of CommandHandler is Query (it's Command, not Query)
            JavaClasses classes = new ClassFileImporter().importClasses(ValidCommandUseCase.class);
            ArchRule rule = classes().should(InputPortChecker.haveGenericTypeAssignableTo(Query.class, 1));

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("is not assignable to");
        }

        @Test
        @DisplayName("should be violated for QueryHandler when checking for Command type at position 2")
        void shouldBeViolatedForQueryHandlerWhenCheckingCommandType() {
            // Given - Checking if position 2 of QueryHandler is Command (it's Query, not Command)
            JavaClasses classes = new ClassFileImporter().importClasses(ValidQueryUseCase.class);
            ArchRule rule = classes().should(InputPortChecker.haveGenericTypeAssignableTo(Command.class, 2));

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("is not assignable to");
        }

        @Test
        @DisplayName("should handle position out of bounds gracefully")
        void shouldHandlePositionOutOfBounds() {
            // Given - Position 10 is out of bounds for any interface
            JavaClasses classes = new ClassFileImporter().importClasses(ValidCommandUseCase.class);
            ArchRule rule = classes().should(InputPortChecker.haveGenericTypeAssignableTo(Command.class, 10));

            // When & Then - Should not throw exception, just no violation added
            // The condition only adds violations when the position exists and type doesn't match
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }
    }

}
