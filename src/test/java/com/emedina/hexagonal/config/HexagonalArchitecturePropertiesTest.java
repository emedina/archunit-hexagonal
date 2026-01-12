package com.emedina.hexagonal.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.emedina.hexagonal.config.HexagonalArchitectureProperties.AdapterProperties;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties.CommandProperties;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties.DomainProperties;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties.HandlerProperties;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties.InputPortsProperties;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties.OutputPortsProperties;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties.QueryProperties;
import com.emedina.hexagonal.config.HexagonalArchitectureProperties.SharedKernelProperties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Unit tests for {@link HexagonalArchitectureProperties} record.
 *
 * @author Enrique Medina Montenegro
 */
@DisplayName("HexagonalArchitectureProperties")
class HexagonalArchitecturePropertiesTest {

    @Nested
    @DisplayName("record construction and accessors")
    class RecordConstructionTest {

        @Test
        @DisplayName("should create instance with all properties")
        void shouldCreateInstanceWithAllProperties() {
            // Given
            SharedKernelProperties sharedKernel = new SharedKernelProperties(
                List.of("com.example.sharedkernel.."),
                List.of("java..", "javax..")
            );
            DomainProperties domain = new DomainProperties(
                List.of("com.example.domain.."),
                List.of("java..")
            );
            OutputPortsProperties outputPorts = new OutputPortsProperties(
                List.of("com.example.ports.out.."),
                List.of("java..")
            );
            InputPortsProperties inputPorts = new InputPortsProperties(
                List.of("com.example.ports.in.."),
                List.of("java..")
            );
            CommandProperties command = new CommandProperties(
                List.of("com.example.command.."),
                List.of("java..", "io.vavr..")
            );
            QueryProperties query = new QueryProperties(
                List.of("com.example.query.."),
                List.of("java..", "io.vavr..")
            );
            HandlerProperties handler = new HandlerProperties(
                List.of("com.example.handler.."),
                List.of("java..")
            );
            AdapterProperties adapters = new AdapterProperties(
                List.of("com.example.adapters..")
            );

            // When
            HexagonalArchitectureProperties properties = new HexagonalArchitectureProperties(
                sharedKernel, domain, outputPorts, inputPorts, command, query, handler, adapters
            );

            // Then
            assertThat(properties).isNotNull();
            assertThat(properties.sharedKernel()).isEqualTo(sharedKernel);
            assertThat(properties.domain()).isEqualTo(domain);
            assertThat(properties.outputPorts()).isEqualTo(outputPorts);
            assertThat(properties.inputPorts()).isEqualTo(inputPorts);
            assertThat(properties.command()).isEqualTo(command);
            assertThat(properties.query()).isEqualTo(query);
            assertThat(properties.handler()).isEqualTo(handler);
            assertThat(properties.adapters()).isEqualTo(adapters);
        }

        @Test
        @DisplayName("should handle null values in properties")
        void shouldHandleNullValues() {
            // Given & When
            HexagonalArchitectureProperties properties = new HexagonalArchitectureProperties(
                null, null, null, null, null, null, null, null
            );

            // Then
            assertThat(properties).isNotNull();
            assertThat(properties.sharedKernel()).isNull();
            assertThat(properties.domain()).isNull();
        }
    }

    @Nested
    @DisplayName("nested record properties")
    class NestedRecordPropertiesTest {

        @Test
        @DisplayName("SharedKernelProperties should store fqdns and allowedLibraries")
        void sharedKernelPropertiesShouldStoreValues() {
            // Given
            List<String> fqdns = List.of("com.example.sharedkernel..");
            List<String> allowedLibraries = List.of("java..", "javax..");

            // When
            SharedKernelProperties props = new SharedKernelProperties(fqdns, allowedLibraries);

            // Then
            assertThat(props.fqdns()).isEqualTo(fqdns);
            assertThat(props.allowedLibraries()).isEqualTo(allowedLibraries);
        }

        @Test
        @DisplayName("DomainProperties should store fqdns and allowedLibraries")
        void domainPropertiesShouldStoreValues() {
            // Given
            List<String> fqdns = List.of("com.example.domain..");
            List<String> allowedLibraries = List.of("java..");

            // When
            DomainProperties props = new DomainProperties(fqdns, allowedLibraries);

            // Then
            assertThat(props.fqdns()).isEqualTo(fqdns);
            assertThat(props.allowedLibraries()).isEqualTo(allowedLibraries);
        }

