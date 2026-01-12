package com.emedina.hexagonal.fixtures.commands;

import com.emedina.sharedkernel.command.Command;
import io.vavr.control.Validation;

/**
 * A command fixture that has a validateThenCreate method but it's not static.
 */
public class CommandWithNonStaticMethod implements Command {

    private final String value;

    private CommandWithNonStaticMethod(final String value) {
        this.value = value;
    }

    // Wrong: not static
    public Validation<String, CommandWithNonStaticMethod> validateThenCreate(final String value) {
        if (value == null || value.isBlank()) {
            return Validation.invalid("Value cannot be null or blank");
        }
        return Validation.valid(new CommandWithNonStaticMethod(value));
    }

    public String getValue() {
        return value;
    }

}
