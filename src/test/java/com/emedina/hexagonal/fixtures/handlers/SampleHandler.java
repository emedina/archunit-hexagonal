package com.emedina.hexagonal.fixtures.handlers;

import com.emedina.sharedkernel.application.annotation.ApplicationService;

/**
 * A valid handler that implements a @UseCase interface with correct naming.
 * Implements SampleUseCase -> named SampleHandler (correct).
 */
@ApplicationService
public class SampleHandler implements SampleUseCase {

    @Override
    public void execute() {
        // Implementation
    }

}
