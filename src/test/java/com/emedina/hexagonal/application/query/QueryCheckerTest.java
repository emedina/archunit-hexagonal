package com.emedina.hexagonal.application.query;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emedina.hexagonal.fixtures.queries.QueryWithPublicDefaultConstructor;
import com.emedina.hexagonal.fixtures.queries.QueryWithoutValidateThenCreate;
import com.emedina.hexagonal.fixtures.queries.ValidQuery;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link QueryChecker} helper methods.
 *
 * @author Enrique Medina Montenegro
 */
@DisplayName("QueryChecker")
class QueryCheckerTest {

    @Nested
    @DisplayName("haveValidateThenCreateMethod condition")
    class HaveValidateThenCreateMethodTest {

        @Test
        @DisplayName("should be satisfied for class with valid validateThenCreate method")
        void shouldBeSatisfiedForValidQuery() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(ValidQuery.class);
            ArchRule rule = classes().should(QueryChecker.haveValidateThenCreateMethod());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be violated for class without validateThenCreate method")
        void shouldBeViolatedForQueryWithoutMethod() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(QueryWithoutValidateThenCreate.class);
            ArchRule rule = classes().should(QueryChecker.haveValidateThenCreateMethod());

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
            JavaClasses classes = new ClassFileImporter().importClasses(ValidQuery.class);
            ArchRule rule = classes().should(QueryChecker.notHavePublicDefaultConstructor());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be violated for class with public default constructor")
        void shouldBeViolatedForClassWithPublicDefaultConstructor() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(QueryWithPublicDefaultConstructor.class);
            ArchRule rule = classes().should(QueryChecker.notHavePublicDefaultConstructor());

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("has a public default constructor");
        }

        @Test
        @DisplayName("should be satisfied for class without any default constructor")
        void shouldBeSatisfiedForClassWithoutDefaultConstructor() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(QueryWithoutValidateThenCreate.class);
            ArchRule rule = classes().should(QueryChecker.notHavePublicDefaultConstructor());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }
    }

}
