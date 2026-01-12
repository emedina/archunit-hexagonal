package com.emedina.hexagonal.fixtures.domain;

import com.emedina.sharedkernel.domain.service.annotation.DomainService;

/**
 * A domain class with an allowed @DomainService annotation.
 */
@DomainService
public class DomainWithDomainServiceAnnotation {

    public void processBusinessLogic() {
        // Domain service logic
    }

}
