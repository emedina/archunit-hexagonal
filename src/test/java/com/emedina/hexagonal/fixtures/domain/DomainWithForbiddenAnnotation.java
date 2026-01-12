package com.emedina.hexagonal.fixtures.domain;

import com.emedina.sharedkernel.application.annotation.UseCase;

/**
 * A domain class with a forbidden @UseCase annotation.
 * Domain classes should not have application-layer annotations.
 */
@UseCase
public interface DomainWithForbiddenAnnotation {

    void execute();

}
