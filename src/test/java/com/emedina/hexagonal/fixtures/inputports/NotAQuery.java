package com.emedina.hexagonal.fixtures.inputports;

/**
 * A class that is NOT a Query (doesn't implement Query interface).
 * Used for testing that QueryHandler must use a Query type.
 */
public record NotAQuery(String data) {

}
