package com.emedina.hexagonal.fixtures.commands;

import com.emedina.sharedkernel.command.Command;
import io.vavr.control.Validation;

/**
 * A command fixture that has a validateThenCreate method but with wrong return type.
 * The success type should be the class itself, but here it's String.
 */
public class CommandWithWrongMethodSignature implements Command {

    private final String value;

    private CommandWithWrongMethodSignature(final String value) {
        this.value = value;
    }

    // Wrong: returns Validation<String, String> instead of Validation<String, CommandWithWrongMethodSignature>
    public static Validation<String, String> validateThenCreate(final String value) {
        if (value == null || value.isBlank()) {
            return Validation.invalid("Value cannot be null or blank");
        }
        return Validation.valid(value);
    }

    public String getValue() {
        return value;
    }

}
