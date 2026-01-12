package com.emedina.hexagonal.fixtures.handlers;

import com.emedina.sharedkernel.application.annotation.ApplicationService;

/**
 * A handler with wrong naming convention.
 * Implements SampleUseCase but is named HandlerWithWrongNaming instead of SampleHandler.
 */
@ApplicationService
public class HandlerWithWrongNaming implements SampleUseCase {

    @Override
    public void execute() {
        // Implementation
    }

}
