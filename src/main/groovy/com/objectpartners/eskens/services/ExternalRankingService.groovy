package com.objectpartners.eskens.services

import com.objectpartners.eskens.entities.Person
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

/**
 * This is to mimic calls to an external 3rd party service that you wouldn't want to test locally.
 */
@Service
@Validated //This makes it a spring proxied service, so unwrapping is necessary to use a Mock
class ExternalRankingService {

    @SuppressWarnings("GrMethodMayBeStatic")
    Rank getRank(Person person) {
        throw new RuntimeException('This feature is not yet implemented.')
    }
}

class Rank {
    int level
    String classification
}
