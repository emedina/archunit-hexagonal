package com.emedina.hexagonal.fixtures.commands;

import com.emedina.sharedkernel.command.Command;

/**
 * A command fixture that is missing the validateThenCreate method.
 */
public class CommandWithoutValidateThenCreate implements Command {

    private final String value;

    private CommandWithoutValidateThenCreate(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
