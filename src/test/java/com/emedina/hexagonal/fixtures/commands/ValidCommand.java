package com.emedina.hexagonal.fixtures.commands;

import com.emedina.sharedkernel.command.Command;
import io.vavr.control.Validation;

/**
 * A valid command fixture that follows all the rules:
 * - Implements Command interface
 * - Has a public static validateThenCreate method returning Validation<?, ValidCommand>
 * - Does not have a public default constructor
 */
public class ValidCommand implements Command {

    private final String value;

    private ValidCommand(final String value) {
        this.value = value;
    }

    public static Validation<String, ValidCommand> validateThenCreate(final String value) {
        if (value == null || value.isBlank()) {
            return Validation.invalid("Value cannot be null or blank");
        }
        return Validation.valid(new ValidCommand(value));
    }

    public String getValue() {
        return value;
    }

}
