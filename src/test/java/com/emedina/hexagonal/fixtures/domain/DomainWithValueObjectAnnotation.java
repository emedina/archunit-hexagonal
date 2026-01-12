package com.emedina.hexagonal.fixtures.domain;

import com.emedina.sharedkernel.domain.model.annotation.ValueObject;

/**
 * A domain class with an allowed @ValueObject annotation.
 */
@ValueObject
public class DomainWithValueObjectAnnotation {

    private final String value;

    public DomainWithValueObjectAnnotation(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
