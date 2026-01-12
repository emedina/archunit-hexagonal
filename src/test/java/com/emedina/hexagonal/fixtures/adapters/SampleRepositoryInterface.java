package com.emedina.hexagonal.fixtures.adapters;

import com.emedina.sharedkernel.domain.repository.annotation.Repository;

/**
 * A repository interface annotated with @Repository.
 */
@Repository
public interface SampleRepositoryInterface {

    void save(Object entity);

    Object findById(String id);

}
