package com.emedina.hexagonal.fixtures.adapters;

/**
 * An adapter class that implements a non-Repository interface.
 * The implementRepositoryInterface predicate should return false for this class.
 */
public class NonRepositoryImplementation implements NonRepositoryInterface {

    @Override
    public void doSomething() {
        // Implementation
    }

}
