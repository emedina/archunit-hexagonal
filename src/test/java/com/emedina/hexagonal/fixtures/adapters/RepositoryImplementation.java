package com.emedina.hexagonal.fixtures.adapters;

/**
 * An adapter class that implements a @Repository interface.
 * The implementRepositoryInterface predicate should return true for this class.
 */
public class RepositoryImplementation implements SampleRepositoryInterface {

    @Override
    public void save(Object entity) {
        // Implementation
    }

    @Override
    public Object findById(String id) {
        return null;
    }

}
