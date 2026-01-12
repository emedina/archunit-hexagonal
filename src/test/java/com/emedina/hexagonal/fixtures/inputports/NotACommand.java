package com.emedina.hexagonal.fixtures.inputports;

/**
 * A class that is NOT a Command (doesn't implement Command interface).
 * Used for testing that CommandHandler must use a Command type.
 */
public record NotACommand(String data) {

}
