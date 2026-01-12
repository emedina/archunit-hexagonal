package com.emedina.hexagonal.fixtures.inputports;

import com.emedina.sharedkernel.application.annotation.UseCase;
import com.emedina.sharedkernel.command.core.CommandHandler;

/**
 * A valid input port that extends CommandHandler with a Command type.
 */
@UseCase
public interface ValidCommandUseCase extends CommandHandler<SampleCommand> {

}
