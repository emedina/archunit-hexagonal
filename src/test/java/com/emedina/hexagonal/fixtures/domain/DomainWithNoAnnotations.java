package com.emedina.hexagonal.fixtures.domain;

/**
 * A domain class with no annotations (which is allowed).
 */
public class DomainWithNoAnnotations {

    private final String name;

    public DomainWithNoAnnotations(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
