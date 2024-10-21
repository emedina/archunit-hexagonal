package com.emedina.hexagonal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration properties for specifying the packages of the hexagonal architecture.
 *
 * @param sharedKernel the properties for the shared kernel
 * @param domain       the properties for the domain
 * @param outputPorts  the properties for the output ports
 * @param inputPorts   the properties for the input ports
 * @param command      the properties for the command
 * @param query        the properties for the query
 * @param handler      the properties for the handler
 * @param adapters     the properties for the adapters
 * @author Enrique Medina Montenegro (em54029)
 */
@ConfigurationProperties(prefix = "hexagonal.architecture")
public record HexagonalArchitectureProperties(SharedKernelProperties sharedKernel, DomainProperties domain,
                                              OutputPortsProperties outputPorts, InputPortsProperties inputPorts,
                                              CommandProperties command, QueryProperties query,
                                              HandlerProperties handler, AdapterProperties adapters) {

    /**
     * Configuration properties for the shared kernel.
     *
     * @param fqdns            the fully qualified domain name of the packages where the shared kernel is located
     * @param allowedLibraries the allowed libraries for the shared kernel
     */
    public record SharedKernelProperties(List<String> fqdns, List<String> allowedLibraries) {
    }

    /**
     * Configuration properties for the domain.
     *
     * @param fqdns            the fully qualified domain name of the packages where the domain is located
     * @param allowedLibraries the allowed libraries for the domain
     */
    public record DomainProperties(List<String> fqdns, List<String> allowedLibraries) {
    }

    /**
     * Configuration properties for the output ports.
     *
     * @param fqdns            the fully qualified domain name of the packages where the output ports are located
     * @param allowedLibraries the allowed libraries for the output ports
     */
    public record OutputPortsProperties(List<String> fqdns, List<String> allowedLibraries) {
    }

    /**
     * Configuration properties for the input ports.
     *
     * @param fqdns            the fully qualified domain name of the packages where the input ports are located
     * @param allowedLibraries the allowed libraries for the input ports
     */
    public record InputPortsProperties(List<String> fqdns, List<String> allowedLibraries) {
    }

    /**
     * Configuration properties for the command.
     *
     * @param fqdns            the fully qualified domain name of the packages where the commands are located
     * @param allowedLibraries the allowed libraries for the command
     */
    public record CommandProperties(List<String> fqdns, List<String> allowedLibraries) {
    }

    /**
     * Configuration properties for the query.
     *
     * @param fqdns            the fully qualified domain name of the packages where the queries are located
     * @param allowedLibraries the allowed libraries for the query
     */
    public record QueryProperties(List<String> fqdns, List<String> allowedLibraries) {
    }

    /**
     * Configuration properties for the handler.
     *
     * @param fqdns            the fully qualified domain name of the packages where the handlers are located
     * @param allowedLibraries the allowed libraries for the handler
     */
    public record HandlerProperties(List<String> fqdns, List<String> allowedLibraries) {
    }

    /**
     * Configuration properties for the adapters.
     *
     * @param fqdns the fully qualified domain name of the packages where the adapters are located
     */
    public record AdapterProperties(List<String> fqdns) {
    }

}
