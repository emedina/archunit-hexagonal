package com.emedina.hexagonal.fixtures.inputports;

import com.emedina.sharedkernel.application.annotation.UseCase;
import com.emedina.sharedkernel.query.core.QueryHandler;

/**
 * A valid input port that extends QueryHandler with a Query type.
 */
@UseCase
public interface ValidQueryUseCase extends QueryHandler<String, SampleQuery> {

}
