package com.emedina.hexagonal.fixtures.commands;

import com.emedina.sharedkernel.command.Command;
import io.vavr.control.Validation;

/**
 * A command fixture that has a public default constructor (which is not allowed).
 */
public class CommandWithPublicDefaultConstructor implements Command {

    private String value;

    public CommandWithPublicDefaultConstructor() {
        // Public default constructor - not allowed
    }

    private CommandWithPublicDefaultConstructor(final String value) {
        this.value = value;
    }

    public static Validation<String, CommandWithPublicDefaultConstructor> validateThenCreate(final String value) {
        if (value == null || value.isBlank()) {
            return Validation.invalid("Value cannot be null or blank");
        }
        return Validation.valid(new CommandWithPublicDefaultConstructor(value));
    }

    public String getValue() {
        return value;
    }

}