        @Test
        @DisplayName("AdapterProperties should store fqdns only")
        void adapterPropertiesShouldStoreFqdnsOnly() {
            // Given
            List<String> fqdns = List.of("com.example.adapters..");

            // When
            AdapterProperties props = new AdapterProperties(fqdns);

            // Then
            assertThat(props.fqdns()).isEqualTo(fqdns);
        }

        @Test
        @DisplayName("CommandProperties should store fqdns and allowedLibraries")
        void commandPropertiesShouldStoreValues() {
            // Given
            List<String> fqdns = List.of("com.example.command..");
            List<String> allowedLibraries = List.of("java..", "io.vavr..");

            // When
            CommandProperties props = new CommandProperties(fqdns, allowedLibraries);

            // Then
            assertThat(props.fqdns()).isEqualTo(fqdns);
            assertThat(props.allowedLibraries()).isEqualTo(allowedLibraries);
        }

        @Test
        @DisplayName("QueryProperties should store fqdns and allowedLibraries")
        void queryPropertiesShouldStoreValues() {
            // Given
            List<String> fqdns = List.of("com.example.query..");
            List<String> allowedLibraries = List.of("java..", "io.vavr..");

            // When
            QueryProperties props = new QueryProperties(fqdns, allowedLibraries);

            // Then
            assertThat(props.fqdns()).isEqualTo(fqdns);
            assertThat(props.allowedLibraries()).isEqualTo(allowedLibraries);
        }

        @Test
        @DisplayName("HandlerProperties should store fqdns and allowedLibraries")
        void handlerPropertiesShouldStoreValues() {
            // Given
            List<String> fqdns = List.of("com.example.handler..");
            List<String> allowedLibraries = List.of("java..");

            // When
            HandlerProperties props = new HandlerProperties(fqdns, allowedLibraries);

            // Then
            assertThat(props.fqdns()).isEqualTo(fqdns);
            assertThat(props.allowedLibraries()).isEqualTo(allowedLibraries);
        }

        @Test
        @DisplayName("InputPortsProperties should store fqdns and allowedLibraries")
        void inputPortsPropertiesShouldStoreValues() {
            // Given
            List<String> fqdns = List.of("com.example.ports.in..");
            List<String> allowedLibraries = List.of("java..");

            // When
            InputPortsProperties props = new InputPortsProperties(fqdns, allowedLibraries);

            // Then
            assertThat(props.fqdns()).isEqualTo(fqdns);
            assertThat(props.allowedLibraries()).isEqualTo(allowedLibraries);
        }

        @Test
        @DisplayName("OutputPortsProperties should store fqdns and allowedLibraries")
        void outputPortsPropertiesShouldStoreValues() {
            // Given
            List<String> fqdns = List.of("com.example.ports.out..");
            List<String> allowedLibraries = List.of("java..");

            // When
            OutputPortsProperties props = new OutputPortsProperties(fqdns, allowedLibraries);

            // Then
            assertThat(props.fqdns()).isEqualTo(fqdns);
            assertThat(props.allowedLibraries()).isEqualTo(allowedLibraries);
        }
    }

    @Nested
    @DisplayName("record equality")
    class RecordEqualityTest {

        @Test
        @DisplayName("should be equal for same values")
        void shouldBeEqualForSameValues() {
            // Given
            SharedKernelProperties props1 = new SharedKernelProperties(
                List.of("com.example.."),
                List.of("java..")
            );
            SharedKernelProperties props2 = new SharedKernelProperties(
                List.of("com.example.."),
                List.of("java..")
            );

            // When & Then
            assertThat(props1).isEqualTo(props2);
            assertThat(props1.hashCode()).isEqualTo(props2.hashCode());
        }

        @Test
        @DisplayName("should not be equal for different values")
        void shouldNotBeEqualForDifferentValues() {
            // Given
            SharedKernelProperties props1 = new SharedKernelProperties(
                List.of("com.example.."),
                List.of("java..")
            );
            SharedKernelProperties props2 = new SharedKernelProperties(
                List.of("com.different.."),
                List.of("java..")
            );

            // When & Then
            assertThat(props1).isNotEqualTo(props2);
        }
    }

}
