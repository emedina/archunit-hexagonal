package com.emedina.hexagonal.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.emedina.hexagonal.fixtures.adapters.ClassWithNoInterfaces;
import com.emedina.hexagonal.fixtures.adapters.NonRepositoryImplementation;
import com.emedina.hexagonal.fixtures.adapters.RepositoryImplementation;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AdapterChecker} helper methods.
 *
 * @author Enrique Medina Montenegro
 */
@DisplayName("AdapterChecker")
class AdapterCheckerTest {

    private JavaClasses fixtureClasses;

    @BeforeEach
    void setUp() {
        fixtureClasses = new ClassFileImporter()
            .importPackages("com.emedina.hexagonal.fixtures.adapters");
    }

    @Nested
    @DisplayName("implementRepositoryInterface predicate")
    class ImplementRepositoryInterfaceTest {

        private DescribedPredicate<JavaClass> predicate;

        @BeforeEach
        void setUp() {
            predicate = AdapterChecker.implementRepositoryInterface();
        }

        @Test
        @DisplayName("should return true for class implementing @Repository interface")
        void shouldReturnTrueForRepositoryImplementation() {
            // Given
            JavaClass repositoryImpl = fixtureClasses.get(RepositoryImplementation.class);

            // When
            boolean result = predicate.test(repositoryImpl);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return false for class implementing non-Repository interface")
        void shouldReturnFalseForNonRepositoryImplementation() {
            // Given
            JavaClass nonRepositoryImpl = fixtureClasses.get(NonRepositoryImplementation.class);

            // When
            boolean result = predicate.test(nonRepositoryImpl);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("should return false for class with no interfaces")
        void shouldReturnFalseForClassWithNoInterfaces() {
            // Given
            JavaClass classWithNoInterfaces = fixtureClasses.get(ClassWithNoInterfaces.class);

            // When
            boolean result = predicate.test(classWithNoInterfaces);

            // Then
            assertThat(result).isFalse();
        }
    }

}
