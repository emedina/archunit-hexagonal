package com.emedina.hexagonal.fixtures.queries;

import com.emedina.sharedkernel.query.Query;
import io.vavr.control.Validation;

/**
 * A query fixture that has a public default constructor (which is not allowed).
 */
public class QueryWithPublicDefaultConstructor implements Query {

    private String id;

    public QueryWithPublicDefaultConstructor() {
        // Public default constructor - not allowed
    }

    private QueryWithPublicDefaultConstructor(final String id) {
        this.id = id;
    }

    public static Validation<String, QueryWithPublicDefaultConstructor> validateThenCreate(final String id) {
        if (id == null || id.isBlank()) {
            return Validation.invalid("ID cannot be null or blank");
        }
        return Validation.valid(new QueryWithPublicDefaultConstructor(id));
    }

    public String getId() {
        return id;
    }

}
