package com.emedina.hexagonal.fixtures.inputports;

import com.emedina.sharedkernel.query.Query;

/**
 * A sample query for testing input port generic type validation.
 */
public record SampleQuery(String id) implements Query {

}
