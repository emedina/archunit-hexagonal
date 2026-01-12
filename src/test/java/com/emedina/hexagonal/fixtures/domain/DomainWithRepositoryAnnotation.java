package com.emedina.hexagonal.fixtures.domain;

import com.emedina.sharedkernel.domain.repository.annotation.Repository;

/**
 * A domain class with an allowed @Repository annotation.
 */
@Repository
public interface DomainWithRepositoryAnnotation {

    void save(Object entity);

}
