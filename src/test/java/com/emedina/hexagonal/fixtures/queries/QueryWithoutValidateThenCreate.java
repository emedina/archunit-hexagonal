package com.emedina.hexagonal.fixtures.queries;

import com.emedina.sharedkernel.query.Query;

/**
 * A query fixture that is missing the validateThenCreate method.
 */
public class QueryWithoutValidateThenCreate implements Query {

    private final String id;

    private QueryWithoutValidateThenCreate(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
