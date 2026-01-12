package com.emedina.hexagonal.fixtures.inputports;

import com.emedina.sharedkernel.command.Command;

/**
 * A sample command for testing input port generic type validation.
 */
public record SampleCommand(String value) implements Command {

}
