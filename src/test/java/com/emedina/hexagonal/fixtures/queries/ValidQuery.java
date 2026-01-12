package com.emedina.hexagonal.fixtures.queries;

import com.emedina.sharedkernel.query.Query;
import io.vavr.control.Validation;

/**
 * A valid query fixture that follows all the rules:
 * - Implements Query interface
 * - Has a public static validateThenCreate method returning Validation<?, ValidQuery>
 * - Does not have a public default constructor
 */
public class ValidQuery implements Query {

    private final String id;

    private ValidQuery(final String id) {
        this.id = id;
    }

    public static Validation<String, ValidQuery> validateThenCreate(final String id) {
        if (id == null || id.isBlank()) {
            return Validation.invalid("ID cannot be null or blank");
        }
        return Validation.valid(new ValidQuery(id));
    }

    public String getId() {
        return id;
    }

}
