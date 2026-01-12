package com.emedina.hexagonal.fixtures.handlers;

/**
 * A class that implements a non-UseCase interface.
 * The implementUseCaseInterface predicate should return false for this class.
 */
public class NonUseCaseHandler implements NonUseCaseInterface {

    @Override
    public void doSomething() {
        // Implementation
    }

}
