package com.emedina.hexagonal.application.domain;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emedina.hexagonal.fixtures.domain.DomainWithDomainServiceAnnotation;
import com.emedina.hexagonal.fixtures.domain.DomainWithForbiddenAnnotation;
import com.emedina.hexagonal.fixtures.domain.DomainWithNoAnnotations;
import com.emedina.hexagonal.fixtures.domain.DomainWithRepositoryAnnotation;
import com.emedina.hexagonal.fixtures.domain.DomainWithValueObjectAnnotation;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DomainChecker} helper methods.
 *
 * @author Enrique Medina Montenegro
 */
@DisplayName("DomainChecker")
class DomainCheckerTest {

    @Nested
    @DisplayName("haveAllowedAnnotationsOrNone condition")
    class HaveAllowedAnnotationsOrNoneTest {

        @Test
        @DisplayName("should be satisfied for class with @Repository annotation")
        void shouldBeSatisfiedForRepositoryAnnotation() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(DomainWithRepositoryAnnotation.class);
            ArchRule rule = classes().should(DomainChecker.haveAllowedAnnotationsOrNone());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be satisfied for class with @DomainService annotation")
        void shouldBeSatisfiedForDomainServiceAnnotation() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(DomainWithDomainServiceAnnotation.class);
            ArchRule rule = classes().should(DomainChecker.haveAllowedAnnotationsOrNone());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be satisfied for class with @ValueObject annotation")
        void shouldBeSatisfiedForValueObjectAnnotation() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(DomainWithValueObjectAnnotation.class);
            ArchRule rule = classes().should(DomainChecker.haveAllowedAnnotationsOrNone());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be satisfied for class with no annotations")
        void shouldBeSatisfiedForNoAnnotations() {
            // Given
            JavaClasses classes = new ClassFileImporter().importClasses(DomainWithNoAnnotations.class);
            ArchRule rule = classes().should(DomainChecker.haveAllowedAnnotationsOrNone());

            // When & Then
            assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should be violated for class with forbidden annotation")
        void shouldBeViolatedForForbiddenAnnotation() {
            // Given - DomainWithForbiddenAnnotation has @UseCase which is not in the allowed list
            JavaClasses classes = new ClassFileImporter().importClasses(DomainWithForbiddenAnnotation.class);
            ArchRule rule = classes().should(DomainChecker.haveAllowedAnnotationsOrNone());

            // When & Then
            assertThatThrownBy(() -> rule.check(classes))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("has annotations, but none of the allowed ones");
        }
    }

}
